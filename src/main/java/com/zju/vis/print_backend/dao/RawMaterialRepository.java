package com.zju.vis.print_backend.dao;

import com.zju.vis.print_backend.entity.RawMaterial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RawMaterialRepository extends JpaRepository<RawMaterial, Long> {

    // 查单个
    RawMaterial findRawMaterialByRawMaterialId(Long rawMaterialId);
    RawMaterial findRawMaterialByRawMaterialName(String rawMaterialName);
    RawMaterial findRawMaterialByRawMaterialIndex(String rawMaterialIndex);

    // 查多个
    List<RawMaterial> findAll();
    List<RawMaterial> findAllByRawMaterialNameContaining(String rawMaterialName);
    List<RawMaterial> findAllByRawMaterialIndexContaining(String rawMaterialIndex);

    void deleteByRawMaterialId(Long rawMaterialId);
}
