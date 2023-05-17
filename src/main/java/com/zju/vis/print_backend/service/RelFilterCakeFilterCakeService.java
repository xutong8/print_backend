package com.zju.vis.print_backend.service;

import com.zju.vis.print_backend.Utils.CollectionUtil;
import com.zju.vis.print_backend.Utils.FileUtil;
import com.zju.vis.print_backend.Utils.ResultVoUtil;
import com.zju.vis.print_backend.compositekey.RelFilterCakeFilterCakeKey;
import com.zju.vis.print_backend.dao.FilterCakeRepository;
import com.zju.vis.print_backend.dao.ProductRepository;
import com.zju.vis.print_backend.dao.RelFilterCakeFilterCakeRepository;
import com.zju.vis.print_backend.entity.FilterCake;
import com.zju.vis.print_backend.entity.RelFilterCakeFilterCake;
import com.zju.vis.print_backend.entity.RelFilterCakeRawMaterial;
import com.zju.vis.print_backend.vo.ExcelRelFilterCakeFilterCakeVo;
import com.zju.vis.print_backend.vo.ExcelRelFilterCakeFilterCakeWriteVo;
import com.zju.vis.print_backend.vo.ExcelRelProductRawMaterialWriteVo;
import com.zju.vis.print_backend.vo.ResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class RelFilterCakeFilterCakeService {
    @Resource
    private RelFilterCakeFilterCakeRepository relFilterCakeFilterCakeRepository;

    @Resource
    private FileService fileService;

    @Resource
    private FilterCakeRepository filterCakeRepository;

    public RelFilterCakeFilterCake addRelFilterCakeFilterCake(RelFilterCakeFilterCake relFilterCakeFilterCake) {
        return relFilterCakeFilterCakeRepository.save(relFilterCakeFilterCake);
    }

    public RelFilterCakeFilterCakeRepository getRelFilterCakeFilterCakeRepository() {
        return relFilterCakeFilterCakeRepository;
    }

    public void setRelFilterCakeFilterCakeRepository(RelFilterCakeFilterCakeRepository relFilterCakeFilterCakeRepository) {
        this.relFilterCakeFilterCakeRepository = relFilterCakeFilterCakeRepository;
    }

    public void delete(RelFilterCakeFilterCake relFilterCakeFilterCake){
        relFilterCakeFilterCakeRepository.delete(relFilterCakeFilterCake);
    }

    // 导入文件
    //-------------------------------------------------------------------------
    public ResultVo importRelFilterCakeFilterCakeExcelAndPersistence(MultipartFile file){
        ResultVo<List<ExcelRelFilterCakeFilterCakeVo>> importResult = fileService.importEntityExcel(file, ExcelRelFilterCakeFilterCakeVo.class);
        if(!importResult.checkSuccess()){
            log.error(importResult.getMsg());
            return importResult;
        }
        List<ExcelRelFilterCakeFilterCakeVo> excelRelFilterCakeFilterCakeVos = importResult.getData();

        // 删除原有关系
        Set<String> relFilterCakesToDelete = new HashSet<>();
        // 获取所有传入的关系表滤饼名称，并据此删除原先的关系
        for(ExcelRelFilterCakeFilterCakeVo excelRelFilterCakeFilterCakeVo: excelRelFilterCakeFilterCakeVos){
            relFilterCakesToDelete.add(excelRelFilterCakeFilterCakeVo.getFilterCakeName());
        }
        // 删除数据库中原有的FF的关系
        for(RelFilterCakeFilterCake relFilterCakeFilterCake: relFilterCakeFilterCakeRepository.findAll()){
            // 如果关系的滤饼与新传入的关系列表对应则删除
            if(relFilterCakesToDelete.contains(relFilterCakeFilterCake.getFilterCake().getFilterCakeName())){
                delete(relFilterCakeFilterCake);
            }
        }

        // 添加新关系
        List<String> warnStringList = new ArrayList<>();
        for(ExcelRelFilterCakeFilterCakeVo excelRelFilterCakeFilterCakeVo: excelRelFilterCakeFilterCakeVos){
            // excel信息转化为关系表实体对象，注意这里有可能出现null对象（不匹配的情况）
            RelFilterCakeFilterCake relFilterCakeFilterCake = transExcelToEntity(excelRelFilterCakeFilterCakeVo);
            if(relFilterCakeFilterCake != null){
                // 如果已有数据则会更新，没有则添加
                addRelFilterCakeFilterCake(relFilterCakeFilterCake);
            }else{
                String warnString = "[Warning] " + "滤饼{ " + excelRelFilterCakeFilterCakeVo.getFilterCakeName() +" }" + "或被使用滤饼{ " + excelRelFilterCakeFilterCakeVo.getFilterCakeNameUsed() +" }" + "未找到对应表项,关系添加失败";
                warnStringList.add(warnString);
            }
        }
        if(warnStringList.size()>0){
            return ResultVoUtil.success(201,"存在未导入表项，请仔细检查数据表以及数据库内容并重新导入",warnStringList);
        }
        return ResultVoUtil.success(excelRelFilterCakeFilterCakeVos);
    }

    public RelFilterCakeFilterCake transExcelToEntity(ExcelRelFilterCakeFilterCakeVo excelRelFilterCakeFilterCakeVo){
        RelFilterCakeFilterCake relFilterCakeFilterCake = new RelFilterCakeFilterCake();
        FilterCake filterCake = filterCakeRepository.findFilterCakeByFilterCakeName(excelRelFilterCakeFilterCakeVo.getFilterCakeName());
        FilterCake filterCakeUsed = filterCakeRepository.findFilterCakeByFilterCakeName(excelRelFilterCakeFilterCakeVo.getFilterCakeNameUsed());
        // 与数据库当前数据有不匹配则返回空
        if(filterCake == null || filterCakeUsed == null){
            return null;
        }
        RelFilterCakeFilterCakeKey id = new RelFilterCakeFilterCakeKey();
        id.setFilterCakeId(filterCake.getFilterCakeId());
        id.setFilterCakeIdUsed(filterCakeUsed.getFilterCakeId());
        relFilterCakeFilterCake.setId(id);
        relFilterCakeFilterCake.setFilterCake(filterCake);
        relFilterCakeFilterCake.setFilterCakeUsed(filterCakeUsed);
        relFilterCakeFilterCake.setInventory(excelRelFilterCakeFilterCakeVo.getInventory());
        return relFilterCakeFilterCake;
    }

    // 导出文件
    //-------------------------------------------------------------------------
    public ResultVo<String> exportRelFilterCakeFilterCakeExcel(HttpServletResponse response){
        // 1.根据查询条件获取结果集
        List<ExcelRelFilterCakeFilterCakeWriteVo> excelRelFilterCakeFilterCakeWriteVos = getExcelRelFilterCakeFilterCakeWriteVoListByCondition();
        if (CollectionUtil.isEmpty(excelRelFilterCakeFilterCakeWriteVos)) {
            log.info("【导出Excel文件】要导出的数据为空，无法导出！");
            return ResultVoUtil.success("数据为空");
        }
        // 2.获取要下载Excel文件的路径
        ResultVo<String> resultVo = fileService.getDownLoadPath(ExcelRelFilterCakeFilterCakeWriteVo.class,excelRelFilterCakeFilterCakeWriteVos);
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

    public List<ExcelRelFilterCakeFilterCakeWriteVo> getExcelRelFilterCakeFilterCakeWriteVoListByCondition(){
        List<ExcelRelFilterCakeFilterCakeWriteVo> excelRelFilterCakeFilterCakeWriteVos = new ArrayList<>();
        for(RelFilterCakeFilterCake relFilterCakeFilterCake: relFilterCakeFilterCakeRepository.findAll()){
            excelRelFilterCakeFilterCakeWriteVos.add(transEntityToExcel(relFilterCakeFilterCake));
        }
        return excelRelFilterCakeFilterCakeWriteVos;
    }

    public ExcelRelFilterCakeFilterCakeWriteVo transEntityToExcel(RelFilterCakeFilterCake relFilterCakeFilterCake){
        ExcelRelFilterCakeFilterCakeWriteVo excelRelFilterCakeFilterCakeWriteVo = new ExcelRelFilterCakeFilterCakeWriteVo();
        excelRelFilterCakeFilterCakeWriteVo.setFilterCakeName(relFilterCakeFilterCake.getFilterCake().getFilterCakeName());
        excelRelFilterCakeFilterCakeWriteVo.setFilterCakeNameUsed(relFilterCakeFilterCake.getFilterCakeUsed().getFilterCakeName());
        excelRelFilterCakeFilterCakeWriteVo.setInventory(relFilterCakeFilterCake.getInventory());
        return excelRelFilterCakeFilterCakeWriteVo;
    }

}
