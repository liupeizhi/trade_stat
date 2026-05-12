package com.doorway.tradememo.service.impl;

import com.doorway.tradememo.mapper.TradeDetailMapper;
import com.doorway.tradememo.service.ITradeTimesService;
import com.doorway.tradememo.utils.DateUtils;
import com.doorway.tradememo.vo.TradeTimesVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Note
 * Author:liupz
 * Date:2022/5/29
 */
@Component
public class TradeTimesServiceImpl implements ITradeTimesService {
    @Autowired
    private TradeDetailMapper tradeDetailMapper;


    @Override
    public Long getTotalTimes() {
        return tradeDetailMapper.getAllTradeCount();
    }

    @Override
    public Map<String, Long> getAllStockTradeTimes() {
        Map<String, Long> counts = new HashMap<>();
        List<Map<String,Object>> stockCounts = tradeDetailMapper.getAllStockTradeCounts();
        for(Map<String,Object> count:stockCounts){
            counts.put((String)count.get("code"),(Long) count.get("times"));
        }
        return counts;
    }

    @Override
    public Long getThisWeekTradeTimes() {
        return tradeDetailMapper.getRangeTradeTimes(DateUtils.parseDateToStr(DateUtils.getFirstDayOfWeek(new Date()),DateUtils.DATE_FORMAT_YYYY_MM_DD),DateUtils.parseDateToStr(new Date(),DateUtils.DATE_FORMAT_YYYY_MM_DD));

    }

    @Override
    public Long getThisMonthTradeTimes() {
        return tradeDetailMapper.getRangeTradeTimes(DateUtils.parseDateToStr(new Date(),"yyyy-MM-01"),DateUtils.parseDateToStr(new Date(),DateUtils.DATE_FORMAT_YYYY_MM_DD));
    }


    @Override
    public Map<String, TradeTimesVO> getStockTradeTimes(String code, String period, String start, String end) {
        Map<String, TradeTimesVO> counts = new TreeMap<>();
        List<Map<String,Object>> stockCounts = tradeDetailMapper.getTradeTimes(period,code,start,end);
        int sum = 0;
        for(Map<String,Object> count:stockCounts){
            TradeTimesVO vo = new TradeTimesVO();
            vo.setTime((String)count.get("time"));
            vo.setTimes(Integer.parseInt(count.get("times")+"") );
            sum += Integer.parseInt(count.get("times")+"");
            vo.setSumTimes(sum);
            counts.put((String)count.get("time"),vo);
        }
        return counts;
    }

    @Override
    public Map<String, Long> getSumStockTradeTimes(String code, String period, String start, String end) {
        return null;
    }
}
