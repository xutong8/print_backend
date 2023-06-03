package com.zju.vis.print_backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 用于返回产品列表名
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSimpleVo {
    private Long productId;
    private String productName;
    private Double inventory;
}
