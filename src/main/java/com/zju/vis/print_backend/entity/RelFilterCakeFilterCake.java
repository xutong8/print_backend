package com.zju.vis.print_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zju.vis.print_backend.compositekey.RelFilterCakeFilterCakeKey;

import javax.persistence.*;
@Entity
@Table(name = "rel_fc_fc")
public class RelFilterCakeFilterCake {
    @EmbeddedId
    RelFilterCakeFilterCakeKey id;

    @ManyToOne
    @MapsId("filterCakeId")
    @JoinColumn(name = "filter_cake_id")
    @JsonIgnore
    FilterCake filterCake;
    @ManyToOne
    @MapsId("filterCakeIdUsed")
    @JoinColumn(name = "filter_cake_id_used")
    @JsonIgnore
    FilterCake filterCakeUsed;
    @Column(name = "inventory")
    Double inventory;

    public RelFilterCakeFilterCakeKey getId() {
        return id;
    }

    public void setId(RelFilterCakeFilterCakeKey id) {
        this.id = id;
    }

    public FilterCake getFilterCake() {
        return filterCake;
    }

    public void setFilterCake(FilterCake filterCake) {
        this.filterCake = filterCake;
    }

    public FilterCake getFilterCakeUsed() {
        return filterCakeUsed;
    }

    public void setFilterCakeUsed(FilterCake filterCakeUsed) {
        this.filterCakeUsed = filterCakeUsed;
    }

    public Double getInventory() {
        return inventory;
    }

    public void setInventory(Double inventory) {
        this.inventory = inventory;
    }
}
