package com.zju.vis.print_backend.service;

import com.zju.vis.print_backend.Utils.ResultVoUtil;
import com.zju.vis.print_backend.compositekey.RelFilterCakeRawMaterialKey;
import com.zju.vis.print_backend.dao.FilterCakeRepository;
import com.zju.vis.print_backend.dao.RawMaterialRepository;
import com.zju.vis.print_backend.dao.RelFilterCakeRawMaterialRepository;
import com.zju.vis.print_backend.entity.FilterCake;
import com.zju.vis.print_backend.entity.RawMaterial;
import com.zju.vis.print_backend.entity.RelFilterCakeRawMaterial;
import com.zju.vis.print_backend.vo.ExcelRelFilterCakeRawMaterialVo;
import com.zju.vis.print_backend.vo.ExcelRelProductRawMaterialVo;
import com.zju.vis.print_backend.vo.ResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
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

}
