package com.zju.vis.print_backend.controller;

import com.zju.vis.print_backend.Utils.Utils;
import com.zju.vis.print_backend.dao.RawMaterialRepository;
import com.zju.vis.print_backend.entity.RawMaterial;
import com.zju.vis.print_backend.service.RawMaterialService;
import com.zju.vis.print_backend.service.RelDateRawMaterialService;
import com.zju.vis.print_backend.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.apache.commons.collections4.Get;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@Api(description = "原料管理")
@RequestMapping("/rawMaterial")
@CrossOrigin
@RestController
public class RawMaterialController {

    @Resource
    private RawMaterialService rawMaterialService;

    @Resource
    private RelDateRawMaterialService relDateRawMaterialService;

    @ApiOperation(value = "获取所有原料")
    @RequestMapping(value = "/findAllRawMaterial", method = RequestMethod.GET)
    @ResponseBody
    public PackageVo findAll(
            @RequestParam(value = "pageNo" ,defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize" ,defaultValue = "10") Integer pageSize
    ) {
        return rawMaterialService.findAll(pageNo-1,pageSize);
    }

    @ApiOperation(value = "获取所有原料名称")
    @RequestMapping(value = "/findAllRawMaterialName", method = RequestMethod.GET)
    @ResponseBody
    public List<EntityNameVo> findAllRawMaterialName() {
        return rawMaterialService.findAllRawMaterialName();
    }

    @ApiOperation(value = "根据条件返回所有的原料")
    @RequestMapping(value = "/findAllRawMaterialByCondition", method = RequestMethod.GET)
    @ResponseBody
    public PackageVo findAllRawMaterialByCondition(
            @RequestParam(value = "typeOfQuery", defaultValue = "原料品名") String typeOfQuery,
            @RequestParam(value = "conditionOfQuery" ,defaultValue = "") String conditionOfQuery,
            @RequestParam(value = "pageNo" ,defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize" ,defaultValue = "10") Integer pageSize
    ) {
        return rawMaterialService.findAllRawMaterialByCondition(typeOfQuery,conditionOfQuery,pageNo-1,pageSize);
    }

    @ApiOperation(value = "根据 ID 返回原料")
    @RequestMapping(value = "/findRawMaterialByRawMaterialId", method = RequestMethod.GET)
    @ResponseBody
    public RawMaterialStandardVo findRawMaterialByRawMaterialId(
            @RequestParam(value = "rawMaterialId", defaultValue = "") Long rawMaterialId
    ) {
        return rawMaterialService.findRawMaterialByRawMaterialId(rawMaterialId);
    }

    // @ApiOperation(value = "根据名称返回对应的产品")
    // @RequestMapping(value = "/findAllProductByRawMaterialName", method = RequestMethod.GET)
    // @ResponseBody
    // public Set<Product> findProductsByRawMaterialName(String MaterialName) {
    //     return rawMaterialService.findProductsByRawMaterialName(MaterialName);
    // }

    @ApiOperation(value = "新增原料")
    @RequestMapping(value = "/addRawMaterial", method = RequestMethod.POST)
    @ResponseBody
    public ResultVo addRawMaterial(@Valid @RequestBody RawMaterialStandardVo rawMaterialStandard) {
        return rawMaterialService.addRawMaterial(rawMaterialStandard);
    }

    @ApiOperation(value = "更新原料信息")
    @RequestMapping(value = "/updateRawMaterial", method = RequestMethod.PUT)
    @ResponseBody
    public ResultVo updateRawMaterial(
            @Valid @RequestBody RawMaterialStandardVo updatedRawMaterial
    ) {
        return rawMaterialService.updateRawMaterial(updatedRawMaterial);
    }

    @ApiOperation(value = "通过 RawMaterialId删除记录")
    @RequestMapping(value = "/deleteByRawMaterialId", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<String> deleteByRawMaterialId(
            @RequestParam(value = "rawMaterialId") Long rawMaterialId
    ) {
        rawMaterialService.deleteByRawMaterialId(rawMaterialId);
        return ResponseEntity.ok("RawMaterial with ID: " + rawMaterialId + " has been deleted.");
    }

    // 文件上传
    @ApiOperation(value = "上传RawMaterial文件并持久化")
    @RequestMapping(value = "/upload")
    public ResultVo importRawMaterialExcel(@RequestParam("file") MultipartFile excel){
        return rawMaterialService.importRawMaterialExcelAndPersistence(excel);
    }

    // 关系表文件上传 RelDateRawMaterial
    @ApiOperation(value = "上传RelDateRawMaterial文件并持久化")
    @RequestMapping(value = "/uploadRelDR")
    public ResultVo importRelDateRawMaterialExcel(@RequestParam("file") MultipartFile excel){
        return relDateRawMaterialService.importRelDateRawMaterialExcelAndPersistence(excel);
    }

    // 下载文件
    @ApiOperation(value = "下载RawMaterial文件")
    @RequestMapping("/exportExcel")
    public ResultVo exportRawMaterialExcel(final HttpServletResponse response) {
        return rawMaterialService.exportRawMaterialExcel(response);
    }

    // 关系表文件下载 RelDateRawMaterial
    @ApiOperation(value = "下载RelDateRawMaterial文件")
    @RequestMapping("/exportRelDRExcel")
    public ResultVo exportRelDateRawMaterialExcel(final HttpServletResponse response){
        return relDateRawMaterialService.exportRelDateRawMaterialExcel(response);
    }

    @Resource
    private RawMaterialRepository rawMaterialRepository;

    // 测试原料历史价格
    @ApiOperation(value = "测历史价格")
    @RequestMapping(value = "/testhistoryPrice",method = RequestMethod.GET)
    public Double testHistoryPrice(
            @RequestParam(value = "id" ,defaultValue = "10") Long id,
            @RequestParam(value = "date" ,defaultValue = "2022-12-15") String date
    ){
        Date finalDate = Utils.stringToDate(date);
        return rawMaterialService.getRawMaterialHistoryPrice(rawMaterialRepository.findRawMaterialByRawMaterialId(id),finalDate);
    }

    // 原料历史价格列表 todo 按月返回原料的历史价格列表
    @ApiOperation(value = "历史价格列表")
    @RequestMapping(value = "/getRawMaterialHistoryPriceList",method = RequestMethod.GET)
    public List<HistoryPriceVo> getRawMaterialHistoryPriceList(
            @RequestParam(value = "rawMaterialId", defaultValue = "1" ) Long rawMaterialId,
            @RequestParam(value = "months", defaultValue = "12") Long months
    ){
        return rawMaterialService.getRawMaterialHistoryPriceList(rawMaterialId,months);
    }
}
