package com.zju.vis.print_backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// FilterCake 标准化形式类
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterCakeStandardVo {
    private Long filterCakeId;                  //滤饼id
    private String filterCakeName;              //滤饼名称
    private String filterCakeIndex;             //滤饼编号
    private String filterCakeColor;             //滤饼颜色
    private Float filterCakeProcessingCost;     //滤饼批处理价格
    private Integer filterCakeAccountingQuantity;//滤饼批次生产数量
    private Double filterCakeUnitPrice;         //滤饼单位价格
    private Integer filterCakePriceIncreasePercent;//滤饼价格增幅比例
    private String filterCakeSpecification;     //滤饼规格
    private String filterCakeRemarks;           //备注
    private List<RawMaterialSimpleVo> rawMaterialSimpleList;
    private List<FilterCakeSimpleVo> filterCakeSimpleList;
}
