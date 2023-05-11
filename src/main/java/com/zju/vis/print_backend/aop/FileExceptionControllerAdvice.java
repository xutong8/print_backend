package com.zju.vis.print_backend.aop;


import com.zju.vis.print_backend.Utils.ResultVoUtil;
import com.zju.vis.print_backend.exception.ParamErrorException;
import com.zju.vis.print_backend.vo.ResultVo;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class FileExceptionControllerAdvice {

    // 处理文件为空的异常
    @ExceptionHandler(ParamErrorException.class)
    public ResultVo<String> fileExceptionHandler(ParamErrorException exception) {
        return ResultVoUtil.error(exception.getCode(), exception.getMsg());
    }

}

