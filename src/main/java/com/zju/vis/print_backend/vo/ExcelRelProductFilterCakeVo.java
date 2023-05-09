package com.zju.vis.print_backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcelRelProductFilterCakeVo {
    // 商品名称
    private String productName;
    // 滤饼名称
    private String filterCakeName;
    // 投料量
    private Double inventory;
}
