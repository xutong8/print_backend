package com.zju.vis.print_backend.dao;

import com.zju.vis.print_backend.compositekey.RelDateRawMaterialKey;
import com.zju.vis.print_backend.entity.RelDateRawMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RelDateRawMaterialRepository extends JpaRepository<RelDateRawMaterial, RelDateRawMaterialKey> {

}
