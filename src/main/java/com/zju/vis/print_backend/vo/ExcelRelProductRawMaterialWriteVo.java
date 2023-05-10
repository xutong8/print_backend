package com.zju.vis.print_backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcelRelProductRawMaterialWriteVo {
    // 商品名称
    private String productName;
    // 原料名称
    private String rawMaterialName;
    // 投料量
    private Double inventory;
}
