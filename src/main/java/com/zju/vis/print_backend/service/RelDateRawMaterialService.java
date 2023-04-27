package com.zju.vis.print_backend.service;

import com.zju.vis.print_backend.dao.RelDateRawMaterialRepository;
import com.zju.vis.print_backend.entity.RelDateRawMaterial;

import javax.annotation.Resource;

public class RelDateRawMaterialService {
    @Resource
    private RelDateRawMaterialRepository rawMaterialRepository;

    public RelDateRawMaterial addRelDateRawMaterial(RelDateRawMaterial relDateRawMaterial){
        return rawMaterialRepository.save(relDateRawMaterial);
    }


}
