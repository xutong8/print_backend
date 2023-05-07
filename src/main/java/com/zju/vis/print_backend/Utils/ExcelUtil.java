package com.zju.vis.print_backend.Utils;

import com.alibaba.excel.EasyExcel;
import com.zju.vis.print_backend.listener.ExcelListener;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Data
@Component
@Slf4j
public class ExcelUtil<T> {

    // excel文件后缀
    private final static String EXCE_L2003 = "xls";
    private final static String EXCEL_2007 = "xlsx";

    // sheet名字
    public final static String SHEET_NAME = "模板";

    // 校验文件后缀是否为 xls、xlsx
    public static boolean checkExcelExtension(MultipartFile excel) {
        String filename = excel.getOriginalFilename();
        if (StringUtil.isBlank(filename)) {
            log.info("【校验Excel文件后缀】Excel文件名为空");
            return false;
        }
        int index = filename.lastIndexOf(".");
        if (index == -1) {
            log.info("【校验Excel文件后缀】Excel文件名中没有点号");
            return false;
        }
        String extension = filename.substring(index + 1);
        return Arrays.asList(EXCE_L2003, EXCEL_2007).contains(extension);
    }

    // 读取excel文件
    public List<T> simpleExcelRead(String filePath, Class<T> clazz) {
        ExcelListener<T> excelListener = new ExcelListener();
        EasyExcel.read(filePath, clazz, excelListener).sheet().doRead();
        List<T> dataList = excelListener.getDataList();
        return dataList;
    }

    // 写Excel文件
    public void simpleExcelWrite(String filePath, Class<T> clazz, List<T> dataList) {
        EasyExcel.write(filePath, clazz).sheet(SHEET_NAME).doWrite(dataList);
    }



}
