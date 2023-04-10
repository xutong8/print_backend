package com.zju.vis.print_backend.controller;

import java.util.List;

import javax.annotation.Resource;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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


  @ApiOperation(value = "根据产品Id返回对应的原料")
  @RequestMapping(value = "/findRawMaterialByProductID", method = RequestMethod.GET)
  @ResponseBody
  public void getProductAndRawMaterial(
          @RequestParam(value = "productId") Long productId
  ){
    productService.getProductAndRawMaterial(productId);
  }



  // @ApiOperation(value = "添加新产品")
  // @RequestMapping(value = "/addProduct", method = RequestMethod.POST)
  // @ResponseBody
  // public boolean addProduct(
  //         @RequestParam(value = "ProductJson") Json ProductJson
  // ) {
  //   return true;
  // }
  //
  // @ApiOperation(value = "删除产品")
  // @RequestMapping(value = "/deleteById", method = RequestMethod.POST)
  // @ResponseBody
  // public boolean deleteById(
  //         @RequestParam(value = "product_id") Integer product_id
  // ){
  //   return true;
  // }
  //
  // @ApiOperation(value = "编辑产品信息")
  // @RequestMapping(value = "/updateProduct", method = RequestMethod.POST)
  // @ResponseBody
  // public boolean updateProduct(
  //         @RequestParam(value = "product_id") Integer product_id,
  //         @RequestParam(value = "ProductJson") Json ProductJson
  // ){
  //   return true;
  // }

}
