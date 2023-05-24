package com.zju.vis.print_backend.Utils;


import com.zju.vis.print_backend.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {

    public static java.util.Date stepMonth(java.util.Date sourceDate, int month) {
        Calendar c = Calendar.getInstance();
        c.setTime(sourceDate);
        c.add(Calendar.MONTH, month);
        return c.getTime();
    }

    public static java.util.Date stepDay(java.util.Date sourceDate, int day){
        Calendar c = Calendar.getInstance();
        c.setTime(sourceDate);
        c.add(Calendar.DATE, day);
        return c.getTime();
    }

    public static java.sql.Date stringToDate(String sDate) {
        /**
         *str转date方法
         */
        String str = sDate;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date d = null;
        try {
            d = format.parse(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        java.sql.Date date = new java.sql.Date(d.getTime());
        return date;
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
