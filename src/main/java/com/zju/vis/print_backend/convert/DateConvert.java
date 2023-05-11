package com.zju.vis.print_backend.convert;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.CellData;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class DateConvert implements Converter<Date> {
    @Override
    public Class supportJavaTypeKey() {
        return Date.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public Date convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        // return Converter.super.convertToJavaData(cellData, contentProperty, globalConfiguration);
        return StringToDate(cellData.getStringValue());
    }

    @Override
    public WriteCellData<?> convertToExcelData(Date value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        return Converter.super.convertToExcelData(value, contentProperty, globalConfiguration);
        // CellData<String> cellData = new CellData();
        // cellData.setStringValue(value.toString());
        // return cellData;


        // return new CellData(value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }

    public static java.sql.Date StringToDate(String sDate) {
        /**
         *str转date方法
         */
        String str = sDate;
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
        java.util.Date d = null;
        try {
            d = format.parse(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        java.sql.Date date = new java.sql.Date(d.getTime());
        return date;
    }
}
