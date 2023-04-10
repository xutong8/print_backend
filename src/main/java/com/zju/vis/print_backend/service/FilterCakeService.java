package com.zju.vis.print_backend.service;

import com.zju.vis.print_backend.dao.FilterCakeRepository;
import com.zju.vis.print_backend.entity.FilterCake;
import com.zju.vis.print_backend.entity.Product;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class FilterCakeService {
    @Resource
    FilterCakeRepository filterCakeRepository;

    public boolean isEmptyString(String string) { return string == null || string.isEmpty();}

    public List<FilterCake> findAll() {
        return filterCakeRepository.findAll();
    }


    public List<FilterCake> findAllByFilterCakeNameContaining(String filterCakeName){
            // 空字符串返回全部值
            if(isEmptyString(filterCakeName)){
                return filterCakeRepository.findAll();
            }
            return filterCakeRepository.findAllByFilterCakeNameContaining(filterCakeName);
    }

    public Set<Product> findProductsByFilterCakeName(String filterCakeName){
        List<FilterCake> filterCakeList = findAllByFilterCakeNameContaining(filterCakeName);
        System.out.println("滤饼列表集合");
        System.out.println(filterCakeList.size());
        Set<Product> productSet = new HashSet<>();
        if(filterCakeList!=null && filterCakeList.size()>0){
            for(FilterCake filterCake: filterCakeList){
                List<Product> productList = filterCake.getProductList();
                if(productList!=null && productList.size()>0){
                    for(Product product: productList){
                        productSet.add(product);
                    }
                }
            }
        }
        System.out.println("滤饼对应的产品集合大小");
        System.out.println(productSet.size());
        return productSet;
    }

}
