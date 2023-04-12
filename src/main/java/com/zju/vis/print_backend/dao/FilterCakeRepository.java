package com.zju.vis.print_backend.dao;

import com.zju.vis.print_backend.entity.FilterCake;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FilterCakeRepository extends JpaRepository<FilterCake, Long> {
    List<FilterCake> findAll();
    List<FilterCake> findAllByFilterCakeNameContaining(String filterCakeName);
//    void deleteByFilterCakeId(Long filterCakeId);

}
