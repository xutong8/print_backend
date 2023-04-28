package com.zju.vis.print_backend.service;

import com.zju.vis.print_backend.compositekey.RelDateRawMaterialKey;
import com.zju.vis.print_backend.dao.RelDateRawMaterialRepository;
import com.zju.vis.print_backend.entity.RelDateRawMaterial;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class RelDateRawMaterialService {
    @Resource
    private RelDateRawMaterialRepository relDateRawMaterialRepository;

    public RelDateRawMaterial addRelDateRawMaterial(RelDateRawMaterial relDateRawMaterial){
        return relDateRawMaterialRepository.save(relDateRawMaterial);
    }

    public void delete(RelDateRawMaterial relDateRawMaterial){
        relDateRawMaterialRepository.delete(relDateRawMaterial);
    }

    public void deleteById(RelDateRawMaterialKey id){
        relDateRawMaterialRepository.deleteById(id);
    }
}
