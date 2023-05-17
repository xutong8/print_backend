package com.zju.vis.print_backend.service;

import com.zju.vis.print_backend.Utils.CollectionUtil;
import com.zju.vis.print_backend.Utils.FileUtil;
import com.zju.vis.print_backend.Utils.ResultVoUtil;
import com.zju.vis.print_backend.compositekey.RelDateRawMaterialKey;
import com.zju.vis.print_backend.dao.RawMaterialRepository;
import com.zju.vis.print_backend.dao.RelDateRawMaterialRepository;
import com.zju.vis.print_backend.entity.RawMaterial;
import com.zju.vis.print_backend.entity.RelDateRawMaterial;
import com.zju.vis.print_backend.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class RelDateRawMaterialService {
    @Resource
    private RelDateRawMaterialRepository relDateRawMaterialRepository;

    @Resource
    private FileService fileService;

    @Resource
    private RawMaterialRepository rawMaterialRepository;

    public RelDateRawMaterial addRelDateRawMaterial(RelDateRawMaterial relDateRawMaterial){
        return relDateRawMaterialRepository.save(relDateRawMaterial);
    }

    public void delete(RelDateRawMaterial relDateRawMaterial){
        relDateRawMaterialRepository.delete(relDateRawMaterial);
    }

    public void deleteById(RelDateRawMaterialKey id){
        relDateRawMaterialRepository.deleteById(id);
    }

    // 导入文件
    //-------------------------------------------------------------------------
    public ResultVo importRelDateRawMaterialExcelAndPersistence(MultipartFile file){
        ResultVo<List<ExcelRelDateRawMaterialVo>> importResult = fileService.importEntityExcel(file, ExcelRelDateRawMaterialVo.class);
        if(!importResult.checkSuccess()){
            log.error(importResult.getMsg());
            return importResult;
        }
        List<ExcelRelDateRawMaterialVo> excelRelDateRawMaterialVos = importResult.getData();

        // 删除原有关系
        Set<String> relRawMaterialsToDelete = new HashSet<>();
        // 获取所有传入的原料名称，并据此删除原先的关系
        for(ExcelRelDateRawMaterialVo excelRelDateRawMaterialVo: excelRelDateRawMaterialVos){
            relRawMaterialsToDelete.add(excelRelDateRawMaterialVo.getRawMaterialName());
        }
        // 删除数据库中原有的DR关系
        for(RelDateRawMaterial relDateRawMaterial: relDateRawMaterialRepository.findAll()){
            // 如果关系的原料名与新传入的关系列表对应则删除
            if(relRawMaterialsToDelete.contains(relDateRawMaterial.getRawMaterial().getRawMaterialName())){
                delete(relDateRawMaterial);
            }
        }

        // 添加新关系
        List<String> warnStringList = new ArrayList<>();
        for(ExcelRelDateRawMaterialVo excelRelDateRawMaterialVo: excelRelDateRawMaterialVos){
            // excel信息转化为关系表实体对象，注意这里有可能出现null对象（不匹配的情况）
            RelDateRawMaterial relDateRawMaterial = transExcelToEntity(excelRelDateRawMaterialVo);
            if(relDateRawMaterial != null){
                // 如果已有数据则会更新，没有则添加
                addRelDateRawMaterial(relDateRawMaterial);
            }else{
                String warnString = "[Warning] " + "原料{ " + excelRelDateRawMaterialVo.getRawMaterialName() +" }" + "日期{ " + excelRelDateRawMaterialVo.getRawMaterialDate() +" }" + "添加失败，请检查原料或日期表项";
                warnStringList.add(warnString);
            }
        }
        if(warnStringList.size()>0){
            return ResultVoUtil.success(201,"存在未导入表项，请仔细检查数据表以及数据库内容并重新导入",warnStringList);
        }
        return ResultVoUtil.success(excelRelDateRawMaterialVos);
    }

    public static java.sql.Date StringToDate(String sDate) {
        /**
         *str转date方法
         */
        String str = sDate;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date d = null;
        try {
            d = format.parse(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        java.sql.Date date = new java.sql.Date(d.getTime());
        return date;
    }

    public RelDateRawMaterial transExcelToEntity(ExcelRelDateRawMaterialVo excelRelDateRawMaterialVo){
        RelDateRawMaterial relDateRawMaterial = new RelDateRawMaterial();
        RawMaterial rawMaterial = rawMaterialRepository.findRawMaterialByRawMaterialName(excelRelDateRawMaterialVo.getRawMaterialName());
        Date rawMaterialDate = StringToDate(excelRelDateRawMaterialVo.getRawMaterialDate());
        // Date rawMaterialDate = excelRelDateRawMaterialVo.getRawMaterialDate();
        // 与数据库当前数据有不匹配则返回空
        if(rawMaterial == null || rawMaterialDate == null){
            return null;
        }
        RelDateRawMaterialKey id = new RelDateRawMaterialKey();
        id.setRawMaterialId(rawMaterial.getRawMaterialId());
        id.setRawMaterialDate(rawMaterialDate);
        relDateRawMaterial.setId(id);
        relDateRawMaterial.setRawMaterial(rawMaterial);
        relDateRawMaterial.setPrice(excelRelDateRawMaterialVo.getPrice());
        return relDateRawMaterial;
    }

    // 导出文件
    //-------------------------------------------------------------------------
    public ResultVo<String> exportRelDateRawMaterialExcel(HttpServletResponse response){
        // 1.根据查询条件获取结果集
        List<ExcelRelDateRawMaterialWriteVo> excelRelDateRawMaterialWriteVos = getExcelRelDateRawMaterialWriteVoListByCondition();
        if (CollectionUtil.isEmpty(excelRelDateRawMaterialWriteVos)) {
            log.info("【导出Excel文件】要导出的数据为空，无法导出！");
            return ResultVoUtil.success("数据为空");
        }
        // 2.获取要下载Excel文件的路径
        ResultVo<String> resultVo = fileService.getDownLoadPath(ExcelRelDateRawMaterialWriteVo.class,excelRelDateRawMaterialWriteVos);
        if (!resultVo.checkSuccess()) {
            log.error("【导出Excel文件】获取要下载Excel文件的路径失败");
            return resultVo;
        }
        // 3.下载Excel文件
        String fileDownLoadPath = resultVo.getData();
        ResultVo<String> downLoadResultVo = fileService.downloadFile(fileDownLoadPath, response);
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

    public List<ExcelRelDateRawMaterialWriteVo> getExcelRelDateRawMaterialWriteVoListByCondition(){
        List<ExcelRelDateRawMaterialWriteVo> excelRelDateRawMaterialWriteVos = new ArrayList<>();
        for(RelDateRawMaterial relDateRawMaterial: relDateRawMaterialRepository.findAll()){
            excelRelDateRawMaterialWriteVos.add(transEntityToExcel(relDateRawMaterial));
        }
        return excelRelDateRawMaterialWriteVos;
    }

    public ExcelRelDateRawMaterialWriteVo transEntityToExcel(RelDateRawMaterial relDateRawMaterial){
        ExcelRelDateRawMaterialWriteVo excelRelDateRawMaterialWriteVo = new ExcelRelDateRawMaterialWriteVo();
        excelRelDateRawMaterialWriteVo.setRawMaterialName(relDateRawMaterial.getRawMaterial().getRawMaterialName());
        excelRelDateRawMaterialWriteVo.setRawMaterialDate(relDateRawMaterial.getId().getRawMaterialDate().toString());
        excelRelDateRawMaterialWriteVo.setPrice(relDateRawMaterial.getPrice());
        return excelRelDateRawMaterialWriteVo;
    }
}
