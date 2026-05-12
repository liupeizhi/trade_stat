package com.doorway.tradememo.service.impl;

import com.doorway.tradememo.domain.StockPositionDay;
import com.doorway.tradememo.domain.TradeDetail;
import com.doorway.tradememo.mapper.StockHistoryPositionMapper;
import com.doorway.tradememo.mapper.StockPositionDayMapper;
import com.doorway.tradememo.mapper.TradeDetailMapper;
import com.doorway.tradememo.service.ICostService;
import com.doorway.tradememo.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Note
 * Author:liupz
 * Date:2022/7/8
 */
@Service
public class CostServiceImpl implements ICostService {

    @Autowired
    private StockPositionDayMapper stockPositionDayMapper;

    @Autowired
    private TradeDetailMapper tradeDetailMapper;

    @Override
    public Map<String, Double> getCostLine(String code) {
        List<StockPositionDay> stockPositionDays = stockPositionDayMapper.getByCode(code);
        Map<String, Double> costLine = new TreeMap<>();
        for(StockPositionDay day:stockPositionDays){
            costLine.put(day.getDay(),day.getAvgCost().doubleValue());
        }
        return costLine;
    }

    @Override
    public Map<String, Double> getCostLineByTerm(String code, String term) {
        List<TradeDetail> tradeDetails = tradeDetailMapper.getTradesByTerm(term);
        Date start = null;
        Date end = null;
        for(TradeDetail td:tradeDetails){

            if(start==null||start.after(td.getTradeTime())){
                start = td.getTradeTime();
            }

            if(end==null||end.before(td.getTradeTime())){
                end = td.getTradeTime();
            }

        }
        List<StockPositionDay> stockPositionDays = stockPositionDayMapper.getBetweenDay(DateUtils.parseDateToStr(start,DateUtils.DATE_FORMAT_YYYY_MM_DD),DateUtils.parseDateToStr(end,DateUtils.DATE_FORMAT_YYYY_MM_DD),code);
        Map<String, Double> costLine = new TreeMap<>();
        for(StockPositionDay day:stockPositionDays){
            costLine.put(day.getDay(),day.getAvgCost().doubleValue());
        }
        return costLine;
    }
}
