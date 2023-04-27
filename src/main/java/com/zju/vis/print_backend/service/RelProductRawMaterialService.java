package com.zju.vis.print_backend.service;

import com.zju.vis.print_backend.compositekey.RelProductRawMaterialKey;
import com.zju.vis.print_backend.dao.RelProductRawMaterialRepository;
import com.zju.vis.print_backend.entity.RelProductRawMaterial;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class RelProductRawMaterialService {

    @Resource
    private RelProductRawMaterialRepository relProductRawMaterialRepository;

    public RelProductRawMaterial findRelProductRawMaterialById(RelProductRawMaterialKey id){
        return relProductRawMaterialRepository.findRelProductRawMaterialByIdEquals(id);
    }

    public RelProductRawMaterial addRelProductRawMaterial(RelProductRawMaterial relProductRawMaterial) {
        return relProductRawMaterialRepository.save(relProductRawMaterial);
    }

    public void deleteRelProductRawMaterial(RelProductRawMaterial relProductRawMaterial){
        relProductRawMaterialRepository.delete(relProductRawMaterial);
    }

    public RelProductRawMaterialRepository getRelProductRawMaterialRepository() {
        return relProductRawMaterialRepository;
    }

    public void setRelProductRawMaterialRepository(RelProductRawMaterialRepository relProductRawMaterialRepository) {
        this.relProductRawMaterialRepository = relProductRawMaterialRepository;
    }

    public void delete(RelProductRawMaterial relProductRawMaterial){
        relProductRawMaterialRepository.delete(relProductRawMaterial);
    }

    public void deleteById(RelProductRawMaterialKey id){
        relProductRawMaterialRepository.deleteById(id);
    }
}