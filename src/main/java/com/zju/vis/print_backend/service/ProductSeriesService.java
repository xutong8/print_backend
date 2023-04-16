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
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
public class ProductSeriesService {
    @Resource
    ProductSeriesRepository productSeriesRepository;

    public boolean isEmptyString(String string) {
        return string == null || string.isEmpty();
    }

    //查
    //-------------------------------------------------------------------------
    public List<ProductSeries> findAll(Integer pageNo,
                                       Integer pageSize
    ) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<ProductSeries> page = productSeriesRepository.findAll(pageable);
        return page.toList();
    }

    public ProductSeries findProductSeriesByProductSeriesId(Long productSeriesId) {
        return productSeriesRepository.findProductSeriesByProductSeriesId(productSeriesId);
    }

    public List<ProductSeries> findProductSeriesByProductSeriesNameContaining(String productSeries) {
        // 空字符串返回全部值
        if (isEmptyString(productSeries)) {
            return productSeriesRepository.findAll();
        }
        return productSeriesRepository.findProductSeriesByProductSeriesNameContaining(productSeries);
    }

    public ProductSeries findProductSeriesByProductSeriesName(String productSeriesName){
        return productSeriesRepository.findProductSeriesByProductSeriesName(productSeriesName);
    }

    public Set<Product> findProductsByProductSeriesName(String productSeriesName) {
        System.out.println("productSeriesName: " + productSeriesName);
        ProductSeries productSeries = findProductSeriesByProductSeriesName(productSeriesName);
        // List<ProductSeries> productSeriesList = findProductSeriesByProductSeriesNameContaining(productSeriesName);
        Set<Product> productSet = new HashSet<>();
        if (productSeries != null) {
            productSet.addAll(productSeries.getProductList());
            // for (ProductSeries productSeries : productSeriesList) {
            //     List<Product> productList = productSeries.getProductList();
            //     productSet.addAll(productList);
            //     // if (productList != null && productList.size() > 0) {
            //     //     for (Product product : productList) {
            //     //         productSet.add(product);
            //     //     }
            //     // }
            // }
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

    //add productSeries data
    public ProductSeries addProductSeries(ProductSeries productSeries) {
        return productSeriesRepository.save(productSeries);
    }

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
