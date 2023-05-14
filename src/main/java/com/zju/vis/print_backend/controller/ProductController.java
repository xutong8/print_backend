package com.zju.vis.print_backend.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;


import com.zju.vis.print_backend.dao.ProductRepository;
import com.zju.vis.print_backend.entity.RawMaterial;
import com.zju.vis.print_backend.service.RelProductFilterCakeService;
import com.zju.vis.print_backend.service.RelProductRawMaterialService;
import com.zju.vis.print_backend.vo.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.zju.vis.print_backend.entity.Product;
import com.zju.vis.print_backend.service.ProductService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

@Api(description = "产品管理")
@RequestMapping("/product")
@CrossOrigin
@RestController
public class ProductController {
    @Resource
    private ProductService productService;

    @Resource
    RelProductRawMaterialService productRawMaterialService;

    @Resource
    RelProductFilterCakeService productFilterCakeService;


    @ApiOperation(value = "获取所有产品")
    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    @ResponseBody
    public PackageVo findAll(
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize
    ) {
        return productService.findAll(pageNo - 1, pageSize);
    }

    @ApiOperation(value = "获取所有产品名称")
    @RequestMapping(value = "/findAllProductName", method = RequestMethod.GET)
    @ResponseBody
    public List<EntityNameVo> findAllProductName() {
        return productService.findAllProductName();
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
    public PackageVo findAllByRelCondition(
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
        PackageVo list = productService.findAllByRelCondition(rawMaterialName, filterCakeName, productSeriesName, pageNo - 1, pageSize);
        return list;
    }

    @ApiOperation(value = "Product直接查询")
    @RequestMapping(value = "/findAllByDirectCondition", method = RequestMethod.GET)
    @ResponseBody
    public PackageVo findAllByDirectCondition(
            @RequestParam(value = "typeOfQuery", defaultValue = "产品名称") String typeOfQuery,
            @RequestParam(value = "conditionOfQuery", defaultValue = "") String conditionOfQuery,
            // @Valid @RequestBody ,
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize
    ) {
        PackageVo list = productService.findAllByDirectCondition(typeOfQuery, conditionOfQuery, pageNo - 1, pageSize);
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
    public ResultVo addProduct(@Valid @RequestBody ProductStandardVo productStandard) {
        return productService.addProduct(productStandard);
        // return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    @ApiOperation(value = "更新产品信息")
    @RequestMapping(value = "/updateProduct", method = RequestMethod.PUT)
    @ResponseBody
    public ResultVo updateProduct(
            // @RequestParam(value = "productId") Long productId,
            @Valid @RequestBody ProductStandardVo updatedProduct
    ) {
        return productService.updateProduct(updatedProduct);
    }

    @ApiOperation(value = "根据 ID 返回产品")
    @RequestMapping(value = "/findProductByProductId", method = RequestMethod.GET)
    @ResponseBody
    // public Product findProductByProductId(
    public ProductStandardVo findProductByProductId(
            @RequestParam(value = "productId", defaultValue = "") Long productId
    ) {
        return productService.findProductByProductId(productId);
    }

    @Resource
    private ProductRepository productRepository;

    // @ApiOperation(value = "测试商品历史价格用接口")
    // @RequestMapping(value = "/findFilterCakeHistoryPriceTest", method = RequestMethod.GET)
    // @ResponseBody
    // public Double testHistoryPrice(
    //         @RequestParam(value = "productId", defaultValue = "") Long productId,
    //         @RequestParam(value = "testDate", defaultValue = "2022-12-20") String testDate
    // ) {
    //     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    //     Date date = null;
    //     try {
    //         date = sdf.parse(testDate);
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    //     System.out.println(date);
    //     return productService.calculateProductHistoryPrice(productRepository.findProductByProductId(productId), date);
    // }

    @ApiOperation(value = "产品历史价格列表用接口")
    @RequestMapping(value = "/getProductHistoryPriceList", method = RequestMethod.GET)
    public List<HistoryPriceVo> getProductHistoryPriceList(
            @RequestParam(value = "productId", defaultValue = "1" ) Long productId,
            @RequestParam(value = "months", defaultValue = "12") Long months
    ){
        return productService.getProductHistoryPriceList(productId,months);
    }

    // 上传文件
    @ApiOperation(value = "上传Product文件并持久化")
    @RequestMapping(value = "/upload")
    public ResultVo importProductExcel(@RequestParam("file") MultipartFile excel) {
        return productService.importProductExcelAndPersistence(excel);
    }

    // 关系表文件上传 ProductRawMaterial
    @ApiOperation(value = "上传RelProductRawMaterial文件并持久化")
    @RequestMapping(value = "/uploadRelPR")
    public ResultVo importRelProductRawMaterialExcel(@RequestParam("file") MultipartFile excel){
        return productRawMaterialService.importRelProductRawMaterialExcelAndPersistence(excel);
    }

    // 关系表文件上传 ProductFilterCake
    @ApiOperation(value = "上传RelProductFilterCake文件并持久化")
    @RequestMapping(value = "/uploadRelPF")
    public ResultVo importRelProductFilterCakeExcel(@RequestParam("file") MultipartFile excel){
        return productFilterCakeService.importRelProductFilterCakeExcelAndPersistence(excel);
    }

    // 下载文件
    @ApiOperation(value = "下载Product文件")
    @PostMapping("/exportExcel")
    public ResultVo exportProductExcel(final HttpServletResponse response) {
        return productService.exportProductExcel(response);
    }

    // 关系表文件下载 RelProductRawMaterial
    @ApiOperation(value = "下载RelProductRawMaterial文件")
    @PostMapping("/exportRelPRExcel")
    public ResultVo exportRelProductRawMaterialExcel(final HttpServletResponse response){
        return productRawMaterialService.exportRelProductRawMaterialExcel(response);
    }

    // 关系表文件下载 RelProductFilterCake
    @ApiOperation(value = "下载RelProductFilterCake文件")
    @PostMapping("/exportRelPFExcel")
    public ResultVo exportRelProductFilterCakeExcel(final HttpServletResponse response){
        return productFilterCakeService.exportRelProductFilterCakeExcel(response);
    }

}