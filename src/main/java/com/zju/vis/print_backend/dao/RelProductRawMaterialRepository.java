package com.zju.vis.print_backend.dao;

import com.zju.vis.print_backend.entity.Product;
import com.zju.vis.print_backend.entity.RawMaterial;
import com.zju.vis.print_backend.entity.RelProductFilterCake;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.zju.vis.print_backend.entity.RelProductRawMaterial;
import com.zju.vis.print_backend.compositekey.RelProductRawMaterialKey;

@Repository
public interface RelProductRawMaterialRepository extends JpaRepository<RelProductRawMaterial, RelProductRawMaterialKey> {
    RelProductRawMaterial findRelProductRawMaterialByProductAndRawMaterial(Product product, RawMaterial rawMaterial);
    RelProductRawMaterial findRelProductRawMaterialByIdEquals(RelProductRawMaterialKey id);

    void deleteByIdEquals(RelProductRawMaterialKey id);
}
