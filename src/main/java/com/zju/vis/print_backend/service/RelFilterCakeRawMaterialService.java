package com.zju.vis.print_backend.service;

import com.zju.vis.print_backend.Utils.CollectionUtil;
import com.zju.vis.print_backend.Utils.FileUtil;
import com.zju.vis.print_backend.Utils.ResultVoUtil;
import com.zju.vis.print_backend.compositekey.RelFilterCakeRawMaterialKey;
import com.zju.vis.print_backend.dao.FilterCakeRepository;
import com.zju.vis.print_backend.dao.RawMaterialRepository;
import com.zju.vis.print_backend.dao.RelFilterCakeRawMaterialRepository;
import com.zju.vis.print_backend.entity.FilterCake;
import com.zju.vis.print_backend.entity.RawMaterial;
import com.zju.vis.print_backend.entity.RelFilterCakeRawMaterial;
import com.zju.vis.print_backend.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Filter;

@Slf4j
@Service
public class RelFilterCakeRawMaterialService {
    @Resource
    private RelFilterCakeRawMaterialRepository relFilterCakeRawMaterialRepository;

    @Resource
    private FileService fileService;

    @Resource
    private FilterCakeRepository filterCakeRepository;

    @Resource
    private RawMaterialRepository rawMaterialRepository;

    public RelFilterCakeRawMaterial addRelFilterCakeRawMaterial(RelFilterCakeRawMaterial relFilterCakeRawMaterial) {
        return relFilterCakeRawMaterialRepository.save(relFilterCakeRawMaterial);
    }

    public RelFilterCakeRawMaterialRepository getRelFilterCakeRawMaterialRepository() {
        return relFilterCakeRawMaterialRepository;
    }

    public void setRelFilterCakeRawMaterialRepository(RelFilterCakeRawMaterialRepository relFilterCakeRawMaterialRepository) {
        this.relFilterCakeRawMaterialRepository = relFilterCakeRawMaterialRepository;
    }

    public void delete(RelFilterCakeRawMaterial relFilterCakeRawMaterial){
        relFilterCakeRawMaterialRepository.delete(relFilterCakeRawMaterial);
    }

    public void deleteById(RelFilterCakeRawMaterialKey id){
        relFilterCakeRawMaterialRepository.deleteById(id);
    }

    // 导入文件
    //-------------------------------------------------------------------------
    public ResultVo importRelFilterCakeRawMaterialExcelAndPersistence(MultipartFile file){
        ResultVo<List<ExcelRelFilterCakeRawMaterialVo>> importResult = fileService.importEntityExcel(file, ExcelRelFilterCakeRawMaterialVo.class);
        if(!importResult.checkSuccess()){
            log.error(importResult.getMsg());
            return importResult;
        }
        List<ExcelRelFilterCakeRawMaterialVo> excelRelFilterCakeRawMaterialVos = importResult.getData();
        for(ExcelRelFilterCakeRawMaterialVo excelRelFilterCakeRawMaterialVo: excelRelFilterCakeRawMaterialVos){
            // excel信息转化为关系表实体对象，注意这里有可能出现null对象（不匹配的情况）
            RelFilterCakeRawMaterial relFilterCakeRawMaterial = transExcelToEntity(excelRelFilterCakeRawMaterialVo);
            if(relFilterCakeRawMaterial != null){
                // 如果已有数据则会更新，没有则添加
                addRelFilterCakeRawMaterial(relFilterCakeRawMaterial);
            }
        }
        return ResultVoUtil.success(excelRelFilterCakeRawMaterialVos);
    }

    public RelFilterCakeRawMaterial transExcelToEntity(ExcelRelFilterCakeRawMaterialVo excelRelFilterCakeRawMaterialVo){
        RelFilterCakeRawMaterial relFilterCakeRawMaterial = new RelFilterCakeRawMaterial();
        FilterCake filterCake = filterCakeRepository.findFilterCakeByFilterCakeName(excelRelFilterCakeRawMaterialVo.getFilterCakeName());
        RawMaterial rawMaterial = rawMaterialRepository.findRawMaterialByRawMaterialName(excelRelFilterCakeRawMaterialVo.getRawMaterialName());
        if(filterCake == null || rawMaterial == null){
            return null;
        }
        RelFilterCakeRawMaterialKey id = new RelFilterCakeRawMaterialKey();
        id.setRawMaterialId(rawMaterial.getRawMaterialId());
        id.setFilterCakeId(filterCake.getFilterCakeId());
        relFilterCakeRawMaterial.setId(id);
        relFilterCakeRawMaterial.setRawMaterial(rawMaterial);
        relFilterCakeRawMaterial.setFilterCake(filterCake);
        relFilterCakeRawMaterial.setInventory(excelRelFilterCakeRawMaterialVo.getInventory());
        return relFilterCakeRawMaterial;
    }

    // 导入文件
    //-------------------------------------------------------------------------
    public ResultVo<String> exportRelFilterCakeRawMaterialExcel(HttpServletResponse response){
        // 1.根据查询条件获取结果集
        List<ExcelRelFilterCakeRawMaterialWriteVo> excelRelFilterCakeRawMaterialWriteVos = getExcelRelFilterCakeRawMaterialWriteVoListByCondition();
        if (CollectionUtil.isEmpty(excelRelFilterCakeRawMaterialWriteVos)) {
            log.info("【导出Excel文件】要导出的数据为空，无法导出！");
            return ResultVoUtil.success("数据为空");
        }
        // 2.获取要下载Excel文件的路径
        ResultVo<String> resultVo = fileService.getDownLoadPath(ExcelRelFilterCakeRawMaterialWriteVo.class,excelRelFilterCakeRawMaterialWriteVos);
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

    public List<ExcelRelFilterCakeRawMaterialWriteVo> getExcelRelFilterCakeRawMaterialWriteVoListByCondition(){
        List<ExcelRelFilterCakeRawMaterialWriteVo> excelRelFilterCakeRawMaterialWriteVos = new ArrayList<>();
        for(RelFilterCakeRawMaterial relFilterCakeRawMaterial: relFilterCakeRawMaterialRepository.findAll()){
            excelRelFilterCakeRawMaterialWriteVos.add(transEntityToExcel(relFilterCakeRawMaterial));
        }
        return excelRelFilterCakeRawMaterialWriteVos;
    }

    public ExcelRelFilterCakeRawMaterialWriteVo transEntityToExcel(RelFilterCakeRawMaterial relFilterCakeRawMaterial){
        ExcelRelFilterCakeRawMaterialWriteVo excelRelFilterCakeRawMaterialWriteVo = new ExcelRelFilterCakeRawMaterialWriteVo();
        excelRelFilterCakeRawMaterialWriteVo.setRawMaterialName(relFilterCakeRawMaterial.getRawMaterial().getRawMaterialName());
        excelRelFilterCakeRawMaterialWriteVo.setFilterCakeName(relFilterCakeRawMaterial.getFilterCake().getFilterCakeName());
        excelRelFilterCakeRawMaterialWriteVo.setInventory(relFilterCakeRawMaterial.getInventory());
        return excelRelFilterCakeRawMaterialWriteVo;
    }
}
