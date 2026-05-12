package com.doorway.tradememo.service.impl;

import com.doorway.tradememo.domain.StockHistoryPosition;
import com.doorway.tradememo.domain.StockPosition;
import com.doorway.tradememo.domain.TradeDetail;
import com.doorway.tradememo.event.TradeEvent;
import com.doorway.tradememo.event.TradeEventEnum;
import com.doorway.tradememo.listener.ITradeEventListener;
import com.doorway.tradememo.mapper.StockHistoryPositionMapper;
import com.doorway.tradememo.mapper.StockPositionMapper;
import com.doorway.tradememo.mapper.TradeDetailMapper;
import com.doorway.tradememo.service.IStockQuotationService;
import com.doorway.tradememo.utils.ComputeCost;
import com.doorway.tradememo.utils.CostResp;
import com.doorway.tradememo.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Note
 * Author:liupz
 * Date:2022/4/3
 */
@Component
@Slf4j
public class TradeListener implements ITradeEventListener<TradeDetail> {

    @Autowired
    private TradeDetailMapper tradeDetailMapper;
    @Autowired
    private StockHistoryPositionMapper stockHistoryPositionMapper;
    @Autowired
    private StockPositionMapper stockPositionMapper;

    @Autowired
    private IStockQuotationService stockCondition;


    @Override
    public void handleEvent(TradeEvent<TradeDetail> tradeEvent) {

        if(TradeEventEnum.OPEN.equals(tradeEvent.getType())){
            handleOpen(tradeEvent.getData());
        }

        if(TradeEventEnum.CLOSE.equals(tradeEvent.getType())){
            handleClose(tradeEvent.getData());
        }

        if(TradeEventEnum.ADD.equals(tradeEvent.getType())){
            handleAdd(tradeEvent.getData());
        }

        if(TradeEventEnum.REDUCE.equals(tradeEvent.getType())){
            handleReduce(tradeEvent.getData());
        }

    }



    private void handleOpen(TradeDetail tradeDetail){
        log.info("开仓："+tradeDetail.getCode()+","+tradeDetail.getVol());

        StockPosition  stockPosition = stockPositionMapper.getByCode(tradeDetail.getCode());
        if(stockPosition == null){
            StockPosition position = new StockPosition();
            position.setId(UUID.randomUUID().toString().replaceAll("-",""));
            position.setCode(tradeDetail.getCode());
            position.setVol(tradeDetail.getVol());
            position.setCost(tradeDetail.getPrice());
            position.setCreatedTime(new Date());
            position.setPrice(BigDecimal.valueOf(stockCondition.getLatestPrice(tradeDetail.getCode())));
            stockPositionMapper.save(position);
        }else {
            log.info("当前持仓已经有该股票了："+tradeDetail.getCode());
        }


    }
    private void handleClose(TradeDetail tradeDetail){
        log.info("清仓："+tradeDetail.getCode()+","+tradeDetail.getVol());

        StockPosition  stockPosition = stockPositionMapper.getByCode(tradeDetail.getCode());
        if(stockPosition == null){
            log.info("没有找到该股票的持仓："+tradeDetail.getCode());
        }else {
            stockPositionMapper.delById(stockPosition.getId());
        }

        List<TradeDetail> tradeDetails = tradeDetailMapper.getTradesEmpty(tradeDetail.getCode(),tradeDetail.getTradeTime());
        if(CollectionUtils.isEmpty(tradeDetails)){
            log.info("数据有问题，清仓失败："+tradeDetail.getCode()+","+tradeDetail.getTradeTime());
            return;
        }
        int count = 0;
        for(TradeDetail td:tradeDetails){
            td.setTerm(tradeDetails.get(0).getDay());
            if(td.getOpt()){
                count+=td.getVol();
            }else {
                count-=td.getVol();
            }

            tradeDetailMapper.modifySelective(td);
        }

        if(count!=0){
            log.error("数据有问题，交易记录不符合减仓条件："+tradeDetail.getCode()+"，"+tradeDetail.getTradeTime());
        }

        CostResp costResp = ComputeCost.compute(tradeDetails);

        String cost = costResp.getResult().get(tradeDetail.getCode());

        double expend = Double.parseDouble(cost.split("_")[0]);
        double income = Double.parseDouble(cost.split("_")[1]);
        StockHistoryPosition stockHistoryPosition = new StockHistoryPosition();
        stockHistoryPosition.setId(UUID.randomUUID().toString().replaceAll("-",""));
        stockHistoryPosition.setCode(tradeDetail.getCode());
        stockHistoryPosition.setCloseTime(tradeDetail.getTradeTime());
        stockHistoryPosition.setOpenTime(tradeDetails.get(0).getTradeTime());
        stockHistoryPosition.setTradeCount(tradeDetails.size());
        stockHistoryPosition.setExpend(BigDecimal.valueOf(expend));
        stockHistoryPosition.setIncome(BigDecimal.valueOf(income));
        stockHistoryPosition.setHoldTime(DateUtils.getDistanceDays(stockHistoryPosition.getOpenTime(),stockHistoryPosition.getCloseTime()));
        stockHistoryPosition.setProfit(BigDecimal.valueOf(income-expend));
        stockHistoryPosition.setProfitRate(BigDecimal.valueOf((income - expend) / expend));
        stockHistoryPosition.setTerm(tradeDetails.get(0).getDay());
        stockHistoryPositionMapper.save(stockHistoryPosition);

    }



    private void handleAdd(TradeDetail tradeDetail){
        log.info("加仓："+tradeDetail.getCode()+","+tradeDetail.getVol());
        StockPosition  stockPosition = stockPositionMapper.getByCode(tradeDetail.getCode());
        if(stockPosition == null){
            log.info("没有找到该股票的持仓："+tradeDetail.getCode());
            handleOpen(tradeDetail);
        }else {
            processPosition(stockPosition,tradeDetail);
        }

    }

    private void processPosition(StockPosition position,TradeDetail tradeDetail) {

        List<TradeDetail> tradeDetails = tradeDetailMapper.getTradesEmpty(tradeDetail.getCode(),tradeDetail.getTradeTime());
        CostResp costResp = ComputeCost.compute(tradeDetails);

        String cost = costResp.getResult().get(tradeDetail.getCode());

        double expend = Double.parseDouble(cost.split("_")[0]);
        double income = Double.parseDouble(cost.split("_")[1]);
        int vol = Integer.parseInt(cost.split("_")[2]);

        position.setCode(tradeDetail.getCode());
        position.setVol(tradeDetail.getVol());
        if(vol>0) {
            position.setCost(BigDecimal.valueOf((expend - income) / vol));
        }
        position.setUpdatedTime(new Date());
        position.setVol(vol);

        stockPositionMapper.modifySelective(position);

    }

    private void handleReduce(TradeDetail tradeDetail){
        log.info("减仓："+tradeDetail.getCode()+","+tradeDetail.getVol());
        StockPosition  stockPosition = stockPositionMapper.getByCode(tradeDetail.getCode());
        if(stockPosition == null){
            log.info("当前持仓没有该股票，无法减仓："+tradeDetail.getCode());
        }else {
            processPosition(stockPosition,tradeDetail);
        }

    }
}
