package com.zju.vis.print_backend.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.zju.vis.print_backend.entity.RelProductRawMaterial;
import com.zju.vis.print_backend.compositekey.RelProductRawMaterialKey;

@Repository
public interface RelProductRawMaterialRepository extends JpaRepository<RelProductRawMaterial, RelProductRawMaterialKey> {
}
