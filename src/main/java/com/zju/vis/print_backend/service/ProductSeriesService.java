package com.zju.vis.print_backend.service;

import com.zju.vis.print_backend.dao.ProductSeriesRepository;
import com.zju.vis.print_backend.entity.Product;
import com.zju.vis.print_backend.entity.ProductSeries;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ProductSeriesService {
    @Resource
    ProductSeriesRepository productSeriesRepository;

    public boolean isEmptyString(String string) { return string == null || string.isEmpty();}

    //查
    //-------------------------------------------------------------------------
    public List<ProductSeries> findAll(){
        return productSeriesRepository.findAll();
    }

    public ProductSeries findProductSeriesByProductSeriesId(Long productSeriesId){
        return productSeriesRepository.findProductSeriesByProductSeriesId(productSeriesId);
    }

    public List<ProductSeries> findProductSeriesByProductSeriesNameContaining(String productSeries){
        // 空字符串返回全部值
        if(isEmptyString(productSeries)){
            return productSeriesRepository.findAll();
        }
        return productSeriesRepository.findProductSeriesByProductSeriesNameContaining(productSeries);
    }

    public Set<Product> findProductsByProductSeriesName(String productSeriesName){
        System.out.println("productSeriesName: " + productSeriesName);
        List<ProductSeries> productSeriesList = findProductSeriesByProductSeriesNameContaining(productSeriesName);
        System.out.println("产品系列集合数量");
        System.out.println(productSeriesList.size());
        Set<Product> productSet = new HashSet<>();
        if(productSeriesList!=null && productSeriesList.size()>0){
            for(ProductSeries productSeries: productSeriesList){
                List<Product> productList = productSeries.getProductList();
                if(productList!=null && productList.size()>0){
                    for(Product product: productList){
                        productSet.add(product);
                    }
                }
            }
        }
        System.out.println("产品系列对应的产品数量");
        System.out.println(productSet.size());
        return productSet;
    }

    // 其他函数

}
