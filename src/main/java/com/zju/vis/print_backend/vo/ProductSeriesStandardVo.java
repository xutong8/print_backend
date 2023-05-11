package com.zju.vis.print_backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// 与前端交互的标准化类形式
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSeriesStandardVo {
    private Long productSeriesId;
    private String productSeriesName;
    private String productSeriesFunction;
    private List<EntityNameVo> productSimpleList;
}
