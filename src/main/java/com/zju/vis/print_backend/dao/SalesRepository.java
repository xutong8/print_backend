package com.zju.vis.print_backend.dao;

import com.zju.vis.print_backend.entity.Sales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalesRepository extends JpaRepository<Sales,Long> {
    List<Sales> findAllByProductIndexEquals(String productIndex);
}
