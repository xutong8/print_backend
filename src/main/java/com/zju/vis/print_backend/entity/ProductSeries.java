package com.zju.vis.print_backend.entity;

import javax.persistence.*;

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
}
