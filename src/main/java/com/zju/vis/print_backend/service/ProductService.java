package com.zju.vis.print_backend.service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;


import com.zju.vis.print_backend.compositekey.RelProductFilterCakeKey;
import com.zju.vis.print_backend.compositekey.RelProductRawMaterialKey;
import com.zju.vis.print_backend.dao.RelProductFilterCakeRepository;
import com.zju.vis.print_backend.dao.RelProductRawMaterialRepository;
import com.zju.vis.print_backend.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import com.zju.vis.print_backend.dao.ProductRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {
    //
    @Resource
    private ProductRepository productRepository;

    // 调用一般方法
    Utils utils = new Utils();

    // 调用其他Service的方法
    @Resource
    private RawMaterialService rawMaterialService;

    @Resource
    private FilterCakeService filterCakeService;

    @Resource
    private ProductSeriesService productSeriesService;

    @Resource
    private RelProductRawMaterialService relProductRawMaterialService;

    @Resource
    private RelProductFilterCakeService relProductFilterCakeService;
    // Product 结果封装
    public class ProductPackage {
        // 附加信息
        private Integer pageNo;
        private Integer pageSize;
        private Integer pageNum;
        private Integer total;

        // 返回的标准列表
        private List<ProductStandard> list;

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

        public List<ProductStandard> getList() {
            return list;
        }

        public void setList(List<ProductStandard> list) {
            this.list = list;
        }
    }

    // List<Product> 添加额外信息打包发送
    public ProductPackage packProduct(List<Product> productList,
                                      Integer pageNo, Integer pageSize,
                                      Integer productNum) {
        List<ProductStandard> productStandardList = new ArrayList<>();
        for (Product product : productList) {
            productStandardList.add(ProductStandardization(product));
        }
        ProductPackage productPackage = new ProductPackage();
        productPackage.setPageNo(pageNo + 1);
        productPackage.setPageSize(pageSize);
        productPackage.setPageNum(
                (productNum - 1) / pageSize + 1
        );
        productPackage.setTotal(productNum);
        productPackage.setList(productStandardList);
        return productPackage;
    }

    // Product 标准化形式类 (前端单个节点最终结果
    public static class ProductStandard {
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

        private Float productProcessingCost;
        private Integer productAccountingQuantity;
        private List<RawMaterialService.RawMaterialSimple> rawMaterialSimpleList;
        private List<FilterCakeService.FilterCakeSimple> filterCakeSimpleList;

        public Float getProductProcessingCost() {
            return productProcessingCost;
        }

        public void setProductProcessingCost(Float productProcessingCost) {
            this.productProcessingCost = productProcessingCost;
        }

        public Integer getProductAccountingQuantity() {
            return productAccountingQuantity;
        }

        public void setProductAccountingQuantity(Integer productAccountingQuantity) {
            this.productAccountingQuantity = productAccountingQuantity;
        }

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

        public List<RawMaterialService.RawMaterialSimple> getRawMaterialSimpleList() {
            return rawMaterialSimpleList;
        }

        public void setRawMaterialSimpleList(List<RawMaterialService.RawMaterialSimple> rawMaterialSimpleList) {
            this.rawMaterialSimpleList = rawMaterialSimpleList;
        }

        public List<FilterCakeService.FilterCakeSimple> getFilterCakeSimpleList() {
            return filterCakeSimpleList;
        }

        public void setFilterCakeSimpleList(List<FilterCakeService.FilterCakeSimple> filterCakeSimpleList) {
            this.filterCakeSimpleList = filterCakeSimpleList;
        }
    }

    public ProductRepository getProductRepository() {
        return productRepository;
    }

    public void setProductRepository(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Utils getUtils() {
        return utils;
    }

    public void setUtils(Utils utils) {
        this.utils = utils;
    }

    public RawMaterialService getRawMaterialService() {
        return rawMaterialService;
    }

    public void setRawMaterialService(RawMaterialService rawMaterialService) {
        this.rawMaterialService = rawMaterialService;
    }

    public FilterCakeService getFilterCakeService() {
        return filterCakeService;
    }

    public void setFilterCakeService(FilterCakeService filterCakeService) {
        this.filterCakeService = filterCakeService;
    }

    public ProductSeriesService getProductSeriesService() {
        return productSeriesService;
    }

    public void setProductSeriesService(ProductSeriesService productSeriesService) {
        this.productSeriesService = productSeriesService;
    }

    public RelProductFilterCakeRepository getRelProductFilterCakeRepository() {
        return relProductFilterCakeRepository;
    }

    public void setRelProductFilterCakeRepository(RelProductFilterCakeRepository relProductFilterCakeRepository) {
        this.relProductFilterCakeRepository = relProductFilterCakeRepository;
    }


    // Product 转化为标准对象 ProductStandard
    public ProductStandard ProductStandardization(Product product) {
        ProductStandard productStandard = new ProductStandard();
        productStandard.setProductId(product.getProductId());
        productStandard.setProductName(product.getProductName());
        productStandard.setProductIndex(product.getProductIndex());
        productStandard.setProductCode(product.getProductCode());
        productStandard.setProductColor(product.getProductColor());
        productStandard.setProductAccountingQuantity(product.getProductAccountingQuantity());
        productStandard.setProductProcessingCost(product.getProductProcessingCost());
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

        // 设置返回的简单滤饼表
        List<FilterCakeService.FilterCakeSimple> filterCakeSimpleList = new ArrayList<>();
        for (FilterCake filterCake : product.getFilterCakeList()) {
            filterCakeSimpleList.add(filterCakeService.simplifyFilterCake(filterCake, product.getProductId()));
        }
        productStandard.setFilterCakeSimpleList(filterCakeSimpleList);

        // 设置返回的简单原料表
        List<RawMaterialService.RawMaterialSimple> rawMaterialSimpleList = new ArrayList<>();
        for (RawMaterial rawMaterial : product.getRawMaterialList()) {
            rawMaterialSimpleList.add(rawMaterialService.simplifyRawMaterial(rawMaterial, product.getProductId()));
        }
        productStandard.setRawMaterialSimpleList(rawMaterialSimpleList);
        return productStandard;
    }

    //查
    //-------------------------------------------------------------------------
    public ProductPackage findAll(Integer pageNo,
                                  Integer pageSize
    ) {
        Integer productNum = productRepository.findAll().size();
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Product> page = productRepository.findAll(pageable);
        return packProduct(page.toList(), pageNo, pageSize, productNum);
    }


    public ProductService.ProductStandard findProductByProductId(Long productId) {
        if (productRepository.findProductByProductId(productId) == null) {
            return new ProductStandard();
        }
        return ProductStandardization(productRepository.findProductByProductId(productId));
    }

    // public Product findProductByProductId(Long productId){
    //     return  productRepository.findProductByProductId(productId);
    // }

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

    public class ProductCondition {
        private String rawMaterialName;
        private String filterCakeName;
        private String productSeriesName;
        private Integer pageNo;
        private Integer pageSize;
    }

    public ProductPackage findAllByCondition(String rawMaterialName,
                                             String filterCakeName,
                                             String productSeriesName,
                                             Integer pageNo,
                                             Integer pageSize
    ) {
        if (utils.isEmptyString(rawMaterialName) && utils.isEmptyString(filterCakeName) && utils.isEmptyString(productSeriesName)) {
            System.out.println("findAllNND");
            return findAll(pageNo, pageSize);
            // return null;
        }
        Set<Product> rawMaterialProductSet = new HashSet<>();
        Set<Product> filterCakeProductSet = new HashSet<>();
        Set<Product> productSeriesProductSet = new HashSet<>();
        if (!utils.isEmptyString(rawMaterialName)) {
            rawMaterialProductSet = rawMaterialService.findProductsByRawMaterialName(rawMaterialName);
            System.out.println("rawMaterialProductSet 大小" + rawMaterialProductSet.size());
        }
        if (!utils.isEmptyString(filterCakeName)) {
            filterCakeProductSet = filterCakeService.findProductsByFilterCakeName(filterCakeName);
            System.out.println("filterCakeProductSet 大小" + filterCakeProductSet.size());
        }
        if (!utils.isEmptyString(productSeriesName)) {
            productSeriesProductSet = productSeriesService.findProductsByProductSeriesName(productSeriesName);
            System.out.println("productSeriesProductSet 大小" + productSeriesProductSet.size());
        }

        Set<Product> resultSet = mixedSet(rawMaterialProductSet, mixedSet(filterCakeProductSet, productSeriesProductSet));
        System.out.println("resultSet 大小" + resultSet.size());
        List<Product> resultList = new ArrayList<>();
        resultList.addAll(mixedSet(rawMaterialProductSet, mixedSet(filterCakeProductSet, productSeriesProductSet)));
        System.out.println("resultList 大小" + resultList.size());


        List<Product> subList = utils.pageList(resultList, pageNo, pageSize);
        Integer productNum = resultList.size();
        return packProduct(subList, pageNo, pageSize, productNum);
    }

    //删
    //-------------------------------------------------------------------------
    //根据productId 删除记录
    @Transactional
    public void deleteByProductId(Long productId) {
        productRepository.deleteByProductId(productId);
    }

    //增
    //-------------------------------------------------------------------------
    public static class DeStandardizeResult {
        private Product product;
        private List<RelProductRawMaterial> relProductRawMaterials=new ArrayList<>();
        private List<RelProductFilterCake> relProductFilterCakes=new ArrayList<>();

        // 构造函数
        public DeStandardizeResult(Product product, List<RelProductRawMaterial> relProductRawMaterials, List<RelProductFilterCake> relProductFilterCakes) {
            this.product = product;
            this.relProductRawMaterials = relProductRawMaterials;
            this.relProductFilterCakes = relProductFilterCakes;
        }

        public Product getProduct() {
            return product;
        }

        public void setProduct(Product product) {
            this.product = product;
        }

        public List<RelProductRawMaterial> getRelProductRawMaterials() {
            return relProductRawMaterials;
        }

        public void setRelProductRawMaterials(List<RelProductRawMaterial> relProductRawMaterials) {
            this.relProductRawMaterials = relProductRawMaterials;
        }

        public List<RelProductFilterCake> getRelProductFilterCakes() {
            return relProductFilterCakes;
        }

        public void setRelProductFilterCakes(List<RelProductFilterCake> relProductFilterCakes) {
            this.relProductFilterCakes = relProductFilterCakes;
        }
    }
    public DeStandardizeResult deStandardizeProduct(ProductStandard productStandard) {
        Product product = new Product();
        product.setProductId(productStandard.getProductId());
        product.setProductName(productStandard.getProductName());
        product.setProductIndex(productStandard.getProductIndex());
        product.setProductCode(productStandard.getProductCode());
        product.setProductColor(productStandard.getProductColor());
        product.setProductAccountingQuantity(productStandard.getProductAccountingQuantity());
        product.setProductProcessingCost(productStandard.getProductProcessingCost());
        product.setProductFactoryName(productStandard.getProductFactoryName());
        product.setProductRemarks(productStandard.getProductRemarks());

        // Set the real productSeriesId based on the productSeriesName
        Long productSeriesId = productSeriesService.findProductSeriesIdByProductSeriesName(productStandard.getProductSeriesName());
        if (productSeriesId != null) {
            product.setProductSeriesId(productSeriesId);
        }
        // Convert the simplified raw material list back to its original format
        List<RawMaterial> rawMaterialList = new ArrayList<>();
        for (RawMaterialService.RawMaterialSimple rawMaterialSimple : productStandard.getRawMaterialSimpleList()) {
            RawMaterial rawMaterial = rawMaterialService.deSimplifyRawMaterial(rawMaterialSimple, productStandard.getProductId());
            rawMaterialList.add(rawMaterial);
        }
        product.setRawMaterialList(rawMaterialList);

        // Convert the simplified filter cake list back to its original format
        List<FilterCake> filterCakeList = new ArrayList<>();
        for (FilterCakeService.FilterCakeSimple filterCakeSimple : productStandard.getFilterCakeSimpleList()) {
            FilterCake filterCake = filterCakeService.deSimplifyFilterCake(filterCakeSimple, productStandard.getProductId());
            filterCakeList.add(filterCake);
        }
        product.setFilterCakeList(filterCakeList);

        List<RelProductRawMaterial> relProductRawMaterials = new ArrayList<>();
        for (RawMaterialService.RawMaterialSimple rawMaterialSimple : productStandard.getRawMaterialSimpleList()) {
            Long rawMaterialId = rawMaterialSimple.getRawMaterialId();
            Double inventory = rawMaterialSimple.getInventory();

            RelProductRawMaterial relProductRawMaterial = new RelProductRawMaterial();
            RelProductRawMaterialKey relProductRawMaterialKey = new RelProductRawMaterialKey();
            relProductRawMaterialKey.setProductId(product.getProductId());
            relProductRawMaterialKey.setRawMaterialId(rawMaterialId);
            relProductRawMaterial.setId(relProductRawMaterialKey);
            relProductRawMaterial.setInventory(inventory);

            relProductRawMaterials.add(relProductRawMaterial);
        }
        List<RelProductFilterCake> relProductFilterCakes = new ArrayList<>();
        for (FilterCakeService.FilterCakeSimple filterCakeSimple : productStandard.getFilterCakeSimpleList()) {
            Long filterCakeId = filterCakeSimple.getFilterCakeId();
            Double inventory = filterCakeSimple.getInventory();

            RelProductFilterCake relProductFilterCake = new RelProductFilterCake();
            RelProductFilterCakeKey relProductFilterCakeKey = new RelProductFilterCakeKey();
            relProductFilterCakeKey.setProductId(product.getProductId());
            relProductFilterCakeKey.setFilterCakeId(filterCakeId);
            relProductFilterCake.setId(relProductFilterCakeKey);
            relProductFilterCake.setInventory(inventory);

            relProductFilterCakes.add(relProductFilterCake);
        }

        return new DeStandardizeResult(product, relProductRawMaterials, relProductFilterCakes);
    }
    private void saveRelProductRawMaterials(Product savedProduct, List<RelProductRawMaterial> relProductRawMaterials) {
        for (RelProductRawMaterial relProductRawMaterial : relProductRawMaterials) {
            relProductRawMaterial.getId().setProductId(savedProduct.getProductId());
            relProductRawMaterial.setProduct(savedProduct);
            relProductRawMaterialService.addRelProductRawMaterial(relProductRawMaterial);
        }
    }

    private void saveRelProductFilterCakes(Product savedProduct, List<RelProductFilterCake> relProductFilterCakes) {
        for (RelProductFilterCake relProductFilterCake : relProductFilterCakes) {
            relProductFilterCake.getId().setProductId(savedProduct.getProductId());
            relProductFilterCake.setProduct(savedProduct);
            relProductFilterCakeService.addRelProductFilterCake(relProductFilterCake);
        }
    }
    public Product addProduct(ProductStandard productStandard) {
        DeStandardizeResult result = deStandardizeProduct(productStandard);
        Product product = result.getProduct();
        List<RelProductRawMaterial> relProductRawMaterials = result.getRelProductRawMaterials();
        List<RelProductFilterCake> relProductFilterCakes = result.getRelProductFilterCakes();

        Product savedProduct = productRepository.save(product);
        saveRelProductRawMaterials(savedProduct, relProductRawMaterials);
        saveRelProductFilterCakes(savedProduct, relProductFilterCakes);
        return savedProduct;
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


    // test
    @Resource
    RelProductFilterCakeRepository relProductFilterCakeRepository;

    public List<RelProductFilterCake> findAllRel() {
        return relProductFilterCakeRepository.findAll();
    }

}