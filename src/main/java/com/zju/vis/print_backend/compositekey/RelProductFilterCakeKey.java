package com.zju.vis.print_backend.compositekey;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class RelProductFilterCakeKey implements Serializable {
    @Column(name = "product_id")
    Long productId;

    @Column(name = "filter_cake_id")
    Long filterCakeId;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getFilterCakeId() {
        return filterCakeId;
    }

    public void setFilterCakeId(Long filterCakeId) {
        this.filterCakeId = filterCakeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RelProductFilterCakeKey that = (RelProductFilterCakeKey) o;
        return Objects.equals(productId, that.productId) && Objects.equals(filterCakeId, that.filterCakeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, filterCakeId);
    }
}
