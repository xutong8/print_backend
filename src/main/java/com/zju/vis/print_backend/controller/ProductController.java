package com.zju.vis.print_backend.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;


import com.zju.vis.print_backend.dao.ProductRepository;
import com.zju.vis.print_backend.dao.ProductSeriesRepository;
import com.zju.vis.print_backend.entity.RawMaterial;
import com.zju.vis.print_backend.entity.RelProductFilterCake;
import com.zju.vis.print_backend.service.RawMaterialService;
import com.zju.vis.print_backend.vo.ResultVo;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import springfox.documentation.spring.web.json.Json;

@Api(description = "产品管理")
@RequestMapping("/product")
@CrossOrigin
@RestController
public class ProductController {
    @Resource
    private ProductService productService;

    @ApiOperation(value = "获取所有产品")
    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    @ResponseBody
    public ProductService.ProductPackage findAll(
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize
    ) {
        return productService.findAll(pageNo - 1, pageSize);
    }


    @ApiOperation(value = "根据产品Id返回对应的原料")
    @RequestMapping(value = "/findRawMaterialByProductID", method = RequestMethod.GET)
    @ResponseBody
    public List<RawMaterial> getProductAndRawMaterial(
            @RequestParam(value = "productId") Long productId
    ) {
        return productService.getProductAndRawMaterial(productId);
    }

    @ApiOperation(value = "根据关联条件返回对应的产品(滤饼名、原料名、系列名)")
    @RequestMapping(value = "/findAllByRelCondition", method = RequestMethod.GET)
    @ResponseBody
    public ProductService.ProductPackage findAllByRelCondition(
            @RequestParam(value = "rawMaterialName", defaultValue = "") String rawMaterialName,
            @RequestParam(value = "filterCakeName", defaultValue = "") String filterCakeName,
            @RequestParam(value = "productSeriesName", defaultValue = "") String productSeriesName,
            // @Valid @RequestBody ,
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize
    ) {

        // findAllByCondition(String rawMaterialName, String filterCakeName, String productSeriesName)
        System.out.println("List<Product> findAllByCondition   " +
                "rawMaterialName: " + rawMaterialName +
                "  filterCakeName: " + filterCakeName +
                "  productSeriesName: " + productSeriesName);

        long s = System.currentTimeMillis();
        ProductService.ProductPackage list = productService.findAllByRelCondition(rawMaterialName, filterCakeName, productSeriesName, pageNo - 1, pageSize);
        long e = System.currentTimeMillis();
        System.out.println("findbycondition开始的时间：" + s);
        System.out.println("findbycondition结束的时间：" + e);
        System.out.println("findbycondition查询的时间差为：" + (e - s));

        return list;
    }

    @ApiOperation(value = "Product直接查询")
    @RequestMapping(value = "/findAllByDirectCondition", method = RequestMethod.GET)
    @ResponseBody
    public ProductService.ProductPackage findAllByDirectCondition(
            @RequestParam(value = "typeOfQuery", defaultValue = "产品名称") String typeOfQuery,
            @RequestParam(value = "conditionOfQuery", defaultValue = "") String conditionOfQuery,
            // @Valid @RequestBody ,
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize
    ) {
        long s = System.currentTimeMillis();
        ProductService.ProductPackage list = productService.findAllByDirectCondition(typeOfQuery, conditionOfQuery, pageNo - 1, pageSize);
        long e = System.currentTimeMillis();
        System.out.println("findbycondition开始的时间：" + s);
        System.out.println("findbycondition结束的时间：" + e);
        System.out.println("findbycondition查询的时间差为：" + (e - s));
        return list;
    }

    // 产品系列映射关系根据产品系列生成，此接口暂时废弃
    // @ApiOperation(value = "获取所有产品名称")
    // @RequestMapping(value = "/findAllProductName", method = RequestMethod.GET)
    // @ResponseBody
    // public List<ProductService.ProductSimple> findAllProductName() {
    //     return productService.findAllProductName();
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
    public ResponseEntity<Product> addProduct(@Valid @RequestBody ProductService.ProductStandard productStandard) {
        Product savedProduct = productService.addProduct(productStandard);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    @ApiOperation(value = "更新产品信息")
    @RequestMapping(value = "/updateProduct", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<String> updateProduct(
            // @RequestParam(value = "productId") Long productId,
            @Valid @RequestBody ProductService.ProductStandard updatedProduct
    ) {
        String result = productService.updateProduct(updatedProduct);
        return new ResponseEntity<>(result, HttpStatus.OK);
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

    @Resource
    private ProductRepository productRepository;

    @ApiOperation(value = "测试商品历史价格用接口")
    @RequestMapping(value = "/findFilterCakeHistoryPriceTest", method = RequestMethod.GET)
    @ResponseBody
    public Double testHistoryPrice(
            @RequestParam(value = "productId", defaultValue = "") Long productId,
            @RequestParam(value = "testDate", defaultValue = "2022-12-20") String testDate
    ) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(testDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(date);
        return productService.calculateProductHistoryPrice(productRepository.findProductByProductId(productId), date);
    }

    // 上传文件
    // @ApiOperation(value = "上传商品文件")
    // @RequestMapping(value = "/upload")
    // @ResponseBody
    // public ResultVo importExcel(@RequestParam("file") MultipartFile excel) {
    //     return fileService.importExcel(excel);
    // }

    @ApiOperation(value = "上传Product文件并持久化")
    @RequestMapping(value = "/upload")
    public ResultVo importProductExcel(@RequestParam("file") MultipartFile excel) {
        return productService.importProductExcelAndPersistence(excel);
    }

}