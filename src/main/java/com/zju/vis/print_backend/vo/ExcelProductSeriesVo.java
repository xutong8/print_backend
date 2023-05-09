package com.zju.vis.print_backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcelProductSeriesVo {
    // 产品系列
    private String productSeriesName;
    // 应用范围
    private String productSeriesFunction;
}
