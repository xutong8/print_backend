package com.zju.vis.print_backend.Utils;

import com.zju.vis.print_backend.constant.DateConstant;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;

@Component
public class DateUtil {

    // 获取当前时间
    public static String getCurrentTime() {
        DateTime now = new DateTime();
        return now.toString(DateConstant.DEFAULT_FORMAT_PATTERN);
    }

    // 获取当前日期
    public static String getCurrentDate() {
        LocalDate localDate = new LocalDate();
        return localDate.toString();
    }

}
