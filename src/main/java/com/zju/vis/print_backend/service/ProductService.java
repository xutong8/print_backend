package com.zju.vis.print_backend.service;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;


import com.zju.vis.print_backend.Utils.*;
import com.zju.vis.print_backend.compositekey.RelProductFilterCakeKey;
import com.zju.vis.print_backend.compositekey.RelProductProductKey;
import com.zju.vis.print_backend.compositekey.RelProductRawMaterialKey;
import com.zju.vis.print_backend.dao.*;
import com.zju.vis.print_backend.entity.*;
import com.zju.vis.print_backend.vo.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static com.zju.vis.print_backend.Utils.Utils.stepMonth;

@Slf4j
@Service
public class ProductService {
    //
    @Resource
    private ProductRepository productRepository;

    @Resource
    private RawMaterialRepository rawMaterialRepository;

    @Resource
    private FilterCakeRepository filterCakeRepository;

    @Resource
    RelProductFilterCakeRepository relProductFilterCakeRepository;

    @Resource
    RelProductRawMaterialRepository relProductRawMaterialRepository;

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

    @Resource
    private RelProductProductService relProductProductService;

    @Resource
    private FileService fileService;

    // List<Product> 添加额外信息打包发送
    public PackageVo packProduct(List<Product> productList,
                                 Integer pageNo, Integer pageSize,
                                 Integer productNum) {
        List<ProductStandardVo> productStandardList = new ArrayList<>();
        for (Product product : productList) {
            productStandardList.add(ProductStandardization(product));
        }
        PackageVo productPackage = new PackageVo();
        // 前端page从1开始，返回时+1
        productPackage.setPageNo(pageNo + 1);
        productPackage.setPageSize(pageSize);
        productPackage.setPageNum(
                (productNum - 1) / pageSize + 1
        );
        productPackage.setTotal(productNum);
        productPackage.setList(productStandardList);
        return productPackage;
    }

    // Product 转化为标准对象 ProductStandard
    public ProductStandardVo ProductStandardization(Product product) {
        ProductStandardVo productStandard = new ProductStandardVo();
        productStandard.setProductId(product.getProductId());
        productStandard.setProductName(product.getProductName());
        productStandard.setProductIndex(product.getProductIndex());
        productStandard.setProductCode(product.getProductCode());
        productStandard.setProductColor(product.getProductColor());
        productStandard.setProductAccountingQuantity(product.getProductAccountingQuantity());
        productStandard.setProductProcessingCost(product.getProductProcessingCost());
        Double currentPrice = calculateProductPrice(product);
        Date date = stepMonth(new Date(),-3);
        Double historyPrice = calculateProductHistoryPrice(product,date);
        productStandard.setProductUnitPrice(
                currentPrice
                // Math.random() * (500.0 - 10.0) + 10.0
        );
        int increasePercent = 0;
        // System.out.println("currentPrice: " + currentPrice);
        // System.out.println("historyPrice: " + historyPrice);
        if(historyPrice.doubleValue() - 0.0 > 1e-5){
            increasePercent = (int)(100 * (currentPrice - historyPrice) / historyPrice);
        }
        productStandard.setProductPriceIncreasePercent(
                increasePercent
                // (int) (Math.random() * 100) - 50
        );
        // 设置系列名称
        productStandard.setProductSeriesName(
                productSeriesService.findProductSeriesByProductSeriesId(product.getProductSeriesId())
                        .getProductSeriesName()
        );
        productStandard.setProductFactoryName(product.getProductFactoryName());
        productStandard.setProductRemarks(product.getProductRemarks());

        // 设置返回的简单原料表
        List<ProductSimpleVo> productSimpleVoList = getSimplyProductList(product);
        productStandard.setProductSimpleList(productSimpleVoList);

        // 设置返回的简单滤饼表
        List<FilterCakeSimpleVo> filterCakeSimpleList = new ArrayList<>();
        for (FilterCake filterCake : product.getFilterCakeList()) {
            filterCakeSimpleList.add(filterCakeService.simplifyFilterCake(filterCake, product.getProductId()));
        }
        productStandard.setFilterCakeSimpleList(filterCakeSimpleList);

        // 设置返回的简单原料表
        List<RawMaterialSimpleVo> rawMaterialSimpleList = new ArrayList<>();
        for (RawMaterial rawMaterial : product.getRawMaterialList()) {
            rawMaterialSimpleList.add(rawMaterialService.simplifyRawMaterial(rawMaterial, product.getProductId()));
        }
        productStandard.setRawMaterialSimpleList(rawMaterialSimpleList);
        return productStandard;
    }

    //查
    //-------------------------------------------------------------------------
    public PackageVo findAll(Integer pageNo,
                             Integer pageSize
    ) {
        Integer productNum = productRepository.findAll().size();
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Product> page = productRepository.findAll(pageable);
        return packProduct(page.toList(), pageNo, pageSize, productNum);
    }

    public List<ProductSimpleVo> getSimplyProductList(Product product){
        List<RelProductProduct> relProductProductList = product.getRelProductProductListUser();
        List<ProductSimpleVo> resultList = new ArrayList<>();
        for(RelProductProduct relProductProduct: relProductProductList){
            ProductSimpleVo productSimple = new ProductSimpleVo(
                    relProductProduct.getProductUsed().getProductId(),
                    relProductProduct.getProductUsed().getProductName(),
                    relProductProduct.getInventory()
            );
            resultList.add(productSimple);
        }
        return resultList;
    }

    public ProductSimpleVo simplifyProductName(Product product){
        ProductSimpleVo productSimple = new ProductSimpleVo(product.getProductId(),product.getProductName(),-1.0);
        return productSimple;
    }

    public List<EntityNameVo> findAllProductName(){
        List<EntityNameVo> productNameList = new ArrayList<>();
        for(Product product: productRepository.findAll()){
            EntityNameVo entityNameVo = new EntityNameVo();
            entityNameVo.setId(product.getProductId());
            entityNameVo.setName(product.getProductName());
            productNameList.add(entityNameVo);
        }
        return productNameList;
    }

    public ProductStandardVo findProductByProductId(Long productId) {
        if (productRepository.findProductByProductId(productId) == null) {
            return new ProductStandardVo();
        }
        return ProductStandardization(productRepository.findProductByProductId(productId));
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
            // if (B != null) return B;
            return new HashSet<>();
        } else if (B == null || B.size() == 0) {
            // return A;
            return new HashSet<>();
        }
        Set<Product> resultSet = A.stream().filter(B::contains).collect(Collectors.toSet());
        return resultSet;
    }

    public PackageVo findAllByDirectCondition(
            String typeOfQuery,
            String conditionOfQuery,
            Integer pageNo,
            Integer pageSize
    ){
        List<Product> resultList = new ArrayList<>();
        if(utils.isEmptyString(conditionOfQuery)){
            // System.out.println("M1 findAll");
            return findAll(pageNo, pageSize);
        }

        switch (typeOfQuery){
            case "产品名称":
                resultList = productRepository.findAllByProductNameContaining(conditionOfQuery);
                break;
            case "产品编号":
                resultList = productRepository.findAllByProductIndexContaining(conditionOfQuery);
                break;
            case "产品代码":
                resultList = productRepository.findAllByProductCodeContaining(conditionOfQuery);
                break;
            case "产品颜色":
                resultList = productRepository.findAllByProductColorContaining(conditionOfQuery);
                break;
        }
        System.out.println("resultList 大小" + resultList.size());
        List<Product> subList = utils.pageList(resultList, pageNo, pageSize);
        Integer productNum = resultList.size();
        return packProduct(subList, pageNo, pageSize, productNum);
    }

    public PackageVo findAllByRelCondition(
            String rawMaterialName,
            String filterCakeName,
            String productSeriesName,
            Integer pageNo,
            Integer pageSize
    ) {
        List<Product> resultList = new ArrayList<>();

        if (utils.isEmptyString(rawMaterialName) && utils.isEmptyString(filterCakeName) && utils.isEmptyString(productSeriesName)) {
            log.info("FindByCondition return All");
            return findAll(pageNo, pageSize);
        }
        Set<Product> rawMaterialProductSet = new HashSet<>();
        Set<Product> filterCakeProductSet = new HashSet<>();
        Set<Product> productSeriesProductSet = new HashSet<>();
        // 原料关联
        if (!utils.isEmptyString(rawMaterialName)) {
            rawMaterialProductSet = rawMaterialService.findProductsByRawMaterialName(rawMaterialName);
        }else{
            rawMaterialProductSet.addAll(productRepository.findAll());
        }
        log.info("RawMaterialProductSet 大小 --> " + rawMaterialProductSet.size());
        // 滤饼关联
        if (!utils.isEmptyString(filterCakeName)) {
            filterCakeProductSet = filterCakeService.findProductsByFilterCakeName(filterCakeName);
        }else{
            filterCakeProductSet.addAll(productRepository.findAll());
        }
        log.info("FilterCakeProductSet 大小 --> " + filterCakeProductSet.size());
        // 产品系列关联
        if (!utils.isEmptyString(productSeriesName)) {
            productSeriesProductSet = productSeriesService.findProductsByProductSeriesName(productSeriesName);
        }else{
            productSeriesProductSet.addAll(productRepository.findAll());
        }
        log.info("productSeriesProductSet 大小 --> " + productSeriesProductSet.size());
        // Set<Product> resultSet = mixedSet(rawMaterialProductSet, mixedSet(filterCakeProductSet, productSeriesProductSet));
        // System.out.println("resultSet 大小" + resultSet.size());
        resultList.addAll(mixedSet(rawMaterialProductSet, mixedSet(filterCakeProductSet, productSeriesProductSet)));
        log.info("resultList 大小 --> " + resultList.size());
        List<Product> subList = utils.pageList(resultList, pageNo, pageSize);
        Integer productNum = resultList.size();
        return packProduct(subList, pageNo, pageSize, productNum);
    }

    // 计算当期价格
    public Double calculateProductPrice(Product product){

        // 简单被使用商品表 todo
        List<ProductSimpleVo> productSimpleList = getSimplyProductList(product);

        // 简单滤饼表
        List<FilterCakeSimpleVo> filterCakeSimpleList = new ArrayList<>();
        if(product.getFilterCakeList()!=null){
            for (FilterCake filterCake : product.getFilterCakeList()) {
                filterCakeSimpleList.add(filterCakeService.simplifyFilterCake(filterCake, product.getProductId()));
            }
        }

        // 简单原料表
        List<RawMaterialSimpleVo> rawMaterialSimpleList = new ArrayList<>();
        if(product.getRawMaterialList()!=null){
            for (RawMaterial rawMaterial : product.getRawMaterialList()) {
                rawMaterialSimpleList.add(rawMaterialService.simplifyRawMaterial(rawMaterial, product.getProductId()));
            }
        }

        Double sum = 0.0;
        // 加上批处理价格
        sum += product.getProductProcessingCost();
        // 计算所用商品的价格
        if(productSimpleList.size()!=0){
            for(ProductSimpleVo productSimple: productSimpleList){
                sum += productSimple.getInventory() * calculateProductPrice(productRepository.findProductByProductId(productSimple.getProductId()));
            }
        }
        // 计算所用滤饼的价格
        if(filterCakeSimpleList.size() != 0){
            for(FilterCakeSimpleVo filterCakeSimple: filterCakeSimpleList){
                sum += filterCakeSimple.getInventory() * filterCakeService.calculateFilterCakePrice(filterCakeRepository.findFilterCakeByFilterCakeId(filterCakeSimple.getFilterCakeId()));
            }
        }
        // 计算所用原料的价格
        for(RawMaterialSimpleVo rawMaterialSimple:rawMaterialSimpleList){
            sum += rawMaterialSimple.getInventory() * rawMaterialService.findRawMaterialByRawMaterialId(rawMaterialSimple.getRawMaterialId()).getRawMaterialUnitPrice();
        }
        return sum / product.getProductAccountingQuantity();
    }

    // 计算历史价格
    public Double calculateProductHistoryPrice(Product product, Date historyDate){
        if(product == null){
            return -1.0;
        }
        List<ProductSimpleVo> productSimpleList = getSimplyProductList(product);

        // 简单滤饼表
        List<FilterCakeSimpleVo> filterCakeSimpleList = new ArrayList<>();
        if(product.getFilterCakeList() != null){
            for (FilterCake filterCake : product.getFilterCakeList()) {
                filterCakeSimpleList.add(filterCakeService.simplifyFilterCake(filterCake, product.getProductId()));
            }
        }

        // 简单原料表
        List<RawMaterialSimpleVo> rawMaterialSimpleList = new ArrayList<>();
        if(product.getRawMaterialList() != null){
            for (RawMaterial rawMaterial : product.getRawMaterialList()) {
                rawMaterialSimpleList.add(rawMaterialService.simplifyRawMaterial(rawMaterial, product.getProductId()));
            }
        }
        Double sum = 0.0;
        // 加上批处理价格
        sum += product.getProductProcessingCost();
        // 获取产品历史价格
        if(productSimpleList.size()!=0){
            for(ProductSimpleVo productSimple: productSimpleList){
                sum += productSimple.getInventory() *
                        calculateProductHistoryPrice(productRepository.findProductByProductId(productSimple.getProductId()),historyDate);
            }
        }

        // 获取滤饼历史价格
        if(filterCakeSimpleList.size()!=0){
            for(FilterCakeSimpleVo filterCakeSimple:filterCakeSimpleList){
                sum += filterCakeSimple.getInventory() *
                        filterCakeService.calculateFilterCakeHistoryPrice(filterCakeRepository.findFilterCakeByFilterCakeId(filterCakeSimple.getFilterCakeId()),historyDate);
            }
        }
        // 获取原料历史价格
        for(RawMaterialSimpleVo rawMaterialSimple: rawMaterialSimpleList){
            List<HistoryPriceVo> historyPriceList = rawMaterialService.findRawMaterialByRawMaterialId(rawMaterialSimple.getRawMaterialId()).getRawMaterialHistoryPrice();
            if(historyPriceList.size()!=0){
                boolean flag = false;
                Collections.reverse(historyPriceList);
                for(HistoryPriceVo historyPrice: historyPriceList){
                    if(historyPrice.getDate().getTime() - (86400*1000) <= historyDate.getTime()){
                        // System.out.println("选取的时间:" + historyPrice.getDate());
                        sum += rawMaterialSimple.getInventory() * historyPrice.getPrice();
                        flag = true;
                        break;
                    }
                }
                if(!flag){
                    sum += rawMaterialSimple.getInventory() * historyPriceList.get(historyPriceList.size() - 1).getPrice();
                }
            }
        }
        // System.out.println("sum: " + sum + "\nfilterCake.getFilterCakeAccountingQuantity(): " + product.getProductAccountingQuantity());
        return sum / product.getProductAccountingQuantity();
    }

    // 列表形式返回历史价格
    public List<HistoryPriceVo> getProductHistoryPriceList(Long productId, Long months){
        System.out.println("历史价格productId:" + productId);
        Product product = productRepository.findProductByProductId(productId);
        List<HistoryPriceVo> historyPriceList = new ArrayList<>();
        if(product != null){
            for(int i = 0;i < months ; i++){
                Date date = stepMonth(new Date(),-i);
                // System.out.println(dateFormat.format(date));
                HistoryPriceVo historyPrice = new HistoryPriceVo();
                historyPrice.setDate(new java.sql.Date(date.getTime()));
                historyPrice.setPrice(calculateProductHistoryPrice(product,date));
                historyPriceList.add(historyPrice);
            }
        }
        return historyPriceList;
    }

    //增
    //-------------------------------------------------------------------------

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeStandardizeResult {
        private Product product;
        private List<RelProductRawMaterial> relProductRawMaterials;
        private List<RelProductFilterCake> relProductFilterCakes;
        private List<RelProductProduct> relProductProducts;
    }

    public DeStandardizeResult deStandardizeProduct(ProductStandardVo productStandard) {
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
        if(productStandard.getRawMaterialSimpleList() != null){
            for (RawMaterialSimpleVo rawMaterialSimple : productStandard.getRawMaterialSimpleList()) {
                RawMaterial rawMaterial = rawMaterialService.deSimplifyRawMaterial(rawMaterialSimple, productStandard.getProductId());
                rawMaterialList.add(rawMaterial);
            }
        }
        product.setRawMaterialList(rawMaterialList);

        // Convert the simplified filter cake list back to its original format
        List<FilterCake> filterCakeList = new ArrayList<>();
        if(productStandard.getFilterCakeSimpleList()!=null){
            for (FilterCakeSimpleVo filterCakeSimple : productStandard.getFilterCakeSimpleList()) {
                FilterCake filterCake = filterCakeService.deSimplifyFilterCake(filterCakeSimple, productStandard.getProductId());
                filterCakeList.add(filterCake);
            }
        }
        product.setFilterCakeList(filterCakeList);


        List<RelProductRawMaterial> relProductRawMaterials = new ArrayList<>();
        if(productStandard.getRawMaterialSimpleList()!=null){
            for (RawMaterialSimpleVo rawMaterialSimple : productStandard.getRawMaterialSimpleList()) {
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
        }
        List<RelProductFilterCake> relProductFilterCakes = new ArrayList<>();
        if(productStandard.getFilterCakeSimpleList()!=null){
            for (FilterCakeSimpleVo filterCakeSimple : productStandard.getFilterCakeSimpleList()) {
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
        }

        List<RelProductProduct> relProductProducts = new ArrayList<>();
        if(productStandard.getProductSimpleList()!=null){
            for(ProductSimpleVo productSimple: productStandard.getProductSimpleList()){
                Long productUsedId = productSimple.getProductId();
                Double inventory = productSimple.getInventory();

                RelProductProduct relProductProduct = new RelProductProduct();
                RelProductProductKey id = new RelProductProductKey();
                id.setProductId(product.getProductId());
                id.setProductIdUsed(productUsedId);
                relProductProduct.setId(id);
                relProductProduct.setInventory(inventory);

                relProductProducts.add(relProductProduct);
            }
        }

        return new DeStandardizeResult(product, relProductRawMaterials, relProductFilterCakes,relProductProducts);
    }

    private void saveRelProductRawMaterials(Product savedProduct, List<RelProductRawMaterial> relProductRawMaterials) {
        for (RelProductRawMaterial relProductRawMaterial : relProductRawMaterials) {
            // 使用自增id而非原id
            relProductRawMaterial.getId().setProductId(savedProduct.getProductId());
            // System.out.println("pid: " + savedProduct.getProductId() + "   rid: " + relProductRawMaterial.getId().getRawMaterialId());

            relProductRawMaterial.setProduct(savedProduct);
            relProductRawMaterial.setRawMaterial(rawMaterialRepository.findRawMaterialByRawMaterialId(relProductRawMaterial.getId().getRawMaterialId()));
            relProductRawMaterialService.addRelProductRawMaterial(relProductRawMaterial);
        }
    }

    private void saveRelProductFilterCakes(Product savedProduct, List<RelProductFilterCake> relProductFilterCakes) {
        for (RelProductFilterCake relProductFilterCake : relProductFilterCakes) {
            // 使用自增id而非原id
            relProductFilterCake.getId().setProductId(savedProduct.getProductId());
            // System.out.println("pid: " + savedProduct.getProductId() + "  fid: " + relProductFilterCake.getId().getFilterCakeId());

            relProductFilterCake.setProduct(savedProduct);
            relProductFilterCake.setFilterCake(filterCakeRepository.findFilterCakeByFilterCakeId(relProductFilterCake.getId().getFilterCakeId()));
            relProductFilterCakeService.addRelProductFilterCake(relProductFilterCake);
        }
    }

    private void saveRelProductProducts(Product savedProduct, List<RelProductProduct> relProductProducts){
        for(RelProductProduct relProductProduct : relProductProducts){
            // 使用自增id而非原id
            relProductProduct.getId().setProductId(savedProduct.getProductId());

            relProductProduct.setProduct(savedProduct);
            relProductProduct.setProductUsed(productRepository.findProductByProductId(relProductProduct.getId().getProductIdUsed()));
            relProductProductService.addRelProductProduct(relProductProduct);
        }
    }

    // 根据Product 删除所有原料关联
    private void deleteRelProductRawMaterials(Product product){
        if(product == null) return;
        if(product.getRawMaterialList()!=null){
            for(RawMaterial rawMaterial: product.getRawMaterialList()){
                RelProductRawMaterialKey id = new RelProductRawMaterialKey();
                id.setProductId(product.getProductId());
                id.setRawMaterialId(rawMaterial.getRawMaterialId());
                RelProductRawMaterial relProductRawMaterial = relProductRawMaterialService.findRelProductRawMaterialById(id);
                relProductRawMaterialService.deleteRelProductRawMaterial(relProductRawMaterial);
            }
        }
    }

    // 根据Product 删除所有滤饼关联
    private void deleteRelProductFilterCakes(Product product){
        if(product == null) return;
        if(product.getFilterCakeList()!=null){
            for(FilterCake filterCake: product.getFilterCakeList()){
                RelProductFilterCakeKey id = new RelProductFilterCakeKey();
                id.setProductId(product.getProductId());
                id.setFilterCakeId(filterCake.getFilterCakeId());
                RelProductFilterCake relProductFilterCake = relProductFilterCakeService.findRelProductFilterCakeById(id);
                relProductFilterCakeService.deleteRelProductFilterCake(relProductFilterCake);
            }
        }
    }

    // 根据Product 删除所有被使用原料关联
    private void deleteRelProductProducts(Product product){
        if(product == null) return;
        if(product.getRelProductProductListUser() != null){
            for(RelProductProduct relProductProduct: product.getRelProductProductListUser()){
                relProductProductService.delete(relProductProduct);
            }
        }
    }

    // isImport 如果是导入文件则一定不会带关联信息因此不更新关联表
    public ResultVo saveProduct(ProductStandardVo productStandard, Boolean isImport){
        DeStandardizeResult result = deStandardizeProduct(productStandard);
        Product product = result.getProduct();
        // 1.表示添加
        if(productStandard.getProductId() == 0){
            // 2.表示已有同名表项
            if(productRepository.findProductByProductName(productStandard.getProductName()) != null){
                log.info("{}商品名字重复",productStandard.getProductName());
                return ResultVoUtil.error("商品名字重复");
            }
        }else{
            Product originProduct = productRepository.findProductByProductId(productStandard.getProductId());
            // 1.新商品名称与原商品名称不一致 2.新商品名字与数据库中已有的名字重复
            if(!product.getProductName().equals(originProduct.getProductName()) &&
                    productRepository.findProductByProductName(product.getProductName())!=null){
                log.info("{}商品名字重复",product.getProductName());
                return ResultVoUtil.error("商品名字重复");
            }

            // 处理级联保存关系表消失的问题
            if(isImport){
                product.setRawMaterialList(originProduct.getRawMaterialList());
                product.setFilterCakeList(originProduct.getFilterCakeList());
            }
        }

        List<RelProductRawMaterial> relProductRawMaterials = result.getRelProductRawMaterials();
        List<RelProductFilterCake> relProductFilterCakes = result.getRelProductFilterCakes();
        List<RelProductProduct> relProductProducts = result.getRelProductProducts();
        // 这个save会删除关系表

        Product savedProduct = productRepository.save(product);
        if(!isImport){
            saveRelProductRawMaterials(savedProduct, relProductRawMaterials);
            saveRelProductFilterCakes(savedProduct, relProductFilterCakes);
            saveRelProductProducts(savedProduct, relProductProducts);
        }
        // 设置变换后的id
        productStandard.setProductId(savedProduct.getProductId());
        return ResultVoUtil.success(productStandard);
    }

    public ResultVo addProduct(ProductStandardVo productStandard) {
        productStandard.setProductId(new Long(0));
        // if(productRepository.findProductByProductName(productStandard.getProductName()) != null){
        //     log.info("{}商品名字重复",productStandard.getProductName());
        //     return ResultVoUtil.error("商品名字重复");
        // }
        return saveProduct(productStandard,false);
    }

    // @Transactional
    // public ResultVo addProduct(ProductStandardVo productStandard) {
    //     DeStandardizeResult result = deStandardizeProduct(productStandard);
    //     Product product = result.getProduct();
    //
    //     if(productRepository.findProductByProductName(product.getProductName()) != null){
    //         log.info("{}商品名字重复",product.getProductName());
    //         return ResultVoUtil.error("商品名字重复");
    //     }
    //
    //     List<RelProductRawMaterial> relProductRawMaterials = result.getRelProductRawMaterials();
    //     List<RelProductFilterCake> relProductFilterCakes = result.getRelProductFilterCakes();
    //     // 添加时指定一个不存在的id进而使用自增id
    //     product.setProductId(new Long(0));
    //     Product savedProduct = productRepository.save(product);
    //     saveRelProductRawMaterials(savedProduct, relProductRawMaterials);
    //     saveRelProductFilterCakes(savedProduct, relProductFilterCakes);
    //     // 设置变换后的id
    //     productStandard.setProductId(savedProduct.getProductId());
    //     return ResultVoUtil.success(productStandard);
    // }

    //改
    //-------------------------------------------------------------------------
    public ResultVo updateProduct(ProductStandardVo updatedProduct) {
        Product originProduct = productRepository.findProductByProductId(updatedProduct.getProductId());
        if(originProduct == null){
            log.info("Product Add ---> {}",updatedProduct.getProductName());
            ResultVo<ProductStandardVo> result = addProduct(updatedProduct);
            return result;
        }
        DeStandardizeResult result = deStandardizeProduct(updatedProduct);
        Product product = result.getProduct();

        // 1.新商品名称与原商品名称不一致 2.新商品名字与数据库中已有的名字重复
        if(!product.getProductName().equals(originProduct.getProductName()) &&
                productRepository.findProductByProductName(product.getProductName())!=null){
            log.info("{}商品名字重复",product.getProductName());
            return ResultVoUtil.error("商品名字重复");
        }

        log.info("Product Update ---> {}",updatedProduct.getProductName());

        // 先删掉原先的关系
        deleteRelProductRawMaterials(originProduct);
        deleteRelProductFilterCakes(originProduct);
        deleteRelProductProducts(originProduct);

        // 重新添加关系以及修改内容
        List<RelProductRawMaterial> relProductRawMaterials = result.getRelProductRawMaterials();
        List<RelProductFilterCake> relProductFilterCakes = result.getRelProductFilterCakes();
        List<RelProductProduct> relProductProducts = result.getRelProductProducts();

        Product savedProduct = productRepository.save(product);
        saveRelProductRawMaterials(savedProduct, relProductRawMaterials);
        saveRelProductFilterCakes(savedProduct, relProductFilterCakes);
        saveRelProductProducts(savedProduct, relProductProducts);
        return ResultVoUtil.success(updatedProduct);
    }

    //删
    //-------------------------------------------------------------------------
    //根据productId 删除记录
    public void deleteByProductId(Long productId) {
        deleteRelByProductId(productId);
        deleteProductByProductId(productId);
    }

    @Transactional
    public void deleteProductByProductId(Long productId) {
        Product product = productRepository.findProductByProductId(productId);
        productRepository.delete(product);
    }

    @Transactional
    public void deleteRelByProductId(Long productId) {
        Product product = productRepository.findProductByProductId(productId);
        // 删除产品关联表
        for(RelProductProduct relProductProduct: product.getRelProductProductListUser()){
            relProductProductService.delete(relProductProduct);
        }
        // 删除产品被关联表
        for(RelProductProduct relProductProduct: product.getRelProductProductListUsed()){
            relProductProductService.delete(relProductProduct);
        }
    }


    // 导入文件
    //-------------------------------------------------------------------------
    public ResultVo importProductExcelAndPersistence(MultipartFile file){
        ResultVo<List<ExcelProductVo>> importResult = fileService.importEntityExcel(file,ExcelProductVo.class);
        if(!importResult.checkSuccess()){
            log.error(importResult.getMsg());
            return importResult;
        }
        List<ExcelProductVo> excelProductVos = importResult.getData();
        List<String> warnStringList = new ArrayList<>();
        System.out.println("save 前个数" + relProductFilterCakeRepository.findAll().size() + "/" + relProductRawMaterialRepository.findAll().size());
        for(ExcelProductVo excelProductVo: excelProductVos){
            // excel信息转化为标准类
            ProductStandardVo productStandard = transExcelToStandard(excelProductVo);
            if(productSeriesService.findProductSeriesIdByProductSeriesName(productStandard.getProductSeriesName()) == null){
                String warnString = "[Warning] " + "产品系列{ " + excelProductVo.getProductSeriesName() +" }"  + "未找到对应表项设置为空";
                warnStringList.add(warnString);
            }
            // 更新数据库，已经存在的则会直接修改，为更新关联数据已存在的关联数据会被删除
            saveProduct(productStandard,true);
        }

        System.out.println("save 后个数" + relProductFilterCakeRepository.findAll().size() + "/" + relProductRawMaterialRepository.findAll().size());
        if(warnStringList.size()>0){
            return ResultVoUtil.success(201,"产品有未导入选项,请仔细检查数据表以及数据库内容并重新导入",warnStringList);
        }
        return ResultVoUtil.success(excelProductVos);
    }

    public ProductStandardVo transExcelToStandard(ExcelProductVo excelProductVo){
        ProductStandardVo productStandard = new ProductStandardVo();
        // 如果已经存在了则修改，否则则添加
        if(productRepository.findProductByProductName(excelProductVo.getProductName()) == null){
            // 表示添加
            productStandard.setProductId(new Long(0));
        }else{
            productStandard.setProductId(productRepository.findProductByProductName(excelProductVo.getProductName()).getProductId());
        }
        productStandard.setProductName(excelProductVo.getProductName());
        productStandard.setProductIndex(excelProductVo.getProductIndex());
        productStandard.setProductCode(excelProductVo.getProductCode());
        productStandard.setProductColor(excelProductVo.getProductColor());
        productStandard.setProductSeriesName(excelProductVo.getProductSeriesName());
        productStandard.setProductFactoryName(excelProductVo.getProductFactoryName());
        productStandard.setProductRemarks("");
        productStandard.setProductProcessingCost(excelProductVo.getProductProcessingCost());
        productStandard.setProductAccountingQuantity(excelProductVo.getProductAccountingQuantity());
        return productStandard;
    }

    // 导出文件
    //-------------------------------------------------------------------------
    public ResultVo<String> exportProductExcel(HttpServletResponse response){
        // 1.根据查询条件获取结果集
        List<ExcelProductWriteVo> excelProductWriteVos = getExcelProductWriteVoListByCondition();
        if (CollectionUtil.isEmpty(excelProductWriteVos)) {
            log.info("【导出Excel文件】要导出的数据为空，无法导出！");
            return ResultVoUtil.success("数据为空");
        }
        // 2.获取要下载Excel文件的路径
        ResultVo<String> resultVo = fileService.getDownLoadPath(ExcelProductWriteVo.class, excelProductWriteVos);
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

    public List<ExcelProductWriteVo> getExcelProductWriteVoListByCondition(){
        List<ExcelProductWriteVo> excelProductWriteVos = new ArrayList<>();
        for(Product product: productRepository.findAll()){
            excelProductWriteVos.add(transEntityToExcel(product));
        }
        return excelProductWriteVos;
    }

    public ExcelProductWriteVo transEntityToExcel(Product product){
        ExcelProductWriteVo excelProductWriteVo = new ExcelProductWriteVo();
        excelProductWriteVo.setProductName(product.getProductName());
        excelProductWriteVo.setProductIndex(product.getProductIndex());
        excelProductWriteVo.setProductCode(product.getProductCode());
        excelProductWriteVo.setProductColor(product.getProductColor());
        // 数字转化为名字
        excelProductWriteVo.setProductSeriesName(
                productSeriesService.findProductSeriesByProductSeriesId(
                        product.getProductSeriesId()
                ).getProductSeriesName()
        );
        excelProductWriteVo.setProductFactoryName(product.getProductFactoryName());
        excelProductWriteVo.setProductAccountingQuantity(product.getProductAccountingQuantity());
        excelProductWriteVo.setProductProcessingCost(product.getProductProcessingCost());
        return excelProductWriteVo;
    }
}