package com.zju.vis.print_backend.compositekey;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.sql.Date;
import java.util.Objects;

@Embeddable
public class RelDateRawMaterialKey implements Serializable {
    @Column(name = "raw_material_id")
    Long rawMaterialId;

    @Column(name = "raw_material_date")
    Date rawMaterialDate;

    public Long getRawMaterialId() {
        return rawMaterialId;
    }

    public void setRawMaterialId(Long rawMaterialId) {
        this.rawMaterialId = rawMaterialId;
    }

    public Date getRawMaterialDate() {
        return rawMaterialDate;
    }

    public void setRawMaterialDate(Date rawMaterialDate) {
        this.rawMaterialDate = rawMaterialDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RelDateRawMaterialKey that = (RelDateRawMaterialKey) o;
        return Objects.equals(rawMaterialId, that.rawMaterialId) && Objects.equals(rawMaterialDate, that.rawMaterialDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rawMaterialId, rawMaterialDate);
    }
}
