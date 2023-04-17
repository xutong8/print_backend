package com.zju.vis.print_backend.controller;


import com.zju.vis.print_backend.entity.FilterCake;
import com.zju.vis.print_backend.entity.Product;
import com.zju.vis.print_backend.service.FilterCakeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
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
    public FilterCakeService.FilterCakePackage findAll(
            @RequestParam(value = "pageNo" ,defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize" ,defaultValue = "10") Integer pageSize
    ){
        return filterCakeService.findAll(pageNo-1 ,pageSize);
    }

    // 精简列表项
    @ApiOperation(value = "获取所有滤饼的名称")
    @RequestMapping(value = "/findAllFilterCakeName", method = RequestMethod.GET)
    @ResponseBody
    public List<FilterCakeService.FilterCakeName> findAllFilterCakeName(){
        return filterCakeService.findAllFilterCakeName();
    }

    // 根据条件返回
    @ApiOperation(value = "根据条件返回所有的滤饼")
    @RequestMapping(value = "/findAllFilterCakeByCondition" ,method = RequestMethod.GET)
    @ResponseBody
    public FilterCakeService.FilterCakePackage findAllFilterCakeByCondition(
            @RequestParam(value = "typeOfQuery", defaultValue = "滤饼名称") String typeOfQuery,
            @RequestParam(value = "conditionOfQuery" ,defaultValue = "") String conditionOfQuery,
            @RequestParam(value = "pageNo" ,defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize" ,defaultValue = "10") Integer pageSize
    ){
        // return filterCakeService.findAllByFilterCakeNameContaining(filterCakeName);
        return filterCakeService.findAllFilterCakeByCondition(typeOfQuery, conditionOfQuery, pageNo-1, pageSize);
    }


    // @ApiOperation(value = "根据滤饼名称返回对应的产品")
    // @RequestMapping(value = "/findProductsByFilterCakeName", method = RequestMethod.GET)
    // @ResponseBody
    // public Set<Product> findProductsByFilterCakeName(String MaterialName){
    //     return filterCakeService.findProductsByFilterCakeName(MaterialName);
    // }

    @ApiOperation(value = "添加新滤饼")
    @RequestMapping(value = "/addFilterCake", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<FilterCake> addFilterCake(@Valid @RequestBody FilterCake filterCake) {
        FilterCake savedFilterCake = filterCakeService.addFilterCake(filterCake);
        return new ResponseEntity<>(savedFilterCake, HttpStatus.CREATED);
    }

    @ApiOperation(value = "更新滤饼信息")
    @RequestMapping(value = "/updateFilterCake", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<FilterCake> updateFilterCake(
            @RequestParam(value = "filterCakeId") Long filterCakeId,
            @Valid @RequestBody FilterCake updatedFilterCake
    ) {
        FilterCake updated = filterCakeService.updateFilterCake(filterCakeId, updatedFilterCake);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }
}
