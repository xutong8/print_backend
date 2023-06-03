package com.zju.vis.print_backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcelRelProductProductVo {
    // 商品名称
    private String productName;
    // 被使用的商品名称
    private String productNameUsed;
    // 投料量
    private Double inventory;
}
