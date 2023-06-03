package com.zju.vis.print_backend.entity;

import com.sun.istack.NotNull;
import com.zju.vis.print_backend.service.ProductSeriesService;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Resource;
import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
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

    @OneToMany(mappedBy = "product")
    private List<RelProductFilterCake> relProductFilterCakeList;

    @OneToMany(mappedBy = "product")
    private List<RelProductProduct> relProductProductListUser;

    @OneToMany(mappedBy = "productUsed")
    private List<RelProductProduct> relProductProductListUsed;
}
