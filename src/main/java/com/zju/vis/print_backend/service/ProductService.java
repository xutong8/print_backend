package com.zju.vis.print_backend.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.zju.vis.print_backend.entity.ProductSeries;
import com.zju.vis.print_backend.entity.RawMaterial;
import org.springframework.stereotype.Service;

import com.zju.vis.print_backend.dao.ProductRepository;
import com.zju.vis.print_backend.entity.Product;

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

  public boolean isEmptyString(String string) { return string == null || string.isEmpty();}

  public List<Product> findAll() {
    return productRepository.findAll();
  }

  public List<RawMaterial> getProductAndRawMaterial(Long productId){
    Product product = productRepository.findProductByProductId(productId);
    if(product != null){
      System.out.println("产品名称:" + product.getProductName());
      System.out.println("产品编号:" + product.getProductIndex());

      // 获取原料列表
      List<RawMaterial> rawMaterialList = product.getRawMaterialList();
      if (rawMaterialList!=null && rawMaterialList.size()>0){
        System.out.println("产品对应的原料:");
        for (RawMaterial rawMaterial : rawMaterialList ){
          System.out.println(rawMaterial.getRawMaterialName()+";");
        }
      }
      return rawMaterialList;
    }
    return new ArrayList<>();
  }

  // 求交集
  public Set<Product> mixedSet(Set<Product> A, Set<Product> B){
    if(A == null || A.size() == 0){
      if(B!=null) return B;
    }else if(B == null || B.size() == 0){
      return A;
    }
    Set<Product> resultSet = A.stream().filter(B::contains).collect(Collectors.toSet());
    return resultSet;
  }

  public  List<Product> findAllByCondition(String rawMaterialName, String filterCakeName, String productSeriesName){
    if(isEmptyString(rawMaterialName) && isEmptyString(filterCakeName) && isEmptyString(productSeriesName)){
      return findAll();
    }
    Set<Product> rawMaterialProductSet = new HashSet<>();
    Set<Product> filterCakeProductSet = new HashSet<>();
    Set<Product> productSeriesProductSet = new HashSet<>();
    if(!isEmptyString(rawMaterialName)){
      rawMaterialProductSet = rawMaterialService.findProductsByRawMaterialName(rawMaterialName);
    }
    if(!isEmptyString(filterCakeName)){
      filterCakeProductSet = filterCakeService.findProductsByFilterCakeName(filterCakeName);
    }
    if(!isEmptyString(productSeriesName)){
      productSeriesProductSet = productSeriesService.findProductsByProductSeriesName(productSeriesName);
    }

    List<Product> resultList = new ArrayList<>();
    resultList.addAll(mixedSet(rawMaterialProductSet,mixedSet(filterCakeProductSet,productSeriesProductSet)));
    return resultList;
  }

  // public boolean addProduct(){
  //
  //   return productRepository.addProduct();
  // }

}
