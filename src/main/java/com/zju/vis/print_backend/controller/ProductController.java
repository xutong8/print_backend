package com.zju.vis.print_backend.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;


import com.zju.vis.print_backend.entity.RawMaterial;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.zju.vis.print_backend.entity.Product;
import com.zju.vis.print_backend.service.ProductService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Api(description = "产品管理")
@RequestMapping("/product")
@CrossOrigin
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
    public List<RawMaterial> getProductAndRawMaterial(
            @RequestParam(value = "productId") Long productId
    ) {
        return productService.getProductAndRawMaterial(productId);
    }

  @ApiOperation(value = "根据条件返回对应的产品(滤饼名、原料名、系列名)")
  @RequestMapping(value = "/findAllByCondition", method = RequestMethod.GET)
  @ResponseBody
  public List<Product> findAllByCondition(
          @RequestParam(value = "rawMaterialName", defaultValue = "") String rawMaterialName,
          @RequestParam(value = "filterCakeName", defaultValue = "") String filterCakeName,
          @RequestParam(value = "productSeriesName", defaultValue = "") String productSeriesName
  ){
    // findAllByCondition(String rawMaterialName, String filterCakeName, String productSeriesName)
    System.out.println("List<Product> findAllByCondition   " +
            "rawMaterialName: " + rawMaterialName +
            "  filterCakeName: " + filterCakeName +
            "  productSeriesName: " + productSeriesName);
    return productService.findAllByCondition(rawMaterialName,filterCakeName,productSeriesName);
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

    @ApiOperation(value = "通过 productId 删除记录")
    @RequestMapping(value = "/deleteByProductId", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<String> deleteByProductId(
            @RequestParam(value = "productId") Long productId
    ) {
        productService.deleteByProductId(productId);
        return ResponseEntity.ok("Product with ID: " + productId + " has been deleted.");
    }

    @ApiOperation(value = "添加新产品")
    @RequestMapping(value = "/addProduct", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Product> addProduct(@Valid @RequestBody Product product) {
        Product savedProduct = productService.addProduct(product);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    @ApiOperation(value = "更新产品信息")
    @RequestMapping(value = "/updateProduct", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<Product> updateProduct(
            @RequestParam(value = "productId") Long productId,
            @Valid @RequestBody Product updatedProduct
    ) {
        Product updated = productService.updateProduct(productId, updatedProduct);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

}