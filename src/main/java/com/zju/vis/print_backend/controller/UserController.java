package com.zju.vis.print_backend.controller;


import com.zju.vis.print_backend.service.UserService;
import com.zju.vis.print_backend.vo.*;
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
@RestController
public class UserController {

    @Resource
    private UserService userService;

    @ApiOperation(value = "登录")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResultVo doLogin(
            @Valid @RequestBody UserLoginVo userLoginVo
    ){
        return userService.doLogin(userLoginVo);
    };

    @ApiOperation(value = "注册新用户")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResultVo doRegister(
            @Valid @RequestBody UserLoginVo userLoginVo
    ) {
        return userService.doRegister(userLoginVo);
    }

    @ApiOperation(value = "通过用户名删除用户")
    @RequestMapping(value = "/deleteUser", method = RequestMethod.DELETE)
    public ResultVo doDelete(
            @RequestParam(value = "userName") String userName
    ) {
        return userService.doDelete(userName);
    }

    @ApiOperation(value = "获取所有用户简要信息")
    @RequestMapping(value = "/findAllUserName", method = RequestMethod.GET)
    public List<UserSimpleVo> findAllUserSimple() {
        return userService.findAllUserSimple();
    }

    @ApiOperation(value = "用户管理")
    @RequestMapping(value = "/manageUser", method = RequestMethod.PUT)
    public ResultVo manageUser(
            @Valid @RequestBody UserModifyVo userModify
    ) {
        return userService.manageUser(userModify);
    }

    @ApiOperation(value = "更改用户密码")
    @RequestMapping(value = "/updatePassword", method = RequestMethod.PUT)
    public ResultVo updatePassword(
            @Valid @RequestBody PasswordModifyVo passwordModify
    ) {
        return userService.updatePassword(passwordModify);
    }
}
