package com.zju.vis.print_backend.service;

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

    public boolean isEmptyString(String string) {
        return string == null || string.isEmpty();
    }

    //查
    //-------------------------------------------------------------------------

    public List<Product> findAll(Integer pageNo,
                                 Integer pageSize
    ) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Product> page = productRepository.findAll(pageable);
        return page.toList();
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


    public List<Product> findAllByCondition(String rawMaterialName,
                                            String filterCakeName,
                                            String productSeriesName,
                                            Integer pageNo,
                                            Integer pageSize
    ) {
        if (isEmptyString(rawMaterialName) && isEmptyString(filterCakeName) && isEmptyString(productSeriesName)) {
            System.out.println("findAllNND");
            return findAll(pageNo,pageSize);
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

        List<Product> subList = pageList(resultList,0,10);
        return subList;
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