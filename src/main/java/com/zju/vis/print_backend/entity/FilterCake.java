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

    // 关联表

    @OneToMany(mappedBy = "filterCake")
    private List<RelProductFilterCake> relProductFilterCakeList;

    // 这里改成被使用的滤饼对应列表，因为获取投料量的时候都是根据被使用的一方出发的
    @OneToMany(mappedBy = "filterCakeUsed")
    private List<RelFilterCakeFilterCake> relFilterCakeFilterCakeListUsed;

    @OneToMany(mappedBy = "filterCake")
    private List<RelFilterCakeFilterCake> relFilterCakeFilterCakeListUser;

    @OneToMany(mappedBy = "filterCake")
    private List<RelFilterCakeRawMaterial> relFilterCakeRawMaterialList;
}
