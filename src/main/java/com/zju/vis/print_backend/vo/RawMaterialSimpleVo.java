package com.zju.vis.print_backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 用于简单原料简单信息
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RawMaterialSimpleVo {
    private Long rawMaterialId;
    private String rawMaterialName;
    private Double inventory;
}
