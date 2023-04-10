package com.zju.vis.print_backend.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zju.vis.print_backend.entity.Product;


public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAll();

    Product findProductByProductId(Long productId);

    //根据productId 删除记录
    void deleteByProductId(Long productId);
}
