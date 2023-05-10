package com.zju.vis.print_backend.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcelRelFilterCakeRawMaterialWriteVo {
    // 滤饼名称
    @ExcelProperty("滤饼名称")
    private String filterCakeName;

    // 原料名称
    @ExcelProperty("原料名称")
    private String rawMaterialName;

    // 投料量
    @ExcelProperty("投料量")
    private Double inventory;
}
