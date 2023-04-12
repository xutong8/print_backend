package com.zju.vis.print_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "tb_raw_material")
public class RawMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "raw_material_id", nullable = false)
    private Long rawMaterialId;

    @Column(name = "raw_material_name", nullable = false)
    private String rawMaterialName;

    @Column(name = "raw_material_index", nullable = false)
    private String rawMaterialIndex;

    @Column(name = "raw_material_price", nullable = false)
    private Float rawMaterialPrice;

    @Column(name = "raw_material_conventional", nullable = false)
    private String rawMaterialConventional;

    @Column(name = "raw_material_specification", nullable = false)
    private String rawMaterialSpecification;

    // 原料产品对应反向映射
    //@JsonIgnore 保证不会重复调用JSON无限嵌套循环
    @ManyToMany
    @JoinTable(name = "rel_p_rm",
            joinColumns = {@JoinColumn(name = "raw_material_id")},
            inverseJoinColumns = {@JoinColumn(name="product_id")
            })
    @JsonIgnore
    private List<Product> productList=new ArrayList<>();



    public Long getRawMaterialId() {
        return rawMaterialId;
    }

    public void setRawMaterialId(Long rawMaterialId) {
        this.rawMaterialId = rawMaterialId;
    }

    public String getRawMaterialName() {
        return rawMaterialName;
    }

    public void setRawMaterialName(String rawMaterialName) {
        this.rawMaterialName = rawMaterialName;
    }

    public String getRawMaterialIndex() {
        return rawMaterialIndex;
    }

    public void setRawMaterialIndex(String rawMaterialIndex) {
        this.rawMaterialIndex = rawMaterialIndex;
    }

    public Float getRawMaterialPrice() {
        return rawMaterialPrice;
    }

    public void setRawMaterialPrice(Float rawMaterialPrice) {
        this.rawMaterialPrice = rawMaterialPrice;
    }

    public String getRawMaterialConventional() {
        return rawMaterialConventional;
    }

    public void setRawMaterialConventional(String rawMaterialConventional) {
        this.rawMaterialConventional = rawMaterialConventional;
    }

    public String getRawMaterialSpecification() {
        return rawMaterialSpecification;
    }

    public void setRawMaterialSpecification(String rawMaterialSpecification) {
        this.rawMaterialSpecification = rawMaterialSpecification;
    }

    public List<Product> getProductList() {
        return productList;
    }

    // public void setProductList(List<Product> productList) {
    //     this.productList = productList;
    // }

}
