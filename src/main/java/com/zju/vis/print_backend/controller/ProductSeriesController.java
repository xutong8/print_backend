package com.zju.vis.print_backend.controller;

import com.zju.vis.print_backend.entity.Product;
import com.zju.vis.print_backend.entity.ProductSeries;
import com.zju.vis.print_backend.service.ProductSeriesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
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
    public Set<Product> findProductsByProductSeriesName(String MaterialName){
        return productSeriesService.findProductsByProductSeriesName(MaterialName);
    }
}
