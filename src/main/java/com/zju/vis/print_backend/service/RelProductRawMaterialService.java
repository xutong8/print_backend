package com.zju.vis.print_backend.service;

import com.zju.vis.print_backend.dao.RelProductRawMaterialRepository;
import com.zju.vis.print_backend.entity.RelProductRawMaterial;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RelProductRawMaterialService {

    @Autowired
    private RelProductRawMaterialRepository relProductRawMaterialRepository;

    public RelProductRawMaterial addRelProductRawMaterial(RelProductRawMaterial relProductRawMaterial) {
        return relProductRawMaterialRepository.save(relProductRawMaterial);
    }

    public RelProductRawMaterialRepository getRelProductRawMaterialRepository() {
        return relProductRawMaterialRepository;
    }

    public void setRelProductRawMaterialRepository(RelProductRawMaterialRepository relProductRawMaterialRepository) {
        this.relProductRawMaterialRepository = relProductRawMaterialRepository;
    }
}