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

//    @ApiOperation(value = "通过 filterCakeId 删除记录")
//    @RequestMapping(value = "/deleteByFilterCakeId", method = RequestMethod.DELETE)
//    @ResponseBody
//    public ResponseEntity<String> deleteByFilterCakeId(
//            @RequestParam(value = "filterCakeId") Long filterCakeId
//    ) {
//        filterCakeService.deleteByFilterCakeId(filterCakeId);
//        return ResponseEntity.ok("FilterCake with ID: " + filterCakeId + " has been deleted.");
//    }

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
