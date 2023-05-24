package com.zju.vis.print_backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// 打包返回的销售数据信息
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesPackageVo {
    // 销售总数
    private Long totalNum;
    // 总利润
    private Double totalProfit;
    // 打包的数据
    private List<SalesStandardVo> list;
}
