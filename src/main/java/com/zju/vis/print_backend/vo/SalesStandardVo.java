package com.zju.vis.print_backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 用于返回销售量的类
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesStandardVo {
    // 商品名称
    private String productName;
    // 商品编号
    private String productIndex;
    // 起始时间
    private String startTime;
    // 结束时间
    private String endTime;
    // 销售量
    private Long number;
    // 利润
    private Double profit;
}
