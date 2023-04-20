package com.zju.vis.print_backend.service;

import com.zju.vis.print_backend.dao.RelProductFilterCakeRepository;

import com.zju.vis.print_backend.entity.RelProductFilterCake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RelProductFilterCakeService {
    @Autowired
    private RelProductFilterCakeRepository relProductFilterCakeRepository;
    public RelProductFilterCake addRelProductFilterCake(RelProductFilterCake relProductFilterCake) {
        return relProductFilterCakeRepository.save(relProductFilterCake);
    }
}
