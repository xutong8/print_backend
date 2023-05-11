package com.zju.vis.print_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
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
    private Double rawMaterialPrice;

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

    @ManyToMany
    @JoinTable(name = "rel_rm_fc",
            joinColumns = {@JoinColumn(name = "raw_material_id")},
            inverseJoinColumns = {@JoinColumn(name="filter_cake_id")
            })
    @JsonIgnore
    private List<FilterCake> filterCakeList=new ArrayList<>();


    @OneToMany(mappedBy = "rawMaterial")
    private List<RelProductRawMaterial> relProductRawMaterialList;

    @OneToMany(mappedBy = "rawMaterial")
    private List<RelFilterCakeRawMaterial> relFilterCakeRawMaterialList;

    @OneToMany(mappedBy = "rawMaterial")
    private List<RelDateRawMaterial> relDateRawMaterialList;

}
