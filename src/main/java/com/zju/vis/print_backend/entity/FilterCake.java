package com.zju.vis.print_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "tb_filter_cake")
public class FilterCake {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "filter_cake_id", nullable = false)
    private Long filterCakeId;

    @Column(name = "filter_cake_name", nullable = false)
    private String filterCakeName;

    @Column(name = "filter_cake_index", nullable = false)
    private String filterCakeIndex;

    @Column(name = "filter_cake_color", nullable = false)
    private String filterCakeColor;

    @Column(name = "filter_cake_processing_cost", nullable = false)
    private Float filterCakeProcessingCost;

    @Column(name = "filter_cake_accounting_quantity", nullable = false)
    private Integer filterCakeAccountingQuantity;

    @Column(name = "filter_cake_specification", nullable = false)
    private String filterCakeSpecification;

    @Column(name = "filter_cake_remarks", nullable = false)
    private String filterCakeRemarks;

    // 滤饼产品多对多反向映射
    @ManyToMany
    @JoinTable(name = "rel_p_fc",
            joinColumns = {@JoinColumn(name = "filter_cake_id")},
            inverseJoinColumns = {@JoinColumn(name = "product_id")
            })
    @JsonIgnore
    private List<Product> productList = new ArrayList<>();

    @OneToMany(mappedBy = "filterCake")
    List<RelProductFilterCake> relProductFilterCakeList;

    public Long getFilterCakeId() {
        return filterCakeId;
    }

    public void setFilterCakeId(Long filterCakeId) {
        this.filterCakeId = filterCakeId;
    }

    public String getFilterCakeName() {
        return filterCakeName;
    }

    public void setFilterCakeName(String filterCakeName) {
        this.filterCakeName = filterCakeName;
    }

    public String getFilterCakeIndex() {
        return filterCakeIndex;
    }

    public void setFilterCakeIndex(String filterCakeIndex) {
        this.filterCakeIndex = filterCakeIndex;
    }

    public String getFilterCakeColor() {
        return filterCakeColor;
    }

    public void setFilterCakeColor(String filterCakeColor) {
        this.filterCakeColor = filterCakeColor;
    }

    public Float getFilterCakeProcessingCost() {
        return filterCakeProcessingCost;
    }

    public void setFilterCakeProcessingCost(Float filterCakeProcessingCost) {
        this.filterCakeProcessingCost = filterCakeProcessingCost;
    }

    public Integer getFilterCakeAccountingQuantity() {
        return filterCakeAccountingQuantity;
    }

    public void setFilterCakeAccountingQuantity(Integer filterCakeAccountingQuantity) {
        this.filterCakeAccountingQuantity = filterCakeAccountingQuantity;
    }

    public String getFilterCakeSpecification() {
        return filterCakeSpecification;
    }

    public void setFilterCakeSpecification(String filterCakeSpecification) {
        this.filterCakeSpecification = filterCakeSpecification;
    }

    public String getFilterCakeRemarks() {
        return filterCakeRemarks;
    }

    public void setFilterCakeRemarks(String filterCakeRemarks) {
        this.filterCakeRemarks = filterCakeRemarks;
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    public List<RelProductFilterCake> getRelProductFilterCakeList() {
        return relProductFilterCakeList;
    }

    public void setRelProductFilterCakeList(List<RelProductFilterCake> relProductFilterCakeList) {
        this.relProductFilterCakeList = relProductFilterCakeList;
    }
}
