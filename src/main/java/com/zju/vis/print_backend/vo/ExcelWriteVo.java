package com.zju.vis.print_backend.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcelWriteVo {

    // 姓名
    @ExcelProperty("姓名")
    private String name;

    // 性别 1：女 0：男
    @ExcelProperty("性别")
    private String sex;

    // 创建时间
    @DateTimeFormat("yyyy年MM月dd日HH时mm分ss秒")
    @ExcelProperty("创建时间")
    private String createTime;

}

