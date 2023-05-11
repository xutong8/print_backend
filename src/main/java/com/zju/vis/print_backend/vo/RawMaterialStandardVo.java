package com.zju.vis.print_backend.vo;

import com.zju.vis.print_backend.Utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// RawMaterial 标准化形式类
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RawMaterialStandardVo {
    private Long rawMaterialId;
    private String rawMaterialName;
    private String rawMaterialIndex;
    private Double rawMaterialUnitPrice;
    private Integer rawMaterialIncreasePercent;
    private String rawMaterialConventional;
    private String rawMaterialSpecification;
    private List<Utils.HistoryPrice> rawMaterialHistoryPrice;
}
