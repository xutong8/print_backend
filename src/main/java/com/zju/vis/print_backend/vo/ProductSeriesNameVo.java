package com.zju.vis.print_backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 返回系列名称
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSeriesNameVo {
    private Long productSeriesId;
    private String productSeriesName;
}
