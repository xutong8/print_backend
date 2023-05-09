package com.zju.vis.print_backend.controller;

import com.zju.vis.print_backend.entity.Product;
import com.zju.vis.print_backend.entity.ProductSeries;
import com.zju.vis.print_backend.service.ProductSeriesService;
import com.zju.vis.print_backend.service.RawMaterialService;
import com.zju.vis.print_backend.vo.ResultVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Api(description = "产品系列管理")
@RequestMapping("/productSeries")
@CrossOrigin
@RestController
public class ProductSeriesController {
    @Resource
    ProductSeriesService productSeriesService;

    @ApiOperation(value = "获取所有的产品系列")
    @RequestMapping(value = "/findAllProductSeries", method = RequestMethod.GET)
    @ResponseBody
    public ProductSeriesService.ProductSeriesPackage findAll(
            @RequestParam(value = "pageNo" ,defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize" ,defaultValue = "10") Integer pageSize
    ){
        return productSeriesService.findAll(pageNo-1,pageSize);
    }

    @ApiOperation(value = "获取所有的产品系列名称")
    @RequestMapping(value = "/findAllProductSeriesName", method = RequestMethod.GET)
    @ResponseBody
    public List<ProductSeriesService.ProductSeriesName> findAllProductSeriesName(){
        return productSeriesService.findAllProductSeriesName();
    }

    @ApiOperation(value = "根据 ID 返回产品系列")
    @RequestMapping(value = "/findProductSeriesByProductSeriesId", method = RequestMethod.GET)
    @ResponseBody
    public ProductSeriesService.ProductSeriesStandard findProductSeriesByProductSeriesId(
            @RequestParam(value = "productSeriesId", defaultValue = "") Long productSeriesId
    ) {
        return productSeriesService.findProductSeriesByProductSeriesId(productSeriesId);
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
    public ResponseEntity<String> addProductSeries(@RequestBody ProductSeriesService.ProductSeriesStandard productSeriesStandard) {
        ProductSeries newProductSeries = productSeriesService.addProductSeries(productSeriesStandard);
        return ResponseEntity.ok("New Product Series with ID: " + newProductSeries.getProductSeriesId() + " has been added.");
    }

    @ApiOperation(value = "更新产品系列信息")
    @RequestMapping(value = "/updateProductSeries", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<String> updateProductSeries(
            @Valid @RequestBody ProductSeriesService.ProductSeriesStandard updatedProductSeries
    ) {
        try {
            String result = productSeriesService.updateProductSeries(updatedProductSeries);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // 文件上传
    @ApiOperation(value = "上传ProductSeries文件并持久化")
    @RequestMapping(value = "/upload")
    public ResultVo importProductSeriesExcel(@RequestParam("file") MultipartFile excel){
        return productSeriesService.importProductSeriesExcelAndPersistence(excel);
    }

    // 文件下载
    @ApiOperation(value = "下载ProductSeries文件")
    @PostMapping("/exportExcel")
    public ResultVo exportProductSeriesExcel(final HttpServletResponse response){
        return productSeriesService.exportProductSeriesExcel(response);
    }

}
