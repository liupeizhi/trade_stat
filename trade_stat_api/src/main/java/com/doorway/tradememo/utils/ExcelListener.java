package com.doorway.tradememo.utils;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Note
 * Author:liupz
 * Date:2022/4/2
 */
@Slf4j
public class ExcelListener<T> extends AnalysisEventListener<T> {
    private final List<T> rows = new ArrayList<>();

    @Override
    public void invoke(T object, AnalysisContext context) {
        rows.add(object);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("read {} rows %n", rows.size());
    }

    public List<T> getRows() {
        return rows;
    }

}
