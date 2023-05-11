package com.zju.vis.print_backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 用于返回滤饼简单信息
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterCakeSimpleVo {
    private Long filterCakeId;
    private String filterCakeName;
    private Double inventory;
}
