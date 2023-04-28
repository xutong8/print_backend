package com.zju.vis.print_backend.service;

import com.zju.vis.print_backend.compositekey.RelDateRawMaterialKey;
import com.zju.vis.print_backend.dao.RawMaterialRepository;
import com.zju.vis.print_backend.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.*;
import java.sql.Date;

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

    // 用于调用一般方法
    Utils utils = new Utils();

    // 用于返回原料列表名
    public class RawMaterialName{
        private Long rawMaterialId;
        private String rawMaterialName;

        public Long getRawMaterialId() {
            return rawMaterialId;
        }

        public void setRawMaterialId(Long rawMaterialId) {
            this.rawMaterialId = rawMaterialId;
        }

        public String getRawMaterialName() {
            return rawMaterialName;
        }

        public void setRawMaterialName(String rawMaterialName) {
            this.rawMaterialName = rawMaterialName;
        }
    }

    // RawMaterial 结果封装
    public class RawMaterialPackage{
        // 附加信息
        private Integer pageNo;
        private Integer pageSize;
        private Integer pageNum;
        private Integer total;

        // 返回的标准列表
        private List<RawMaterialStandard> list;

        public Integer getPageNo() {
            return pageNo;
        }

        public void setPageNo(Integer pageNo) {
            this.pageNo = pageNo;
        }

        public Integer getPageSize() {
            return pageSize;
        }

        public void setPageSize(Integer pageSize) {
            this.pageSize = pageSize;
        }

        public Integer getPageNum() {
            return pageNum;
        }

        public void setPageNum(Integer pageNum) {
            this.pageNum = pageNum;
        }

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }

        public List<RawMaterialStandard> getList() {
            return list;
        }

        public void setList(List<RawMaterialStandard> list) {
            this.list = list;
        }
    }

    // List<RawMaterial> 添加额外信息标准化打包发送
    public RawMaterialPackage packMaterial(List<RawMaterial> rawMaterialList,
                                           Integer pageNo,Integer pageSize,
                                           Integer rawMaterialNum){
        List<RawMaterialStandard> rawMaterialStandardList = new ArrayList<>();
        for(RawMaterial rawMaterial: rawMaterialList){
            rawMaterialStandardList.add(RawMaterialStandardization(rawMaterial));
        }
        RawMaterialPackage rawMaterialPackage = new RawMaterialPackage();
        rawMaterialPackage.setPageNo(pageNo + 1);
        rawMaterialPackage.setPageSize(pageSize);
        rawMaterialPackage.setPageNum(
                (rawMaterialNum-1) / pageSize + 1
        );
        rawMaterialPackage.setTotal(rawMaterialNum);
        rawMaterialPackage.setList(rawMaterialStandardList);
        return rawMaterialPackage;
    }


    // RawMaterial 标准化形式类
    public static class RawMaterialStandard{
        private Long rawMaterialId;
        private String rawMaterialName;
        private String rawMaterialIndex;
        private Double rawMaterialUnitPrice;
        private Integer rawMaterialIncreasePercent;
        private String rawMaterialConventional;
        private String rawMaterialSpecification;
        private List<Utils.HistoryPrice> rawMaterialHistoryPrice;

        public Long getRawMaterialId() {
            return rawMaterialId;
        }

        public void setRawMaterialId(Long rawMaterialId) {
            this.rawMaterialId = rawMaterialId;
        }

        public String getRawMaterialName() {
            return rawMaterialName;
        }

        public void setRawMaterialName(String rawMaterialName) {
            this.rawMaterialName = rawMaterialName;
        }

        public String getRawMaterialIndex() {
            return rawMaterialIndex;
        }

        public void setRawMaterialIndex(String rawMaterialIndex) {
            this.rawMaterialIndex = rawMaterialIndex;
        }

        public Double getRawMaterialUnitPrice() {
            return rawMaterialUnitPrice;
        }

        public void setRawMaterialUnitPrice(Double rawMaterialUnitPrice) {
            this.rawMaterialUnitPrice = rawMaterialUnitPrice;
        }

        public Integer getRawMaterialIncreasePercent() {
            return rawMaterialIncreasePercent;
        }

        public void setRawMaterialIncreasePercent(Integer rawMaterialIncreasePercent) {
            this.rawMaterialIncreasePercent = rawMaterialIncreasePercent;
        }

        public String getRawMaterialConventional() {
            return rawMaterialConventional;
        }

        public void setRawMaterialConventional(String rawMaterialConventional) {
            this.rawMaterialConventional = rawMaterialConventional;
        }

        public String getRawMaterialSpecification() {
            return rawMaterialSpecification;
        }

        public void setRawMaterialSpecification(String rawMaterialSpecification) {
            this.rawMaterialSpecification = rawMaterialSpecification;
        }

        public List<Utils.HistoryPrice> getRawMaterialHistoryPrice() {
            return rawMaterialHistoryPrice;
        }

        public void setRawMaterialHistoryPrice(List<Utils.HistoryPrice> rawMaterialHistoryPrice) {
            this.rawMaterialHistoryPrice = rawMaterialHistoryPrice;
        }
    }

    // RawMaterial 转化为标准对象 RawMaterialStandard
    public RawMaterialStandard RawMaterialStandardization(RawMaterial rawMaterial){
        RawMaterialStandard rawMaterialStandard = new RawMaterialStandard();
        rawMaterialStandard.setRawMaterialId(rawMaterial.getRawMaterialId());
        rawMaterialStandard.setRawMaterialName(rawMaterial.getRawMaterialName());
        rawMaterialStandard.setRawMaterialIndex(rawMaterial.getRawMaterialIndex());
        rawMaterialStandard.setRawMaterialUnitPrice(rawMaterial.getRawMaterialPrice());
        // 设置产品价格涨幅 当前为假数据 todo：递归计算真实数据
        rawMaterialStandard.setRawMaterialIncreasePercent(
                (int) (Math.random() * 100) - 50
        );
        rawMaterialStandard.setRawMaterialConventional(rawMaterial.getRawMaterialConventional());
        rawMaterialStandard.setRawMaterialSpecification(rawMaterial.getRawMaterialSpecification());
        // 设置历史价格
        List<Utils.HistoryPrice> historyPriceList = new ArrayList<>();
        for(RelDateRawMaterial relDateRawMaterial: rawMaterial.getRelDateRawMaterialList()){
            Utils.HistoryPrice historyPrice = new Utils.HistoryPrice();
            historyPrice.setDate(relDateRawMaterial.getId().getRawMaterialDate());
            historyPrice.setPrice(relDateRawMaterial.getPrice());
            historyPriceList.add(historyPrice);
        }
        rawMaterialStandard.setRawMaterialHistoryPrice(historyPriceList);
        return rawMaterialStandard;
    }

    public RawMaterial deSimplifyRawMaterial(RawMaterialSimple rawMaterialSimple, Long productId) {
        RawMaterial rawMaterial = new RawMaterial();
        rawMaterial = findRawMaterialByRawMaterialName(rawMaterialSimple.rawMaterialName);
        //todo : 用料量信息在简化的原料信息里面
        return rawMaterial;
    }
    // 用于简单原料简单信息
    public static class RawMaterialSimple{
        private Long rawMaterialId;
        private String rawMaterialName;
        private Double inventory;

        public Long getRawMaterialId() {
            return rawMaterialId;
        }

        public void setRawMaterialId(Long rawMaterialId) {
            this.rawMaterialId = rawMaterialId;
        }

        public String getRawMaterialName() {
            return rawMaterialName;
        }

        public void setRawMaterialName(String rawMaterialName) {
            this.rawMaterialName = rawMaterialName;
        }

        public Double getInventory() {
            return inventory;
        }

        public void setInventory(Double inventory) {
            this.inventory = inventory;
        }
    }

    // 根据调用者的productId filterCakeId 结合自身找到对应的投料量
    public Double getInventory(List<RelProductRawMaterial> relProductRawMaterialList, Long productId, Long rawMaterialId){
        for(RelProductRawMaterial relProductRawMaterial: relProductRawMaterialList){
            if((relProductRawMaterial.getId().getProductId().longValue() == productId.longValue()) && (relProductRawMaterial.getId().getRawMaterialId().longValue() == rawMaterialId.longValue())){
                System.out.println("返回原料产品的Inventory:" + relProductRawMaterial.getInventory());
                return relProductRawMaterial.getInventory();
            }
        }
        return -1.0;
    }
    public Double getInventoryF(List<RelFilterCakeRawMaterial> relFilterCakeRawMaterialList, Long filterCakeId, Long rawMaterialId){
        for(RelFilterCakeRawMaterial relFilterCakeRawMaterial: relFilterCakeRawMaterialList){
            if((relFilterCakeRawMaterial.getId().getFilterCakeId().longValue() == filterCakeId.longValue()) && (relFilterCakeRawMaterial.getId().getRawMaterialId().longValue() == rawMaterialId.longValue())){
                System.out.println("返回原料滤饼的Inventory:" + relFilterCakeRawMaterial.getInventory());
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
    public RawMaterialSimple simplifyRawMaterial(RawMaterial rawMaterial,Long productId){
        RawMaterialSimple rawMaterialSimple = new RawMaterialSimple();
        rawMaterialSimple.setRawMaterialId(rawMaterial.getRawMaterialId());
        rawMaterialSimple.setRawMaterialName(rawMaterial.getRawMaterialName());
        // 被封装对象的关系列表中查找，id与两个都对应的项
        rawMaterialSimple.setInventory(getInventory(rawMaterial.getRelProductRawMaterialList(),
                productId,rawMaterial.getRawMaterialId()));
        return rawMaterialSimple;
    }

    // 滤饼类的简单原料信息封装
    public RawMaterialSimple simplifyRawMaterialF(RawMaterial rawMaterial,Long filterCakeId){
        RawMaterialSimple rawMaterialSimple = new RawMaterialSimple();
        rawMaterialSimple.setRawMaterialId(rawMaterial.getRawMaterialId());
        rawMaterialSimple.setRawMaterialName(rawMaterial.getRawMaterialName());
        rawMaterialSimple.setInventory(getInventoryF(rawMaterial.getRelFilterCakeRawMaterialList(),
                filterCakeId,rawMaterial.getRawMaterialId()));
        return rawMaterialSimple;
    }

    //查
    //-------------------------------------------------------------------------
    public RawMaterialPackage findAll(Integer pageNo,
                                     Integer pageSize
    ) {
        Integer rawMaterialNum = rawMaterialRepository.findAll().size();
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<RawMaterial> page = rawMaterialRepository.findAll(pageable);
        return packMaterial(page.toList(),pageNo,pageSize,rawMaterialNum);
    }

    public List<RawMaterialName> findAllRawMaterialName(){
        List<RawMaterialName> rawMaterialNameList = new ArrayList<>();
        for (RawMaterial rawMaterial: rawMaterialRepository.findAll()){
            RawMaterialName rawMaterialName = new RawMaterialName();
            rawMaterialName.setRawMaterialId(rawMaterial.getRawMaterialId());
            rawMaterialName.setRawMaterialName(rawMaterial.getRawMaterialName());
            rawMaterialNameList.add(rawMaterialName);
        }
        return rawMaterialNameList;
    }

    public RawMaterialPackage findAllRawMaterialByCondition(
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

    public RawMaterialStandard findRawMaterialByRawMaterialId(Long rawMaterialId){
        if(rawMaterialRepository.findRawMaterialByRawMaterialId(rawMaterialId) == null){
            return new RawMaterialStandard();
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
    public static class DeStandardizeResult{
        private RawMaterial rawMaterial;
        private List<RelDateRawMaterial> relDateRawMaterialList;

        //构造函数
        public DeStandardizeResult(RawMaterial rawMaterial, List<RelDateRawMaterial> relDateRawMaterialList) {
            this.rawMaterial = rawMaterial;
            this.relDateRawMaterialList = relDateRawMaterialList;
        }

        public RawMaterial getRawMaterial() {
            return rawMaterial;
        }

        public void setRawMaterial(RawMaterial rawMaterial) {
            this.rawMaterial = rawMaterial;
        }

        public List<RelDateRawMaterial> getRelDateRawMaterialList() {
            return relDateRawMaterialList;
        }

        public void setRelDateRawMaterialList(List<RelDateRawMaterial> relDateRawMaterialList) {
            this.relDateRawMaterialList = relDateRawMaterialList;
        }
    }

    public DeStandardizeResult deStandardizeRawMaterial(RawMaterialStandard rawMaterialStandard){
        RawMaterial rawMaterial = new RawMaterial();
        rawMaterial.setRawMaterialId(rawMaterialStandard.getRawMaterialId());
        rawMaterial.setRawMaterialName(rawMaterialStandard.getRawMaterialName());
        rawMaterial.setRawMaterialIndex(rawMaterialStandard.getRawMaterialIndex());
        rawMaterial.setRawMaterialPrice(rawMaterialStandard.getRawMaterialUnitPrice());
        rawMaterial.setRawMaterialConventional(rawMaterialStandard.getRawMaterialConventional());
        rawMaterial.setRawMaterialSpecification(rawMaterialStandard.getRawMaterialSpecification());

        // 关系表设置
        List<RelDateRawMaterial> relDateRawMaterialList = new ArrayList<>();
        for(Utils.HistoryPrice historyPrice: rawMaterialStandard.getRawMaterialHistoryPrice()){
            Date rawMaterialDate = historyPrice.getDate();
            Float price = historyPrice.getPrice();

            RelDateRawMaterial relDateRawMaterial = new RelDateRawMaterial();
            RelDateRawMaterialKey relDateRawMaterialKey = new RelDateRawMaterialKey();
            relDateRawMaterialKey.setRawMaterialId(rawMaterial.getRawMaterialId());
            relDateRawMaterialKey.setRawMaterialDate(rawMaterialDate);
            relDateRawMaterial.setId(relDateRawMaterialKey);
            relDateRawMaterial.setPrice(price);

            relDateRawMaterialList.add(relDateRawMaterial);
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

    public RawMaterial addRawMaterial(RawMaterialStandard rawMaterialStandard) {
        DeStandardizeResult result = deStandardizeRawMaterial(rawMaterialStandard);
        RawMaterial rawMaterial = result.getRawMaterial();
        List<RelDateRawMaterial> relDateRawMaterialList = result.getRelDateRawMaterialList();

        rawMaterial.setRawMaterialId(new Long(0));
        RawMaterial savedRawMaterial = rawMaterialRepository.save(rawMaterial);
        saveRelDateRawMaterials(savedRawMaterial, relDateRawMaterialList);
        return savedRawMaterial;
    }

    //改
    //-------------------------------------------------------------------------
    public RawMaterial updateRawMaterial(Long rawMaterialId, RawMaterial updatedRawMaterial) {
        return rawMaterialRepository.findById(rawMaterialId)
                .map(rawMaterial -> {
                    rawMaterial.setRawMaterialName(updatedRawMaterial.getRawMaterialName());
                    rawMaterial.setRawMaterialIndex(updatedRawMaterial.getRawMaterialIndex());
                    rawMaterial.setRawMaterialPrice(updatedRawMaterial.getRawMaterialPrice());
                    rawMaterial.setRawMaterialConventional(updatedRawMaterial.getRawMaterialConventional());
                    rawMaterial.setRawMaterialSpecification(updatedRawMaterial.getRawMaterialSpecification());
                    return rawMaterialRepository.save(rawMaterial);
                })
                .orElseThrow(() -> new NoSuchElementException("RawMaterial not found with id " + rawMaterialId));
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
}
