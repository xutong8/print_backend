package com.zju.vis.print_backend.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.zju.vis.print_backend.convert.DateConvert;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcelRelDateRawMaterialWriteVo {
    // 原料名称
    @ExcelProperty("原料名称")
    private String rawMaterialName;

    // 日期
    @ExcelProperty("日期")
    private String rawMaterialDate;

    // 当期价格
    @ExcelProperty("当期价格")
    private Double price;
}
