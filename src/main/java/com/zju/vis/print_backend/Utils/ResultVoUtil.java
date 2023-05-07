package com.zju.vis.print_backend.Utils;

import com.zju.vis.print_backend.vo.ResultVo;
import lombok.Data;

public class ResultVoUtil {

    public static ResultVo success() {
        return success(null);
    }
    public static ResultVo success(Object object) {
        ResultVo result = new ResultVo();
        result.setCode(FileUtil.ResultCodeEnum.SUCCESS.getCode());
        result.setMsg("成功");
        result.setData(object);
        return result;
    }
    public static ResultVo success(Integer code, Object object) {
        return success(code, null, object);
    }
    public static ResultVo success(Integer code, String msg, Object object) {
        ResultVo result = new ResultVo();

        result.setCode(code);
        result.setMsg(msg);
        result.setData(object);
        return result;
    }

    public static ResultVo error(String msg) {
        ResultVo result = new ResultVo();
        result.setCode(FileUtil.ResultCodeEnum.ERROR.getCode());
        result.setMsg(msg);
        return result;
    }
    public static ResultVo error(Integer code, String msg) {
        ResultVo result = new ResultVo();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

}

