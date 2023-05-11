package com.zju.vis.print_backend.enums;

import lombok.Getter;

// 自定义的枚举类
@Getter
public enum ResultCodeEnum {
    SUCCESS(200, "成功"),
    ERROR(301, "错误"),
    PARAM_ERROR(303, "参数错误"),
    FILE_NOT_EXIST(304, "文件不存在"),
    CLOSE_FAILD(305, "关闭流失败");

    private Integer code;
    private String message;

    ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
