package com.zju.vis.print_backend.service;

import com.zju.vis.print_backend.dao.RelFilterCakeFilterCakeRepository;
import com.zju.vis.print_backend.entity.RelFilterCakeFilterCake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RelFilterCakeFilterCakeService {
    @Autowired
    private RelFilterCakeFilterCakeRepository relFilterCakeFilterCakeRepository;
    public RelFilterCakeFilterCake addRelFilterCakeFilterCake(RelFilterCakeFilterCake relFilterCakeFilterCake) {
        return relFilterCakeFilterCakeRepository.save(relFilterCakeFilterCake);
    }
}
