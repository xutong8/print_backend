package com.zju.vis.print_backend.dao;

import com.zju.vis.print_backend.compositekey.RelProductProductKey;
import com.zju.vis.print_backend.entity.RelProductProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RelProductProductRepository extends JpaRepository<RelProductProduct, RelProductProductKey> {
}
