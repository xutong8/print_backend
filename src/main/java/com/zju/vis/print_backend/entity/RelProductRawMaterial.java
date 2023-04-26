package com.zju.vis.print_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zju.vis.print_backend.compositekey.RelProductRawMaterialKey;

import javax.persistence.*;

@Entity
@Table(name = "rel_p_rm")
public class RelProductRawMaterial {
    @EmbeddedId
    RelProductRawMaterialKey id;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    @JsonIgnore
    Product product;

    @ManyToOne
    @MapsId("rawMaterialId")
    @JoinColumn(name = "raw_material_id")
    @JsonIgnore
    RawMaterial rawMaterial;

    @Column(name = "inventory")
    Double inventory;

    public RelProductRawMaterialKey getId() {
        return id;
    }

    public void setId(RelProductRawMaterialKey id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public RawMaterial getRawMaterial() {
        return rawMaterial;
    }

    public void setRawMaterial(RawMaterial rawMaterial) {
        this.rawMaterial = rawMaterial;
    }

    public Double getInventory() {
        return inventory;
    }

    public void setInventory(Double inventory) {
        this.inventory = inventory;
    }
}
