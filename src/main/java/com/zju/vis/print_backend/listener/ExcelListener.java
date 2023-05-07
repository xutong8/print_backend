package com.zju.vis.print_backend.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ExcelListener<T> extends AnalysisEventListener<T> {

    // 返回读取到的excel中的数据
    List<T> dataList = new ArrayList<>();

    public ExcelListener() {
    }

    // 每一条数据解析都会来调用
    @Override
    public void invoke(T t, AnalysisContext analysisContext) {
        log.info("【Excel文件】解析到一条数据{}:", JSON.toJSONString(t));
        dataList.add(t);
    }

    // 所有数据解析完成了 才会来调用
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        log.info("【Excel文件】Excel所有数据解析完毕！");
    }

    public List<T> getDataList() {
        return dataList;
    }
}

