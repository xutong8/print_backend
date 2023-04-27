package com.zju.vis.print_backend.compositekey;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.sql.Date;
import java.util.Objects;

@Embeddable
public class RelDateRawMaterialKay implements Serializable {
    @Column(name = "raw_material_id")
    Long rawMaterialId;

    @Column(name = "raw_material_date")
    Date rawMaterialData;

    public Long getRawMaterialId() {
        return rawMaterialId;
    }

    public void setRawMaterialId(Long rawMaterialId) {
        this.rawMaterialId = rawMaterialId;
    }

    public Date getRawMaterialData() {
        return rawMaterialData;
    }

    public void setRawMaterialData(Date rawMaterialData) {
        this.rawMaterialData = rawMaterialData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RelDateRawMaterialKay that = (RelDateRawMaterialKay) o;
        return Objects.equals(rawMaterialId, that.rawMaterialId) && Objects.equals(rawMaterialData, that.rawMaterialData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rawMaterialId, rawMaterialData);
    }
}
