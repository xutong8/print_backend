package com.zju.vis.print_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zju.vis.print_backend.compositekey.RelFilterCakeRawMaterialKey;
import lombok.*;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rel_rm_fc")
public class RelFilterCakeRawMaterial {
    @EmbeddedId
    RelFilterCakeRawMaterialKey id;

    @ManyToOne
    @MapsId("filterCakeId")
    @JoinColumn(name = "filter_cake_id")
    @JsonIgnore
    FilterCake filterCake;

    @ManyToOne
    @MapsId("rawMaterialId")
    @JoinColumn(name = "raw_material_id")
    @JsonIgnore
    RawMaterial rawMaterial;

    @Column(name = "inventory")
    Double inventory;

}
