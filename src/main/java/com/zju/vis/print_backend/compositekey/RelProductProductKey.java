package com.zju.vis.print_backend.compositekey;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
public class RelProductProductKey implements Serializable {
    @Column(name = "product_id")
    Long productId;

    @Column(name = "product_id_used")
    Long productIdUsed;
}
