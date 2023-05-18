package com.zju.vis.print_backend.service;

import com.zju.vis.print_backend.Utils.CollectionUtil;
import com.zju.vis.print_backend.Utils.FileUtil;
import com.zju.vis.print_backend.Utils.ResultVoUtil;
import com.zju.vis.print_backend.compositekey.RelProductFilterCakeKey;
import com.zju.vis.print_backend.dao.FilterCakeRepository;
import com.zju.vis.print_backend.dao.ProductRepository;
import com.zju.vis.print_backend.dao.RelProductFilterCakeRepository;

import com.zju.vis.print_backend.entity.FilterCake;
import com.zju.vis.print_backend.entity.Product;
import com.zju.vis.print_backend.entity.RelProductFilterCake;
import com.zju.vis.print_backend.entity.RelProductRawMaterial;
import com.zju.vis.print_backend.vo.ExcelRawMaterialWriteVo;
import com.zju.vis.print_backend.vo.ExcelRelProductFilterCakeVo;
import com.zju.vis.print_backend.vo.ExcelRelProductFilterCakeWriteVo;
import com.zju.vis.print_backend.vo.ResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

        // 删除原有关系
        Set<String> relProductsToDelete = new HashSet<>();
        // 获取所有传入的关系表商品名称，并据此删除原先的关系
        for(ExcelRelProductFilterCakeVo excelRelProductFilterCakeVo: excelRelProductFilterCakeVos){
            relProductsToDelete.add(excelRelProductFilterCakeVo.getProductName());
        }
        // 删除数据库中原有的PF关系
        List<RelProductFilterCake> list = new ArrayList<>();
        for(RelProductFilterCake relProductFilterCake: relProductFilterCakeRepository.findAll()){
            // 如果关系的商品名与新传入的关系列表对应则删除
            if(relProductsToDelete.contains(relProductFilterCake.getProduct().getProductName())){
                list.add(relProductFilterCake);
                // delete(relProductFilterCake);
            }
        }
        relProductFilterCakeRepository.deleteAllInBatch(list);

        // 添加新关系
        List<String> warnStringList = new ArrayList<>();
        for(ExcelRelProductFilterCakeVo excelRelProductFilterCakeVo: excelRelProductFilterCakeVos){
            // excel信息转化为关系表实体对象，注意这里有可能出现null对象（不匹配的情况）
            RelProductFilterCake relProductFilterCake = transExcelToEntity(excelRelProductFilterCakeVo);
            // 只有当关系为有效关系时才更新数据库
            if(relProductFilterCake != null){
                // 如果已有数据则会更新，没有则添加
                addRelProductFilterCake(relProductFilterCake);
            }else{
                String warnString = "[Warning] " + "产品{ " + excelRelProductFilterCakeVo.getProductName() +" }" + "或滤饼{ " + excelRelProductFilterCakeVo.getFilterCakeName() +" }" + "未找到对应表项,关系添加失败";
                warnStringList.add(warnString);
            }
        }
        if(warnStringList.size()>0){
            return ResultVoUtil.success(201,"存在未导入表项，请仔细检查数据表以及数据库内容并重新导入",warnStringList);
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
    public ResultVo<String> exportRelProductFilterCakeExcel(HttpServletResponse response){
        // 1.根据查询条件获取结果集
        List<ExcelRelProductFilterCakeWriteVo> excelRelProductFilterCakeWriteVos = getExcelRelProductFilterCakeWriteVoListByCondition();
        if (CollectionUtil.isEmpty(excelRelProductFilterCakeWriteVos)) {
            log.info("【导出Excel文件】要导出的数据为空，无法导出！");
            return ResultVoUtil.success("数据为空");
        }
        // 2.获取要下载Excel文件的路径
        ResultVo<String> resultVo = fileService.getDownLoadPath(ExcelRelProductFilterCakeWriteVo.class,excelRelProductFilterCakeWriteVos);
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

    public List<ExcelRelProductFilterCakeWriteVo> getExcelRelProductFilterCakeWriteVoListByCondition(){
        List<ExcelRelProductFilterCakeWriteVo> excelRelProductFilterCakeWriteVos = new ArrayList<>();
        for(RelProductFilterCake relProductFilterCake: relProductFilterCakeRepository.findAll()){
            excelRelProductFilterCakeWriteVos.add(transRelProductFilterCakeToExcel(relProductFilterCake));
        }
        return excelRelProductFilterCakeWriteVos;
    }

    public ExcelRelProductFilterCakeWriteVo transRelProductFilterCakeToExcel(RelProductFilterCake relProductFilterCake){
        ExcelRelProductFilterCakeWriteVo excelRelProductFilterCakeWriteVo = new ExcelRelProductFilterCakeWriteVo();
        excelRelProductFilterCakeWriteVo.setProductName(relProductFilterCake.getProduct().getProductName());
        excelRelProductFilterCakeWriteVo.setFilterCakeName(relProductFilterCake.getFilterCake().getFilterCakeName());
        excelRelProductFilterCakeWriteVo.setInventory(relProductFilterCake.getInventory());
        return excelRelProductFilterCakeWriteVo;
    }
}
