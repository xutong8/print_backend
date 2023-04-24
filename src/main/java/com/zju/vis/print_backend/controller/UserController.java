package com.zju.vis.print_backend.controller;

import com.zju.vis.print_backend.entity.User;
import com.zju.vis.print_backend.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(description = "用户管理")
@RequestMapping("/User")
@CrossOrigin
@Controller
public class UserController {

    @Resource
    private UserService userService;

    @ApiOperation(value = "登录")
    @RequestMapping(value = "/Login", method = RequestMethod.POST)
    @ResponseBody
    public UserService.UserStandard doLogin(
            @RequestParam(value = "userName") String userName,
            @RequestParam(value = "password") String password
    ){
        return userService.doLogin(userName,password);
    };
}
