package com.zju.vis.print_backend.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcelRawMaterialWriteVo {
    // 原料名称
    @ExcelProperty("原料名称")
    private String rawMaterialName;

    // 存货编号
    @ExcelProperty("存货编号")
    private String rawMaterialIndex;

    // 是否为常规原料
    @ExcelProperty("是否为常规原料")
    private String rawMaterialConventional;

    // 规格
    @ExcelProperty("规格")
    private String rawMaterialSpecification;

    // 最新价格
    @ExcelProperty("最新价格")
    private Double rawMaterialPrice;
}
