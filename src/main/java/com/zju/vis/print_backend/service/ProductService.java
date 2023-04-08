package com.zju.vis.print_backend.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.zju.vis.print_backend.dao.ProductRepository;
import com.zju.vis.print_backend.entity.Product;

@Service
public class ProductService {
  @Resource
  private ProductRepository productRepository;

  public List<Product> findAll() {
    return productRepository.findAll();
  }
}
