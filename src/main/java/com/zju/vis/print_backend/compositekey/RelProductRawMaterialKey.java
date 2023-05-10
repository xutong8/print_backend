package com.zju.vis.print_backend.compositekey;

import com.zju.vis.print_backend.entity.RelProductRawMaterial;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class RelProductRawMaterialKey implements Serializable {
    @Column(name = "product_id")
    Long productId;

    @Column(name = "raw_material_id")
    Long rawMaterialId;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
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
        RelProductRawMaterialKey that = (RelProductRawMaterialKey) o;
        return Objects.equals(productId, that.productId) && Objects.equals(rawMaterialId, that.rawMaterialId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, rawMaterialId);
    }
}
