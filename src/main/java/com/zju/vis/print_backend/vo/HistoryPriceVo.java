package com.zju.vis.print_backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

// 通用日期价格类(static 用于外部调用
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoryPriceVo {
    private Date date;
    private Double price;
}
