package com.doorway.tradememo.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.doorway.tradememo.domain.StockQuotation;
import com.doorway.tradememo.domain.StockPositionDay;
import com.doorway.tradememo.domain.TradeDetail;
import com.doorway.tradememo.event.SettleEvent;
import com.doorway.tradememo.listener.ISettleEventListener;
import com.doorway.tradememo.mapper.StockPositionDayMapper;
import com.doorway.tradememo.mapper.TradeDetailMapper;
import com.doorway.tradememo.service.IStockQuotationService;
import com.doorway.tradememo.utils.ComputeCost;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * Note
 * Author:liupz
 * Date:2022/4/3
 */
@Component
@Slf4j
public class SettleEventListener implements ISettleEventListener {
    @Autowired
    private TradeDetailMapper tradeDetailMapper;

    @Autowired
    private IStockQuotationService stockCondition;

    @Autowired
    private StockPositionDayMapper stockPositionDayMapper;

    @Override
    public void handleEvent(SettleEvent settleEvent) {
        List<TradeDetail> tradeDetails = tradeDetailMapper.getTradesEmptyDay(settleEvent.getDay());
        if (settleEvent.getCode() != null) {
            tradeDetails = tradeDetailMapper.getTradesLessThanDayAndCode(settleEvent.getDay(), settleEvent.getCode());
        }

        Map<String, String> result = computeCost(tradeDetails);

        log.info(settleEvent.getDay() + "的持仓为：" + JSONObject.toJSONString(result));
        if (CollectionUtils.isEmpty(result)) {
            return;
        }
        List<StockPositionDay> stockPositionDays = new ArrayList<>();
        for (String code : result.keySet()) {
            StockPositionDay stockPositionDay = new StockPositionDay();
            stockPositionDay.setCode(code);
            String expend_income_vol = result.get(code);
            stockPositionDay.setId(UUID.randomUUID().toString().replaceAll("-", ""));
            stockPositionDay.setAvgCost(BigDecimal.valueOf(Double.parseDouble(expend_income_vol.split("_")[3])));
            stockPositionDay.setVol(Integer.parseInt(expend_income_vol.split("_")[2]));

            StockQuotation quotation = stockCondition.getStockQuotationAtDay(code,settleEvent.getDay());
            if(quotation!=null){
                stockPositionDay.setClosePrice(quotation.getClose());
                stockPositionDay.setTotalProfit(BigDecimal.valueOf((quotation.getClose().doubleValue()-stockPositionDay.getAvgCost().doubleValue())*stockPositionDay.getVol()));
            }

            stockPositionDay.setDay(settleEvent.getDay());
            stockPositionDay.setCreatedTime(new Date());

            StockPositionDay exsit = stockPositionDayMapper.getByDayCodeVol(settleEvent.getDay(), code, stockPositionDay.getVol());
            if (exsit != null) {
                log.info("出现重复记录，不保存：" + settleEvent.getDay() + "：" + code + "：" + stockPositionDay.getVol());
            } else {
                stockPositionDays.add(stockPositionDay);
            }
        }

        List<StockPositionDay> saves = new ArrayList<>();
        for (StockPositionDay stockPositionDay : stockPositionDays) {
            String code = stockPositionDay.getCode();
            String day = stockPositionDay.getDay();
            StockQuotation sc = stockCondition.getStockQuotationAtDay(code, day);
            if (sc != null) {
                stockPositionDay.setClosePrice(sc.getClose());
                stockPositionDay.setTotalProfit(BigDecimal.valueOf((sc.getClose().doubleValue() - stockPositionDay.getAvgCost().doubleValue()) * stockPositionDay.getVol()));
                saves.add(stockPositionDay);
            }
        }
        if (!CollectionUtils.isEmpty(saves)) {
            stockPositionDayMapper.saveBatch(saves);
        }


    }

    private Map<String, String> computeCost(List<TradeDetail> tradeDetails) {


        return ComputeCost.compute(tradeDetails).getResult();

    }

}
