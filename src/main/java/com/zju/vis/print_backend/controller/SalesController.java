package com.zju.vis.print_backend.controller;

import com.zju.vis.print_backend.service.SalesService;
import com.zju.vis.print_backend.vo.ResultVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
}
