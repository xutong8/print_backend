package com.zju.vis.print_backend.service;

import com.zju.vis.print_backend.Utils.*;
import com.zju.vis.print_backend.compositekey.RelFilterCakeFilterCakeKey;
import com.zju.vis.print_backend.compositekey.RelFilterCakeRawMaterialKey;
import com.zju.vis.print_backend.dao.FilterCakeRepository;
import com.zju.vis.print_backend.dao.RawMaterialRepository;
import com.zju.vis.print_backend.entity.*;
import com.zju.vis.print_backend.vo.ExcelFilterCakeVo;
import com.zju.vis.print_backend.vo.ExcelFilterCakeWriteVo;
import com.zju.vis.print_backend.vo.ExcelRawMaterialWriteVo;
import com.zju.vis.print_backend.vo.ResultVo;
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
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class FilterCakeService {
    @Resource
    FilterCakeRepository filterCakeRepository;

    @Resource
    private RawMaterialRepository rawMaterialRepository;

    @Resource
    private RawMaterialService rawMaterialService;

    // 关联表
    @Resource
    private RelProductFilterCakeService relProductFilterCakeService;

    @Resource
    private RelFilterCakeFilterCakeService relFilterCakeFilterCakeService;

    @Resource
    private RelFilterCakeRawMaterialService relFilterCakeRawMaterialService;


    // 调用一般方法
    Utils utils = new Utils();

    // 导入excel文件
    @Resource
    private ExcelUtil excelUtil;

    @Resource
    private FileService fileService;

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
            filterCakeStandardList.add(filterCakeStandardization(filterCake));
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
        private Long filterCakeId;                  //滤饼id
        private String filterCakeName;              //滤饼名称
        private String filterCakeIndex;             //滤饼编号
        private String filterCakeColor;             //滤饼颜色
        private Float filterCakeProcessingCost;     //滤饼批处理价格
        private Integer filterCakeAccountingQuantity;//滤饼批次生产数量
        private Double filterCakeUnitPrice;         //滤饼单位价格
        private Integer filterCakePriceIncreasePercent;//滤饼价格增幅比例
        private String filterCakeSpecification;     //滤饼规格
        private String filterCakeRemarks;           //备注
        private List<RawMaterialService.RawMaterialSimple> rawMaterialSimpleList;
        private List<FilterCakeService.FilterCakeSimple> filterCakeSimpleList;

        public List<RawMaterialService.RawMaterialSimple> getRawMaterialSimpleList() {
            return rawMaterialSimpleList;
        }

        public void setRawMaterialSimpleList(List<RawMaterialService.RawMaterialSimple> rawMaterialSimpleList) {
            this.rawMaterialSimpleList = rawMaterialSimpleList;
        }

        public List<FilterCakeSimple> getFilterCakeSimpleList() {
            return filterCakeSimpleList;
        }

        public void setFilterCakeSimpleList(List<FilterCakeSimple> filterCakeSimpleList) {
            this.filterCakeSimpleList = filterCakeSimpleList;
        }

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

        public Float getFilterCakeProcessingCost() {
            return filterCakeProcessingCost;
        }

        public void setFilterCakeProcessingCost(Float filterCakeProcessingCost) {
            this.filterCakeProcessingCost = filterCakeProcessingCost;
        }

        public Integer getFilterCakeAccountingQuantity() {
            return filterCakeAccountingQuantity;
        }

        public void setFilterCakeAccountingQuantity(Integer filterCakeAccountingQuantity) {
            this.filterCakeAccountingQuantity = filterCakeAccountingQuantity;
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
    public FilterCakeStandard filterCakeStandardization(FilterCake filterCake){
        FilterCakeStandard filterCakeStandard = new FilterCakeStandard();
        filterCakeStandard.setFilterCakeId(filterCake.getFilterCakeId());
        filterCakeStandard.setFilterCakeName(filterCake.getFilterCakeName());
        filterCakeStandard.setFilterCakeIndex(filterCake.getFilterCakeIndex());
        filterCakeStandard.setFilterCakeColor(filterCake.getFilterCakeColor());
        filterCakeStandard.setFilterCakeProcessingCost(filterCake.getFilterCakeProcessingCost());
        filterCakeStandard.setFilterCakeAccountingQuantity(filterCake.getFilterCakeAccountingQuantity());
        // 设置滤饼单价 当前为假数据 todo：递归计算真实数据
        filterCakeStandard.setFilterCakeUnitPrice(
                // 真实数据测试无问题
                // calculateFilterCakePrice(filterCake)
                Math.random() * (500.0 - 10.0) + 10.0
        );
        // 设置滤饼价格涨幅 当前为假数据 todo：递归计算真实数据
        filterCakeStandard.setFilterCakePriceIncreasePercent(
                (int) (Math.random() * 100) - 50
        );
        filterCakeStandard.setFilterCakeSpecification(filterCake.getFilterCakeSpecification());
        filterCakeStandard.setFilterCakeRemarks(filterCake.getFilterCakeRemarks());

        // 设置返回的简单滤饼表
        List<FilterCakeService.FilterCakeSimple> filterCakeSimpleList = new ArrayList<>();
        for (FilterCake filterCake1 : filterCake.getFilterCakeList()) {
            filterCakeSimpleList.add(simplifyFilterCakeF(filterCake1, filterCake.getFilterCakeId()));
        }
        filterCakeStandard.setFilterCakeSimpleList(filterCakeSimpleList);

        // 设置返回的简单原料表
        List<RawMaterialService.RawMaterialSimple> rawMaterialSimpleList = new ArrayList<>();
        for (RawMaterial rawMaterial : filterCake.getRawMaterialList()) {
            rawMaterialSimpleList.add(rawMaterialService.simplifyRawMaterialF(rawMaterial, filterCake.getFilterCakeId()));
        }
        filterCakeStandard.setRawMaterialSimpleList(rawMaterialSimpleList);

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
                // System.out.println("返回滤饼产品的Inventory:" + relProductFilterCake.getInventory());
                return relProductFilterCake.getInventory();
            }
        }
        return -1.0;
    }

    public Double getInventoryF(List<RelFilterCakeFilterCake> relFilterCakeFilterCakeList, Long filterCakeId, Long filterCakeIdUsed){
        for(RelFilterCakeFilterCake relFilterCakeFilterCake: relFilterCakeFilterCakeList){
            if((relFilterCakeFilterCake.getId().getFilterCakeId().longValue() == filterCakeId.longValue()) && (relFilterCakeFilterCake.getId().getFilterCakeIdUsed().longValue() == filterCakeIdUsed.longValue())){
                // System.out.println("返回滤饼滤饼的Inventory:" + relFilterCakeFilterCake.getInventory());
                return relFilterCakeFilterCake.getInventory();
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

    public FilterCakeSimple simplifyFilterCakeF(FilterCake filterCake, Long filterCakeId){
        FilterCakeSimple filterCakeSimple = new FilterCakeSimple();
        filterCakeSimple.setFilterCakeId(filterCake.getFilterCakeId());
        filterCakeSimple.setFilterCakeName(filterCake.getFilterCakeName());
        filterCakeSimple.setInventory(getInventoryF(filterCake.getRelFilterCakeFilterCakeListUsed(),
                filterCakeId,filterCake.getFilterCakeId()));
        return filterCakeSimple;
    }

    public Double calculateFilterCakePrice(FilterCake filterCake){
        // 标准化获取简化列表
        List<FilterCakeService.FilterCakeSimple> filterCakeSimpleList = new ArrayList<>();
        for (FilterCake filterCake1 : filterCake.getFilterCakeList()) {
            filterCakeSimpleList.add(simplifyFilterCakeF(filterCake1, filterCake.getFilterCakeId()));
        }

        // 设置返回的简单原料表
        List<RawMaterialService.RawMaterialSimple> rawMaterialSimpleList = new ArrayList<>();
        for (RawMaterial rawMaterial : filterCake.getRawMaterialList()) {
            rawMaterialSimpleList.add(rawMaterialService.simplifyRawMaterialF(rawMaterial, filterCake.getFilterCakeId()));
        }

        Double sum = 0.0;
        sum += filterCake.getFilterCakeProcessingCost();
        if(filterCakeSimpleList.size() != 0){
            for(FilterCakeSimple filterCakeSimple: filterCakeSimpleList){
                System.out.println("currnt filtercake id :" + filterCakeSimple.getFilterCakeId());
                sum +=  filterCakeSimple.getInventory() * calculateFilterCakePrice(filterCakeRepository.findFilterCakeByFilterCakeId(filterCakeSimple.getFilterCakeId()));
            }
        }
        for(RawMaterialService.RawMaterialSimple rawMaterialSimple: rawMaterialSimpleList){
            sum += rawMaterialSimple.getInventory() * rawMaterialService.findRawMaterialByRawMaterialId(rawMaterialSimple.getRawMaterialId()).getRawMaterialUnitPrice();
        }
        return sum / filterCake.getFilterCakeAccountingQuantity();
    }

    public Double calculateFilterCakeHistoryPrice(FilterCake filterCake, Date historyDate){
        // 标准化获取简化列表
        List<FilterCakeService.FilterCakeSimple> filterCakeSimpleList = new ArrayList<>();
        for (FilterCake filterCake1 : filterCake.getFilterCakeList()) {
            filterCakeSimpleList.add(simplifyFilterCakeF(filterCake1, filterCake.getFilterCakeId()));
        }
        // 设置返回的简单原料表
        List<RawMaterialService.RawMaterialSimple> rawMaterialSimpleList = new ArrayList<>();
        for (RawMaterial rawMaterial : filterCake.getRawMaterialList()) {
            rawMaterialSimpleList.add(rawMaterialService.simplifyRawMaterialF(rawMaterial, filterCake.getFilterCakeId()));
        }
        Double sum = 0.0;
        // 加上批处理价格
        sum += filterCake.getFilterCakeProcessingCost();
        // 获取滤饼历史价格
        if(filterCakeSimpleList.size()!=0){
            for(FilterCakeSimple filterCakeSimple: filterCakeSimpleList){
                sum +=  filterCakeSimple.getInventory() * calculateFilterCakeHistoryPrice(filterCakeRepository.findFilterCakeByFilterCakeId(filterCakeSimple.getFilterCakeId()),historyDate);
            }
        }
        // 获取原料历史价格
        for(RawMaterialService.RawMaterialSimple rawMaterialSimple: rawMaterialSimpleList){
            List<Utils.HistoryPrice> historyPriceList = rawMaterialService.findRawMaterialByRawMaterialId(rawMaterialSimple.getRawMaterialId()).getRawMaterialHistoryPrice();
            if(historyPriceList.size()!=0){
                // 标识是否有增加过历史数据，没有则说明最早没有当时的历史数据则取当前原料最早的数据作为当时的虚拟数据
                boolean flag = false;
                // 逆序日期从大到小排序 2023.01.15 > 2022.12.31
                Collections.reverse(historyPriceList);
                for(Utils.HistoryPrice historyPrice: historyPriceList){
                    // 只有日期默认是 8.00 开始算，因此默认减一天时间进行比较
                    // 取第一个小于当前日期的价格
                    if(historyPrice.getDate().getTime() - (86400*1000) <= historyDate.getTime()){
                        // System.out.println("选取的时间:" + historyPrice.getDate());
                        sum += rawMaterialSimple.getInventory() * historyPrice.getPrice();
                        flag = true;
                        break;
                    }
                }
                if(!flag){
                    sum += rawMaterialSimple.getInventory() * historyPriceList.get(historyPriceList.size() - 1).getPrice();
                }
            }
        }
        // System.out.println("sum: " + sum + "  filterCake.getFilterCakeAccountingQuantity(): " + filterCake.getFilterCakeAccountingQuantity());
        return sum / filterCake.getFilterCakeAccountingQuantity();
    }

    // 列表形式返回历史价格，不过由于当前返回形式未确定因此这部分暂时空置
    // public List<Utils.HistoryPrice> getFilterCakeHistoryPriceList(FilterCake filterCake){
    public List<Utils.HistoryPrice> getFilterCakeHistoryPriceList(Long filterCakeId){
        FilterCake filterCake = filterCakeRepository.findFilterCakeByFilterCakeId(filterCakeId);
        List<Utils.HistoryPrice> historyPriceList = new ArrayList<>();
        // SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd");
        for(int i = 0;i < 12 ; i++){
            Date date = stepMonth(new Date(),-i);
            // System.out.println(dateFormat.format(date));
            Utils.HistoryPrice historyPrice = new Utils.HistoryPrice();
            historyPrice.setDate(new java.sql.Date(date.getTime()));
            historyPrice.setPrice(calculateFilterCakeHistoryPrice(filterCake,date));
            historyPriceList.add(historyPrice);
        }
        return historyPriceList;
    }

    public static Date stepMonth(Date sourceDate, int month) {
        Calendar c = Calendar.getInstance();
        c.setTime(sourceDate);
        c.add(Calendar.MONTH, month);
        return c.getTime();
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
        return  filterCakeStandardization(filterCakeRepository.findFilterCakeByFilterCakeId(filterCakeId));
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
    public static class DeStandardizeResult{
        private FilterCake filtercake;
        private List<RelFilterCakeRawMaterial> relFilterCakeRawMaterialList;
        private List<RelFilterCakeFilterCake> relFilterCakeFilterCakeList;

        // 构造函数
        public DeStandardizeResult(FilterCake filtercake, List<RelFilterCakeRawMaterial> relFilterCakeRawMaterialList, List<RelFilterCakeFilterCake> relFilterCakeFilterCakeList) {
            this.filtercake = filtercake;
            this.relFilterCakeRawMaterialList = relFilterCakeRawMaterialList;
            this.relFilterCakeFilterCakeList = relFilterCakeFilterCakeList;
        }

        public FilterCake getFiltercake() {
            return filtercake;
        }

        public void setFiltercake(FilterCake filtercake) {
            this.filtercake = filtercake;
        }

        public List<RelFilterCakeRawMaterial> getRelFilterCakeRawMaterialList() {
            return relFilterCakeRawMaterialList;
        }

        public void setRelFilterCakeRawMaterialList(List<RelFilterCakeRawMaterial> relFilterCakeRawMaterialList) {
            this.relFilterCakeRawMaterialList = relFilterCakeRawMaterialList;
        }

        public List<RelFilterCakeFilterCake> getRelFilterCakeFilterCakeList() {
            return relFilterCakeFilterCakeList;
        }

        public void setRelFilterCakeFilterCakeList(List<RelFilterCakeFilterCake> relFilterCakeFilterCakeList) {
            this.relFilterCakeFilterCakeList = relFilterCakeFilterCakeList;
        }
    }

    // 解包标准类
    public DeStandardizeResult deStandardizeFilterCake(FilterCakeStandard filterCakeStandard){
        FilterCake filterCake = new FilterCake();
        filterCake.setFilterCakeId(filterCakeStandard.getFilterCakeId());
        filterCake.setFilterCakeName(filterCakeStandard.getFilterCakeName());
        filterCake.setFilterCakeIndex(filterCakeStandard.getFilterCakeIndex());
        filterCake.setFilterCakeColor(filterCakeStandard.getFilterCakeColor());
        filterCake.setFilterCakeProcessingCost(filterCakeStandard.getFilterCakeProcessingCost());
        filterCake.setFilterCakeAccountingQuantity(filterCakeStandard.getFilterCakeAccountingQuantity());
        filterCake.setFilterCakeSpecification(filterCakeStandard.getFilterCakeSpecification());
        filterCake.setFilterCakeRemarks(filterCakeStandard.getFilterCakeRemarks());

        // 关系表设置
        List<RelFilterCakeRawMaterial> relFilterCakeRawMaterialList = new ArrayList<>();
        if(filterCakeStandard.getRawMaterialSimpleList()!=null){
            for(RawMaterialService.RawMaterialSimple rawMaterialSimple: filterCakeStandard.getRawMaterialSimpleList()){
                Long rawMaterialId = rawMaterialSimple.getRawMaterialId();
                Double inventory = rawMaterialSimple.getInventory();

                RelFilterCakeRawMaterial relFilterCakeRawMaterial = new RelFilterCakeRawMaterial();
                RelFilterCakeRawMaterialKey relFilterCakeRawMaterialKey = new RelFilterCakeRawMaterialKey();
                relFilterCakeRawMaterialKey.setFilterCakeId(filterCake.getFilterCakeId());
                relFilterCakeRawMaterialKey.setRawMaterialId(rawMaterialId);
                relFilterCakeRawMaterial.setId(relFilterCakeRawMaterialKey);
                relFilterCakeRawMaterial.setInventory(inventory);

                relFilterCakeRawMaterialList.add(relFilterCakeRawMaterial);
            }
        }


        List<RelFilterCakeFilterCake> relFilterCakeFilterCakeList = new ArrayList<>();
        if(filterCakeStandard.getFilterCakeSimpleList()!=null){
            for(FilterCakeSimple filterCakeSimple: filterCakeStandard.getFilterCakeSimpleList()){
                Long filterCakeUsedId = filterCakeSimple.getFilterCakeId();
                Double inventory = filterCakeSimple.getInventory();

                RelFilterCakeFilterCake relFilterCakeFilterCake = new RelFilterCakeFilterCake();
                RelFilterCakeFilterCakeKey relFilterCakeFilterCakeKey = new RelFilterCakeFilterCakeKey();
                relFilterCakeFilterCakeKey.setFilterCakeId(filterCake.getFilterCakeId());
                relFilterCakeFilterCakeKey.setFilterCakeIdUsed(filterCakeUsedId);
                relFilterCakeFilterCake.setId(relFilterCakeFilterCakeKey);
                relFilterCakeFilterCake.setInventory(inventory);

                relFilterCakeFilterCakeList.add(relFilterCakeFilterCake);
            }
        }
        return new DeStandardizeResult(filterCake,relFilterCakeRawMaterialList,relFilterCakeFilterCakeList);
    }

    private void saveRelFilterCakeRawMaterials(FilterCake savedFilterCake, List<RelFilterCakeRawMaterial> relFilterCakeRawMaterialList){
        for(RelFilterCakeRawMaterial relFilterCakeRawMaterial: relFilterCakeRawMaterialList){
            // 使用调用者id不一定与原id相等
            relFilterCakeRawMaterial.getId().setFilterCakeId(savedFilterCake.getFilterCakeId());

            relFilterCakeRawMaterial.setFilterCake(savedFilterCake);
            relFilterCakeRawMaterial.setRawMaterial(rawMaterialRepository.findRawMaterialByRawMaterialId(relFilterCakeRawMaterial.getId().getRawMaterialId()));
            relFilterCakeRawMaterialService.addRelFilterCakeRawMaterial(relFilterCakeRawMaterial);
        }
    }

    private void saveRelFilterCakeFilterCakes(FilterCake savedFilterCake, List<RelFilterCakeFilterCake> relFilterCakeFilterCakeList){
        for(RelFilterCakeFilterCake relFilterCakeFilterCake: relFilterCakeFilterCakeList){
            relFilterCakeFilterCake.getId().setFilterCakeId(savedFilterCake.getFilterCakeId());

            relFilterCakeFilterCake.setFilterCake(savedFilterCake);
            relFilterCakeFilterCake.setFilterCakeUsed(filterCakeRepository.findFilterCakeByFilterCakeId(relFilterCakeFilterCake.getId().getFilterCakeIdUsed()));
            relFilterCakeFilterCakeService.addRelFilterCakeFilterCake(relFilterCakeFilterCake);
        }
    }



    public FilterCake addFilterCake(FilterCakeStandard filterCakeStandard) {
        DeStandardizeResult result = deStandardizeFilterCake(filterCakeStandard);
        FilterCake filterCake = result.getFiltercake();
        List<RelFilterCakeRawMaterial> relFilterCakeRawMaterialList = result.getRelFilterCakeRawMaterialList();
        List<RelFilterCakeFilterCake> relFilterCakeFilterCakeList = result.getRelFilterCakeFilterCakeList();
        // 添加时指定一个不存在的id进而使用自增id
        filterCake.setFilterCakeId(new Long(0));
        FilterCake savedFilterCake = filterCakeRepository.save(filterCake);
        saveRelFilterCakeRawMaterials(savedFilterCake,relFilterCakeRawMaterialList);
        saveRelFilterCakeFilterCakes(savedFilterCake,relFilterCakeFilterCakeList);
        return savedFilterCake;
    }


    //改
    //-------------------------------------------------------------------------
    // update FilterCake data
    public String updateFilterCake(FilterCakeStandard updatedFilterCake) {
        FilterCake originFilterCake = filterCakeRepository.findFilterCakeByFilterCakeId(updatedFilterCake.getFilterCakeId());
        if(originFilterCake == null){
            FilterCake addedFilterCake = addFilterCake(updatedFilterCake);
            return "数据库中不存在对应数据,已添加Id为" + addedFilterCake.getFilterCakeName() + "的条目" ;
        }
        // 先删掉原先的关系删除单向关系
        // 删除原料关联表
        for(RelFilterCakeRawMaterial relFilterCakeRawMaterial: originFilterCake.getRelFilterCakeRawMaterialList()){
            relFilterCakeRawMaterialService.delete(relFilterCakeRawMaterial);
        }
        // 删除滤饼关联表
        for(RelFilterCakeFilterCake relFilterCakeFilterCake: originFilterCake.getRelFilterCakeFilterCakeListUser()){
            relFilterCakeFilterCakeService.delete(relFilterCakeFilterCake);
        }

        // 重新添加关系以修改内容
        DeStandardizeResult result = deStandardizeFilterCake(updatedFilterCake);
        FilterCake filterCake = result.getFiltercake();
        List<RelFilterCakeFilterCake> relFilterCakeFilterCakeLList = result.getRelFilterCakeFilterCakeList();
        List<RelFilterCakeRawMaterial> relFilterCakeRawMaterialList = result.getRelFilterCakeRawMaterialList();

        FilterCake savedFilterCake = filterCakeRepository.save(filterCake);
        saveRelFilterCakeFilterCakes(savedFilterCake,relFilterCakeFilterCakeLList);
        saveRelFilterCakeRawMaterials(savedFilterCake,relFilterCakeRawMaterialList);
        return "FilterCake " + originFilterCake.getFilterCakeName() + " has been changed";
    }

    //删
    //-------------------------------------------------------------------------
    public void deleteByFilterCakeId(Long filterCakeId){
        deleteRelByFilterCakeId(filterCakeId);
        deleteFilterCakeByFilterCakeId(filterCakeId);
    }

    @Transactional
    public void deleteRelByFilterCakeId(Long filterCakeId){
        // 级联删除
        FilterCake filterCake = filterCakeRepository.findFilterCakeByFilterCakeId(filterCakeId);
        // 删除原料关联表
        for(RelFilterCakeRawMaterial relFilterCakeRawMaterial: filterCake.getRelFilterCakeRawMaterialList()){
            relFilterCakeRawMaterialService.delete(relFilterCakeRawMaterial);
        }

        // 删除产品关联表
        for(RelProductFilterCake relProductFilterCake: filterCake.getRelProductFilterCakeList()){
            relProductFilterCakeService.delete(relProductFilterCake);
        }

        // 删除滤饼关联表
        for(RelFilterCakeFilterCake relFilterCakeFilterCake: filterCake.getRelFilterCakeFilterCakeListUser()){
            relFilterCakeFilterCakeService.delete(relFilterCakeFilterCake);
        }

        // 删除滤饼被关联表
        for(RelFilterCakeFilterCake relFilterCakeFilterCake: filterCake.getRelFilterCakeFilterCakeListUsed()){
            relFilterCakeFilterCakeService.delete(relFilterCakeFilterCake);
        }
    }

    @Transactional
    public void deleteFilterCakeByFilterCakeId(Long filterCakeId){
        FilterCake filterCakeToDelete = filterCakeRepository.findFilterCakeByFilterCakeId(filterCakeId);
        filterCakeRepository.delete(filterCakeToDelete);
    }

    // 导入文件
    //-------------------------------------------------------------------------
    public ResultVo importFilterCakeExcelAndPersistence(MultipartFile file){
        ResultVo<List<ExcelFilterCakeVo>> importResult = fileService.importEntityExcel(file,ExcelFilterCakeVo.class);
        if(!importResult.checkSuccess()){
            log.error(importResult.getMsg());
            return importResult;
        }
        List<ExcelFilterCakeVo> excelFilterCakeVos = importResult.getData();
        for(ExcelFilterCakeVo excelFilterCakeVo: excelFilterCakeVos){
            // excel信息转化为标准类
            FilterCakeStandard filterCakeStandard = transExcelToStandard(excelFilterCakeVo);
            // 更新数据库，已存在则会直接替换
            updateFilterCake(filterCakeStandard);
        }
        return ResultVoUtil.success(excelFilterCakeVos);
    }

    public FilterCakeStandard transExcelToStandard(ExcelFilterCakeVo excelFilterCakeVo){
        FilterCakeStandard filterCakeStandard = new FilterCakeStandard();
        // 如果已经存在了则修改，否则则添加
        if(filterCakeRepository.findFilterCakeByFilterCakeName(excelFilterCakeVo.getFilterCakeName()) == null){
            // 表示添加
            filterCakeStandard.setFilterCakeId(new Long(0));
        }else{
            filterCakeStandard.setFilterCakeId(filterCakeRepository.findFilterCakeByFilterCakeName(excelFilterCakeVo.getFilterCakeName()).getFilterCakeId());
        }
        filterCakeStandard.setFilterCakeName(excelFilterCakeVo.getFilterCakeName());
        filterCakeStandard.setFilterCakeIndex(excelFilterCakeVo.getFilterCakeIndex());
        filterCakeStandard.setFilterCakeColor(excelFilterCakeVo.getFilterCakeColor());
        filterCakeStandard.setFilterCakeProcessingCost(excelFilterCakeVo.getFilterCakeProcessingCost());
        filterCakeStandard.setFilterCakeAccountingQuantity(excelFilterCakeVo.getFilterCakeAccountingQuantity());
        filterCakeStandard.setFilterCakeSpecification(excelFilterCakeVo.getFilterCakeSpecification());
        filterCakeStandard.setFilterCakeRemarks(excelFilterCakeVo.getFilterCakeRemarks());
        return filterCakeStandard;
    }

    // 导出文件
    //-------------------------------------------------------------------------
    public ResultVo<String> exportFilterCakeExcel(HttpServletResponse response){
        // 1.根据查询条件获取结果集
        List<ExcelFilterCakeWriteVo> excelFilterCakeWriteVos = getExcelFilterCakeWriteVoListByCondition();
        if (CollectionUtil.isEmpty(excelFilterCakeWriteVos)) {
            log.info("【导出Excel文件】要导出的数据为空，无法导出！");
            return ResultVoUtil.success("数据为空");
        }
        // 2.获取要下载Excel文件的路径
        ResultVo<String> resultVo = fileService.getDownLoadPath(ExcelFilterCakeWriteVo.class,excelFilterCakeWriteVos);
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

    public List<ExcelFilterCakeWriteVo> getExcelFilterCakeWriteVoListByCondition(){
        List<ExcelFilterCakeWriteVo> excelFilterCakeWriteVos = new ArrayList<>();
        for(FilterCake filterCake: filterCakeRepository.findAll()){
            excelFilterCakeWriteVos.add(transFilterCakeToExcel(filterCake));
        }
        return excelFilterCakeWriteVos;
    }

    public ExcelFilterCakeWriteVo transFilterCakeToExcel(FilterCake filterCake){
        ExcelFilterCakeWriteVo excelFilterCakeWriteVo = new ExcelFilterCakeWriteVo();
        excelFilterCakeWriteVo.setFilterCakeName(filterCake.getFilterCakeName());
        excelFilterCakeWriteVo.setFilterCakeIndex(filterCake.getFilterCakeIndex());
        excelFilterCakeWriteVo.setFilterCakeColor(filterCake.getFilterCakeColor());
        excelFilterCakeWriteVo.setFilterCakeAccountingQuantity(filterCake.getFilterCakeAccountingQuantity());
        excelFilterCakeWriteVo.setFilterCakeProcessingCost(filterCake.getFilterCakeProcessingCost());
        excelFilterCakeWriteVo.setFilterCakeSpecification(filterCake.getFilterCakeSpecification());
        excelFilterCakeWriteVo.setFilterCakeRemarks(filterCake.getFilterCakeRemarks());
        return excelFilterCakeWriteVo;
    }
}
