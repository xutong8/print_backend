package com.zju.vis.print_backend.dao;


import com.zju.vis.print_backend.compositekey.RelFilterCakeFilterCakeKey;
import com.zju.vis.print_backend.entity.RelFilterCakeFilterCake;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RelFilterCakeFilterCakeRepository extends JpaRepository<RelFilterCakeFilterCake, RelFilterCakeFilterCakeKey> {

}