package com.zju.vis.print_backend.compositekey;

import com.zju.vis.print_backend.entity.RelProductRawMaterial;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Data
@Embeddable
public class RelProductRawMaterialKey implements Serializable {
    @Column(name = "product_id")
    Long productId;

    @Column(name = "raw_material_id")
    Long rawMaterialId;
}
