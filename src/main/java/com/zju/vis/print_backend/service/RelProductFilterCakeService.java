package com.zju.vis.print_backend.service;

import com.zju.vis.print_backend.Utils.ResultVoUtil;
import com.zju.vis.print_backend.compositekey.RelProductFilterCakeKey;
import com.zju.vis.print_backend.dao.FilterCakeRepository;
import com.zju.vis.print_backend.dao.ProductRepository;
import com.zju.vis.print_backend.dao.RelProductFilterCakeRepository;

import com.zju.vis.print_backend.entity.FilterCake;
import com.zju.vis.print_backend.entity.Product;
import com.zju.vis.print_backend.entity.RelProductFilterCake;
import com.zju.vis.print_backend.vo.ExcelRelProductFilterCakeVo;
import com.zju.vis.print_backend.vo.ResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class RelProductFilterCakeService {
    @Resource
    private RelProductFilterCakeRepository relProductFilterCakeRepository;

    @Resource
    private FileService fileService;

    @Resource
    private ProductRepository productRepository;

    @Resource
    private FilterCakeRepository filterCakeRepository;


    public RelProductFilterCake findRelProductFilterCakeById(RelProductFilterCakeKey id){
        return relProductFilterCakeRepository.findRelProductFilterCakeByIdEquals(id);
    }

    public RelProductFilterCake addRelProductFilterCake(RelProductFilterCake relProductFilterCake) {
        return relProductFilterCakeRepository.save(relProductFilterCake);
    }

    public void delete(RelProductFilterCake relProductFilterCake){
        relProductFilterCakeRepository.delete(relProductFilterCake);
    }

    public void deleteRelProductFilterCake(RelProductFilterCake relProductFilterCake){
        relProductFilterCakeRepository.delete(relProductFilterCake);
    }

    public RelProductFilterCakeRepository getRelProductFilterCakeRepository() {
        return relProductFilterCakeRepository;
    }

    public void setRelProductFilterCakeRepository(RelProductFilterCakeRepository relProductFilterCakeRepository) {
        this.relProductFilterCakeRepository = relProductFilterCakeRepository;
    }

    // 导入文件
    //-------------------------------------------------------------------------
    public ResultVo importRelProductFilterCakeExcelAndPersistence(MultipartFile file){
        ResultVo<List<ExcelRelProductFilterCakeVo>> importResult = fileService.importEntityExcel(file, ExcelRelProductFilterCakeVo.class);
        if(!importResult.checkSuccess()){
            log.error(importResult.getMsg());
            return importResult;
        }
        List<ExcelRelProductFilterCakeVo> excelRelProductFilterCakeVos = importResult.getData();
        for(ExcelRelProductFilterCakeVo excelRelProductFilterCakeVo: excelRelProductFilterCakeVos){
            // excel信息转化为关系表实体对象，注意这里有可能出现null对象（不匹配的情况）
            RelProductFilterCake relProductFilterCake = transExcelToEntity(excelRelProductFilterCakeVo);
            // 只有当关系为有效关系时才更新数据库
            if(relProductFilterCake != null){
                // 如果已有数据则会更新，没有则添加
                addRelProductFilterCake(relProductFilterCake);
            }
        }
        return ResultVoUtil.success(excelRelProductFilterCakeVos);
    }

    // 可能出现不匹配的情况
    public RelProductFilterCake transExcelToEntity(ExcelRelProductFilterCakeVo excelRelProductFilterCakeVo){
        RelProductFilterCake relProductFilterCake = new RelProductFilterCake();
        Product product = productRepository.findProductByProductName(excelRelProductFilterCakeVo.getProductName());
        FilterCake filterCake = filterCakeRepository.findFilterCakeByFilterCakeName(excelRelProductFilterCakeVo.getFilterCakeName());
        // 与数据库当前数据有不匹配的现象
        if(product == null || filterCake == null){
            return null;
        }
        RelProductFilterCakeKey id = new RelProductFilterCakeKey();
        id.setProductId(product.getProductId());
        id.setFilterCakeId(filterCake.getFilterCakeId());
        relProductFilterCake.setId(id);
        relProductFilterCake.setProduct(product);
        relProductFilterCake.setFilterCake(filterCake);
        relProductFilterCake.setInventory(excelRelProductFilterCakeVo.getInventory());
        return relProductFilterCake;
    }

    // 导出文件
    //-------------------------------------------------------------------------

}
