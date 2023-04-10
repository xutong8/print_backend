package com.zju.vis.print_backend.controller;


import com.zju.vis.print_backend.entity.FilterCake;
import com.zju.vis.print_backend.entity.Product;
import com.zju.vis.print_backend.service.FilterCakeService;
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

@Api(description = "滤饼管理")
@RequestMapping("/filterCake")
@CrossOrigin
@Controller
public class FilterCakeController {

    @Resource
    private FilterCakeService filterCakeService;

    @ApiOperation(value = "获取所有滤饼")
    @RequestMapping(value = "/findAllFilterCake", method = RequestMethod.GET)
    @ResponseBody
    public List<FilterCake> findAll(){
        return filterCakeService.findAll();
    }

    @ApiOperation(value = "根据滤饼名称返回对应滤饼")
    @RequestMapping(value = "/findAllByFilterCakeNameContaining" ,method = RequestMethod.GET)
    @ResponseBody
    public List<FilterCake> findAllByFilterCakeNameContaining(String filterCakeName){
        return filterCakeService.findAllByFilterCakeNameContaining(filterCakeName);
    }

    @ApiOperation(value = "根据滤饼名称返回对应的产品")
    @RequestMapping(value = "/findProductsByFilterCakeName", method = RequestMethod.GET)
    @ResponseBody
    public Set<Product> findProductsByFilterCakeName(String MaterialName){
        return filterCakeService.findProductsByFilterCakeName(MaterialName);
    }
}
