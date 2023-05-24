package com.zju.vis.print_backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcelSalesVo {
    // 存货编码
    private String productIndex;

    // 开票日期
    private String date;

    // 客户简称
    private String customer;

    // 含税单价
    private Double unitPrice;

    // 数量
    private Long number;
}
