package com.zju.vis.print_backend.controller;

import com.zju.vis.print_backend.entity.RawMaterial;
import com.zju.vis.print_backend.service.RawMaterialService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@Api(description = "原料管理")
@RequestMapping("/rawMaterial")
@CrossOrigin
@Controller
public class RawMaterialController {

    @Resource
    private RawMaterialService rawMaterialService;

    @ApiOperation(value = "获取所有原料")
    @RequestMapping(value = "/findAllRawMaterial", method = RequestMethod.GET)
    @ResponseBody
    public RawMaterialService.RawMaterialPackage findAll(
            @RequestParam(value = "pageNo" ,defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize" ,defaultValue = "10") Integer pageSize
    ) {
        return rawMaterialService.findAll(pageNo-1,pageSize);
    }

    @ApiOperation(value = "获取所有原料名称")
    @RequestMapping(value = "/findAllRawMaterialName", method = RequestMethod.GET)
    @ResponseBody
    public List<RawMaterialService.RawMaterialName> findAllRawMaterialName() {
        return rawMaterialService.findAllRawMaterialName();
    }

    @ApiOperation(value = "根据条件返回所有的原料")
    @RequestMapping(value = "/findAllRawMaterialByCondition", method = RequestMethod.GET)
    @ResponseBody
    public RawMaterialService.RawMaterialPackage findAllRawMaterialByCondition(
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
    public RawMaterialService.RawMaterialStandard findRawMaterialByRawMaterialId(
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
    public ResponseEntity<RawMaterial> addRawMaterial(@Valid @RequestBody RawMaterialService.RawMaterialStandard rawMaterialStandard) {
        RawMaterial rawMaterial = rawMaterialService.addRawMaterial(rawMaterialStandard);
        return new ResponseEntity<>(rawMaterial, HttpStatus.OK);
    }

    @ApiOperation(value = "更新原料信息")
    @RequestMapping(value = "/updateRawMaterial", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<RawMaterial> updateRawMaterial(
            @RequestParam(value = "rawMaterialId") Long rawMaterialId,
            @Valid @RequestBody RawMaterial updatedRawMaterial
    ) {
        RawMaterial updated = rawMaterialService.updateRawMaterial(rawMaterialId, updatedRawMaterial);
        return new ResponseEntity<>(updated, HttpStatus.OK);
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
}
