package com.zju.vis.print_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zju.vis.print_backend.compositekey.RelFilterCakeFilterCakeKey;
import lombok.*;

import javax.persistence.*;
@Data
@NoArgsConstructor
@AllArgsConstructor
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

}
