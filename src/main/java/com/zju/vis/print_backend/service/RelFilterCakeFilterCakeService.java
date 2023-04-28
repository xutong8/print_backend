package com.zju.vis.print_backend.service;

import com.zju.vis.print_backend.dao.RelFilterCakeFilterCakeRepository;
import com.zju.vis.print_backend.entity.RelFilterCakeFilterCake;
import com.zju.vis.print_backend.entity.RelFilterCakeRawMaterial;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class RelFilterCakeFilterCakeService {
    @Resource
    private RelFilterCakeFilterCakeRepository relFilterCakeFilterCakeRepository;
    public RelFilterCakeFilterCake addRelFilterCakeFilterCake(RelFilterCakeFilterCake relFilterCakeFilterCake) {
        return relFilterCakeFilterCakeRepository.save(relFilterCakeFilterCake);
    }

    public RelFilterCakeFilterCakeRepository getRelFilterCakeFilterCakeRepository() {
        return relFilterCakeFilterCakeRepository;
    }

    public void setRelFilterCakeFilterCakeRepository(RelFilterCakeFilterCakeRepository relFilterCakeFilterCakeRepository) {
        this.relFilterCakeFilterCakeRepository = relFilterCakeFilterCakeRepository;
    }

    public void delete(RelFilterCakeFilterCake relFilterCakeFilterCake){
        relFilterCakeFilterCakeRepository.delete(relFilterCakeFilterCake);
    }
}
