package com.zju.vis.print_backend.dao;


import com.zju.vis.print_backend.compositekey.RelProductFilterCakeKey;
import com.zju.vis.print_backend.entity.FilterCake;
import com.zju.vis.print_backend.entity.Product;
import com.zju.vis.print_backend.entity.RelProductFilterCake;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RelProductFilterCakeRepository extends JpaRepository<RelProductFilterCake, RelProductFilterCakeKey> {

    // 查单个
    RelProductFilterCake findRelProductFilterCakeByProductAndFilterCake(Product product, FilterCake filterCake);
    RelProductFilterCake findRelProductFilterCakeByIdEquals(RelProductFilterCakeKey id);
}