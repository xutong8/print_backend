package com.zju.vis.print_backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// Product 结果封装
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductPackageVo {
    // 附加信息
    private Integer pageNo;
    private Integer pageSize;
    private Integer pageNum;
    private Integer total;

    // 返回的标准列表
    private List<ProductStandardVo> list;
}
