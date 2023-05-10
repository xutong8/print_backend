package com.zju.vis.print_backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcelRelFilterCakeRawMaterialVo {
    // 滤饼名称
    private String filterCakeName;
    // 原料名称
    private String rawMaterialName;
    // 投料量
    private Double inventory;
}
