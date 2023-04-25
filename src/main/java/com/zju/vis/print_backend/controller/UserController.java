package com.zju.vis.print_backend.controller;

import com.zju.vis.print_backend.entity.Product;
import com.zju.vis.print_backend.entity.User;
import com.zju.vis.print_backend.service.ProductService;
import com.zju.vis.print_backend.service.RawMaterialService;
import com.zju.vis.print_backend.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@Api(description = "用户管理")
@RequestMapping("/User")
@CrossOrigin
@Controller
public class UserController {

    @Resource
    private UserService userService;

    @ApiOperation(value = "登录")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public UserService.UserStandard doLogin(
            @RequestParam(value = "userName") String userName,
            @RequestParam(value = "password") String password
    ){
        return userService.doLogin(userName,password);
    };

    @ApiOperation(value = "注册新用户")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public UserService.UserStandard doRegister(
            @RequestParam(value = "userName") String userName,
            @RequestParam(value = "password") String password
    ) {
        return userService.doRegister(userName,password);
    }

    @ApiOperation(value = "通过用户名删除用户")
    @RequestMapping(value = "/deleteUser", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<String> doDelete(
            @RequestParam(value = "userName") String userName
    ) {
        if(userService.doDelete(userName)){
            return ResponseEntity.ok(userName + " has been deleted.");
        }
        else{
            return ResponseEntity.ok("The owner cannot be deleted and can only be transferred.");
        }
    }

    @ApiOperation(value = "获取所有用户简要信息")
    @RequestMapping(value = "/findAllUserName", method = RequestMethod.GET)
    @ResponseBody
    public List<UserService.UserSimple> findAllUserSimple() {
        return userService.findAllUserSimple();
    }

    @ApiOperation(value = "更改用户类型")
    @RequestMapping(value = "/updateUserType", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<String> updateUserType(
            @RequestParam(value = "applicant") String applicant,
            @RequestParam(value = "userModified") String userModified,
            @RequestParam(value = "userType") String userType
    ) {
        String result = userService.updateUserType(applicant,userModified,userType);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
