package com.zju.vis.print_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "tb_product_series")
public class ProductSeries {

    @Id
    @GeneratedValue
    @Column(name = "product_series_id", nullable = false)
    private Long productSeriesId ;

    @Column(name = "product_series_name", nullable = false)
    private String productSeriesName;

    @Column(name = "product_series_function", nullable = false)
    private String productSeriesFunction;

    // 产品列表
    @OneToMany
    @JoinColumn(name = "product_series_id")
    private List<Product> productList;

    public Long getProductSeriesId() {
        return productSeriesId;
    }

    public void setProductSeriesId(Long productSeriesId) {
        this.productSeriesId = productSeriesId;
    }

    public String getProductSeriesName() {
        return productSeriesName;
    }

    public void setProductSeriesName(String productSeriesName) {
        this.productSeriesName = productSeriesName;
    }

    public String getProductSeriesFunction() {
        return productSeriesFunction;
    }

    public void setProductSeriesFunction(String productSeriesFunction) {
        this.productSeriesFunction = productSeriesFunction;
    }

    public List<Product> getProductList() {
        return productList;
    }
}
