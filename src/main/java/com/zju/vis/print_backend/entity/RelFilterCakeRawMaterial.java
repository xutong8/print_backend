package com.zju.vis.print_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zju.vis.print_backend.compositekey.RelFilterCakeRawMaterialKey;

import javax.persistence.*;

@Entity
@Table(name = "rel_rm_fc")
public class RelFilterCakeRawMaterial {
    @EmbeddedId
    RelFilterCakeRawMaterialKey id;

    @ManyToOne
    @MapsId("filterCakeId")
    @JoinColumn(name = "filter_cake_id")
    @JsonIgnore
    FilterCake filterCake;

    @ManyToOne
    @MapsId("rawMaterialId")
    @JoinColumn(name = "raw_material_id")
    @JsonIgnore
    RawMaterial rawMaterial;

    @Column(name = "inventory")
    Double inventory;

    public RelFilterCakeRawMaterialKey getId() {
        return id;
    }

    public void setId(RelFilterCakeRawMaterialKey id) {
        this.id = id;
    }

    public FilterCake getFilterCake() {
        return filterCake;
    }

    public void setFilterCake(FilterCake filterCake) {
        this.filterCake = filterCake;
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
