package com.zju.vis.print_backend.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcelProductWriteVo {
    // 商品名称
    @ExcelProperty("商品名称")
    private String productName;

    // 商品编号
    @ExcelProperty("商品编号")
    private String productIndex;

    // 商品代码
    @ExcelProperty("商品代码")
    private String productCode;

    // 商品颜色
    @ExcelProperty("商品颜色")
    private String productColor;

    // 系列名称,记得转化
    @ExcelProperty("系列名称")
    private String productSeriesName;

    // 生产厂家
    @ExcelProperty("生产厂家")
    private String productFactoryName;

    // 批次核算数量
    @ExcelProperty("批次核算数量")
    private Integer productAccountingQuantity;

    // 商品批处理成本
    @ExcelProperty("成品加工费用")
    private Float productProcessingCost;
}
