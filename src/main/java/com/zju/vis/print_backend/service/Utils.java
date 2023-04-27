package com.zju.vis.print_backend.service;

import com.zju.vis.print_backend.Utils.UtilsService;
import com.zju.vis.print_backend.entity.Product;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {
    // 通用日期价格类(static 用于外部调用
    public static class HistoryPrice{
        private Date date;
        private Float price;

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public Float getPrice() {
            return price;
        }

        public void setPrice(Float price) {
            this.price = price;
        }

        // @Override
        // public int compareTo(Object o) {
        //     HistoryPrice historyPrice = (HistoryPrice) o;
        //     return (int)historyPrice.getDate().getTime() - (int)this.getDate().getTime();
        // }
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
