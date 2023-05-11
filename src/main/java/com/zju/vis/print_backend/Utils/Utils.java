package com.zju.vis.print_backend.Utils;


import com.zju.vis.print_backend.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {
    // 通用日期价格类(static 用于外部调用
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HistoryPrice{
        private Date date;
        private Double price;
    }

    // 判断字符串是否为空
    public boolean isEmptyString(String string) {
        return string == null || string.isEmpty();
    }

    // 分页泛型类
    public <T> List<T> pageList(List<T> listToPage, Integer pageNo, Integer pageSize){
        if(listToPage.size() < pageNo*pageSize){
            return new ArrayList<>();
        }
        List<T> subList = listToPage.stream().skip((pageNo)*pageSize).limit(pageSize).
                collect(Collectors.toList());
        return subList;
    }
}
