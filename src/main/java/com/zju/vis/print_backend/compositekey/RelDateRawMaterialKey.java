package com.zju.vis.print_backend.compositekey;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.sql.Date;
import java.util.Objects;

@Data
@Embeddable
public class RelDateRawMaterialKey implements Serializable {
    @Column(name = "raw_material_id")
    Long rawMaterialId;

    @Column(name = "raw_material_date")
    Date rawMaterialDate;

}
