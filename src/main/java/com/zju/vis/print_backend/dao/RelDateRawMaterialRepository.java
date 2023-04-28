package com.zju.vis.print_backend.dao;

import com.zju.vis.print_backend.compositekey.RelDateRawMaterialKey;
import com.zju.vis.print_backend.entity.RelDateRawMaterial;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RelDateRawMaterialRepository extends JpaRepository<RelDateRawMaterial, RelDateRawMaterialKey> {
    void deleteByIdEquals(RelDateRawMaterialKey id);
}
