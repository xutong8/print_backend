package com.zju.vis.print_backend.dao;


import com.zju.vis.print_backend.entity.RelProductFilterCake;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RelProductFilterCakeRepository extends JpaRepository<RelProductFilterCake, Long> {

}