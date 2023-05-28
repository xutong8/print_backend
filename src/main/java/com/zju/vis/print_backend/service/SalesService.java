package com.zju.vis.print_backend.service;

import com.zju.vis.print_backend.Utils.CollectionUtil;
import com.zju.vis.print_backend.Utils.FileUtil;
import com.zju.vis.print_backend.Utils.ResultVoUtil;
import com.zju.vis.print_backend.Utils.Utils;
import com.zju.vis.print_backend.dao.ProductRepository;
import com.zju.vis.print_backend.dao.SalesRepository;
import com.zju.vis.print_backend.entity.Product;
import com.zju.vis.print_backend.entity.ProductSeries;
import com.zju.vis.print_backend.entity.Sales;
import com.zju.vis.print_backend.vo.*;
import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.zju.vis.print_backend.Utils.Utils.stepMonth;
import static com.zju.vis.print_backend.Utils.Utils.stringToDate;

@Slf4j
@Service
public class SalesService {

    @Resource
    private SalesRepository salesRepository;

    @Resource
    private ProductRepository productRepository;

    // 调用一般方法
    Utils utils = new Utils();

    // 调用其他Service的方法
    @Resource
    private FileService fileService;

    @Resource
    private ProductService productService;

    //查
    //-------------------------------------------------------------------------

    // 单产品查询
    public ResultVo getSingleProductSales(String productName, String endTime, String timeSpan){
        Product product = productRepository.findProductByProductName(productName);
        if(product == null){
            return ResultVoUtil.error("产品名没有对应的产品请仔细检查");
        }
        int month = 0;
        switch (timeSpan){
            case "最近三个月": month = -3; break;
            case "最近半年": month = -6; break;
            case "最近一年": month = -12; break;
            case "最近两年": month = -24; break;
            // 0 表示获取所有数据
            case "全部数据": month = 0; break;
        }
        List<Sales> salesList = new ArrayList<Sales>();
        Date endTimeDate = stringToDate(endTime);
        Date startTimeDate = stepMonth(endTimeDate, month);
        if(month == 0){
            salesList = salesRepository.findAllByProductIndexEquals(product.getProductIndex());
            // 找到最早的数据
            Date earliestDate = new Date();
            for(Sales sales: salesList){
                if(sales.getDate().getTime() < earliestDate.getTime()){
                    earliestDate = sales.getDate();
                }
            }
            while(true){
                if(stepMonth(endTimeDate,month).getTime() < earliestDate.getTime()){
                    DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    log.info("最早的时间 --> " + sdf.format(earliestDate));
                    log.info("选取的月份 --> " + month + " 最早的时间 --> " + sdf.format(stepMonth(endTimeDate,month)));
                    break;
                }
                month--;
                if(month < -9999){
                    log.info("选取日期出错");
                    break;
                }
            }
            // Date date = stepMonth(new Date(),-i);
        }else{
            for(Sales sales: salesRepository.findAllByProductIndexEquals(product.getProductIndex())){
                if(startTimeDate.getTime() <= sales.getDate().getTime() &&  sales.getDate().getTime() <= endTimeDate.getTime()){
                    salesList.add(sales);
                }
            }
        }
        // 按照日期从小到大排序
        // Collections.sort(salesList, new Comparator<Sales>() {
        //     @Override
        //     public int compare(Sales o1, Sales o2) {
        //         if(o1.getDate().getTime() > o2.getDate().getTime()){
        //             return 1;
        //         }else{
        //             return -1;
        //         }
        //     }
        // });
        List<SalesStandardVo> salesStandardVos = new ArrayList<>();
        long totalNumber = 0;
        double totalProfit = 0.0;
        for(;month < 0; month++){
            long number = 0;
            double profit = 0.0;
            // System.out.println("-----------------------------------------------------------");
            // System.out.println("startTime: " + stepMonth(endTimeDate,month).toString() + "  endTime: " + stepMonth(endTimeDate,month + 1).toString());
            for(Sales sales: salesList){
                if(stepMonth(endTimeDate,month).getTime() <= sales.getDate().getTime() && sales.getDate().getTime() < stepMonth(endTimeDate,month + 1).getTime()){
                    number += sales.getNumber();
                    profit += sales.getNumber() * (sales.getUnitPrice() - productService.calculateProductHistoryPrice(product,sales.getDate()));
                    totalNumber += number;
                    totalProfit += profit;
                    // System.out.println("productName: " + productName + "  Index: "  + product.getProductIndex() + "  date: " + sales.getDate().toString());
                    // System.out.println("number: " + number + "  profit: " + profit);
                }
            }
            SalesStandardVo salesStandardVo = new SalesStandardVo();
            salesStandardVo.setProductName(productName);
            salesStandardVo.setProductIndex(product.getProductIndex());
            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            salesStandardVo.setStartTime(sdf.format(stepMonth(endTimeDate,month)));
            salesStandardVo.setEndTime(sdf.format(stepMonth(endTimeDate,month + 1)));
            salesStandardVo.setNumber(number);
            salesStandardVo.setProfit(profit);
            salesStandardVos.add(salesStandardVo);
        }
        SalesPackageVo salesPackageVo = new SalesPackageVo(totalNumber,totalProfit,salesStandardVos);
        return ResultVoUtil.success(200, "共检索到" + salesList.size() + "条销售数据" , salesPackageVo);
    }


    // TopN查询
    // method: 0 topN number  1 topN unitPrice
    public ResultVo findTopNSalesProduct(String endTime, String timeSpan ,Integer topNumber, Integer method){
        int month = 0;
        switch (timeSpan){
            case "最近三个月": month = -3; break;
            case "最近半年": month = -6; break;
            case "最近一年": month = -12; break;
            case "最近两年": month = -24; break;
            // 0 表示获取所有数据
            case "全部数据": month = 0; break;
        }
        List<Sales> salesList = new ArrayList<Sales>();
        Date endTimeDate = stringToDate(endTime);
        Date startTimeDate = stepMonth(endTimeDate, month);
        // 1.获取所有在时间范围内的数据
        if(month == 0){
            salesList = salesRepository.findAll();
        }else{
            for(Sales sales: salesRepository.findAll()){
                if(startTimeDate.getTime() <= sales.getDate().getTime() &&  sales.getDate().getTime() <= endTimeDate.getTime()){
                    salesList.add(sales);
                }
            }
        }
        HashMap<String, SalesPackSimple> salesMap = new HashMap<>();
        for(Sales sales: salesList){
            if(salesMap.containsKey(sales.getProductIndex())){
                // 同个地址的对象，直接修改值
                SalesPackSimple salesPackSimple = salesMap.get(sales.getProductIndex());
                long tempNumber = salesPackSimple.getTotalNumber() + sales.getNumber();
                double tempPrice = salesPackSimple.getTotalPrice() + sales.getNumber() * sales.getUnitPrice();
                salesPackSimple.getList().add(sales);
                salesPackSimple.setTotalNumber(tempNumber);
                salesPackSimple.setTotalPrice(tempPrice);
            }else{
                SalesPackSimple salesAdd = new SalesPackSimple();
                salesAdd.setTotalNumber(sales.getNumber());
                salesAdd.setTotalPrice(sales.getUnitPrice() * sales.getNumber());
                List<Sales> list = new ArrayList<>();
                list.add(sales);
                salesAdd.setList(list);
                salesMap.put(sales.getProductIndex(),salesAdd);
            }
        }

        // 2.转list用于对不同模式进行排序
        List<Map.Entry<String,SalesPackSimple>> listForSort = new ArrayList<Map.Entry<String,SalesPackSimple>>(salesMap.entrySet());
        // 按照销售数量排序
        if(method == 0){
            // 大到小排序
            Collections.sort(listForSort, new Comparator<Map.Entry<String, SalesPackSimple>>() {
                @Override
                public int compare(Map.Entry<String, SalesPackSimple> o1, Map.Entry<String, SalesPackSimple> o2) {
                    if(o1.getValue().getTotalNumber() < o2.getValue().getTotalNumber()){
                        return 1;
                    }else{
                        return -1;
                    }
                }
            });
        }
        // 按照销售总价排序
        else if(method == 1){
            // 大到小排序
            Collections.sort(listForSort, new Comparator<Map.Entry<String, SalesPackSimple>>() {
                @Override
                public int compare(Map.Entry<String, SalesPackSimple> o1, Map.Entry<String, SalesPackSimple> o2) {
                    if(o1.getValue().getTotalPrice() < o2.getValue().getTotalPrice()){
                        return 1;
                    }else{
                        return -1;
                    }
                }
            });
        }else{
            return ResultVoUtil.error("请输入有效的查询模式数字 0:TopN Number/1:TopN TotalPrice");
        }

        // 3.topN数据返回对应的销量值
        List<SalesTopNPack> ResultList = new ArrayList<>();
        for(int i = 0; i < topNumber && i < listForSort.size(); i++){
            Map.Entry<String,SalesPackSimple> TopiMap = listForSort.get(i);
            List<Product> TopiIndexProducts = productRepository.findAllByProductIndexEquals(TopiMap.getKey());
            List<SalesPackageVo> TopiSalesPackageList = new ArrayList<>();
            if(TopiIndexProducts.size()>0){
                for(Product product: TopiIndexProducts){
                    TopiSalesPackageList.add((SalesPackageVo)getSingleProductSales(product.getProductName(),endTime,timeSpan).getData());
                }
            }
            ResultList.add(new SalesTopNPack(i+1,TopiMap.getKey(),TopiIndexProducts.size(),TopiSalesPackageList));
        }
        return ResultVoUtil.success(200,"ResultList总数为" + ResultList.size(),ResultList);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SalesTopNPack{
        private Integer topNum;
        private String productIndex;
        private Integer productNum;
        private List<SalesPackageVo> list;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SalesPackSimple{
        private Long totalNumber;
        private Double totalPrice;
        private List<Sales> list;
    }

    // 增
    //-------------------------------------------------------------------------
    public ResultVo saveSales(Sales sales){
        Sales savedSales = salesRepository.save(sales);
        return ResultVoUtil.success(savedSales);
    }

    public ResultVo addSales(Sales sales){
        sales.setSalesId(new Long(0));
        return saveSales(sales);
    }

    // 改
    //-------------------------------------------------------------------------


    // 导入文件
    //-------------------------------------------------------------------------
    public ResultVo importSalesExcelAndPersistence(MultipartFile file){
        ResultVo<List<ExcelSalesVo>> importResult = fileService.importEntityExcel(file,ExcelSalesVo.class);
        if(!importResult.checkSuccess()){
            log.error(importResult.getMsg());
            return importResult;
        }
        List<ExcelSalesVo> excelSalesVos = importResult.getData();
        // 维护数据一致性先删除全部再导入（因为没有唯一标识不能部分删除）
        salesRepository.deleteAll();
        log.info("销售数据已全部删除，当前数据库中数据数量为{}",salesRepository.findAll().size());
        List<String> warnStringList = new ArrayList<>();
        for(ExcelSalesVo excelSalesVo: excelSalesVos){
            Sales sales = transExcelToEntity(excelSalesVo);
            List<Product> products = productRepository.findAllByProductIndexEquals(sales.getProductIndex());
            if(products.size() == 0){
                String warnString = "[Warning] " + "产品编号{ " + excelSalesVo.getProductIndex() +" }"  + "未找到对应的产品";
                warnStringList.add(warnString);
            }
            saveSales(sales);
        }
        // 添加数据库表项
        if(warnStringList.size()>0){
            return ResultVoUtil.success(201,"交易数据成功导入但存在条目对应为空请仔细检查",warnStringList);
        }
        return ResultVoUtil.success(excelSalesVos);
    }

    public Sales transExcelToEntity(ExcelSalesVo excelSalesVo){
        Sales sales = new Sales();
        // todo 可能有唯一确定的方法那样的话可以批量修改
        // 表示添加
        sales.setSalesId(new Long(0));
        sales.setProductIndex(excelSalesVo.getProductIndex());
        sales.setDate(stringToDate(excelSalesVo.getDate()));
        sales.setCustomer(excelSalesVo.getCustomer());
        sales.setUnitPrice(excelSalesVo.getUnitPrice());
        sales.setNumber(excelSalesVo.getNumber());
        return sales;
    }

    // 导出文件
    //-------------------------------------------------------------------------
    public ResultVo<String> exportSalesExcel(HttpServletResponse response){
        // 1.根据查询条件获取结果集
        List<ExcelSalesWriteVo> excelSalesWriteVos = getExcelSalesWriteVoListByCondition();
        if (CollectionUtil.isEmpty(excelSalesWriteVos)) {
            log.info("【导出Excel文件】要导出的数据为空，无法导出！");
            return ResultVoUtil.success("数据为空");
        }
        // 2.获取要下载Excel文件的路径
        ResultVo<String> resultVo = fileService.getDownLoadPath(ExcelSalesWriteVo.class,excelSalesWriteVos);
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

    public List<ExcelSalesWriteVo> getExcelSalesWriteVoListByCondition(){
        List<ExcelSalesWriteVo> excelSalesWriteVos = new ArrayList<>();
        for(Sales sales: salesRepository.findAll()){
            excelSalesWriteVos.add(transEntityToExcel(sales));
        }
        return excelSalesWriteVos;
    }

    public ExcelSalesWriteVo transEntityToExcel(Sales sales){
        ExcelSalesWriteVo excelSalesWriteVo =  new ExcelSalesWriteVo();
        excelSalesWriteVo.setProductIndex(sales.getProductIndex());
        excelSalesWriteVo.setDate(sales.getDate().toString());
        excelSalesWriteVo.setCustomer(sales.getCustomer());
        excelSalesWriteVo.setUnitPrice(sales.getUnitPrice());
        excelSalesWriteVo.setNumber(sales.getNumber());
        return excelSalesWriteVo;
    }
}
