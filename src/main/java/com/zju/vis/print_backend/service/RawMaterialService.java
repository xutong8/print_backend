package com.zju.vis.print_backend.service;

import com.zju.vis.print_backend.dao.RawMaterialRepository;
import com.zju.vis.print_backend.entity.Product;
import com.zju.vis.print_backend.entity.RawMaterial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class RawMaterialService {
    @Resource
    private RawMaterialRepository rawMaterialRepository;

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
    public class RawMaterialStandard{
        private Long rawMaterialId;
        private String rawMaterialName;
        private String rawMaterialIndex;
        private Double rawMaterialUnitPrice;
        private Integer rawMaterialIncreasePercent;
        private String rawMaterialConventional;
        private String rawMaterialSpecification;

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
        return rawMaterialStandard;
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
    public RawMaterial addRawMaterial(RawMaterial rawMaterial) {
        return rawMaterialRepository.save(rawMaterial);
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

}
