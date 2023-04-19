package com.zju.vis.print_backend.compositekey;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
@Embeddable
public class RelFilterCakeRawMaterialKey implements Serializable {
    @Column(name = "filter_cake_id")
    Long filterCakeId;

    @Column(name = "raw_material_id")
    Long rawMaterialId;

    public Long getFilterCakeId() {
        return filterCakeId;
    }

    public void setFilterCakeId(Long filterCakeId) {
        this.filterCakeId = filterCakeId;
    }

    public Long getRawMaterialId() {
        return rawMaterialId;
    }

    public void setRawMaterialId(Long rawMaterialId) {
        this.rawMaterialId = rawMaterialId;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RelFilterCakeRawMaterialKey that = (RelFilterCakeRawMaterialKey) o;
        return Objects.equals(filterCakeId, that.filterCakeId) && Objects.equals(rawMaterialId, that.rawMaterialId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filterCakeId, rawMaterialId);
    }
}
