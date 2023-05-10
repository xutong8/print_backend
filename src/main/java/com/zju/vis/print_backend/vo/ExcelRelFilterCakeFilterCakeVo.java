package com.zju.vis.print_backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcelRelFilterCakeFilterCakeVo {
    // 滤饼名称
    private String filterCakeName;
    // 被使用的滤饼名称
    private String filterCakeNameUsed;
    // 投料量
    private Double inventory;
}
