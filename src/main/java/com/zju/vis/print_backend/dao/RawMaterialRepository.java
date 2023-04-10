package com.zju.vis.print_backend.dao;

import com.zju.vis.print_backend.entity.RawMaterial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RawMaterialRepository extends JpaRepository<RawMaterial, Long> {
    List<RawMaterial> findAll();
    RawMaterial findRawMaterialByRawMaterialId(Long rawMaterialId);
    List<RawMaterial> findAllByRawMaterialNameContaining(String materialNameContaining);
}
