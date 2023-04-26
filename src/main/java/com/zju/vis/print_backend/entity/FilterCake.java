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

    @ManyToMany
    @JoinTable(name = "rel_fc_fc",
            joinColumns = {@JoinColumn(name = "filter_cake_id")},
            inverseJoinColumns = {@JoinColumn(name = "filter_cake_id_used")
            })
    @JsonIgnore
    private List<FilterCake> filterCakeList = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "rel_rm_fc",
            joinColumns = {@JoinColumn(name = "filter_cake_id")},
            inverseJoinColumns = {@JoinColumn(name = "raw_material_id")
            })
    @JsonIgnore
    private List<RawMaterial> rawMaterialList = new ArrayList<>();

    public List<RawMaterial> getRawMaterialList() {
        return rawMaterialList;
    }

    public void setRawMaterialList(List<RawMaterial> rawMaterialList) {
        this.rawMaterialList = rawMaterialList;
    }

    public List<FilterCake> getFilterCakeList() {
        return filterCakeList;
    }

    public void setFilterCakeList(List<FilterCake> filterCakeList) {
        this.filterCakeList = filterCakeList;
    }

    // 关联表

    @OneToMany(mappedBy = "filterCake")
    List<RelProductFilterCake> relProductFilterCakeList;

    // 这里改成被使用的滤饼对应列表，因为获取投料量的时候都是根据被使用的一方出发的
    @OneToMany(mappedBy = "filterCakeUsed")
    List<RelFilterCakeFilterCake> relFilterCakeFilterCakeList;

    @OneToMany(mappedBy = "filterCake")
    List<RelFilterCakeRawMaterial> relFilterCakeRawMaterialList;

    public List<RelFilterCakeFilterCake> getRelFilterCakeFilterCakeList() {
        return relFilterCakeFilterCakeList;
    }

    public void setRelFilterCakeFilterCakeList(List<RelFilterCakeFilterCake> relFilterCakeFilterCakeList) {
        this.relFilterCakeFilterCakeList = relFilterCakeFilterCakeList;
    }

    public List<RelFilterCakeRawMaterial> getRelFilterCakeRawMaterialList() {
        return relFilterCakeRawMaterialList;
    }

    public void setRelFilterCakeRawMaterialList(List<RelFilterCakeRawMaterial> relFilterCakeRawMaterialList) {
        this.relFilterCakeRawMaterialList = relFilterCakeRawMaterialList;
    }

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
