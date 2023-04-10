package com.zju.vis.print_backend.controller;

import com.zju.vis.print_backend.entity.RawMaterial;
import com.zju.vis.print_backend.service.RawMaterialService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

@Api(description = "原料管理")
@RequestMapping("/rawMaterial")
@Controller
public class RawMaterialController {

  @Resource
  private RawMaterialService rawMaterialService;

  @ApiOperation(value = "获取所有原料")
  @RequestMapping(value = "/findAllRawMaterial", method = RequestMethod.GET)
  @ResponseBody
  public List<RawMaterial> findAll(){
    return rawMaterialService.findAll();
  }


  @ApiOperation(value = "根据原料ID返回对应的产品")
  @RequestMapping(value = "/findProductByRawMaterialID", method = RequestMethod.GET)
  @ResponseBody
  public void getProductAndRawMaterial(
          @RequestParam(value = "rawMaterialID") Long rawMaterialID
  ){
    // productService.getProductAndRawMaterial(productId);
    rawMaterialService.getProductByRawMaterialId(rawMaterialID);
  }
  // @ApiOperation(value = "根据名称返回原料")
  // @RequestMapping(value = "/findAllByRawMaterialNameContaining" ,method = RequestMethod.GET)
  // @ResponseBody
  // public List<RawMaterial> findAllByRawMaterialNameContaining(String RawMaterialName){
  //   return rawMaterialService.findAllByRawMaterialNameContaining(RawMaterialName);
  // }
  //
  // @ApiOperation(value = "根据名称返回对应的")
  // @RequestMapping(value = "/findAllProductByRawMaterialName", method = RequestMethod.GET)
  // @ResponseBody
  // public Set<Product> findProductByRawMaterial(String MaterialName){
  //   return rawMaterialService.findProductByRawMaterial(MaterialName);
  // }
}
