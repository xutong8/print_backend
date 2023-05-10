package com.zju.vis.print_backend.service;

import com.zju.vis.print_backend.Utils.ResultVoUtil;
import com.zju.vis.print_backend.compositekey.RelProductRawMaterialKey;
import com.zju.vis.print_backend.dao.ProductRepository;
import com.zju.vis.print_backend.dao.RawMaterialRepository;
import com.zju.vis.print_backend.dao.RelProductRawMaterialRepository;
import com.zju.vis.print_backend.entity.Product;
import com.zju.vis.print_backend.entity.RawMaterial;
import com.zju.vis.print_backend.entity.RelProductRawMaterial;
import com.zju.vis.print_backend.vo.ExcelRelProductFilterCakeVo;
import com.zju.vis.print_backend.vo.ExcelRelProductRawMaterialVo;
import com.zju.vis.print_backend.vo.ResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class RelProductRawMaterialService {

    @Resource
    private RelProductRawMaterialRepository relProductRawMaterialRepository;

    @Resource
    private FileService fileService;

    @Resource
    private ProductRepository productRepository;

    @Resource
    private RawMaterialRepository rawMaterialRepository;

    public RelProductRawMaterial findRelProductRawMaterialById(RelProductRawMaterialKey id){
        return relProductRawMaterialRepository.findRelProductRawMaterialByIdEquals(id);
    }

    public RelProductRawMaterial addRelProductRawMaterial(RelProductRawMaterial relProductRawMaterial) {
        return relProductRawMaterialRepository.save(relProductRawMaterial);
    }

    public void deleteRelProductRawMaterial(RelProductRawMaterial relProductRawMaterial){
        relProductRawMaterialRepository.delete(relProductRawMaterial);
    }

    public RelProductRawMaterialRepository getRelProductRawMaterialRepository() {
        return relProductRawMaterialRepository;
    }

    public void setRelProductRawMaterialRepository(RelProductRawMaterialRepository relProductRawMaterialRepository) {
        this.relProductRawMaterialRepository = relProductRawMaterialRepository;
    }

    public void delete(RelProductRawMaterial relProductRawMaterial){
        relProductRawMaterialRepository.delete(relProductRawMaterial);
    }

    public void deleteById(RelProductRawMaterialKey id){
        relProductRawMaterialRepository.deleteById(id);
    }

    // 导入文件
    //-------------------------------------------------------------------------
    public ResultVo importRelProductRawMaterialExcelAndPersistence(MultipartFile file){
        ResultVo<List<ExcelRelProductRawMaterialVo>> importResult = fileService.importEntityExcel(file, ExcelRelProductRawMaterialVo.class);
        if(!importResult.checkSuccess()){
            log.error(importResult.getMsg());
            return importResult;
        }
        List<ExcelRelProductRawMaterialVo> excelRelProductRawMaterialVos = importResult.getData();
        for(ExcelRelProductRawMaterialVo excelRelProductRawMaterialVo: excelRelProductRawMaterialVos){
            // excel信息转化为关系表实体对象，注意这里有可能出现null对象（不匹配的情况）
            RelProductRawMaterial relProductRawMaterial = transExcelToEntity(excelRelProductRawMaterialVo);
            if(relProductRawMaterial != null){
                // 如果已有数据则会更新，没有则添加
                addRelProductRawMaterial(relProductRawMaterial);
            }
        }
        return ResultVoUtil.success(excelRelProductRawMaterialVos);
    }

    // 可能出现不匹配的情况
    public RelProductRawMaterial transExcelToEntity(ExcelRelProductRawMaterialVo excelRelProductRawMaterialVo){
        RelProductRawMaterial relProductRawMaterial = new RelProductRawMaterial();
        Product product = productRepository.findProductByProductName(excelRelProductRawMaterialVo.getProductName());
        RawMaterial rawMaterial = rawMaterialRepository.findRawMaterialByRawMaterialName(excelRelProductRawMaterialVo.getRawMaterialName());
        // 与数据库当前数据有不匹配的现象
        if(product == null || rawMaterial == null){
            return null;
        }
        RelProductRawMaterialKey id = new RelProductRawMaterialKey();
        id.setProductId(product.getProductId());
        id.setRawMaterialId(rawMaterial.getRawMaterialId());
        relProductRawMaterial.setId(id);
        relProductRawMaterial.setProduct(product);
        relProductRawMaterial.setRawMaterial(rawMaterial);
        relProductRawMaterial.setInventory(excelRelProductRawMaterialVo.getInventory());
        return relProductRawMaterial;
    }
}