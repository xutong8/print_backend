package com.zju.vis.print_backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;
import java.util.Comparator;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_sales")
public class Sales {
    // 开票id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sales_id")
    private Long salesId;

    // 存货编码
    @Column(name = "product_index")
    private String productIndex;

    // 开票日期
    @Column(name = "date")
    private Date date;

    // 客户简称
    @Column(name = "customer")
    private String customer;

    // 含税单价
    @Column(name = "unit_price")
    private Double unitPrice;

    // 数量
    @Column(name = "number")
    private Long number;

}
