package com.zju.vis.print_backend.service;

import com.alibaba.fastjson.JSON;
import com.zju.vis.print_backend.Utils.*;
import com.zju.vis.print_backend.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class FileService {
    // FileUtil fileUtil = new FileUtil();

    // 上传文件
    public ResultVo<String> uploadFile(MultipartFile file){
        log.info("【文件上传】进入到文件上传方法");
        // 1.参数校验
        if (null == file || file.isEmpty()) {
            log.error("【文件上传】文件为空!");
            throw new FileUtil.ParamErrorException();
        }
        // 2.上传文件
        ResultVo<String> resultVo = FileUtil.uploadFile(file);
        return resultVo;
    }

    // 上传多个文件
    public ResultVo uploadFiles(MultipartFile[] files) {
        log.info("【批量上传】进入到批量上传文件");
        if (null == files || files.length == 0) {
            log.error("【批量上传】上传的文件为空，files={}", files);
            throw new FileUtil.ParamErrorException();
        }
        List<MultipartFile> multipartFiles = Arrays.asList(files);
        // 1.校验是否有空文件
        List<String> emptyFileNames = new ArrayList<>();
        List<MultipartFile> needUploadFiles = new ArrayList<>();
        int count = 0;
        for (MultipartFile file : multipartFiles) {
            if (null == file) {
                count++;
                continue;
            }
            if (file.isEmpty()) {
                emptyFileNames.add(file.getOriginalFilename());
                count++;
                continue;
            }
            needUploadFiles.add(file);
        }
        if (count == multipartFiles.size()) {
            log.error("【批量上传】批量上传的文件为空，无法正确上传");
            return ResultVoUtil.error("批量上传的文件为空，无法正确上传");
        }
        if (CollectionUtil.isNotEmpty(emptyFileNames)) {
            log.info("【批量上传】一共上传了{}个文件，其中，空文件数为{}，空文件名分别是：{}", multipartFiles.size(), count, emptyFileNames);
        } else {
            log.info("【批量上传】一共上传了{}个文件", multipartFiles.size());
        }
        // 2.批量上传文件
        List<String> uploadFailFileNames = new ArrayList<>(needUploadFiles.size());
        needUploadFiles.forEach((file) -> {
            ResultVo<String> resultVo = FileUtil.uploadFile(file);
            // 如果没有上传成功
            if (!resultVo.checkSuccess()) {
                uploadFailFileNames.add(file.getName());
            }
        });
        if (CollectionUtil.isNotEmpty(uploadFailFileNames)) {
            log.error("一共上传了{}个文件，其中上传失败的文件数为{}，文件名分别为：{}", needUploadFiles.size(), uploadFailFileNames.size(), uploadFailFileNames);
            return ResultVoUtil.success("一共上传了" + needUploadFiles.size() + "个文件，其中上传失败的文件数为" + uploadFailFileNames.size() + "，文件名分别为：" + uploadFailFileNames);
        }
        log.info("批量上传文件成功");
        return ResultVoUtil.success();
    }


    // 下载文件
    public ResultVo<String> downloadFile(String filePath, HttpServletResponse response) {
        File file = new File(filePath);
        // 1.参数校验
        if (!file.exists()) {
            log.error("【下载文件】文件路径{}不存在", filePath);
            return ResultVoUtil.error("文件不存在");
        }
        // 2.下载文件
        log.info("【下载文件】下载文件的路径为{}", filePath);
        return FileUtil.downloadFile(file, response);
    }

    // 导入excel文件
    @Resource
    private ExcelUtil excelUtil;

    // 表格操作
    //-------------------------------------------------------------------------
    // 测试导入表格
    public ResultVo importExcel(MultipartFile file) {
        // 1.入参校验
        ResultVo<String> checkExcelParam = checkExcelParam(file);
        if (!checkExcelParam.checkSuccess()) {
            log.error(checkExcelParam.getMsg());
            return checkExcelParam;
        }
        // 2.上传至服务器某路径下
        ResultVo resultVo = uploadFile(file);
        if (!resultVo.checkSuccess()) {
            return resultVo;
        }
        String filePath = (String)resultVo.getData();
        if (StringUtil.isBlank(filePath)) {
            return ResultVoUtil.error("【导入Excel文件】生成的Excel文件的路径为空");
        }
        // 3.读取excel文件
        List<ExcelVo> excelVos = excelUtil.simpleExcelRead(filePath, ExcelVo.class);
        if (CollectionUtil.isEmpty(excelVos) || excelVos.size() < 2) {
            log.error("【导入Excel文件】上传Excel文件{}为空", file.getOriginalFilename());
            return ResultVoUtil.error("上传Excel文件为空");
        }
        // 4.通过线程池开启一个线程去执行数据库操作，主线程继续往下执行
        // 4.1开启一个线程
        TaskCenterUtil taskCenterUtil = TaskCenterUtil.getTaskCenterUtil();
        taskCenterUtil.submitTask(() -> {
            log.info("【批量添加】批量添加数据：{}", JSON.toJSONString(excelVos));
            return null;
        });
        // 4.2删除临时文件
        boolean deleteFile = FileUtil.deleteFile(new File(filePath));
        if (!deleteFile) {
            log.error("【导入Excel文件】删除临时文件失败，临时文件路径为{}", filePath);
            return ResultVoUtil.error("删除临时文件失败");
        }
        log.info("【导入Excel文件】删除临时文件成功，临时文件路径为：{}", filePath);
        System.out.println(excelVos);
        return ResultVoUtil.success(excelVos);
    }

    // 检验参数
    public ResultVo<String> checkExcelParam(MultipartFile file) {
        log.info("【上传Excel文件】进入到上传Excel文件方法...");
        if (null == file || file.isEmpty()) {
            log.error("【上传Excel文件】上传的文件为空，file={}", file);
            throw new FileUtil.ParamErrorException();
        }
        boolean b = ExcelUtil.checkExcelExtension(file);
        if (!b) {
            return ResultVoUtil.error("上传的不是Excel文件，请上传正确格式的Excel文件");
        }
        return ResultVoUtil.success();
    }

    // excel导出
    public ResultVo<String> exportExcel(HttpServletResponse response) {
        // 1.根据查询条件获取结果集
        List<ExcelWriteVo> excelWriteVos = getExcelWriteVoListByCondition();
        if (CollectionUtil.isEmpty(excelWriteVos)) {
            log.info("【导出Excel文件】要导出的数据为空，无法导出！");
            return ResultVoUtil.success("数据为空");
        }
        // 2.获取要下载Excel文件的路径
        ResultVo<String> resultVo = getDownLoadPath(ExcelWriteVo.class, excelWriteVos);
        if (!resultVo.checkSuccess()) {
            log.error("【导出Excel文件】获取要下载Excel文件的路径失败");
            return resultVo;
        }
        // 3.下载Excel文件
        String fileDownLoadPath = resultVo.getData();
        ResultVo<String> downLoadResultVo = downloadFile(fileDownLoadPath, response);
        if (null != downLoadResultVo && !downLoadResultVo.checkSuccess()) {
            log.error("【导出Excel文件】下载文件失败");
            return downLoadResultVo;
        }
        // 4.删除临时文件
        boolean deleteFile = FileUtil.deleteFile(new File(fileDownLoadPath));
        if (!deleteFile) {
            log.error("【导入Excel文件】删除临时文件失败，临时文件路径为{}", fileDownLoadPath);
            return ResultVoUtil.error("删除临时文件失败");
        }
        log.info("【导入Excel文件】删除临时文件成功，临时文件路径为：{}", fileDownLoadPath);
        return null;
    }

    public List<ExcelWriteVo> getExcelWriteVoListByCondition() {
        List<ExcelWriteVo> excelWriteVos = new ArrayList<>(5);
        excelWriteVos.add(new ExcelWriteVo("zzc", "男", "2021-11-14 20:00:00"));
        excelWriteVos.add(new ExcelWriteVo("wzc", "女", "2021-11-14 20:00:00"));
        excelWriteVos.add(new ExcelWriteVo("wxc", "男", "2021-11-14 20:00:00"));
        return excelWriteVos;
    }

    // 导入产品表
    public ResultVo importProductExcel(MultipartFile file){
        // 1.入参校验
        ResultVo<String> checkExcelParam = checkExcelParam(file);
        if(!checkExcelParam.checkSuccess()){
            log.error(checkExcelParam.getMsg());
            return checkExcelParam;
        }
        // 2.上传到服务器某路径下
        ResultVo resultVo = uploadFile(file);
        if(!resultVo.checkSuccess()){
            return resultVo;
        }
        String filePath = (String)resultVo.getData();
        if (StringUtil.isBlank(filePath)) {
            return ResultVoUtil.error("【导入Excel文件】生成的Excel文件的路径为空");
        }
        // 3.读取excel文件
        List<ExcelProductVo> excelProductVos = excelUtil.simpleExcelRead(filePath, ExcelProductVo.class);
        if (CollectionUtil.isEmpty(excelProductVos) || excelProductVos.size() < 2) {
            log.error("【导入Excel文件】上传Excel文件{}为空", file.getOriginalFilename());
            return ResultVoUtil.error("上传Excel文件为空");
        }
        // 4.通过线程池开启一个线程去执行数据库操作，主线程继续往下执行
        // 4.1开启一个线程
        TaskCenterUtil taskCenterUtil = TaskCenterUtil.getTaskCenterUtil();
        taskCenterUtil.submitTask(() -> {
            log.info("【批量添加】批量添加数据：{}", JSON.toJSONString(excelProductVos));
            return null;
        });
        // 4.2删除临时文件
        boolean deleteFile = FileUtil.deleteFile(new File(filePath));
        if (!deleteFile) {
            log.error("【导入Excel文件】删除临时文件失败，临时文件路径为{}", filePath);
            return ResultVoUtil.error("删除临时文件失败");
        }
        log.info("【导入Excel文件】删除临时文件成功，临时文件路径为：{}", filePath);
        return ResultVoUtil.success(excelProductVos);
    }

    // 导入原料表
    public ResultVo importRawMaterialExcel(MultipartFile file){
        // 1.入参校验
        ResultVo<String> checkExcelParam = checkExcelParam(file);
        if(!checkExcelParam.checkSuccess()){
            log.error(checkExcelParam.getMsg());
            return checkExcelParam;
        }
        // 2.上传到服务器某路径下
        ResultVo resultVo = uploadFile(file);
        if(!resultVo.checkSuccess()){
            return resultVo;
        }
        String filePath = (String)resultVo.getData();
        if (StringUtil.isBlank(filePath)) {
            return ResultVoUtil.error("【导入Excel文件】生成的Excel文件的路径为空");
        }
        // 3.读取excel文件
        List<ExcelRawMaterialVo> excelRawMaterialVos = excelUtil.simpleExcelRead(filePath, ExcelRawMaterialVo.class);
        if (CollectionUtil.isEmpty(excelRawMaterialVos) || excelRawMaterialVos.size() < 2) {
            log.error("【导入Excel文件】上传Excel文件{}为空", file.getOriginalFilename());
            return ResultVoUtil.error("上传Excel文件为空");
        }
        // 4.通过线程池开启一个线程去执行数据库操作，主线程继续往下执行
        // 4.1开启一个线程
        TaskCenterUtil taskCenterUtil = TaskCenterUtil.getTaskCenterUtil();
        taskCenterUtil.submitTask(() -> {
            log.info("【批量添加】批量添加数据：{}", JSON.toJSONString(excelRawMaterialVos));
            return null;
        });
        // 4.2删除临时文件
        boolean deleteFile = FileUtil.deleteFile(new File(filePath));
        if (!deleteFile) {
            log.error("【导入Excel文件】删除临时文件失败，临时文件路径为{}", filePath);
            return ResultVoUtil.error("删除临时文件失败");
        }
        log.info("【导入Excel文件】删除临时文件成功，临时文件路径为：{}", filePath);
        return ResultVoUtil.success(excelRawMaterialVos);
    }

    public <T> ResultVo importEntityExcel(MultipartFile file,Class<T> clazz){
        // 1.入参校验
        ResultVo<String> checkExcelParam = checkExcelParam(file);
        if(!checkExcelParam.checkSuccess()){
            log.error(checkExcelParam.getMsg());
            return checkExcelParam;
        }
        // 2.上传到服务器某路径下
        ResultVo resultVo = uploadFile(file);
        if(!resultVo.checkSuccess()){
            return resultVo;
        }
        String filePath = (String)resultVo.getData();
        if (StringUtil.isBlank(filePath)) {
            return ResultVoUtil.error("【导入Excel文件】生成的Excel文件的路径为空");
        }
        // 3.读取excel文件
        List<T> excelTVos = excelUtil.simpleExcelRead(filePath, clazz);
        if (CollectionUtil.isEmpty(excelTVos) || excelTVos.size() < 2) {
            log.error("【导入Excel文件】上传Excel文件{}为空", file.getOriginalFilename());
            return ResultVoUtil.error("上传Excel文件为空");
        }
        // 4.通过线程池开启一个线程去执行数据库操作，主线程继续往下执行
        // 4.1开启一个线程
        TaskCenterUtil taskCenterUtil = TaskCenterUtil.getTaskCenterUtil();
        taskCenterUtil.submitTask(() -> {
            log.info("【批量添加】批量添加数据：{}", JSON.toJSONString(excelTVos));
            return null;
        });
        // 4.2删除临时文件
        boolean deleteFile = FileUtil.deleteFile(new File(filePath));
        if (!deleteFile) {
            log.error("【导入Excel文件】删除临时文件失败，临时文件路径为{}", filePath);
            return ResultVoUtil.error("删除临时文件失败");
        }
        log.info("【导入Excel文件】删除临时文件成功，临时文件路径为：{}", filePath);
        return ResultVoUtil.success(excelTVos);
    }

    public ResultVo<String> getDownLoadPath(Class<ExcelWriteVo> clazz, List<ExcelWriteVo> excelWriteVos) {
        String downLoadPath = FileUtil.getDownLoadPath();
        if (StringUtil.isBlank(downLoadPath)) {
            log.error("【导出Excel文件】生成临时文件失败");
            return ResultVoUtil.error("生成临时文件失败");
        }
        // 1.创建一个临时目录
        FileUtil.mkdirs(downLoadPath);
        // String fullFilePath = downLoadPath + File.separator + System.currentTimeMillis() + "." + ExcelUtil.EXCEL_2007;
        String fullFilePath = downLoadPath + File.separator + System.currentTimeMillis() + "." + "xlsx";
        log.info("【导出Excel文件】文件的临时路径为：{}", fullFilePath);
        // 2.写入数据
        excelUtil.simpleExcelWrite(fullFilePath, clazz, excelWriteVos);
        return ResultVoUtil.success(fullFilePath);
    }

}
