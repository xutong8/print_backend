package com.zju.vis.print_backend.dao;

import java.util.List;

import com.zju.vis.print_backend.entity.RawMaterial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.zju.vis.print_backend.entity.Product;


public interface ProductRepository extends JpaRepository<Product, Long> {

    // 查单个
    Product findProductByProductId(Long productId);
    Product findProductByProductName(String productName);
    // Product findProductByProductIndex(String productIndex);
    // Product findProductByProductCode(String productCode);

    // 查多个
    List<Product> findAll();
    List<Product> findAllByProductNameContaining(String productName);
    List<Product> findAllByProductIndexEquals(String productIndex);
    List<Product> findAllByProductIndexContaining(String productIndex);
    List<Product> findAllByProductCodeContaining(String productCode);
    List<Product> findAllByProductColorContaining(String productColor);

    //根据productId 删除记录
    void deleteByProductId(Long productId);
}
