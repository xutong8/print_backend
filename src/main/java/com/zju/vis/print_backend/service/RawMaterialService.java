package com.zju.vis.print_backend.service;

import com.zju.vis.print_backend.dao.RawMaterialRepository;
import com.zju.vis.print_backend.entity.Product;
import com.zju.vis.print_backend.entity.RawMaterial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class RawMaterialService {
    @Resource
    private RawMaterialRepository rawMaterialRepository;

    // 用于返回原料列表名
    public class RawMaterialName{
        private Long rawMaterialId;
        private String rawMaterialName;

        public Long getRawMaterialId() {
            return rawMaterialId;
        }

        public void setRawMaterialId(Long rawMaterialId) {
            this.rawMaterialId = rawMaterialId;
        }

        public String getRawMaterialName() {
            return rawMaterialName;
        }

        public void setRawMaterialName(String rawMaterialName) {
            this.rawMaterialName = rawMaterialName;
        }
    }

    public boolean isEmptyString(String string) { return string == null || string.isEmpty();}


    public List<RawMaterial> findAll(Integer pageNo,
                                     Integer pageSize
    ) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<RawMaterial> page = rawMaterialRepository.findAll(pageable);
        return page.toList();
    }

    public List<RawMaterialName> findAllRawMaterialName(){
        List<RawMaterialName> rawMaterialNameList = new ArrayList<>();
        for (RawMaterial rawMaterial: rawMaterialRepository.findAll()){
            RawMaterialName rawMaterialName = new RawMaterialName();
            rawMaterialName.setRawMaterialId(rawMaterial.getRawMaterialId());
            rawMaterialName.setRawMaterialName(rawMaterial.getRawMaterialName());
            rawMaterialNameList.add(rawMaterialName);
        }
        return rawMaterialNameList;
    }

    public RawMaterial findRawMaterialByRawMaterialName(String MaterialName){
        return rawMaterialRepository.findRawMaterialByRawMaterialName(MaterialName);
    }


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
        RawMaterial rawMaterial = findRawMaterialByRawMaterialName(MaterialName);
        // List<RawMaterial> rawMaterialList = findAllByRawMaterialNameContaining(MaterialName);
        // System.out.println("原料列表集合");
        // System.out.println(rawMaterialList.size());
        Set<Product> productSet = new HashSet<>();

        if(rawMaterial!=null){
            productSet.addAll(rawMaterial.getProductList());
            // for(RawMaterial rawMaterial: rawMaterialList){
            //     List<Product> productList = rawMaterial.getProductList();
            //     productSet.addAll(productList);
            //     // if(productList!=null && productList.size()>0 ){
            //     //     for(Product product: productList){
            //     //         productSet.add(product);
            //     //     }
            //     // }
            // }
        }
        System.out.println("对应的产品集合");
        System.out.println(productSet.size());
        return productSet;
    }


    public RawMaterial addRawMaterial(RawMaterial rawMaterial) {
        return rawMaterialRepository.save(rawMaterial);
    }
    public RawMaterial updateRawMaterial(Long rawMaterialId, RawMaterial updatedRawMaterial) {
        return rawMaterialRepository.findById(rawMaterialId)
                .map(rawMaterial -> {
                    rawMaterial.setRawMaterialName(updatedRawMaterial.getRawMaterialName());
                    rawMaterial.setRawMaterialIndex(updatedRawMaterial.getRawMaterialIndex());
                    rawMaterial.setRawMaterialPrice(updatedRawMaterial.getRawMaterialPrice());
                    rawMaterial.setRawMaterialConventional(updatedRawMaterial.getRawMaterialConventional());
                    rawMaterial.setRawMaterialSpecification(updatedRawMaterial.getRawMaterialSpecification());
                    return rawMaterialRepository.save(rawMaterial);
                })
                .orElseThrow(() -> new NoSuchElementException("RawMaterial not found with id " + rawMaterialId));
    }

}
