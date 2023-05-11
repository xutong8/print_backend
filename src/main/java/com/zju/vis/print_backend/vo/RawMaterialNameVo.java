package com.zju.vis.print_backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 用于返回原料列表名
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RawMaterialNameVo {
    private Long rawMaterialId;
    private String rawMaterialName;
}
