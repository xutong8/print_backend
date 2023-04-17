package com.zju.vis.print_backend.service;

import com.zju.vis.print_backend.dao.FilterCakeRepository;
import com.zju.vis.print_backend.entity.FilterCake;
import com.zju.vis.print_backend.entity.Product;
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
    public class FilterCakeStandard{
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



    public boolean isEmptyString(String string) {
        return string == null || string.isEmpty();
    }

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

    public  FilterCake findFilterCakeByFilterCakeName(String filterCakeName){
        return filterCakeRepository.findFilterCakeByFilterCakeName(filterCakeName);
    }

    public List<FilterCake> findAllByFilterCakeNameContaining(String filterCakeName) {
        // 空字符串返回全部值
        if (isEmptyString(filterCakeName)) {
            return filterCakeRepository.findAll();
        }
        return filterCakeRepository.findAllByFilterCakeNameContaining(filterCakeName);
    }

    public Set<Product> findProductsByFilterCakeName(String filterCakeName) {
        FilterCake filterCake = findFilterCakeByFilterCakeName(filterCakeName);
        Set<Product> productSet = new HashSet<>();
        if (filterCake != null) {
            productSet.addAll(filterCake.getProductList());
            // for (FilterCake filterCake : filterCakeList) {
            //     List<Product> productList = filterCake.getProductList();
            //     productSet.addAll(productList);
            //     // if (productList != null && productList.size() > 0) {
            //     //     for (Product product : productList) {
            //     //         productSet.add(product);
            //     //     }
            //     // }
            // }
        }
        System.out.println("滤饼对应的产品集合大小");
        System.out.println(productSet.size());
        return productSet;
    }

    //    @Transactional
//    public void deleteByFilterCakeId(Long filterCakeId) {
//        filterCakeRepository.deleteByFilterCakeId(filterCakeId);
//    }
    public FilterCake addFilterCake(FilterCake filterCake) {
        return filterCakeRepository.save(filterCake);
    }

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
}
