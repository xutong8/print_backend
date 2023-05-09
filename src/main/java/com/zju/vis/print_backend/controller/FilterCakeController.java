package com.zju.vis.print_backend.controller;


import com.zju.vis.print_backend.dao.FilterCakeRepository;
import com.zju.vis.print_backend.entity.FilterCake;
import com.zju.vis.print_backend.service.FilterCakeService;

import com.zju.vis.print_backend.vo.ResultVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Api(description = "滤饼管理")
@RequestMapping("/filterCake")
@CrossOrigin
@RestController
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
    public ResponseEntity<FilterCake> addFilterCake(@Valid @RequestBody FilterCakeService.FilterCakeStandard filterCakeStandard) {
        FilterCake savedFilterCake = filterCakeService.addFilterCake(filterCakeStandard);
        return new ResponseEntity<>(savedFilterCake, HttpStatus.CREATED);
    }

    @ApiOperation(value = "更新滤饼信息")
    @RequestMapping(value = "/updateFilterCake", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<String> updateFilterCake(
            @Valid @RequestBody FilterCakeService.FilterCakeStandard updatedFilterCake
    ) {
        String result = filterCakeService.updateFilterCake(updatedFilterCake);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "根据 ID 返回滤饼")
    @RequestMapping(value = "/findFilterCakeByFilterCakeId", method = RequestMethod.GET)
    @ResponseBody
    public FilterCakeService.FilterCakeStandard findFilterCakeByFilterCakeId(
            @RequestParam(value = "filterCakeId", defaultValue = "") Long filterCakeId
    ) {
        return filterCakeService.findFilterCakeByFilterCakeId(filterCakeId);
    }

    @Resource
    private FilterCakeRepository filterCakeRepository;

    @ApiOperation(value = "测试滤饼历史价格用接口")
    @RequestMapping(value = "/findFilterCakeHistoryPriceTest", method = RequestMethod.GET)
    @ResponseBody
    public Double testHistoryPrice(
            @RequestParam(value = "filterCakeId", defaultValue = "") Long filterCakeId,
            @RequestParam(value = "testDate", defaultValue = "2022-12-20") String testDate
            // @RequestParam(value = "year") Integer year,
            // @RequestParam(value = "month") Integer month,
            // @RequestParam(value = "date") Integer date
    ) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try{
            date = sdf.parse(testDate);
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println(date);
        return filterCakeService.calculateFilterCakeHistoryPrice(filterCakeRepository.findFilterCakeByFilterCakeId(filterCakeId),date);
    }

    @ApiOperation(value = "通过 filterCakeId删除记录")
    @RequestMapping(value = "/deleteByRawMaterialId", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<String> deleteByFilterCakeId(
            @RequestParam(value = "filterCakeId") Long filterCakeId
    ) {
        filterCakeService.deleteByFilterCakeId(filterCakeId);
        return ResponseEntity.ok("FilterCake with ID: " + filterCakeId + " has been deleted.");
    }

    // 文件上传
    @ApiOperation(value = "上传FilterCake文件并持久化")
    @RequestMapping(value = "/upload")
    public ResultVo importFilterCakeExcel(@RequestParam("file") MultipartFile excel){
        return filterCakeService.importFilterCakeExcelAndPersistence(excel);
    }

    // 文件下载

    
}
