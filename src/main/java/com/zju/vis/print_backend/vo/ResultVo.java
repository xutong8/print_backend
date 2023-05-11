package com.zju.vis.print_backend.vo;

import com.zju.vis.print_backend.enums.ResultCodeEnum;
import lombok.Data;

@Data
public class ResultVo<T> {

    // 错误码
    private Integer code;

    // 提示信息
    private String msg;

    // 返回的数据
    private T data;

    // 判断是否成功
    public boolean checkSuccess() {
        return ResultCodeEnum.SUCCESS.getCode().equals(this.code);
    }

}

