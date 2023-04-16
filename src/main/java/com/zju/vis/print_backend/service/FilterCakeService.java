package com.zju.vis.print_backend.service;

import com.zju.vis.print_backend.dao.FilterCakeRepository;
import com.zju.vis.print_backend.entity.FilterCake;
import com.zju.vis.print_backend.entity.Product;
import com.zju.vis.print_backend.entity.ProductSeries;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
public class FilterCakeService {
    @Resource
    FilterCakeRepository filterCakeRepository;

    public boolean isEmptyString(String string) {
        return string == null || string.isEmpty();
    }

    public List<FilterCake> findAll(Integer pageNo,
                                    Integer pageSize
    ) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<FilterCake> page = filterCakeRepository.findAll(pageable);
        return page.toList();
    }

    public  FilterCake findFilterCakeByFilterCakeName(String filterCakeName){
        return filterCakeRepository.findFilterCakeByFilterCakeName(filterCakeName);
    }

    public List<FilterCake> findAllByFilterCakeNameContaining(String filterCakeName) {
        // 空字符串返回全部值
        if (isEmptyString(filterCakeName)) {
            return filterCakeRepository.findAll();
        }
        return filterCakeRepository.findAllByFilterCakeNameContaining(filterCakeName);
    }

    public Set<Product> findProductsByFilterCakeName(String filterCakeName) {
        FilterCake filterCake = findFilterCakeByFilterCakeName(filterCakeName);
        // List<FilterCake> filterCakeList = findAllByFilterCakeNameContaining(filterCakeName);
        // System.out.println("滤饼列表集合");
        // System.out.println(filterCakeList.size());
        Set<Product> productSet = new HashSet<>();
        if (filterCake != null) {
            productSet.addAll(filterCake.getProductList());
            // for (FilterCake filterCake : filterCakeList) {
            //     List<Product> productList = filterCake.getProductList();
            //     productSet.addAll(productList);
            //     // if (productList != null && productList.size() > 0) {
            //     //     for (Product product : productList) {
            //     //         productSet.add(product);
            //     //     }
            //     // }
            // }
        }
        System.out.println("滤饼对应的产品集合大小");
        System.out.println(productSet.size());
        return productSet;
    }

    //    @Transactional
//    public void deleteByFilterCakeId(Long filterCakeId) {
//        filterCakeRepository.deleteByFilterCakeId(filterCakeId);
//    }
    public FilterCake addFilterCake(FilterCake filterCake) {
        return filterCakeRepository.save(filterCake);
    }

    // update FilterCake data
    public FilterCake updateFilterCake(Long filterCakeId, FilterCake updatedFilterCake) {
        return filterCakeRepository.findById(filterCakeId)
                .map(filterCake -> {
                    filterCake.setFilterCakeName(updatedFilterCake.getFilterCakeName());
                    filterCake.setFilterCakeIndex(updatedFilterCake.getFilterCakeIndex());
                    filterCake.setFilterCakeColor(updatedFilterCake.getFilterCakeColor());
                    filterCake.setFilterCakeProcessingCost(updatedFilterCake.getFilterCakeProcessingCost());
                    filterCake.setFilterCakeAccountingQuantity(updatedFilterCake.getFilterCakeAccountingQuantity());
                    filterCake.setFilterCakeSpecification(updatedFilterCake.getFilterCakeSpecification());
                    filterCake.setFilterCakeRemarks(updatedFilterCake.getFilterCakeRemarks());
                    return filterCakeRepository.save(filterCake);
                })
                .orElseThrow(() -> new NoSuchElementException("FilterCake not found with id " + filterCakeId));
    }
}
