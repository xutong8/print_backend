package com.zju.vis.print_backend.dao;

import com.zju.vis.print_backend.entity.ProductSeries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductSeriesRepository extends JpaRepository<ProductSeries, Long> {

    List<ProductSeries> findAll();
    List<ProductSeries> findProductSeriesByProductSeriesNameContaining(String productSeriesName);
    ProductSeries findProductSeriesByProductSeriesName(String productSeriesName);
    ProductSeries findProductSeriesByProductSeriesId(Long productSeriesId);
    ProductSeries findProductSeriesByProductSeriesIdIs(Long productSeriesId);

    @Query("SELECT ps.productSeriesId FROM ProductSeries ps WHERE ps.productSeriesName = :productSeriesName")
    Long findProductSeriesIdByProductSeriesName(@Param("productSeriesName") String productSeriesName);

    //根据productSeriesId删除记录
    void deleteByProductSeriesId(Long productSeriesId);
}
