package com.zju.vis.print_backend.dao;

import com.zju.vis.print_backend.entity.ProductSeries;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductSeriesRepository extends JpaRepository<ProductSeries, Long> {
    ProductSeries findProductSeriesByProductSeriesId(Long productSeriesId);
    List<ProductSeries> findAll();
    List<ProductSeries> findProductSeriesByProductSeriesNameContaining(String productSeriesName);
    ProductSeries findProductSeriesByProductSeriesName(String productSeriesName);

    //根据productSeriesId删除记录
    void deleteByProductSeriesId(Long productSeriesId);
}
