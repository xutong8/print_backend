package com.zju.vis.print_backend.service;

import com.zju.vis.print_backend.Utils.CollectionUtil;
import com.zju.vis.print_backend.Utils.FileUtil;
import com.zju.vis.print_backend.Utils.ResultVoUtil;
import com.zju.vis.print_backend.compositekey.RelProductRawMaterialKey;
import com.zju.vis.print_backend.dao.ProductRepository;
import com.zju.vis.print_backend.dao.RawMaterialRepository;
import com.zju.vis.print_backend.dao.RelProductRawMaterialRepository;
import com.zju.vis.print_backend.entity.Product;
import com.zju.vis.print_backend.entity.RawMaterial;
import com.zju.vis.print_backend.entity.RelProductRawMaterial;
import com.zju.vis.print_backend.vo.*;
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

        // 删除原有关系
        Set<String> relProductsToDelete = new HashSet<>();
        // 获取所有传入的关系表商品名称，并据此删除原先的关系
        for(ExcelRelProductRawMaterialVo excelRelProductRawMaterialVo: excelRelProductRawMaterialVos){
            relProductsToDelete.add(excelRelProductRawMaterialVo.getProductName());
        }
        // 删除数据库中原有的PR关系
        for(RelProductRawMaterial relProductRawMaterial: relProductRawMaterialRepository.findAll()){
            // 如果关系的商品名与新传入的关系列表对应则删除
            if(relProductsToDelete.contains(relProductRawMaterial.getProduct().getProductName())){
                delete(relProductRawMaterial);
            }
        }

        // 添加新关系
        List<String> warnStringList = new ArrayList<>();
        for(ExcelRelProductRawMaterialVo excelRelProductRawMaterialVo: excelRelProductRawMaterialVos){
            // excel信息转化为关系表实体对象，注意这里有可能出现null对象（不匹配的情况）
            RelProductRawMaterial relProductRawMaterial = transExcelToEntity(excelRelProductRawMaterialVo);
            if(relProductRawMaterial != null){
                // 如果已有数据则会更新，没有则添加
                addRelProductRawMaterial(relProductRawMaterial);
            }else{
                String warnString = "[Warning] " + "产品{ " + excelRelProductRawMaterialVo.getProductName() +" }" + "或原料{ " + excelRelProductRawMaterialVo.getRawMaterialName() +" }" + "未找到对应表项,关系添加失败";
                warnStringList.add(warnString);
            }
        }
        if(warnStringList.size()>0){
            return ResultVoUtil.success(201,"存在未导入表项，请仔细检查数据表以及数据库内容并重新导入",warnStringList);
        }
        return ResultVoUtil.success(excelRelProductRawMaterialVos);
    }

    public RelProductRawMaterial transExcelToEntity(ExcelRelProductRawMaterialVo excelRelProductRawMaterialVo){
        RelProductRawMaterial relProductRawMaterial = new RelProductRawMaterial();
        Product product = productRepository.findProductByProductName(excelRelProductRawMaterialVo.getProductName());
        RawMaterial rawMaterial = rawMaterialRepository.findRawMaterialByRawMaterialName(excelRelProductRawMaterialVo.getRawMaterialName());
        // 与数据库当前数据有不匹配则返回空
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

    // 导出文件
    //-------------------------------------------------------------------------
    public ResultVo<String> exportRelProductRawMaterialExcel(HttpServletResponse response){
        // 1.根据查询条件获取结果集
        List<ExcelRelProductRawMaterialWriteVo> excelRelProductRawMaterialWriteVos = getExcelRelProductRawMaterialWriteVoListByCondition();
        if (CollectionUtil.isEmpty(excelRelProductRawMaterialWriteVos)) {
            log.info("【导出Excel文件】要导出的数据为空，无法导出！");
            return ResultVoUtil.success("数据为空");
        }
        // 2.获取要下载Excel文件的路径
        ResultVo<String> resultVo = fileService.getDownLoadPath(ExcelRelProductRawMaterialWriteVo.class,excelRelProductRawMaterialWriteVos);
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

    public List<ExcelRelProductRawMaterialWriteVo> getExcelRelProductRawMaterialWriteVoListByCondition(){
        List<ExcelRelProductRawMaterialWriteVo> excelRelProductRawMaterialWriteVos = new ArrayList<>();
        for(RelProductRawMaterial relProductRawMaterial: relProductRawMaterialRepository.findAll()){
            excelRelProductRawMaterialWriteVos.add(transRelProductRawMaterialToExcel(relProductRawMaterial));
        }
        return excelRelProductRawMaterialWriteVos;
    }

    public ExcelRelProductRawMaterialWriteVo transRelProductRawMaterialToExcel(RelProductRawMaterial relProductRawMaterial){
        ExcelRelProductRawMaterialWriteVo excelRelProductRawMaterialWriteVo = new ExcelRelProductRawMaterialWriteVo();
        excelRelProductRawMaterialWriteVo.setProductName(relProductRawMaterial.getProduct().getProductName());
        excelRelProductRawMaterialWriteVo.setRawMaterialName(relProductRawMaterial.getRawMaterial().getRawMaterialName());
        excelRelProductRawMaterialWriteVo.setInventory(relProductRawMaterial.getInventory());
        return excelRelProductRawMaterialWriteVo;
    }
}