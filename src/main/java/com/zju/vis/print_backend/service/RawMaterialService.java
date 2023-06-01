package com.zju.vis.print_backend.service;

import com.zju.vis.print_backend.Utils.*;
import com.zju.vis.print_backend.compositekey.RelDateRawMaterialKey;
import com.zju.vis.print_backend.dao.RawMaterialRepository;
import com.zju.vis.print_backend.entity.*;
import com.zju.vis.print_backend.vo.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.util.*;
import java.sql.Date;

import static com.zju.vis.print_backend.Utils.Utils.stepMonth;

@Slf4j
@Service
public class RawMaterialService {
    @Resource
    private RawMaterialRepository rawMaterialRepository;

    // 关联表调用
    @Resource
    private RelDateRawMaterialService relDateRawMaterialService;

    @Resource
    private RelFilterCakeRawMaterialService relFilterCakeRawMaterialService;

    @Resource
    private RelProductRawMaterialService relProductRawMaterialService;

    @Resource
    private FileService fileService;

    // 用于调用一般方法
    Utils utils = new Utils();

    // List<RawMaterial> 添加额外信息标准化打包发送
    public PackageVo packMaterial(List<RawMaterial> rawMaterialList,
                                             Integer pageNo, Integer pageSize,
                                             Integer rawMaterialNum){
        List<RawMaterialStandardVo> rawMaterialStandardList = new ArrayList<>();
        for(RawMaterial rawMaterial: rawMaterialList){
            rawMaterialStandardList.add(RawMaterialStandardization(rawMaterial));
        }
        PackageVo rawMaterialPackage = new PackageVo();
        rawMaterialPackage.setPageNo(pageNo + 1);
        rawMaterialPackage.setPageSize(pageSize);
        rawMaterialPackage.setPageNum(
                (rawMaterialNum-1) / pageSize + 1
        );
        rawMaterialPackage.setTotal(rawMaterialNum);
        rawMaterialPackage.setList(rawMaterialStandardList);
        return rawMaterialPackage;
    }


    // RawMaterial 转化为标准对象 RawMaterialStandard
    public RawMaterialStandardVo RawMaterialStandardization(RawMaterial rawMaterial){
        RawMaterialStandardVo rawMaterialStandard = new RawMaterialStandardVo();
        rawMaterialStandard.setRawMaterialId(rawMaterial.getRawMaterialId());
        rawMaterialStandard.setRawMaterialName(rawMaterial.getRawMaterialName());
        rawMaterialStandard.setRawMaterialIndex(rawMaterial.getRawMaterialIndex());
        Double currentPrice = rawMaterial.getRawMaterialPrice();
        java.util.Date date = stepMonth(new java.util.Date(),-3);
        Double historyPriceForIncrease = getRawMaterialHistoryPrice(rawMaterial,date);
        rawMaterialStandard.setRawMaterialUnitPrice(rawMaterial.getRawMaterialPrice());
        int increasePercent = 0;
        if(historyPriceForIncrease.doubleValue() - 0.0 > 1e-5){
            increasePercent = (int)(100 * (currentPrice - historyPriceForIncrease) / historyPriceForIncrease);
        }
        rawMaterialStandard.setRawMaterialIncreasePercent(
                increasePercent
                // (int) (Math.random() * 100) - 50
        );
        rawMaterialStandard.setRawMaterialConventional(rawMaterial.getRawMaterialConventional());
        rawMaterialStandard.setRawMaterialSpecification(rawMaterial.getRawMaterialSpecification());
        // 设置历史价格
        List<HistoryPriceVo> historyPriceList = new ArrayList<>();
        for(RelDateRawMaterial relDateRawMaterial: rawMaterial.getRelDateRawMaterialList()){
            HistoryPriceVo historyPrice = new HistoryPriceVo();
            historyPrice.setDate(relDateRawMaterial.getId().getRawMaterialDate());
            historyPrice.setPrice(relDateRawMaterial.getPrice());
            historyPriceList.add(historyPrice);
        }
        rawMaterialStandard.setRawMaterialHistoryPrice(historyPriceList);
        return rawMaterialStandard;
    }

    public RawMaterial deSimplifyRawMaterial(RawMaterialSimpleVo rawMaterialSimple, Long productId) {
        RawMaterial rawMaterial = new RawMaterial();
        rawMaterial = findRawMaterialByRawMaterialName(rawMaterialSimple.getRawMaterialName());
        //todo : 用料量信息在简化的原料信息里面
        return rawMaterial;
    }

    // 根据调用者的productId filterCakeId 结合自身找到对应的投料量
    public Double getInventory(List<RelProductRawMaterial> relProductRawMaterialList, Long productId, Long rawMaterialId){
        for(RelProductRawMaterial relProductRawMaterial: relProductRawMaterialList){
            if((relProductRawMaterial.getId().getProductId().longValue() == productId.longValue()) && (relProductRawMaterial.getId().getRawMaterialId().longValue() == rawMaterialId.longValue())){
                //System.out.println("返回原料产品的Inventory:" + relProductRawMaterial.getInventory());
                return relProductRawMaterial.getInventory();
            }
        }
        return -1.0;
    }
    public Double getInventoryF(List<RelFilterCakeRawMaterial> relFilterCakeRawMaterialList, Long filterCakeId, Long rawMaterialId){
        for(RelFilterCakeRawMaterial relFilterCakeRawMaterial: relFilterCakeRawMaterialList){
            if((relFilterCakeRawMaterial.getId().getFilterCakeId().longValue() == filterCakeId.longValue()) && (relFilterCakeRawMaterial.getId().getRawMaterialId().longValue() == rawMaterialId.longValue())){
                //System.out.println("返回原料滤饼的Inventory:" + relFilterCakeRawMaterial.getInventory());
                return relFilterCakeRawMaterial.getInventory();
            }
        }
        return -1.0;
    }

    /**
     *
     * @param rawMaterial 被封装的原料对象
     * @param productId   调用简化的Product的id
     * @return
     */
    // 简单原料信息封装
    public RawMaterialSimpleVo simplifyRawMaterial(RawMaterial rawMaterial, Long productId){
        RawMaterialSimpleVo rawMaterialSimple = new RawMaterialSimpleVo();
        rawMaterialSimple.setRawMaterialId(rawMaterial.getRawMaterialId());
        rawMaterialSimple.setRawMaterialName(rawMaterial.getRawMaterialName());
        // 获取投料量信息
        // 被封装对象的关系列表中查找，id与两个都对应的
        rawMaterialSimple.setInventory(getInventory(rawMaterial.getRelProductRawMaterialList(),
                productId,rawMaterial.getRawMaterialId()));
        return rawMaterialSimple;
    }

    // 滤饼类的简单原料信息封装
    public RawMaterialSimpleVo simplifyRawMaterialF(RawMaterial rawMaterial, Long filterCakeId){
        RawMaterialSimpleVo rawMaterialSimple = new RawMaterialSimpleVo();
        rawMaterialSimple.setRawMaterialId(rawMaterial.getRawMaterialId());
        rawMaterialSimple.setRawMaterialName(rawMaterial.getRawMaterialName());
        rawMaterialSimple.setInventory(getInventoryF(rawMaterial.getRelFilterCakeRawMaterialList(),
                filterCakeId,rawMaterial.getRawMaterialId()));
        return rawMaterialSimple;
    }

    // 获取原料历史价格
    public Double getRawMaterialHistoryPrice(RawMaterial rawMaterial, java.util.Date historyDate){
        if(rawMaterial == null){
            return -1.0;
        }
        List<HistoryPriceVo> historyPriceList = new ArrayList<>();
        for(RelDateRawMaterial relDateRawMaterial: rawMaterial.getRelDateRawMaterialList()){
            HistoryPriceVo historyPrice = new HistoryPriceVo();
            historyPrice.setDate(relDateRawMaterial.getId().getRawMaterialDate());
            historyPrice.setPrice(relDateRawMaterial.getPrice());
            historyPriceList.add(historyPrice);
        }
        if(historyPriceList.size() != 0){
            Collections.reverse(historyPriceList);
            for(HistoryPriceVo historyPrice: historyPriceList){
                if(historyPrice.getDate().getTime() - (86400*1000) <= historyDate.getTime()){
                    return historyPrice.getPrice();
                }
            }
            // 没有选定日期之前的历史数据，则取当前原料最早的数据作为当时的虚拟数据
            return historyPriceList.get(historyPriceList.size() - 1).getPrice();
        }else{
            return -1.0;
        }
    }

    // 获取原料历史价格列表
    public List<HistoryPriceVo> getRawMaterialHistoryPriceList(Long rawMaterialId, Long months){
        log.info("获取rawMaterialId为: " + rawMaterialId + " 的历史价格列表");
        RawMaterial rawMaterial = rawMaterialRepository.findRawMaterialByRawMaterialId(rawMaterialId);
        List<HistoryPriceVo> historyPriceVoList = new ArrayList<>();
        if(rawMaterial != null){
            for(int i = 0;i < months ; i++){
                java.util.Date date = stepMonth(new java.util.Date(),-i);
                HistoryPriceVo historyPrice = new HistoryPriceVo();
                historyPrice.setDate(new java.sql.Date(date.getTime()));
                historyPrice.setPrice(getRawMaterialHistoryPrice(rawMaterial,date));
                historyPriceVoList.add(historyPrice);
            }
        }
        return historyPriceVoList;
    }

    //查
    //-------------------------------------------------------------------------
    public PackageVo findAll(Integer pageNo,
                             Integer pageSize
    ) {
        Integer rawMaterialNum = rawMaterialRepository.findAll().size();
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<RawMaterial> page = rawMaterialRepository.findAll(pageable);
        return packMaterial(page.toList(),pageNo,pageSize,rawMaterialNum);
    }

    public List<EntityNameVo> findAllRawMaterialName(){
        List<EntityNameVo> rawMaterialNameList = new ArrayList<>();
        for (RawMaterial rawMaterial: rawMaterialRepository.findAll()){
            EntityNameVo rawMaterialName = new EntityNameVo();
            rawMaterialName.setId(rawMaterial.getRawMaterialId());
            rawMaterialName.setName(rawMaterial.getRawMaterialName());
            rawMaterialNameList.add(rawMaterialName);
        }
        return rawMaterialNameList;
    }

    public PackageVo findAllRawMaterialByCondition(
            String typeOfQuery, String conditionOfQuery,
            Integer pageNo, Integer pageSize
    ){
        List<RawMaterial> rawMaterialList = new ArrayList<>();
        switch (typeOfQuery){
            case "原料品名":
                rawMaterialList = rawMaterialRepository.findAllByRawMaterialNameContaining(conditionOfQuery);
                break;
            case "存货编号":
                rawMaterialList = rawMaterialRepository.findAllByRawMaterialIndexContaining(conditionOfQuery);
        }
        return packMaterial(
                utils.pageList(rawMaterialList, pageNo, pageSize),pageNo,pageSize,rawMaterialList.size()
        );
    }

    public RawMaterialStandardVo findRawMaterialByRawMaterialId(Long rawMaterialId){
        if(rawMaterialRepository.findRawMaterialByRawMaterialId(rawMaterialId) == null){
            return new RawMaterialStandardVo();
        }
        return  RawMaterialStandardization(rawMaterialRepository.findRawMaterialByRawMaterialId(rawMaterialId));
    }

    public RawMaterial findRawMaterialByRawMaterialName(String MaterialName){
        return rawMaterialRepository.findRawMaterialByRawMaterialName(MaterialName);
    }


    public List<RawMaterial> findAllByRawMaterialNameContaining(String MaterialName) {
        // 空字符串返回全部值
        if(utils.isEmptyString(MaterialName)){
            return rawMaterialRepository.findAll();
        }
        return rawMaterialRepository.findAllByRawMaterialNameContaining(MaterialName);
    }


    public List<Product> getProductByRawMaterialId(Long rawMaterialId){
        // Product product = productRepository.findProductByProductId(productId);
        RawMaterial rawMaterial = rawMaterialRepository.findRawMaterialByRawMaterialId(rawMaterialId);

        if(rawMaterial != null){

            System.out.println("原料名称:" + rawMaterial.getRawMaterialName());
            System.out.println("产品编号:" + rawMaterial.getRawMaterialIndex());

            // 获取原料列表
            List<Product> productList = rawMaterial.getProductList();
            if (productList!=null && productList.size()>0){
                System.out.println("原料对应的产品:");
                for (Product product : productList ){
                    System.out.println(product.getProductName()+";");
                }
            }
            return productList;
        }
        return new ArrayList<>();
    }



    /**
     * 根据原料名字查找对应的产品模糊查询
     * 如果有多个对应的原料则返回这一组原料对应的产品集合
     * @param MaterialName
     */
    public Set<Product> findProductsByRawMaterialName(String MaterialName){
        RawMaterial rawMaterial = findRawMaterialByRawMaterialName(MaterialName);
        Set<Product> productSet = new HashSet<>();
        if(rawMaterial!=null){
            productSet.addAll(rawMaterial.getProductList());
        }
        System.out.println("对应的产品集合");
        System.out.println(productSet.size());
        return productSet;
    }

    //增
    //-------------------------------------------------------------------------
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeStandardizeResult{
        private RawMaterial rawMaterial;
        private List<RelDateRawMaterial> relDateRawMaterialList;
    }

    public DeStandardizeResult deStandardizeRawMaterial(RawMaterialStandardVo rawMaterialStandard){
        RawMaterial rawMaterial = new RawMaterial();
        rawMaterial.setRawMaterialId(rawMaterialStandard.getRawMaterialId());
        rawMaterial.setRawMaterialName(rawMaterialStandard.getRawMaterialName());
        rawMaterial.setRawMaterialIndex(rawMaterialStandard.getRawMaterialIndex());
        rawMaterial.setRawMaterialPrice(rawMaterialStandard.getRawMaterialUnitPrice());
        rawMaterial.setRawMaterialConventional(rawMaterialStandard.getRawMaterialConventional());
        rawMaterial.setRawMaterialSpecification(rawMaterialStandard.getRawMaterialSpecification());

        // 关系表设置
        List<RelDateRawMaterial> relDateRawMaterialList = new ArrayList<>();
        if(rawMaterialStandard.getRawMaterialHistoryPrice() != null){
            for(HistoryPriceVo historyPrice: rawMaterialStandard.getRawMaterialHistoryPrice()){
                Date rawMaterialDate = historyPrice.getDate();
                Double price = historyPrice.getPrice();

                RelDateRawMaterial relDateRawMaterial = new RelDateRawMaterial();
                RelDateRawMaterialKey relDateRawMaterialKey = new RelDateRawMaterialKey();
                relDateRawMaterialKey.setRawMaterialId(rawMaterial.getRawMaterialId());
                relDateRawMaterialKey.setRawMaterialDate(rawMaterialDate);
                relDateRawMaterial.setId(relDateRawMaterialKey);
                relDateRawMaterial.setPrice(price);

                relDateRawMaterialList.add(relDateRawMaterial);
            }
        }
        return new DeStandardizeResult(rawMaterial,relDateRawMaterialList);
    }

    private void saveRelDateRawMaterials(RawMaterial savedRawMaterial, List<RelDateRawMaterial> relDateRawMaterialList){
        for(RelDateRawMaterial relDateRawMaterial: relDateRawMaterialList){
            // 使用调用者id不一定与原id相等
            relDateRawMaterial.getId().setRawMaterialId(savedRawMaterial.getRawMaterialId());

            relDateRawMaterial.setRawMaterial(savedRawMaterial);
            relDateRawMaterialService.addRelDateRawMaterial(relDateRawMaterial);
        }
    }

    public ResultVo saveRawMaterial(RawMaterialStandardVo rawMaterialStandard,Boolean isImport){
        DeStandardizeResult result = deStandardizeRawMaterial(rawMaterialStandard);
        RawMaterial rawMaterial = result.getRawMaterial();
        // 表示添加
        if(rawMaterialStandard.getRawMaterialId().longValue() == 0){
            if(rawMaterialRepository.findRawMaterialByRawMaterialName(rawMaterialStandard.getRawMaterialName()) != null){
                log.info("{}原料名称重复",rawMaterialStandard.getRawMaterialName());
                return ResultVoUtil.error("原料名称重复");
            }
        }else{
            RawMaterial originRawMaterial = rawMaterialRepository.findRawMaterialByRawMaterialId(rawMaterialStandard.getRawMaterialId());
            if(!rawMaterial.getRawMaterialName().equals(originRawMaterial.getRawMaterialName()) &&
                    rawMaterialRepository.findRawMaterialByRawMaterialName(rawMaterial.getRawMaterialName()) != null){
                log.info("{}原料名称重复",rawMaterial.getRawMaterialName());
                return ResultVoUtil.error("原料名称重复");
            }
            // 解决关系表保存消失问题
            if(isImport){
                rawMaterial.setFilterCakeList(originRawMaterial.getFilterCakeList());
                rawMaterial.setProductList(originRawMaterial.getProductList());
            }
        }
        List<RelDateRawMaterial> relDateRawMaterialList = result.getRelDateRawMaterialList();

        // 历史日期如果不为空则选择时间最近的
        if(relDateRawMaterialList.size() != 0){
            long MaxTime = -1;
            Double price = -1.0;
            for(RelDateRawMaterial rel: relDateRawMaterialList){
                // 找到最近的时间对应的价格
                if(rel.getId().getRawMaterialDate().getTime() > MaxTime){
                    MaxTime = rel.getId().getRawMaterialDate().getTime();
                    price = rel.getPrice();
                }
            }
            // 将历史价格设置为最终价格
            rawMaterial.setRawMaterialPrice(price);
        }

        RawMaterial savedRawMaterial = rawMaterialRepository.save(rawMaterial);
        saveRelDateRawMaterials(savedRawMaterial, relDateRawMaterialList);
        // 设置变换后id
        rawMaterialStandard.setRawMaterialId(savedRawMaterial.getRawMaterialId());
        return ResultVoUtil.success(rawMaterialStandard);
    }

    public ResultVo addRawMaterial(RawMaterialStandardVo rawMaterialStandard) {
        // 设置一个不存在的id
        rawMaterialStandard.setRawMaterialId(new Long(0));
        return saveRawMaterial(rawMaterialStandard,false);
    }

    // public ResultVo addRawMaterial(RawMaterialStandardVo rawMaterialStandard) {
    //     DeStandardizeResult result = deStandardizeRawMaterial(rawMaterialStandard);
    //     RawMaterial rawMaterial = result.getRawMaterial();
    //     if(rawMaterialRepository.findRawMaterialByRawMaterialName(rawMaterial.getRawMaterialName()) != null){
    //         log.info("{}原料名称重复",rawMaterial.getRawMaterialName());
    //         return ResultVoUtil.error("原料名称重复");
    //     }
    //     List<RelDateRawMaterial> relDateRawMaterialList = result.getRelDateRawMaterialList();
    //
    //     rawMaterial.setRawMaterialId(new Long(0));
    //     RawMaterial savedRawMaterial = rawMaterialRepository.save(rawMaterial);
    //     saveRelDateRawMaterials(savedRawMaterial, relDateRawMaterialList);
    //     // 设置变换后id
    //     rawMaterialStandard.setRawMaterialId(savedRawMaterial.getRawMaterialId());
    //     return ResultVoUtil.success(rawMaterialStandard);
    // }

    //改
    //-------------------------------------------------------------------------
    public ResultVo updateRawMaterial(RawMaterialStandardVo updatedRawMaterial) {
        RawMaterial originRawMaterial = rawMaterialRepository.findRawMaterialByRawMaterialId(updatedRawMaterial.getRawMaterialId());
        if(originRawMaterial == null){
            log.info("RawMaterial Add ---> {}",updatedRawMaterial.getRawMaterialName());
            ResultVo<RawMaterialStandardVo> result = addRawMaterial(updatedRawMaterial);
            return result;
        }
        DeStandardizeResult result = deStandardizeRawMaterial(updatedRawMaterial);
        RawMaterial rawMaterial = result.getRawMaterial();
        // 1.新名字与原名字不一致 2.新名字与数据库中已有的名字重复
        if(!rawMaterial.getRawMaterialName().equals(originRawMaterial.getRawMaterialName()) &&
                rawMaterialRepository.findRawMaterialByRawMaterialName(rawMaterial.getRawMaterialName()) != null){
            log.info("{}原料名称重复",rawMaterial.getRawMaterialName());
            return ResultVoUtil.error("原料名称重复");
        }

        log.info("RawMaterial Update ---> {}",updatedRawMaterial.getRawMaterialName());

        // 删除原先单向时间关系
        if(originRawMaterial.getRelDateRawMaterialList()!=null){
            for(RelDateRawMaterial relDateRawMaterial: originRawMaterial.getRelDateRawMaterialList()){
                relDateRawMaterialService.delete(relDateRawMaterial);
            }
        }

        // 重新添加关系以修改内容
        List<RelDateRawMaterial> relDateRawMaterialList = result.getRelDateRawMaterialList();
        // 历史日期如果不为空则选择时间最近的
        if(relDateRawMaterialList.size() != 0){
            long MaxTime = -1;
            Double price = -1.0;
            for(RelDateRawMaterial rel: relDateRawMaterialList){
                // 找到最近的时间对应的价格
                if(rel.getId().getRawMaterialDate().getTime() > MaxTime){
                    MaxTime = rel.getId().getRawMaterialDate().getTime();
                    price = rel.getPrice();
                }
            }
            // 将历史价格设置为最终价格
            rawMaterial.setRawMaterialPrice(price);
        }

        RawMaterial savedRawMaterial = rawMaterialRepository.save(rawMaterial);
        saveRelDateRawMaterials(savedRawMaterial,relDateRawMaterialList);
        return ResultVoUtil.success(updatedRawMaterial);
    }

    //删
    //-------------------------------------------------------------------------
    // @Transactional
    public void deleteByRawMaterialId(Long rawMaterialId){
        deleteRelByRawMaterialId(rawMaterialId);
        deleteRawMaterialByRawMaterialId(rawMaterialId);
    }
    @Transactional
    public void deleteRelByRawMaterialId(Long rawMaterialId){
        // 级联删除
        RawMaterial rawMaterial = rawMaterialRepository.findRawMaterialByRawMaterialId(rawMaterialId);

        // 删除时间关系表项
        for(RelDateRawMaterial relDateRawMaterial: rawMaterial.getRelDateRawMaterialList()){
            relDateRawMaterialService.delete(relDateRawMaterial);
        }

        // 删除滤饼关联表项
        for(RelFilterCakeRawMaterial relFilterCakeRawMaterial: rawMaterial.getRelFilterCakeRawMaterialList()){
            relFilterCakeRawMaterialService.delete(relFilterCakeRawMaterial);
        }

        // 删除产品关联表项
        for(RelProductRawMaterial relProductRawMaterial: rawMaterial.getRelProductRawMaterialList()){
            relProductRawMaterialService.delete(relProductRawMaterial);
        }
    }

    @Transactional
    public void deleteRawMaterialByRawMaterialId(Long rawMaterialId){
        RawMaterial rawMaterialToDelete = rawMaterialRepository.findRawMaterialByRawMaterialId(rawMaterialId);
        rawMaterialRepository.delete(rawMaterialToDelete);
    }

    // 导入文件
    //-------------------------------------------------------------------------
    public ResultVo importRawMaterialExcelAndPersistence(MultipartFile file){
        ResultVo<List<ExcelRawMaterialVo>> importResult = fileService.importEntityExcel(file, ExcelRawMaterialVo.class);
        if(!importResult.checkSuccess()){
            log.error(importResult.getMsg());
            return importResult;
        }
        List<ExcelRawMaterialVo> excelRawMaterialVos = importResult.getData();
        for(ExcelRawMaterialVo excelRawMaterialVo: excelRawMaterialVos){
            // excel信息转化为标准类
            RawMaterialStandardVo rawMaterialStandard = transExcelToStandard(excelRawMaterialVo);
            // 更新数据库，已存在则会直接替换
            saveRawMaterial(rawMaterialStandard,true);
        }
        return ResultVoUtil.success(excelRawMaterialVos);
    }

    public RawMaterialStandardVo transExcelToStandard(ExcelRawMaterialVo excelRawMaterialVo){
        RawMaterialStandardVo rawMaterialStandard = new RawMaterialStandardVo();
        // 如果已经存在了则修改，否则则添加
        if(rawMaterialRepository.findRawMaterialByRawMaterialName(excelRawMaterialVo.getRawMaterialName()) == null){
            // 表示添加
            rawMaterialStandard.setRawMaterialId(new Long(0));
        }else{
            rawMaterialStandard.setRawMaterialId(rawMaterialRepository.findRawMaterialByRawMaterialName(excelRawMaterialVo.getRawMaterialName()).getRawMaterialId());
        }
        rawMaterialStandard.setRawMaterialName(excelRawMaterialVo.getRawMaterialName());
        rawMaterialStandard.setRawMaterialIndex(excelRawMaterialVo.getRawMaterialIndex());
        rawMaterialStandard.setRawMaterialUnitPrice(excelRawMaterialVo.getRawMaterialPrice());
        rawMaterialStandard.setRawMaterialConventional(excelRawMaterialVo.getRawMaterialConventional());
        rawMaterialStandard.setRawMaterialSpecification(excelRawMaterialVo.getRawMaterialSpecification());
        return rawMaterialStandard;
    }

    // 导出文件
    //-------------------------------------------------------------------------
    public ResultVo<String> exportRawMaterialExcel(HttpServletResponse response){
        // 1.根据查询条件获取结果集
        List<ExcelRawMaterialWriteVo> excelRawMaterialWriteVos = getExcelRawMaterialWriteVoListByCondition();
        if (CollectionUtil.isEmpty(excelRawMaterialWriteVos)) {
            log.info("【导出Excel文件】要导出的数据为空，无法导出！");
            return ResultVoUtil.success("数据为空");
        }
        // 2.获取要下载Excel文件的路径
        ResultVo<String> resultVo = fileService.getDownLoadPath(ExcelRawMaterialWriteVo.class,excelRawMaterialWriteVos);
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

    public List<ExcelRawMaterialWriteVo> getExcelRawMaterialWriteVoListByCondition(){
        List<ExcelRawMaterialWriteVo> excelRawMaterialWriteVos = new ArrayList<>();
        for(RawMaterial rawMaterial: rawMaterialRepository.findAll()){
            excelRawMaterialWriteVos.add(transEntityToExcel(rawMaterial));
        }
        return excelRawMaterialWriteVos;
    }

    public ExcelRawMaterialWriteVo transEntityToExcel(RawMaterial rawMaterial){
        ExcelRawMaterialWriteVo excelRawMaterialWriteVo = new ExcelRawMaterialWriteVo();
        excelRawMaterialWriteVo.setRawMaterialName(rawMaterial.getRawMaterialName());
        excelRawMaterialWriteVo.setRawMaterialIndex(rawMaterial.getRawMaterialIndex());
        excelRawMaterialWriteVo.setRawMaterialPrice(rawMaterial.getRawMaterialPrice());
        excelRawMaterialWriteVo.setRawMaterialConventional(rawMaterial.getRawMaterialConventional());
        excelRawMaterialWriteVo.setRawMaterialSpecification(rawMaterial.getRawMaterialSpecification());
        return excelRawMaterialWriteVo;
    }
}
