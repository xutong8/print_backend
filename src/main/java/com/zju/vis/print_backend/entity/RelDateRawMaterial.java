package com.zju.vis.print_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zju.vis.print_backend.compositekey.RelDateRawMaterialKey;

import javax.persistence.*;

@Entity
@Table(name = "rel_date_rm")
public class RelDateRawMaterial {
    @EmbeddedId
    RelDateRawMaterialKey id;

    @ManyToOne
    @MapsId("rawMaterialId")
    @JoinColumn(name = "raw_material_id")
    @JsonIgnore
    RawMaterial rawMaterial;

    @Column(name = "price")
    Double price;

    public RelDateRawMaterialKey getId() {
        return id;
    }

    public void setId(RelDateRawMaterialKey id) {
        this.id = id;
    }

    public RawMaterial getRawMaterial() {
        return rawMaterial;
    }

    public void setRawMaterial(RawMaterial rawMaterial) {
        this.rawMaterial = rawMaterial;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
