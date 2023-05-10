package com.zju.vis.print_backend.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcelRelProductRawMaterialVo {
    // 商品名称
    @ExcelProperty("商品名称")
    private String productName;

    // 原料名称
    @ExcelProperty("原料名称")
    private String rawMaterialName;

    // 投料量
    @ExcelProperty("投料量")
    private Double inventory;
}
