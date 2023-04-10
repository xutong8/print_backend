package com.zju.vis.print_backend.service;

import java.util.List;

import javax.annotation.Resource;

import com.zju.vis.print_backend.entity.RawMaterial;
import org.springframework.stereotype.Service;

import com.zju.vis.print_backend.dao.ProductRepository;
import com.zju.vis.print_backend.entity.Product;

@Service
public class ProductService {
  @Resource
  private ProductRepository productRepository;

  // @Resource
  // private RawMaterialRepository rawMaterialRepository;

  public List<Product> findAll() {
    return productRepository.findAll();
  }

  public void getProductAndRawMaterial(Long productId){
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
    }
  }

  // public List<Product> findAllByCondition(String rawMaterialName){
  //   List<>
  //   return ;
  // }

  // public boolean addProduct(){
  //
  //   return productRepository.addProduct();
  // }

}
