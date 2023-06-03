package com.zju.vis.print_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zju.vis.print_backend.compositekey.RelProductProductKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rel_p_p")
public class RelProductProduct {
    @EmbeddedId
    RelProductProductKey id;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    @JsonIgnore
    Product product;

    @ManyToOne
    @MapsId("productIdUsed")
    @JoinColumn(name = "product_id_used")
    @JsonIgnore
    Product productUsed;

    @Column(name = "inventory")
    Double inventory;
}
