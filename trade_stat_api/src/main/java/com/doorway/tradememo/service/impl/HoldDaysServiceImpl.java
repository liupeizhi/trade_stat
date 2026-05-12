package com.doorway.tradememo.service.impl;

import com.doorway.tradememo.domain.StockPosition;
import com.doorway.tradememo.domain.TradeDetail;
import com.doorway.tradememo.mapper.StockHistoryPositionMapper;
import com.doorway.tradememo.mapper.StockPositionMapper;
import com.doorway.tradememo.mapper.TradeDetailMapper;
import com.doorway.tradememo.service.IHoldDaysService;
import com.doorway.tradememo.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Note
 * Author:liupz
 * Date:2022/7/8
 */
@Service
public class HoldDaysServiceImpl implements IHoldDaysService {
    @Autowired
    private StockHistoryPositionMapper stockHistoryPositionMapper;

    @Autowired
    private StockPositionMapper stockPositionMapper;

    @Autowired
    private TradeDetailMapper tradeDetailMapper;

    @Override
    public Map<String, Integer> getAllStockHoldDays() {
        List<TradeDetail> tradeDetails = tradeDetailMapper.getNoClearTrades();
        Map<String, Date> openTimes = new HashMap<>();
        for (TradeDetail td : tradeDetails) {
            if (openTimes.containsKey(td.getCode())) {
                if (openTimes.get(td.getCode()).after(td.getTradeTime())) {
                    openTimes.put(td.getCode(), td.getTradeTime());
                }
            } else {
                openTimes.put(td.getCode(), td.getTradeTime());
            }
        }


        List<Map<String, Object>> times = stockHistoryPositionMapper.getCodesHoldTimes();
        Map<String, Integer> dist = new HashMap<>();
        for (Map<String, Object> data : times) {
            int holdTime = 0;
            if (openTimes.containsKey(data.get("code").toString())) {
                holdTime = DateUtils.getDistanceDays(new Date(), openTimes.get(data.get("code").toString()));
            }

            dist.put(data.get("code").toString(), Integer.parseInt(data.get("times").toString()) + holdTime);
        }

        for (String key : openTimes.keySet()) {
            if (!dist.containsKey(key)) {
                dist.put(key, DateUtils.getDistanceDays(new Date(), openTimes.get(key)));
            }
        }

        return dist;
    }

    @Override
    public Integer getStockHoldDays(String code) {
        List<TradeDetail> tradeDetails = tradeDetailMapper.getNoClearTradesByCode(code);

        Date openTime = new Date();
        boolean found = false;
        for (TradeDetail td : tradeDetails) {
            if (openTime.after(td.getTradeTime())) {
                openTime = td.getTradeTime();
                found = true;
            }
        }
        int holdDays = 0;
        if (found) {
            holdDays = DateUtils.getDistanceDays(new Date(), openTime);
        }

        Map<String, Object> times = stockHistoryPositionMapper.getCodesHoldTimesByCode(code);
        if (!CollectionUtils.isEmpty(times)) {
            return Integer.parseInt(times.get("times").toString()) + holdDays;
        } else {
            return holdDays;
        }
    }
}
