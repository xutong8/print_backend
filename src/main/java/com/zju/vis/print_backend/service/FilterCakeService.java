package com.zju.vis.print_backend.service;

import com.zju.vis.print_backend.dao.FilterCakeRepository;
import com.zju.vis.print_backend.entity.FilterCake;
import com.zju.vis.print_backend.entity.Product;
import com.zju.vis.print_backend.entity.RelProductFilterCake;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.*;

@Service
public class FilterCakeService {
    @Resource
    FilterCakeRepository filterCakeRepository;

    // 调用一般方法
    Utils utils = new Utils();

    // 用于返回滤饼列表名
    public class FilterCakeName{
        private Long filterCakeId;
        private String filterCakeName;

        public Long getFilterCakeId() {
            return filterCakeId;
        }

        public void setFilterCakeId(Long filterCakeId) {
            this.filterCakeId = filterCakeId;
        }

        public String getFilterCakeName() {
            return filterCakeName;
        }

        public void setFilterCakeName(String filterCakeName) {
            this.filterCakeName = filterCakeName;
        }
    }

    // FilterCake 结果封装
    public class FilterCakePackage{
        // 附加信息
        private Integer pageNo;
        private Integer pageSize;
        private Integer pageNum;
        private Integer total;

        // 返回的标准列表
        private List<FilterCakeStandard> list;

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

        public List<FilterCakeStandard> getList() {
            return list;
        }

        public void setList(List<FilterCakeStandard> list) {
            this.list = list;
        }
    }

    // List<FilterCake> 添加额外信息打包发送
    public FilterCakePackage packFilterCake(List<FilterCake> filterCakeList,
                             Integer pageNo,Integer pageSize,
                             Integer filterCakeNum){
        List<FilterCakeStandard> filterCakeStandardList = new ArrayList<>();
        for(FilterCake filterCake: filterCakeList){
            filterCakeStandardList.add(FilterCakeStandardization(filterCake));
        }
        FilterCakePackage filterCakePackage = new FilterCakePackage();
        filterCakePackage.setPageNo(pageNo + 1);
        filterCakePackage.setPageSize(pageSize);
        filterCakePackage.setPageNum(
                (filterCakeNum-1) / pageSize + 1
        );
        filterCakePackage.setTotal(filterCakeNum);
        filterCakePackage.setList(filterCakeStandardList);
        return filterCakePackage;
    }

    // FilterCake 标准化形式类
    public static class FilterCakeStandard{
        private Long filterCakeId;
        private String filterCakeName;
        private String filterCakeIndex;
        private String filterCakeColor;
        private Double filterCakeUnitPrice;
        private Integer filterCakePriceIncreasePercent;
        private String filterCakeSpecification;
        private String filterCakeRemarks;

        public Long getFilterCakeId() {
            return filterCakeId;
        }

        public void setFilterCakeId(Long filterCakeId) {
            this.filterCakeId = filterCakeId;
        }

        public String getFilterCakeName() {
            return filterCakeName;
        }

        public void setFilterCakeName(String filterCakeName) {
            this.filterCakeName = filterCakeName;
        }

        public String getFilterCakeIndex() {
            return filterCakeIndex;
        }

        public void setFilterCakeIndex(String filterCakeIndex) {
            this.filterCakeIndex = filterCakeIndex;
        }

        public String getFilterCakeColor() {
            return filterCakeColor;
        }

        public void setFilterCakeColor(String filterCakeColor) {
            this.filterCakeColor = filterCakeColor;
        }

        public Double getFilterCakeUnitPrice() {
            return filterCakeUnitPrice;
        }

        public void setFilterCakeUnitPrice(Double filterCakeUnitPrice) {
            this.filterCakeUnitPrice = filterCakeUnitPrice;
        }

        public Integer getFilterCakePriceIncreasePercent() {
            return filterCakePriceIncreasePercent;
        }

        public void setFilterCakePriceIncreasePercent(Integer filterCakePriceIncreasePercent) {
            this.filterCakePriceIncreasePercent = filterCakePriceIncreasePercent;
        }

        public String getFilterCakeSpecification() {
            return filterCakeSpecification;
        }

        public void setFilterCakeSpecification(String filterCakeSpecification) {
            this.filterCakeSpecification = filterCakeSpecification;
        }

        public String getFilterCakeRemarks() {
            return filterCakeRemarks;
        }

        public void setFilterCakeRemarks(String filterCakeRemarks) {
            this.filterCakeRemarks = filterCakeRemarks;
        }
    }

    // FilterCake 转化为标准对象 FilterCakeStandard
    public FilterCakeStandard FilterCakeStandardization(FilterCake filterCake){
        FilterCakeStandard filterCakeStandard = new FilterCakeStandard();
        filterCakeStandard.setFilterCakeId(filterCake.getFilterCakeId());
        filterCakeStandard.setFilterCakeName(filterCake.getFilterCakeName());
        filterCakeStandard.setFilterCakeIndex(filterCake.getFilterCakeIndex());
        filterCakeStandard.setFilterCakeColor(filterCake.getFilterCakeColor());
        // 设置滤饼单价 当前为假数据 todo：递归计算真实数据
        filterCakeStandard.setFilterCakeUnitPrice(
                Math.random() * (500.0 - 10.0) + 10.0
        );
        // 设置滤饼价格涨幅 当前为假数据 todo：递归计算真实数据
        filterCakeStandard.setFilterCakePriceIncreasePercent(
                (int) (Math.random() * 100) - 50
        );
        filterCakeStandard.setFilterCakeSpecification(filterCake.getFilterCakeSpecification());
        filterCakeStandard.setFilterCakeRemarks(filterCake.getFilterCakeRemarks());
        return filterCakeStandard;
    }
    public FilterCake deSimplifyFilterCake(FilterCakeSimple filterCakeSimple, Long productId) {
        FilterCake filterCake = new FilterCake();
        filterCake = findFilterCakeByFilterCakeName(filterCakeSimple.filterCakeName);
        //todo : 用料量信息在简化的原料信息里面
        return filterCake;
    }
    // 用于返回滤饼简单信息
    public static class FilterCakeSimple{
        private Long filterCakeId;
        private String filterCakeName;
        private Double inventory;

        public Long getFilterCakeId() {
            return filterCakeId;
        }

        public void setFilterCakeId(Long filterCakeId) {
            this.filterCakeId = filterCakeId;
        }

        public String getFilterCakeName() {
            return filterCakeName;
        }

        public void setFilterCakeName(String filterCakeName) {
            this.filterCakeName = filterCakeName;
        }

        public Double getInventory() {
            return inventory;
        }

        public void setInventory(Double inventory) {
            this.inventory = inventory;
        }
    }

    // 根据调用者的productId filterCakeId 结合自身找到对应的投料量
    public Double getInventory(List<RelProductFilterCake> relProductFilterCakeList, Long productId, Long filterCakeId){
        for(RelProductFilterCake relProductFilterCake: relProductFilterCakeList){
            if((relProductFilterCake.getId().getProductId().longValue() == productId.longValue()) && (relProductFilterCake.getId().getFilterCakeId().longValue() == filterCakeId.longValue())){
                System.out.println("返回的Inventory:" + relProductFilterCake.getInventory());
                return relProductFilterCake.getInventory();
            }
        }
        return -1.0;
    }

    public FilterCakeSimple simplifyFilterCake(FilterCake filterCake, Long productId){
        FilterCakeSimple filterCakeSimple = new FilterCakeSimple();
        filterCakeSimple.setFilterCakeId(filterCake.getFilterCakeId());
        filterCakeSimple.setFilterCakeName(filterCake.getFilterCakeName());
        filterCakeSimple.setInventory(getInventory(filterCake.getRelProductFilterCakeList(),
                productId,filterCake.getFilterCakeId()));
        return filterCakeSimple;
    }


    //查
    //-------------------------------------------------------------------------
    public FilterCakePackage findAll(Integer pageNo,
                                    Integer pageSize
    ) {
        Integer filterCakeNum = filterCakeRepository.findAll().size();
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<FilterCake> page = filterCakeRepository.findAll(pageable);
        return packFilterCake(page.toList(),pageNo,pageSize,filterCakeNum);
    }

    public List<FilterCakeName> findAllFilterCakeName(){
        List<FilterCakeName> filterCakeNameList = new ArrayList<>();
        for(FilterCake filterCake: filterCakeRepository.findAll()){
            FilterCakeName filterCakeName = new FilterCakeName();
            filterCakeName.setFilterCakeId(filterCake.getFilterCakeId());
            filterCakeName.setFilterCakeName(filterCake.getFilterCakeName());
            filterCakeNameList.add(filterCakeName);
        }
        return filterCakeNameList;
    }


    public FilterCakePackage findAllFilterCakeByCondition(
            String typeOfQuery, String conditionOfQuery,
            Integer pageNo, Integer pageSize
    ){
        List<FilterCake> filterCakeList = new ArrayList<>();

        switch (typeOfQuery){
            case "滤饼名称":
                filterCakeList = filterCakeRepository.findAllByFilterCakeNameContaining(conditionOfQuery);
                break;
            case "滤饼编号":
                filterCakeList = filterCakeRepository.findAllByFilterCakeIndexContaining(conditionOfQuery);
                break;
            case "滤饼颜色":
                filterCakeList = filterCakeRepository.findAllByFilterCakeColorContaining(conditionOfQuery);
                break;
        }
        return packFilterCake(
                utils.pageList(filterCakeList, pageNo, pageSize),pageNo,pageSize,filterCakeList.size()
        );
    }

    public FilterCakeService.FilterCakeStandard findFilterCakeByFilterCakeId(Long filterCakeId){
        if(filterCakeRepository.findFilterCakeByFilterCakeId(filterCakeId) == null){
            return new FilterCakeStandard();
        }
        return  FilterCakeStandardization(filterCakeRepository.findFilterCakeByFilterCakeId(filterCakeId));
    }

    public  FilterCake findFilterCakeByFilterCakeName(String filterCakeName){
        return filterCakeRepository.findFilterCakeByFilterCakeName(filterCakeName);
    }

    public List<FilterCake> findAllByFilterCakeNameContaining(String filterCakeName) {
        // 空字符串返回全部值
        if (utils.isEmptyString(filterCakeName)) {
            return filterCakeRepository.findAll();
        }
        return filterCakeRepository.findAllByFilterCakeNameContaining(filterCakeName);
    }

    public Set<Product> findProductsByFilterCakeName(String filterCakeName) {
        FilterCake filterCake = findFilterCakeByFilterCakeName(filterCakeName);
        Set<Product> productSet = new HashSet<>();
        if (filterCake != null) {
            productSet.addAll(filterCake.getProductList());
        }
        System.out.println("滤饼对应的产品集合大小");
        System.out.println(productSet.size());
        return productSet;
    }



    //增
    //-------------------------------------------------------------------------
    public FilterCake addFilterCake(FilterCake filterCake) {
        return filterCakeRepository.save(filterCake);
    }


    //改
    //-------------------------------------------------------------------------
    // update FilterCake data
    public FilterCake updateFilterCake(Long filterCakeId, FilterCake updatedFilterCake) {
        return filterCakeRepository.findById(filterCakeId)
                .map(filterCake -> {
                    filterCake.setFilterCakeName(updatedFilterCake.getFilterCakeName());
                    filterCake.setFilterCakeIndex(updatedFilterCake.getFilterCakeIndex());
                    filterCake.setFilterCakeColor(updatedFilterCake.getFilterCakeColor());
                    filterCake.setFilterCakeProcessingCost(updatedFilterCake.getFilterCakeProcessingCost());
                    filterCake.setFilterCakeAccountingQuantity(updatedFilterCake.getFilterCakeAccountingQuantity());
                    filterCake.setFilterCakeSpecification(updatedFilterCake.getFilterCakeSpecification());
                    filterCake.setFilterCakeRemarks(updatedFilterCake.getFilterCakeRemarks());
                    return filterCakeRepository.save(filterCake);
                })
                .orElseThrow(() -> new NoSuchElementException("FilterCake not found with id " + filterCakeId));
    }

    //    @Transactional
    //    public void deleteByFilterCakeId(Long filterCakeId) {
    //        filterCakeRepository.deleteByFilterCakeId(filterCakeId);
    //    }


}
