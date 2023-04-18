package com.zju.vis.print_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zju.vis.print_backend.compositekey.RelProductFilterCakeKey;

import javax.persistence.*;

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

    public RelProductFilterCakeKey getId() {
        return id;
    }

    public void setId(RelProductFilterCakeKey id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public FilterCake getFilterCake() {
        return filterCake;
    }

    public void setFilterCake(FilterCake filterCake) {
        this.filterCake = filterCake;
    }

    public Double getInventory() {
        return inventory;
    }

    public void setInventory(Double inventory) {
        this.inventory = inventory;
    }
}
