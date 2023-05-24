package com.zju.vis.print_backend.service;

import com.zju.vis.print_backend.Utils.CollectionUtil;
import com.zju.vis.print_backend.Utils.FileUtil;
import com.zju.vis.print_backend.Utils.ResultVoUtil;
import com.zju.vis.print_backend.Utils.Utils;
import com.zju.vis.print_backend.dao.ProductRepository;
import com.zju.vis.print_backend.dao.SalesRepository;
import com.zju.vis.print_backend.entity.Product;
import com.zju.vis.print_backend.entity.Sales;
import com.zju.vis.print_backend.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
