package com.zju.vis.print_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zju.vis.print_backend.compositekey.RelProductFilterCakeKey;
import lombok.*;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rel_p_fc")
public class RelProductFilterCake {
    @EmbeddedId
    RelProductFilterCakeKey id;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    @JsonIgnore
    Product product;

    @ManyToOne
    @MapsId("filterCakeId")
    @JoinColumn(name = "filter_cake_id")
    @JsonIgnore
    FilterCake filterCake;

    @Column(name = "inventory")
    Double inventory;

}