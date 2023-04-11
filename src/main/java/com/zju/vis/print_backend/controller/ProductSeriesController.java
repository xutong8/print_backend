package com.zju.vis.print_backend.controller;

import com.zju.vis.print_backend.entity.Product;
import com.zju.vis.print_backend.entity.ProductSeries;
import com.zju.vis.print_backend.service.ProductSeriesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Api(description = "产品系列管理")
@RequestMapping("/productSeries")
@CrossOrigin
@Controller
public class ProductSeriesController {
    @Resource
    ProductSeriesService productSeriesService;

    @ApiOperation(value = "获取所有的产品系列")
    @RequestMapping(value = "/findAllProductSeries", method = RequestMethod.GET)
    @ResponseBody
    public List<ProductSeries> findAll(){
        return productSeriesService.findAll();
    }

    @ApiOperation(value = "根据系列名称返回对应的产品")
    @RequestMapping(value = "/findProductsByProductSeriesName", method = RequestMethod.GET)
    @ResponseBody
    public Set<Product> findProductsByProductSeriesName(
            @RequestParam(value = "productSeriesName")String productSeriesName
    ){
        return productSeriesService.findProductsByProductSeriesName(productSeriesName);
    }

    @ApiOperation(value = "通过 productSeriesId 删除记录")
    @RequestMapping(value = "/deleteByProductSeriesId", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<String> deleteByProductSeriesId(
            @RequestParam(value = "productSeriesId") Long productSeriesId
    ) {
        productSeriesService.deleteByProductSeriesId(productSeriesId);
        return ResponseEntity.ok("Product with ID: " + productSeriesId + " has been deleted.");
    }

    @ApiOperation(value = "添加新的产品系列")
    @RequestMapping(value = "/addProductSeries", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> addProductSeries(@RequestBody ProductSeries productSeries) {
        ProductSeries newProductSeries = productSeriesService.addProductSeries(productSeries);
        return ResponseEntity.ok("New Product Series with ID: " + newProductSeries.getProductSeriesId() + " has been added.");
    }

    @ApiOperation(value = "更新产品系列信息")
    @RequestMapping(value = "/updateProductSeries", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<ProductSeries> updateProductSeries(
            @RequestParam(value = "productSeriesId") Long productSeriesId,
            @Valid @RequestBody ProductSeries updatedProductSeries
    ) {
        try {
            ProductSeries updatedSeries = productSeriesService.updateProductSeries(productSeriesId, updatedProductSeries);
            return new ResponseEntity<>(updatedSeries, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
