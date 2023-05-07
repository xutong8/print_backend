package com.zju.vis.print_backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcelProductVo {
    // 商品名称
    private String productName;
    // 商品编号
    private String productIndex;
    // 商品代码
    private String productCode;
    // 商品颜色
    private String productColor;
    // 系列名称，记得转化
    private String productSeriesName;
    // 生产商家
    private String productFactoryName;
    // 商品核酸数量
    private Integer productAccountingQuantity;
    // 商品批处理成本
    private Float productProcessingCost;
}
