package com.zju.vis.print_backend.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcelRelDateRawMaterialVo {
    // 原料名称
    private String rawMaterialName;
    // 日期
    // @ExcelProperty(converter = DateConvert.class)
    // @DateTimeFormat("yyyy.MM.dd")
    private String rawMaterialDate;
    // 当期价格
    private Double price;
}
