package com.zju.vis.print_backend.service;

import com.zju.vis.print_backend.compositekey.RelProductFilterCakeKey;
import com.zju.vis.print_backend.dao.RelProductFilterCakeRepository;

import com.zju.vis.print_backend.entity.RelProductFilterCake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class RelProductFilterCakeService {
    @Resource
    private RelProductFilterCakeRepository relProductFilterCakeRepository;

    public RelProductFilterCake findRelProductFilterCakeById(RelProductFilterCakeKey id){
        return relProductFilterCakeRepository.findRelProductFilterCakeByIdEquals(id);
    }

    public RelProductFilterCake addRelProductFilterCake(RelProductFilterCake relProductFilterCake) {
        return relProductFilterCakeRepository.save(relProductFilterCake);
    }

    public void delete(RelProductFilterCake relProductFilterCake){
        relProductFilterCakeRepository.delete(relProductFilterCake);
    }

    public void deleteRelProductFilterCake(RelProductFilterCake relProductFilterCake){
        relProductFilterCakeRepository.delete(relProductFilterCake);
    }

    public RelProductFilterCakeRepository getRelProductFilterCakeRepository() {
        return relProductFilterCakeRepository;
    }

    public void setRelProductFilterCakeRepository(RelProductFilterCakeRepository relProductFilterCakeRepository) {
        this.relProductFilterCakeRepository = relProductFilterCakeRepository;
    }
}
