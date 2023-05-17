package com.zju.vis.print_backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcelFilterCakeVo {
    // 滤饼名称
    private String filterCakeName;
    // 滤饼编号
    private String filterCakeIndex;
    // 滤饼颜色
    private String filterCakeColor;
    // 批次核算数量
    private Integer filterCakeAccountingQuantity;
    // 批次生产成本
    private Float filterCakeProcessingCost;
    // 滤饼规格
    private String filterCakeSpecification;
    // 备注
    private String filterCakeRemarks;
}
