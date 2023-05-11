package com.zju.vis.print_backend.exception;

import com.zju.vis.print_backend.enums.ResultCodeEnum;
import lombok.Data;

// 自定义异常 ParamErrorException
@Data
public class ParamErrorException extends RuntimeException {
    // 错误码
    private Integer code;

    // 错误消息
    private String msg;

    public ParamErrorException() {
        this(ResultCodeEnum.PARAM_ERROR.getCode(), ResultCodeEnum.PARAM_ERROR.getMessage());
    }

    public ParamErrorException(String msg) {
        this(ResultCodeEnum.PARAM_ERROR.getCode(), msg);
    }

    public ParamErrorException(Integer code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

}
