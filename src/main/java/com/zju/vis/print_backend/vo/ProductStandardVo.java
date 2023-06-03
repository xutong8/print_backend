package com.zju.vis.print_backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// Product 标准化形式类 (前端单个节点最终结果
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductStandardVo {
    private Long productId;
    private String productName;
    private String productIndex;
    private String productCode;
    private String productColor;
    private Double productUnitPrice;
    private Integer productPriceIncreasePercent;
    private String productSeriesName;
    private String productFactoryName;
    private String productRemarks;

    private Float productProcessingCost;
    private Integer productAccountingQuantity;
    private List<RawMaterialSimpleVo> rawMaterialSimpleList;
    private List<FilterCakeSimpleVo> filterCakeSimpleList;
    private List<ProductSimpleVo> productSimpleList;
}
