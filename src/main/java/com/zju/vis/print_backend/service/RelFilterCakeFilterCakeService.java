package com.zju.vis.print_backend.service;

import com.zju.vis.print_backend.Utils.CollectionUtil;
import com.zju.vis.print_backend.Utils.FileUtil;
import com.zju.vis.print_backend.Utils.ResultVoUtil;
import com.zju.vis.print_backend.compositekey.RelFilterCakeFilterCakeKey;
import com.zju.vis.print_backend.dao.FilterCakeRepository;
import com.zju.vis.print_backend.dao.ProductRepository;
import com.zju.vis.print_backend.dao.RelFilterCakeFilterCakeRepository;
import com.zju.vis.print_backend.entity.FilterCake;
import com.zju.vis.print_backend.entity.RelFilterCakeFilterCake;
import com.zju.vis.print_backend.entity.RelFilterCakeRawMaterial;
import com.zju.vis.print_backend.vo.ExcelRelFilterCakeFilterCakeVo;
import com.zju.vis.print_backend.vo.ExcelRelFilterCakeFilterCakeWriteVo;
import com.zju.vis.print_backend.vo.ExcelRelProductRawMaterialWriteVo;
import com.zju.vis.print_backend.vo.ResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class RelFilterCakeFilterCakeService {
    @Resource
    private RelFilterCakeFilterCakeRepository relFilterCakeFilterCakeRepository;

    @Resource
    private FileService fileService;

    @Resource
    private FilterCakeRepository filterCakeRepository;

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

    // 导入文件
    //-------------------------------------------------------------------------
    public ResultVo importRelFilterCakeFilterCakeExcelAndPersistence(MultipartFile file){
        ResultVo<List<ExcelRelFilterCakeFilterCakeVo>> importResult = fileService.importEntityExcel(file, ExcelRelFilterCakeFilterCakeVo.class);
        if(!importResult.checkSuccess()){
            log.error(importResult.getMsg());
            return importResult;
        }
        List<ExcelRelFilterCakeFilterCakeVo> excelRelFilterCakeFilterCakeVos = importResult.getData();
        for(ExcelRelFilterCakeFilterCakeVo excelRelFilterCakeFilterCakeVo: excelRelFilterCakeFilterCakeVos){
            // excel信息转化为关系表实体对象，注意这里有可能出现null对象（不匹配的情况）
            RelFilterCakeFilterCake relFilterCakeFilterCake = transExcelToEntity(excelRelFilterCakeFilterCakeVo);
            if(relFilterCakeFilterCake != null){
                // 如果已有数据则会更新，没有则添加
                addRelFilterCakeFilterCake(relFilterCakeFilterCake);
            }
        }
        return ResultVoUtil.success(excelRelFilterCakeFilterCakeVos);
    }

    public RelFilterCakeFilterCake transExcelToEntity(ExcelRelFilterCakeFilterCakeVo excelRelFilterCakeFilterCakeVo){
        RelFilterCakeFilterCake relFilterCakeFilterCake = new RelFilterCakeFilterCake();
        FilterCake filterCake = filterCakeRepository.findFilterCakeByFilterCakeName(excelRelFilterCakeFilterCakeVo.getFilterCakeName());
        FilterCake filterCakeUsed = filterCakeRepository.findFilterCakeByFilterCakeName(excelRelFilterCakeFilterCakeVo.getFilterCakeNameUsed());
        // 与数据库当前数据有不匹配则返回空
        if(filterCake == null || filterCakeUsed == null){
            return null;
        }
        RelFilterCakeFilterCakeKey id = new RelFilterCakeFilterCakeKey();
        id.setFilterCakeId(filterCake.getFilterCakeId());
        id.setFilterCakeIdUsed(filterCakeUsed.getFilterCakeId());
        relFilterCakeFilterCake.setId(id);
        relFilterCakeFilterCake.setFilterCake(filterCake);
        relFilterCakeFilterCake.setFilterCakeUsed(filterCakeUsed);
        relFilterCakeFilterCake.setInventory(excelRelFilterCakeFilterCakeVo.getInventory());
        return relFilterCakeFilterCake;
    }

    // 导出文件
    //-------------------------------------------------------------------------
    public ResultVo<String> exportRelFilterCakeFilterCakeExcel(HttpServletResponse response){
        // 1.根据查询条件获取结果集
        List<ExcelRelFilterCakeFilterCakeWriteVo> excelRelFilterCakeFilterCakeWriteVos = getExcelRelFilterCakeFilterCakeWriteVoListByCondition();
        if (CollectionUtil.isEmpty(excelRelFilterCakeFilterCakeWriteVos)) {
            log.info("【导出Excel文件】要导出的数据为空，无法导出！");
            return ResultVoUtil.success("数据为空");
        }
        // 2.获取要下载Excel文件的路径
        ResultVo<String> resultVo = fileService.getDownLoadPath(ExcelRelFilterCakeFilterCakeWriteVo.class,excelRelFilterCakeFilterCakeWriteVos);
        if (!resultVo.checkSuccess()) {
            log.error("【导出Excel文件】获取要下载Excel文件的路径失败");
            return resultVo;
        }
        // 3.下载Excel文件
        String fileDownLoadPath = resultVo.getData();
        ResultVo<String> downLoadResultVo = fileService.downloadFile(fileDownLoadPath, response);
        if (null != downLoadResultVo && !downLoadResultVo.checkSuccess()) {
            log.error("【导出Excel文件】下载文件失败");
            return downLoadResultVo;
        }
        // 4.删除临时文件
        boolean deleteFile = FileUtil.deleteFile(new File(fileDownLoadPath));
        if (!deleteFile) {
            log.error("【导入Excel文件】删除临时文件失败，临时文件路径为{}", fileDownLoadPath);
            return ResultVoUtil.error("删除临时文件失败");
        }
        log.info("【导入Excel文件】删除临时文件成功，临时文件路径为：{}", fileDownLoadPath);
        return null;
    }

    public List<ExcelRelFilterCakeFilterCakeWriteVo> getExcelRelFilterCakeFilterCakeWriteVoListByCondition(){
        List<ExcelRelFilterCakeFilterCakeWriteVo> excelRelFilterCakeFilterCakeWriteVos = new ArrayList<>();
        for(RelFilterCakeFilterCake relFilterCakeFilterCake: relFilterCakeFilterCakeRepository.findAll()){
            excelRelFilterCakeFilterCakeWriteVos.add(transEntityToExcel(relFilterCakeFilterCake));
        }
        return excelRelFilterCakeFilterCakeWriteVos;
    }

    public ExcelRelFilterCakeFilterCakeWriteVo transEntityToExcel(RelFilterCakeFilterCake relFilterCakeFilterCake){
        ExcelRelFilterCakeFilterCakeWriteVo excelRelFilterCakeFilterCakeWriteVo = new ExcelRelFilterCakeFilterCakeWriteVo();
        excelRelFilterCakeFilterCakeWriteVo.setFilterCakeName(relFilterCakeFilterCake.getFilterCake().getFilterCakeName());
        excelRelFilterCakeFilterCakeWriteVo.setFilterCakeNameUsed(relFilterCakeFilterCake.getFilterCakeUsed().getFilterCakeName());
        excelRelFilterCakeFilterCakeWriteVo.setInventory(relFilterCakeFilterCake.getInventory());
        return excelRelFilterCakeFilterCakeWriteVo;
    }

}
