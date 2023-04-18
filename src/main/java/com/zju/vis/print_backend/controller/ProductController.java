package com.zju.vis.print_backend.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;


import com.zju.vis.print_backend.entity.RawMaterial;
import com.zju.vis.print_backend.entity.RelProductFilterCake;
import com.zju.vis.print_backend.service.RawMaterialService;
import io.swagger.models.auth.In;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.zju.vis.print_backend.entity.Product;
import com.zju.vis.print_backend.service.ProductService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import springfox.documentation.spring.web.json.Json;

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
    public ProductService.ProductPackage findAll(
            @RequestParam(value = "pageNo" ,defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize" ,defaultValue = "10") Integer pageSize
    ) {
        return productService.findAll(pageNo-1,pageSize);
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
  public ProductService.ProductPackage findAllByCondition(
          @RequestParam(value = "rawMaterialName", defaultValue = "") String rawMaterialName,
          @RequestParam(value = "filterCakeName", defaultValue = "") String filterCakeName,
          @RequestParam(value = "productSeriesName", defaultValue = "") String productSeriesName,
          // @Valid @RequestBody ,
          @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
          @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize
          ){

    // findAllByCondition(String rawMaterialName, String filterCakeName, String productSeriesName)
        System.out.println("List<Product> findAllByCondition   " +
            "rawMaterialName: " + rawMaterialName +
            "  filterCakeName: " + filterCakeName +
            "  productSeriesName: " + productSeriesName);

      long s = System.currentTimeMillis();
      ProductService.ProductPackage list = productService.findAllByCondition(rawMaterialName,filterCakeName,productSeriesName,pageNo-1,pageSize);
      long e = System.currentTimeMillis();
      System.out.println("findbycondition开始的时间：" + s);
      System.out.println("findbycondition结束的时间：" + e);
      System.out.println("findbycondition查询的时间差为：" + (e - s));

    return list;
  }

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
    public ResponseEntity<Product> addProduct(@Valid @RequestBody ProductService.ProductStandard productStandard) {
        Product savedProduct = productService.addProduct(productStandard);
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

    @ApiOperation(value = "根据 ID 返回产品")
    @RequestMapping(value = "/findProductByProductId", method = RequestMethod.GET)
    @ResponseBody
    // public Product findProductByProductId(
    public ProductService.ProductStandard findProductByProductId(
            @RequestParam(value = "productId", defaultValue = "") Long productId
    ) {
        return productService.findProductByProductId(productId);
    }

    // test
    @ApiOperation(value = "测试用接口")
    @RequestMapping(value = "/findAllRel", method = RequestMethod.GET)
    @ResponseBody
    public List<RelProductFilterCake> findAllRel(){
        return productService.findAllRel();
    }
}