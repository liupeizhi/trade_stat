package com.doorway.tradememo.service.impl;

import com.doorway.tradememo.domain.StockPosition;
import com.doorway.tradememo.domain.StockPositionDay;
import com.doorway.tradememo.mapper.StockPositionDayMapper;
import com.doorway.tradememo.mapper.StockPositionMapper;
import com.doorway.tradememo.service.IProfitService;
import com.doorway.tradememo.service.IStockQuotationService;
import com.doorway.tradememo.utils.DateUtils;
import com.doorway.tradememo.vo.ProfitVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Note
 * Author:liupz
 * Date:2022/4/9
 */
@Slf4j
@Service
public class ProfitServiceImpl implements IProfitService {
    @Autowired
    private StockPositionDayMapper stockPositionDayMapper;
    @Autowired
    private StockPositionMapper stockPositionMapper;
    @Autowired
    IStockQuotationService stockQuotationService;


    @Override
    public ProfitVO getBeforeTodayProfits() {
        Double total = stockPositionDayMapper.getTotalProfit();
        ProfitVO profitVO = new ProfitVO();
        if(total!=null) {
            profitVO.setProfit(total);
        }
        return profitVO;
    }

    @Override
    public ProfitVO getTodayProfits() {
        List<StockPosition> stockPositions = stockPositionMapper.getAll();
        ProfitVO profitVO = new ProfitVO();
        double profit = 0;
        for (StockPosition stockPosition : stockPositions) {
            Double price = stockQuotationService.getLatestPrice(stockPosition.getCode());
            profit += (price - stockPosition.getCost().doubleValue()) * stockPosition.getVol();
        }
        profitVO.setProfit(profit);
        return profitVO;
    }

    @Override
    public ProfitVO getThisWeekProfit() {
        String firstDay = DateUtils.parseDateToStr(DateUtils.getFirstDayOfWeek(new Date()), DateUtils.DATE_FORMAT_YYYY_MM_DD);
        ProfitVO today = new ProfitVO();
        List<StockPositionDay> stockPositionDays = stockPositionDayMapper.getAfterDay(firstDay);
        for (StockPositionDay day : stockPositionDays) {
            today.setProfit(today.getProfit() + day.getDayProfit().doubleValue());
        }

        return today;
    }

    @Override
    public ProfitVO getThisMonthProfit() {
        ProfitVO today = new ProfitVO();
        String firstDay = DateUtils.parseDateToStr(new Date(), "yyyy-MM-01");

        List<StockPositionDay> stockPositionDays = stockPositionDayMapper.getAfterDay(firstDay);
        for (StockPositionDay day : stockPositionDays) {
            today.setProfit(today.getProfit() + day.getDayProfit().doubleValue());
        }

        return today;
    }

    @Override
    public Map<String, Double> getStockProfits() {
        List<Map<String, Object>> maps = stockPositionDayMapper.getStockProfits();
        Map<String, Double> profits = new HashMap<>();
        maps.forEach(m -> {
            profits.put(m.get("code").toString(), Double.parseDouble(m.get("profit").toString()));
        });
        return profits;
    }


    @Override
    public Map<String, ProfitVO> getStockPeriodProfits(String code, String period, String start, String end) {
        List<Map<String, Object>> maps = stockPositionDayMapper.getStockPeriodProfits(period, code, start, end);
        Map<String, ProfitVO> profits = new TreeMap<>();
        double total = 0;
        for (Map<String, Object> m : maps) {
            Double profit = Double.parseDouble(m.get("profit").toString());
            ProfitVO profitVO = new ProfitVO();
            total += profit;
            profitVO.setProfit(profit);
            profitVO.setSumProfit(total);
            if(profit!=0&&m.containsKey("time")) {
                profits.put(m.get("time").toString(), profitVO);
            }
        }
        return profits;
    }

    @Override
    public Map<String, List<ProfitVO>> getStockPositionDays(String code, String start, String end) {

        List<StockPositionDay> maps = stockPositionDayMapper.getBetweenDay(start, end, code);
        Map<String, List<ProfitVO>> profits = new TreeMap<>();
        List<String> days = stockQuotationService.getTradeDays();
        for (StockPositionDay m : maps) {
//            if (stockQuotationService.getStockQuotationAtDay(code,m.getDay())==null) {
//                continue;
//            }
            if(!days.contains(m.getDay())){
                log.info("非交易日，略过："+m.getDay());
                continue;
            }
            if(m.getDayProfit().equals(0)&&stockQuotationService.getStockQuotationAtDay(m.getCode(),m.getDay())==null){
                log.info("停牌，略过："+m.getDay()+"，"+m.getCode());
                continue;
            }

            ProfitVO vo = new ProfitVO();
            vo.setDay(m.getDay());
            vo.setCode(m.getCode());
            vo.setDayProfit(m.getDayProfit().doubleValue());
            vo.setDayProfitRate(m.getDayProfit().doubleValue() / (m.getAvgCost().doubleValue() * m.getVol()));

            vo.setProfit(m.getTotalProfit().doubleValue());
            vo.setProfitRate(m.getTotalProfit().doubleValue() / (m.getAvgCost().doubleValue() * m.getVol()));

            vo.setPrice(m.getClosePrice().doubleValue());
            vo.setCost(m.getAvgCost().doubleValue());
            vo.setVol(m.getVol());

            if (profits.containsKey(m.getDay())) {
                profits.get(m.getDay()).add(vo);
            } else {
                List<ProfitVO> vos = new ArrayList<>();
                vos.add(vo);
                profits.put(m.getDay(), vos);
            }
        }
        return profits;
    }


}
