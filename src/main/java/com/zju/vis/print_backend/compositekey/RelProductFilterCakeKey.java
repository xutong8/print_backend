package com.zju.vis.print_backend.compositekey;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Data
@Embeddable
public class RelProductFilterCakeKey implements Serializable {
    @Column(name = "product_id")
    Long productId;

    @Column(name = "filter_cake_id")
    Long filterCakeId;

}
