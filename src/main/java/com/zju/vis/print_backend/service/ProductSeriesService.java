package com.zju.vis.print_backend.service;

import com.zju.vis.print_backend.Utils.*;
import com.zju.vis.print_backend.dao.ProductSeriesRepository;
import com.zju.vis.print_backend.entity.Product;
import com.zju.vis.print_backend.entity.ProductSeries;
import com.zju.vis.print_backend.vo.ExcelProductSeriesVo;
import com.zju.vis.print_backend.vo.ExcelProductSeriesWriteVo;
import com.zju.vis.print_backend.vo.ExcelRawMaterialWriteVo;
import com.zju.vis.print_backend.vo.ResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;

@Slf4j
@Service
public class ProductSeriesService {
    @Resource
    ProductSeriesRepository productSeriesRepository;

    // 导入excel文件
    @Resource
    private ExcelUtil excelUtil;

    @Resource
    private FileService fileService;

    // 调用一般方法
    Utils utils = new Utils();

    // 调用其他服务类的方法
    ProductService productService = new ProductService();

    public class ProductSeriesName{
        private Long productSeriesId;
        private String productSeriesName;

        public Long getProductSeriesId() {
            return productSeriesId;
        }

        public void setProductSeriesId(Long productSeriesId) {
            this.productSeriesId = productSeriesId;
        }

        public String getProductSeriesName() {
            return productSeriesName;
        }

        public void setProductSeriesName(String productSeriesName) {
            this.productSeriesName = productSeriesName;
        }
    }

    public class ProductSeriesPackage{
        // 附加信息
        private Integer pageNo;
        private Integer pageSize;
        private Integer pageNum;
        private Integer total;

        // 返回的标准产品系列表
        private List<ProductSeriesStandard> list;

        public Integer getPageNo() {
            return pageNo;
        }

        public void setPageNo(Integer pageNo) {
            this.pageNo = pageNo;
        }

        public Integer getPageSize() {
            return pageSize;
        }

        public void setPageSize(Integer pageSize) {
            this.pageSize = pageSize;
        }

        public Integer getPageNum() {
            return pageNum;
        }

        public void setPageNum(Integer pageNum) {
            this.pageNum = pageNum;
        }

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }

        public List<ProductSeriesStandard> getList() {
            return list;
        }

        public void setList(List<ProductSeriesStandard> list) {
            this.list = list;
        }
    }

    // List<ProductSeries> 添加额外信息打包发送,传输进来的已经是分割过的串
    public ProductSeriesPackage packProductSeries(List<ProductSeries> productSeriesList,
                                                  Integer pageNo, Integer pageSize,
                                                  Integer productSeriesNum){
        List<ProductSeriesStandard> productSeriesStandardList = new ArrayList<>();
        for(ProductSeries productSeries: productSeriesList){
            productSeriesStandardList.add(ProductSeriesStandardization(productSeries));
        }
        ProductSeriesPackage productSeriesPackage = new ProductSeriesPackage();
        // 前端page从1开始，返回时+1
        productSeriesPackage.setPageNo(pageNo + 1);
        productSeriesPackage.setPageSize(pageSize);
        productSeriesPackage.setPageNum(
                (productSeriesNum - 1 ) / pageSize + 1
        );
        productSeriesPackage.setTotal(productSeriesNum);
        productSeriesPackage.setList(productSeriesStandardList);
        return productSeriesPackage;
    }

    public static class ProductSeriesStandard{
        private Long productSeriesId;
        private String productSeriesName;
        private String productSeriesFunction;
        private List<ProductService.ProductSimple> productSimpleList;

        public Long getProductSeriesId() {
            return productSeriesId;
        }

        public void setProductSeriesId(Long productSeriesId) {
            this.productSeriesId = productSeriesId;
        }

        public String getProductSeriesName() {
            return productSeriesName;
        }

        public void setProductSeriesName(String productSeriesName) {
            this.productSeriesName = productSeriesName;
        }

        public String getProductSeriesFunction() {
            return productSeriesFunction;
        }

        public void setProductSeriesFunction(String productSeriesFunction) {
            this.productSeriesFunction = productSeriesFunction;
        }

        public List<ProductService.ProductSimple> getProductSimpleList() {
            return productSimpleList;
        }

        public void setProductSimpleList(List<ProductService.ProductSimple> productSimpleList) {
            this.productSimpleList = productSimpleList;
        }
    }

    public ProductSeriesStandard ProductSeriesStandardization(ProductSeries productSeries){
        ProductSeriesStandard productSeriesStandard = new ProductSeriesStandard();
        productSeriesStandard.setProductSeriesId(productSeries.getProductSeriesId());
        productSeriesStandard.setProductSeriesName(productSeries.getProductSeriesName());
        productSeriesStandard.setProductSeriesFunction(productSeries.getProductSeriesFunction());
        // 设置返回的简单产品列表
        List<ProductService.ProductSimple> productSimpleList = new ArrayList<>();
        for(Product product: productSeries.getProductList()){
            productSimpleList.add(productService.simplifyProduct(product));
        }
        productSeriesStandard.setProductSimpleList(productSimpleList);
        return productSeriesStandard;
    }

    //查
    //-------------------------------------------------------------------------
    public ProductSeriesPackage findAll(Integer pageNo,
                                       Integer pageSize
    ) {
        Integer productSeriesNum = productSeriesRepository.findAll().size();
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<ProductSeries> page = productSeriesRepository.findAll(pageable);
        return packProductSeries(page.toList(), pageNo, pageSize, productSeriesNum);
    }

    public  List<ProductSeriesName> findAllProductSeriesName(){
        List<ProductSeriesName> productSeriesNameList = new ArrayList<>();
        for(ProductSeries productSeries: productSeriesRepository.findAll()){
            ProductSeriesName productSeriesName = new ProductSeriesName();
            productSeriesName.setProductSeriesId(productSeries.getProductSeriesId());
            productSeriesName.setProductSeriesName(productSeries.getProductSeriesName());
            productSeriesNameList.add(productSeriesName);
        }
        return productSeriesNameList;
    }

    public ProductSeriesStandard findProductSeriesByProductSeriesId(Long productSeriesId) {
        if(productSeriesRepository.findProductSeriesByProductSeriesId(productSeriesId) == null){
            return new ProductSeriesStandard();
        }
        return ProductSeriesStandardization(productSeriesRepository.findProductSeriesByProductSeriesId(productSeriesId));
    }

    public List<ProductSeries> findProductSeriesByProductSeriesNameContaining(String productSeries) {
        // 空字符串返回全部值
        if (utils.isEmptyString(productSeries)) {
            return productSeriesRepository.findAll();
        }
        return productSeriesRepository.findProductSeriesByProductSeriesNameContaining(productSeries);
    }

    public ProductSeries findProductSeriesByProductSeriesName(String productSeriesName){
        return productSeriesRepository.findProductSeriesByProductSeriesName(productSeriesName);
    }

    public Long findProductSeriesIdByProductSeriesName(String productSeriesName){
        return productSeriesRepository.findProductSeriesIdByProductSeriesName(productSeriesName);
    }

    public Set<Product> findProductsByProductSeriesName(String productSeriesName) {
        System.out.println("productSeriesName: " + productSeriesName);
        ProductSeries productSeries = findProductSeriesByProductSeriesName(productSeriesName);
        // List<ProductSeries> productSeriesList = findProductSeriesByProductSeriesNameContaining(productSeriesName);
        Set<Product> productSet = new HashSet<>();
        if (productSeries != null) {
            productSet.addAll(productSeries.getProductList());
        }
        System.out.println("产品系列对应的产品数量");
        System.out.println(productSet.size());
        return productSet;
    }

    //根据 删除记录
    @Transactional
    public void deleteByProductSeriesId(Long productSeriesId) {
        productSeriesRepository.deleteByProductSeriesId(productSeriesId);
    }

    //增
    //-------------------------------------------------------------------------
    //add productSeries data
    public ProductSeries deStandardizeProductSeries(ProductSeriesStandard productSeriesStandard){
        ProductSeries productSeries = new ProductSeries();
        productSeries.setProductSeriesId(productSeriesStandard.getProductSeriesId());
        productSeries.setProductSeriesName(productSeriesStandard.getProductSeriesName());
        productSeries.setProductSeriesFunction(productSeriesStandard.getProductSeriesFunction());

        return productSeries;
    }

    public ProductSeries addProductSeries(ProductSeriesStandard productSeriesStandard) {
        ProductSeries productSeries = deStandardizeProductSeries(productSeriesStandard);
        // 添加时指定一个不存在的id进而使用自增id
        productSeries.setProductSeriesId(new Long(0));
        return productSeriesRepository.save(productSeries);
    }

    //改
    //-------------------------------------------------------------------------
    //update product data
    public String updateProductSeries(ProductSeriesStandard updatedProductSeries) {
        if(productSeriesRepository.findProductSeriesByProductSeriesId(updatedProductSeries.getProductSeriesId()) == null){
            ProductSeries addedProductSeries = addProductSeries(updatedProductSeries);
            return "数据库中不存在对应数据,已添加Id为" + addedProductSeries.getProductSeriesId() + "的条目" ;
        }
        ProductSeries productSeries = deStandardizeProductSeries(updatedProductSeries);
        productSeriesRepository.save(productSeries);
        return "ProductSeries " + productSeries.getProductSeriesName() + " has been changed";
    }

    // 导入文件
    //-------------------------------------------------------------------------
    public ResultVo importProductSeriesExcelAndPersistence(MultipartFile file){
        // 1.获取输入结果
        ResultVo<List<ExcelProductSeriesVo>> importResult = fileService.importEntityExcel(file, ExcelProductSeriesVo.class);
        if(!importResult.checkSuccess()){
            log.error(importResult.getMsg());
            return importResult;
        }
        // 2.获取输入类
        List<ExcelProductSeriesVo> excelProductSeriesVos = importResult.getData();
        for(ExcelProductSeriesVo excelProductSeriesVo: excelProductSeriesVos){
            // excel信息转化为标准类
            ProductSeriesStandard productSeriesStandard = transExcelToStandard(excelProductSeriesVo);
            // 更新数据库，已存在则会直接替换
            updateProductSeries(productSeriesStandard);
        }
        return ResultVoUtil.success(excelProductSeriesVos);
    }

    public ProductSeriesStandard transExcelToStandard(ExcelProductSeriesVo excelProductSeriesVo){
        ProductSeriesStandard productSeriesStandard = new ProductSeriesStandard();
        // 如果已经存在了则修改，否则则添加
        if(productSeriesRepository.findProductSeriesByProductSeriesName(excelProductSeriesVo.getProductSeriesName()) == null){
            // 表示添加
            productSeriesStandard.setProductSeriesId(new Long(0));
        }else{
            productSeriesStandard.setProductSeriesId(productSeriesRepository.findProductSeriesByProductSeriesName(excelProductSeriesVo.getProductSeriesName()).getProductSeriesId());
        }
        productSeriesStandard.setProductSeriesName(excelProductSeriesVo.getProductSeriesName());
        productSeriesStandard.setProductSeriesFunction(excelProductSeriesVo.getProductSeriesFunction());
        return productSeriesStandard;
    }

    // 导出文件
    //-------------------------------------------------------------------------
    public ResultVo<String> exportProductSeriesExcel(HttpServletResponse response){
        // 1.根据查询条件获取结果集
        List<ExcelProductSeriesWriteVo> excelProductSeriesWriteVos = getExcelProductSeriesWriteVoListByCondition();
        if (CollectionUtil.isEmpty(excelProductSeriesWriteVos)) {
            log.info("【导出Excel文件】要导出的数据为空，无法导出！");
            return ResultVoUtil.success("数据为空");
        }
        // 2.获取要下载Excel文件的路径
        ResultVo<String> resultVo = fileService.getDownLoadPath(ExcelProductSeriesWriteVo.class,excelProductSeriesWriteVos);
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

    public List<ExcelProductSeriesWriteVo> getExcelProductSeriesWriteVoListByCondition(){
        List<ExcelProductSeriesWriteVo> excelProductSeriesWriteVos = new ArrayList<>();
        for(ProductSeries productSeries: productSeriesRepository.findAll()){
            excelProductSeriesWriteVos.add(transProductSeriesToExcel(productSeries));
        }
        return excelProductSeriesWriteVos;
    }

    public ExcelProductSeriesWriteVo transProductSeriesToExcel(ProductSeries productSeries){
        ExcelProductSeriesWriteVo excelProductSeriesWriteVo = new ExcelProductSeriesWriteVo();
        excelProductSeriesWriteVo.setProductSeriesName(productSeries.getProductSeriesName());
        excelProductSeriesWriteVo.setProductSeriesFunction(productSeries.getProductSeriesFunction());
        return excelProductSeriesWriteVo;
    }

}
