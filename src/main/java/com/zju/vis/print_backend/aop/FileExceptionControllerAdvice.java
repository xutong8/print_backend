package com.zju.vis.print_backend.aop;


import com.zju.vis.print_backend.Utils.FileUtil;
import com.zju.vis.print_backend.Utils.ResultVoUtil;
import com.zju.vis.print_backend.vo.ResultVo;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class FileExceptionControllerAdvice {

    // 处理文件为空的异常
    @ExceptionHandler(FileUtil.ParamErrorException.class)
    public ResultVo<String> fileExceptionHandler(FileUtil.ParamErrorException exception) {
        return ResultVoUtil.error(exception.getCode(), exception.getMsg());
    }

}

