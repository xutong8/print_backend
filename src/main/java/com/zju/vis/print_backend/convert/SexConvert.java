package com.zju.vis.print_backend.convert;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.CellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

public class SexConvert implements Converter<Integer> {

    @Override
    public Class supportJavaTypeKey() {
        return Integer.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    // 这里读的时候会调用
    // @Override
    // public Integer convertToJavaData(CellData cellData, ExcelContentProperty excelContentProperty, GlobalConfiguration globalConfiguration) throws Exception {
    //     switch (cellData.getStringValue()) {
    //         case "男":
    //             return 0;
    //         case "女":
    //             return 1;
    //         default:
    //             return 0;
    //     }
    // }
    //
    // // 这里写的时候会调用
    // @Override
    // public CellData convertToExcelData(Integer integer, ExcelContentProperty excelContentProperty, GlobalConfiguration globalConfiguration) throws Exception {
    //     switch (integer) {
    //         case 1:
    //             return new CellData("女");
    //         case 0:
    //             return new CellData("男");
    //         default:
    //             return new CellData(String.valueOf(integer));
    //     }
    // }

}

