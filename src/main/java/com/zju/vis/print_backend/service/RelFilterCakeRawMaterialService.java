package com.zju.vis.print_backend.service;

import com.zju.vis.print_backend.dao.RelFilterCakeRawMaterialRepository;
import com.zju.vis.print_backend.entity.RelFilterCakeRawMaterial;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RelFilterCakeRawMaterialService {
    @Autowired
    private RelFilterCakeRawMaterialRepository relFilterCakeRawMaterialRepository;
    public RelFilterCakeRawMaterial addRelFilterCakeRawMaterial(RelFilterCakeRawMaterial relFilterCakeRawMaterial) {
        return relFilterCakeRawMaterialRepository.save(relFilterCakeRawMaterial);
    }

    public RelFilterCakeRawMaterialRepository getRelFilterCakeRawMaterialRepository() {
        return relFilterCakeRawMaterialRepository;
    }

    public void setRelFilterCakeRawMaterialRepository(RelFilterCakeRawMaterialRepository relFilterCakeRawMaterialRepository) {
        this.relFilterCakeRawMaterialRepository = relFilterCakeRawMaterialRepository;
    }
}
