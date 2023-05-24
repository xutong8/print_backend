package com.zju.vis.print_backend.controller;

import com.zju.vis.print_backend.service.SalesService;
import com.zju.vis.print_backend.vo.RawMaterialStandardVo;
import com.zju.vis.print_backend.vo.ResultVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@Api(description = "销售数据管理")
@RequestMapping("/Sales")
@CrossOrigin
@RestController
public class SalesController {

    @Resource
    private SalesService salesService;

    // 上传文件
    @ApiOperation(value = "上传Sales文件并持久化")
    @RequestMapping(value = "/upload")
    public ResultVo importProductExcel(@RequestParam("file") MultipartFile excel) {
        return salesService.importSalesExcelAndPersistence(excel);
    }

    // 下载文件
    @ApiOperation(value = "下载Sales文件")
    @RequestMapping("/exportExcel")
    public ResultVo exportRawMaterialExcel(final HttpServletResponse response) {
        return salesService.exportSalesExcel(response);
    }

    // 查单个销售数据
    @ApiOperation(value = "单个产品根据时间跨度选销售数据")
    @RequestMapping(value = "/getSingleProductSales", method = RequestMethod.GET)
    public ResultVo getSingleProductSales(
            @RequestParam(value = "productName", defaultValue = "SCARLET HW-CSN-130%") String productName,
            @RequestParam(value = "endTime", defaultValue = "2022-12-31") String endTime,
            @RequestParam(value = "timeSpan", defaultValue = "最近三个月") String timeSpan
    ) {
        return salesService.getSingleProductSales(productName,endTime,timeSpan);
    }

    // TopN销售数据
    @ApiOperation(value = "TopN销售数据")
    @RequestMapping(value = "/findTopNSalesProduct", method = RequestMethod.GET)
    public ResultVo findTopNSalesProduct(
            @RequestParam(value = "endTime", defaultValue = "2022-12-31") String endTime,
            @RequestParam(value = "timeSpan", defaultValue = "最近三个月") String timeSpan,
            @RequestParam(value = "number", defaultValue = "5") Integer number,
            @RequestParam(value = "method", defaultValue = "0") Integer method
    ){
        return salesService.findTopNSalesProduct(endTime,timeSpan,number,method);
    }
}
