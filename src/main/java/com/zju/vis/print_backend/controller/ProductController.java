package com.zju.vis.print_backend.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zju.vis.print_backend.entity.Product;
import com.zju.vis.print_backend.service.ProductService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(description = "产品管理")
@RequestMapping("/product")
@Controller
public class ProductController {
  
  @Resource
  private ProductService productService;

  @ApiOperation(value = "获取所有产品")
  @RequestMapping(value = "/findAll", method = RequestMethod.GET)
  @ResponseBody
  public List<Product> findAll() {
    return productService.findAll();
  }
}
