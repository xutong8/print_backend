package com.zju.vis.print_backend.service;

import com.zju.vis.print_backend.dao.ProductSeriesRepository;
import com.zju.vis.print_backend.entity.Product;
import com.zju.vis.print_backend.entity.ProductSeries;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
public class ProductSeriesService {
    @Resource
    ProductSeriesRepository productSeriesRepository;

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
    public ProductSeries addProductSeries(ProductSeries productSeries) {
        return productSeriesRepository.save(productSeries);
    }

    //改
    //-------------------------------------------------------------------------
    //update product data
    public ProductSeries updateProductSeries(Long productSeriesId, ProductSeries updatedProductSeries) {
        return productSeriesRepository.findById(productSeriesId)
                .map(productSeries -> {
                    productSeries.setProductSeriesName(updatedProductSeries.getProductSeriesName());
                    productSeries.setProductSeriesFunction(updatedProductSeries.getProductSeriesFunction());
                    return productSeriesRepository.save(productSeries);
                })
                .orElseThrow(() -> new NoSuchElementException("Product Series not found with id " + productSeriesId));
    }

}
