package com.zju.vis.print_backend.entity;

import com.sun.istack.NotNull;
import com.zju.vis.print_backend.service.ProductSeriesService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;
import javax.persistence.*;

@Entity
@Table(name = "tb_product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id", nullable = false)
    private Long productId;

    @NotNull
    @Column(name = "product_name", nullable = false)
    private String productName;

    @NotNull
    @Column(name = "product_index", nullable = false)
    private String productIndex;
    @NotNull
    @Column(name = "product_code", nullable = false)
    private String productCode;

    @Column(name = "product_color")
    private String productColor;

    @Column(name = "product_processing_cost", nullable = false)
    private Float productProcessingCost;

    @Column(name = "product_accounting_quantity", nullable = false)
    private Integer productAccountingQuantity;

    @Column(name = "product_series_id")
    private Long productSeriesId;

    // @Transient
    // private String productSeriesName;

    @Column(name = "product_factory_name", nullable = false)
    private String productFactoryName;

    @Column(name = "product_remarks")
    private String productRemarks;


    // 产品与原料多对多关系
    @ManyToMany
    @JoinTable(name = "rel_p_rm",
            joinColumns = {@JoinColumn(name = "product_id")},
            inverseJoinColumns = {@JoinColumn(name = "raw_material_id")
            })
    private List<RawMaterial> rawMaterialList = new ArrayList<>();

    // 产品与滤饼多对多关系
    @ManyToMany
    @JoinTable(name = "rel_p_fc",
            joinColumns = {@JoinColumn(name = "product_id")},
            inverseJoinColumns = {@JoinColumn(name = "filter_cake_id")
            })
    private List<FilterCake> filterCakeList = new ArrayList<>();


    //
    // @ManyToOne
    // private ProductSeries productSeries;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(productId, product.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductIndex() {
        return productIndex;
    }

    public void setProductIndex(String productIndex) {
        this.productIndex = productIndex;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductColor() {
        return productColor;
    }

    public void setProductColor(String productColor) {
        this.productColor = productColor;
    }

    public Float getProductProcessingCost() {
        return productProcessingCost;
    }

    public void setProductProcessingCost(Float productProcessingCost) {
        this.productProcessingCost = productProcessingCost;
    }

    public Integer getProductAccountingQuantity() {
        return productAccountingQuantity;
    }

    public void setProductAccountingQuantity(Integer productAccountingQuantity) {
        this.productAccountingQuantity = productAccountingQuantity;
    }

    public Long getProductSeriesId() {
        return productSeriesId;
    }

    public void setProductSeriesId(Long productSeriesId) {
        this.productSeriesId = productSeriesId;
    }

    // public String getProductSeriesName() {
    //   return
    // }

    public String getProductFactoryName() {
        return productFactoryName;
    }

    public void setProductFactoryName(String productFactoryName) {
        this.productFactoryName = productFactoryName;
    }

    public String getProductRemarks() {
        return productRemarks;
    }

    public void setProductRemarks(String productRemarks) {
        this.productRemarks = productRemarks;
    }

    public List<RawMaterial> getRawMaterialList() {
        return rawMaterialList;
    }

    public List<FilterCake> getFilterCakeList() {
        return filterCakeList;
    }

    // public ProductSeries getProductSeries() {
    //   return productSeries;
    // }
}
