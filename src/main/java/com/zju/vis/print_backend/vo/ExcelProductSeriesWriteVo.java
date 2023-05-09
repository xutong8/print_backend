package com.zju.vis.print_backend.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcelProductSeriesWriteVo {
    // 产品系列
    @ExcelProperty("产品系列")
    private String productSeriesName;

    // 应用范围
    @ExcelProperty("应用范围")
    private String productSeriesFunction;
}
