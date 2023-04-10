package com.zju.vis.print_backend.service;

import com.zju.vis.print_backend.dao.RawMaterialRepository;
import com.zju.vis.print_backend.entity.Product;
import com.zju.vis.print_backend.entity.RawMaterial;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RawMaterialService {
    @Resource
    private RawMaterialRepository rawMaterialRepository;

    public boolean isEmptyString(String string) { return string == null || string.isEmpty();}

    public List<RawMaterial> findAll() {
        return rawMaterialRepository.findAll();
    }

    /**
     * 根据原料名返回对应的原料对象
     * @param MaterialName
     * @return
     */
    public List<RawMaterial> findAllByRawMaterialNameContaining(String MaterialName) {
        // 空字符串返回全部值
        if(isEmptyString(MaterialName)){
            return rawMaterialRepository.findAll();
        }
        return rawMaterialRepository.findAllByRawMaterialNameContaining(MaterialName);
    }

    public List<Product> getProductByRawMaterialId(Long rawMaterialId){
        // Product product = productRepository.findProductByProductId(productId);
        RawMaterial rawMaterial = rawMaterialRepository.findRawMaterialByRawMaterialId(rawMaterialId);

        if(rawMaterial != null){

            System.out.println("原料名称:" + rawMaterial.getRawMaterialName());
            System.out.println("产品编号:" + rawMaterial.getRawMaterialIndex());

            // 获取原料列表
            List<Product> productList = rawMaterial.getProductList();
            if (productList!=null && productList.size()>0){
                System.out.println("原料对应的产品:");
                for (Product product : productList ){
                    System.out.println(product.getProductName()+";");
                }
            }
            return productList;
        }
        return new ArrayList<>();
    }

    /**
     * 根据原料名字查找对应的产品模糊查询
     * 如果有多个对应的原料则返回这一组原料对应的产品集合
     * @param MaterialName
     */
    public Set<Product> findProductsByRawMaterialName(String MaterialName){
        List<RawMaterial> rawMaterialList = findAllByRawMaterialNameContaining(MaterialName);
        System.out.println("原料列表集合");
        System.out.println(rawMaterialList.size());
        Set<Product> productSet = new HashSet<>();
        if(rawMaterialList!=null && rawMaterialList.size()>0 ){
            for(RawMaterial rawMaterial: rawMaterialList){
                List<Product> productList = rawMaterial.getProductList();
                if(productList!=null && productList.size()>0 ){
                    for(Product product: productList){
                        productSet.add(product);
                    }
                }
            }
        }
        System.out.println("对应的产品集合");
        System.out.println(productSet.size());
        return productSet;
    }
}
