package com.zju.vis.print_backend.service;

import com.zju.vis.print_backend.Utils.CollectionUtil;
import com.zju.vis.print_backend.Utils.FileUtil;
import com.zju.vis.print_backend.Utils.ResultVoUtil;
import com.zju.vis.print_backend.compositekey.RelProductProductKey;
import com.zju.vis.print_backend.dao.ProductRepository;
import com.zju.vis.print_backend.dao.RelProductProductRepository;
import com.zju.vis.print_backend.entity.Product;
import com.zju.vis.print_backend.entity.RelProductProduct;
import com.zju.vis.print_backend.vo.ExcelRelProductProductVo;
import com.zju.vis.print_backend.vo.ExcelRelProductProductWriteVo;
import com.zju.vis.print_backend.vo.ResultVo;
import lombok.extern.slf4j.Slf4j;
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
public class RelProductProductService {
    @Resource
    private RelProductProductRepository relProductProductRepository;

    @Resource
    private FileService fileService;

    @Resource
    private ProductRepository productRepository;

    public RelProductProduct addRelProductProduct(RelProductProduct relProductProduct){
        return relProductProductRepository.save(relProductProduct);
    }

    public void delete(RelProductProduct relProductProduct){
        relProductProductRepository.delete(relProductProduct);
    }

    // 导入文件
    //-------------------------------------------------------------------------
    public ResultVo importRelProductProductExcelAndPersistence(MultipartFile file){
        ResultVo<List<ExcelRelProductProductVo>> importResult = fileService.importEntityExcel(file, ExcelRelProductProductVo.class);
        if(!importResult.checkSuccess()){
            log.error(importResult.getMsg());
            return importResult;
        }
        List<ExcelRelProductProductVo> excelRelProductProductVos = importResult.getData();

        // 删除原有关系
        Set<String> relProductToDelete = new HashSet<>();
        // 获取所有传入的关系表商品的名称，并据此删除原先的关系
        for(ExcelRelProductProductVo excelRelProductProductVo: excelRelProductProductVos){
            relProductToDelete.add(excelRelProductProductVo.getProductName());
        }
        // 删除数据库中原有的PP关系
        for(RelProductProduct relProductProduct: relProductProductRepository.findAll()){
            // 如果关系的产品与新传入的关系列表对应则删除
            if(relProductToDelete.contains(relProductProduct.getProduct().getProductName())){
                delete(relProductProduct);
            }
        }

        // 添加新关系
        List<String> warnStringList = new ArrayList<>();
        for(ExcelRelProductProductVo excelRelProductProductVo: excelRelProductProductVos){
            // excel信息转化为关系表实体对象，注意这里有可能出现null对象（不匹配的情况）
            RelProductProduct relProductProduct = transExcelToEntity(excelRelProductProductVo);
            if(relProductProduct != null){
                // 如果已有数据则会更新
                addRelProductProduct(relProductProduct);
            }else{
                String warnString = "[Warning] " + "商品{ " + excelRelProductProductVo.getProductName() +" }" + "或被使用商品{ " + excelRelProductProductVo.getProductNameUsed() +" }" + "未找到对应表项,关系添加失败";
                warnStringList.add(warnString);
            }
        }
        if(warnStringList.size()>0){
            return ResultVoUtil.success(201,"存在未导入表项，请仔细检查数据表以及数据库内容并重新导入",warnStringList);
        }
        return ResultVoUtil.success(excelRelProductProductVos);
    }

    public RelProductProduct transExcelToEntity(ExcelRelProductProductVo excelRelProductProductVo){
        RelProductProduct relProductProduct = new RelProductProduct();
        Product product = productRepository.findProductByProductName(excelRelProductProductVo.getProductName());
        Product productUsed = productRepository.findProductByProductName(excelRelProductProductVo.getProductNameUsed());
        // 与数据库当前数据有不匹配则返回空
        if(product == null || productUsed == null){
            return null;
        }
        RelProductProductKey id = new RelProductProductKey();
        id.setProductId(product.getProductId());
        id.setProductIdUsed(productUsed.getProductId());
        relProductProduct.setId(id);
        relProductProduct.setProduct(product);
        relProductProduct.setProductUsed(productUsed);
        relProductProduct.setInventory(excelRelProductProductVo.getInventory());
        return relProductProduct;
    }

    // 导出文件
    //-------------------------------------------------------------------------
    public ResultVo<String> exportRelProductProductExcel(HttpServletResponse response){
        // 1.根据查询条件获取结果集
        List<ExcelRelProductProductWriteVo> excelRelProductProductWriteVos = getExcelRelProductProductWriteVoListByCondition();
        if (CollectionUtil.isEmpty(excelRelProductProductWriteVos)) {
            log.info("【导出Excel文件】要导出的数据为空，无法导出！");
            return ResultVoUtil.success("数据为空");
        }
        // 2.获取要下载Excel文件的路径
        ResultVo<String> resultVo = fileService.getDownLoadPath(ExcelRelProductProductWriteVo.class,excelRelProductProductWriteVos);
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

    public List<ExcelRelProductProductWriteVo> getExcelRelProductProductWriteVoListByCondition(){
        List<ExcelRelProductProductWriteVo> excelRelProductProductWriteVos = new ArrayList<>();
        for(RelProductProduct relProductProduct: relProductProductRepository.findAll()){
            excelRelProductProductWriteVos.add(transEntityToExcel(relProductProduct));
        }
        return excelRelProductProductWriteVos;
    }

    public ExcelRelProductProductWriteVo transEntityToExcel(RelProductProduct relProductProduct){
        ExcelRelProductProductWriteVo excelRelProductProductWriteVo = new ExcelRelProductProductWriteVo();
        excelRelProductProductWriteVo.setProductName(relProductProduct.getProduct().getProductName());
        excelRelProductProductWriteVo.setProductNameUsed(relProductProduct.getProductUsed().getProductName());
        excelRelProductProductWriteVo.setInventory(relProductProduct.getInventory());
        return excelRelProductProductWriteVo;
    }

}
