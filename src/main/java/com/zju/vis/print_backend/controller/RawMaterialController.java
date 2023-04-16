package com.zju.vis.print_backend.controller;

import com.zju.vis.print_backend.entity.Product;
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
import java.util.Set;

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
    public List<RawMaterial> findAll(Integer pageNo,
                                     Integer pageSize
    ) {
        return rawMaterialService.findAll(pageNo,pageSize);
    }


    @ApiOperation(value = "根据原料ID返回对应的产品")
    @RequestMapping(value = "/findProductByRawMaterialID", method = RequestMethod.GET)
    @ResponseBody
    public void getProductAndRawMaterial(
            @RequestParam(value = "rawMaterialID") Long rawMaterialID
    ) {
        // productService.getProductAndRawMaterial(productId);
        rawMaterialService.getProductByRawMaterialId(rawMaterialID);
    }

    @ApiOperation(value = "根据名称返回原料")
    @RequestMapping(value = "/findAllByRawMaterialNameContaining", method = RequestMethod.GET)
    @ResponseBody
    public List<RawMaterial> findAllByRawMaterialNameContaining(String RawMaterialName) {
        return rawMaterialService.findAllByRawMaterialNameContaining(RawMaterialName);
    }

    @ApiOperation(value = "根据名称返回对应的产品")
    @RequestMapping(value = "/findAllProductByRawMaterialName", method = RequestMethod.GET)
    @ResponseBody
    public Set<Product> findProductsByRawMaterialName(String MaterialName) {
        return rawMaterialService.findProductsByRawMaterialName(MaterialName);
    }

    @ApiOperation(value = "新增原料")
    @RequestMapping(value = "/addRawMaterial", method = RequestMethod.POST)
    @ResponseBody
    public RawMaterial addRawMaterial(@Valid @RequestBody RawMaterial rawMaterial) {
        return rawMaterialService.addRawMaterial(rawMaterial);
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
}
