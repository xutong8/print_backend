package com.zju.vis.print_backend.service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;



import com.zju.vis.print_backend.entity.ProductSeries;
import com.zju.vis.print_backend.entity.RawMaterial;
import io.swagger.models.auth.In;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.zju.vis.print_backend.dao.ProductRepository;
import com.zju.vis.print_backend.entity.Product;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {
    //
    @Resource
    private ProductRepository productRepository;

    // 调用其他Service的方法
    @Resource
    private RawMaterialService rawMaterialService;

    @Resource
    private FilterCakeService filterCakeService;

    @Resource
    private ProductSeriesService productSeriesService;

    //Product 结果封装
    public class ProductPackage{
        // 附加信息
        private Integer pageNo;
        private Integer pageSize;
        private Integer pageNum;
        private Integer productNum;

        // 返回的标准列表
        private List<ProductStandard>  productStandardList;

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

        public Integer getProductNum() {
            return productNum;
        }

        public void setProductNum(Integer productNum) {
            this.productNum = productNum;
        }

        public List<ProductStandard> getProductStandardList() {
            return productStandardList;
        }

        public void setProductStandardList(List<ProductStandard> productStandardList) {
            this.productStandardList = productStandardList;
        }
    }

    // List<ProductStandard> 添加额外信息打包发送
    public ProductPackage packProduct(List<Product> productList,
                                      Integer pageNo,Integer pageSize,
                                      Integer productNum){
        List<ProductStandard> productStandardList = new ArrayList<>();
        for(Product product: productList){
            productStandardList.add(ProductStandardization(product));
        }
        ProductPackage productPackage = new ProductPackage();
        productPackage.setPageNo(pageNo);
        productPackage.setPageSize(pageSize);
        productPackage.setPageNum(
                (productNum-1) / pageSize + 1
        );
        // productPackage.setProductNum(pageNum);
        // productPackage.setProductNum(productStandardList.size());
        productPackage.setProductNum(productNum);
        productPackage.setProductStandardList(productStandardList);
        return productPackage;
    }

    // Product 标准化形式类 (前端单个节点最终结果
    public class ProductStandard{
        private Long productId;
        private String productName;
        private String productIndex;
        private String productCode;
        private String productColor;
        private Double productUnitPrice;
        private Integer productPriceIncreasePercent;
        private String productSeriesName;
        private String productFactoryName;
        private String productRemarks;

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getProductIndex() {
            return productIndex;
        }

        public void setProductIndex(String productIndex) {
            this.productIndex = productIndex;
        }

        public String getProductCode() {
            return productCode;
        }

        public void setProductCode(String productCode) {
            this.productCode = productCode;
        }

        public String getProductColor() {
            return productColor;
        }

        public void setProductColor(String productColor) {
            this.productColor = productColor;
        }

        public Double getProductUnitPrice() {
            return productUnitPrice;
        }

        public void setProductUnitPrice(Double productUnitPrice) {
            this.productUnitPrice = productUnitPrice;
        }

        public Integer getProductPriceIncreasePercent() {
            return productPriceIncreasePercent;
        }

        public void setProductPriceIncreasePercent(Integer productPriceIncreasePercent) {
            this.productPriceIncreasePercent = productPriceIncreasePercent;
        }

        public String getProductSeriesName() {
            return productSeriesName;
        }

        public void setProductSeriesName(String productSeriesName) {
            this.productSeriesName = productSeriesName;
        }

        public String getProductFactoryName() {
            return productFactoryName;
        }

        public void setProductFactoryName(String productFactoryName) {
            this.productFactoryName = productFactoryName;
        }



        public String getProductRemarks() {
            return productRemarks;
        }

        public void setProductRemarks(String productRemarks) {
            this.productRemarks = productRemarks;
        }


    }

    // Product 转化为标准化对象 ProductStandard
    public ProductStandard ProductStandardization(Product product){
        ProductStandard productStandard = new ProductStandard();
        productStandard.setProductId(product.getProductId());
        productStandard.setProductName(product.getProductName());
        productStandard.setProductIndex(product.getProductIndex());
        productStandard.setProductCode(product.getProductCode());
        productStandard.setProductColor(product.getProductColor());
        // 设置产品单价 当前为假数据 todo：递归计算真实数据
        productStandard.setProductUnitPrice(
                // new BigDecimal(Math.random() * (500.0 - 10.0) + 10.0).setScale(2).doubleValue()
                Math.random() * (500.0 - 10.0) + 10.0
        );
        // 设置产品价格涨幅 当前为假数据 todo：递归计算真实数据
        productStandard.setProductPriceIncreasePercent(
                (int) (Math.random() * 100) - 50
        );
        // 设置系列名称
        productStandard.setProductSeriesName(
                productSeriesService.findProductSeriesByProductSeriesId(product.getProductSeriesId())
                        .getProductSeriesName()
        );
        productStandard.setProductFactoryName(product.getProductFactoryName());
        productStandard.setProductRemarks(product.getProductRemarks());
        return productStandard;
    }


    public boolean isEmptyString(String string) {
        return string == null || string.isEmpty();
    }

    //查
    //-------------------------------------------------------------------------

    public ProductPackage findAll(Integer pageNo,
                                 Integer pageSize
    ) {
        Integer productNum = productRepository.findAll().size();
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Product> page = productRepository.findAll(pageable);
        return packProduct(page.toList(),pageNo,pageSize,productNum);
    }

    public List<RawMaterial> getProductAndRawMaterial(Long productId) {
        Product product = productRepository.findProductByProductId(productId);
        if (product != null) {
            System.out.println("产品名称:" + product.getProductName());
            System.out.println("产品编号:" + product.getProductIndex());

            // 获取原料列表
            List<RawMaterial> rawMaterialList = product.getRawMaterialList();
            if (rawMaterialList != null && rawMaterialList.size() > 0) {
                System.out.println("产品对应的原料:");
                for (RawMaterial rawMaterial : rawMaterialList) {
                    System.out.println(rawMaterial.getRawMaterialName() + ";");
                }
            }
            return rawMaterialList;
        }
        return new ArrayList<>();
    }

    // 求交集
    public Set<Product> mixedSet(Set<Product> A, Set<Product> B) {
        if (A == null || A.size() == 0) {
            if (B != null) return B;
        } else if (B == null || B.size() == 0) {
            return A;
        }
        Set<Product> resultSet = A.stream().filter(B::contains).collect(Collectors.toSet());
        return resultSet;
    }

    // 列表分页 , 页数从0开始记
    public List<Product> pageList(List<Product> listToPage, Integer pageNo, Integer pageSize){
        if(listToPage.size() < pageNo*pageSize){
            return new ArrayList<>();
        }
        List<Product> subList = listToPage.stream().skip((pageNo)*pageSize).limit(pageSize).
                collect(Collectors.toList());
        return subList;
    }


    public ProductPackage findAllByCondition(String rawMaterialName,
                                            String filterCakeName,
                                            String productSeriesName,
                                            Integer pageNo,
                                            Integer pageSize
    ) {
        if (isEmptyString(rawMaterialName) && isEmptyString(filterCakeName) && isEmptyString(productSeriesName)) {
            System.out.println("findAllNND");
            return findAll(pageNo,pageSize);
            // return null;
        }
        Set<Product> rawMaterialProductSet = new HashSet<>();
        Set<Product> filterCakeProductSet = new HashSet<>();
        Set<Product> productSeriesProductSet = new HashSet<>();
        if (!isEmptyString(rawMaterialName)) {
            rawMaterialProductSet = rawMaterialService.findProductsByRawMaterialName(rawMaterialName);
            System.out.println("rawMaterialProductSet 大小" + rawMaterialProductSet.size());
        }
        if (!isEmptyString(filterCakeName)) {
            filterCakeProductSet = filterCakeService.findProductsByFilterCakeName(filterCakeName);
            System.out.println("filterCakeProductSet 大小" + filterCakeProductSet.size());
        }
        if (!isEmptyString(productSeriesName)) {
            productSeriesProductSet = productSeriesService.findProductsByProductSeriesName(productSeriesName);
            System.out.println("productSeriesProductSet 大小" + productSeriesProductSet.size());
        }

        Set<Product> resultSet = mixedSet(rawMaterialProductSet, mixedSet(filterCakeProductSet, productSeriesProductSet));
        System.out.println("resultSet 大小" + resultSet.size());
        List<Product> resultList = new ArrayList<>();
        resultList.addAll(mixedSet(rawMaterialProductSet, mixedSet(filterCakeProductSet, productSeriesProductSet)));
        System.out.println("resultList 大小" + resultList.size());

        List<Product> subList = pageList(resultList,pageNo,pageSize);
        Integer productNum = resultList.size();
        return packProduct(subList,pageNo,pageSize,productNum);
    }



    // public boolean addProduct(){
    //
    //   return productRepository.addProduct();
    // }
    //删
    //-------------------------------------------------------------------------
    //根据productId 删除记录
    @Transactional
    public void deleteByProductId(Long productId) {
        productRepository.deleteByProductId(productId);
    }

    //增
    //-------------------------------------------------------------------------

    //add product data
    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    //update product data
    public Product updateProduct(Long productId, Product updatedProduct) {
        return productRepository.findById(productId)
                .map(product -> {
                    product.setProductName(updatedProduct.getProductName());
                    product.setProductIndex(updatedProduct.getProductIndex());
                    product.setProductCode(updatedProduct.getProductCode());
                    product.setProductColor(updatedProduct.getProductColor());
                    product.setProductProcessingCost(updatedProduct.getProductProcessingCost());
                    product.setProductAccountingQuantity(updatedProduct.getProductAccountingQuantity());
                    product.setProductSeriesId(updatedProduct.getProductSeriesId());
                    product.setProductFactoryName(updatedProduct.getProductFactoryName());
                    product.setProductRemarks(updatedProduct.getProductRemarks());
                    return productRepository.save(product);
                })
                .orElseThrow(() -> new NoSuchElementException("Product not found with id " + productId));
    }


}