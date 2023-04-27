package com.zju.vis.print_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zju.vis.print_backend.compositekey.RelDateRawMaterialKay;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "rel_date_rm")
public class RelDateRawMaterial {
    @EmbeddedId
    RelDateRawMaterialKay id;

    @ManyToOne
    @MapsId("rawMaterialId")
    @JoinColumn(name = "raw_material_id")
    @JsonIgnore
    RawMaterial rawMaterial;

    @Column(name = "price")
    Float price;

    public RelDateRawMaterialKay getId() {
        return id;
    }

    public void setId(RelDateRawMaterialKay id) {
        this.id = id;
    }

    public RawMaterial getRawMaterial() {
        return rawMaterial;
    }

    public void setRawMaterial(RawMaterial rawMaterial) {
        this.rawMaterial = rawMaterial;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }
}
