package com.zju.vis.print_backend.dao;

import com.zju.vis.print_backend.entity.FilterCake;
import com.zju.vis.print_backend.entity.RawMaterial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FilterCakeRepository extends JpaRepository<FilterCake, Long> {

    // 查单个
    FilterCake findFilterCakeByFilterCakeId(Long filterCakeId);
    FilterCake findFilterCakeByFilterCakeName(String filterCakeName);

    // 查多个
    List<FilterCake> findAll();
    List<FilterCake> findAllByFilterCakeNameContaining(String filterCakeName);
    List<FilterCake> findAllByFilterCakeIndexContaining(String filterCakeIndex);
    List<FilterCake> findAllByFilterCakeColorContaining(String filterCakeColor);
}
