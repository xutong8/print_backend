package com.zju.vis.print_backend.dao;


import com.zju.vis.print_backend.compositekey.RelFilterCakeRawMaterialKey;
import com.zju.vis.print_backend.entity.RelFilterCakeRawMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RelFilterCakeRawMaterialRepository extends JpaRepository<RelFilterCakeRawMaterial, RelFilterCakeRawMaterialKey> {
    void deleteByIdEquals(RelFilterCakeRawMaterialKey id);
}