package com.zju.vis.print_backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 用于返回滤饼列表名
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterCakeNameVo {
    private Long filterCakeId;
    private String filterCakeName;
}
