package com.doorway.tradememo.service.impl;

import com.doorway.tradememo.domain.StockHistoryPosition;
import com.doorway.tradememo.domain.StockPosition;
import com.doorway.tradememo.domain.StockPositionDay;
import com.doorway.tradememo.domain.TradeDetail;
import com.doorway.tradememo.mapper.StockHistoryPositionMapper;
import com.doorway.tradememo.mapper.StockPositionDayMapper;
import com.doorway.tradememo.mapper.StockPositionMapper;
import com.doorway.tradememo.mapper.TradeDetailMapper;
import com.doorway.tradememo.req.StockHistoryPositionQO;
import com.doorway.tradememo.resp.PageResponse;
import com.doorway.tradememo.service.IPositionService;
import com.doorway.tradememo.service.IStockQuotationService;
import com.doorway.tradememo.utils.ComputeProfitDay;
import com.doorway.tradememo.utils.CostResp;
import com.doorway.tradememo.utils.DateUtils;
import com.doorway.tradememo.vo.TradeDetailVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * Note
 * Author:liupz
 * Date:2022/4/9
 */
@Slf4j 
@Service
public class PositionServiceImpl implements IPositionService {

    @Autowired
    private StockPositionMapper stockPositionMapper;

    @Autowired
    private StockPositionDayMapper stockPositionDayMapper;


    @Autowired
    private StockHistoryPositionMapper stockHistoryPositionMapper;

    @Autowired
    private TradeDetailMapper tradeDetailMapper;

    @Autowired
    private IStockQuotationService quotation;


    @SuppressWarnings("null")
    @Override
    public List<StockPosition> getCurrentPositions() {
        List<StockPosition> stockPositions = stockPositionMapper.getAll();

        for (StockPosition stockPosition : stockPositions) {
            Double price = quotation.getLatestPrice(stockPosition.getCode());
            stockPosition.setPrice(BigDecimal.valueOf(price));
            
            List<TradeDetail> tradeDetails = tradeDetailMapper.getByCode(stockPosition.getCode());

            // 找出最大的term值
            String maxTerm = null;
            for (TradeDetail detail : tradeDetails) {
                if (detail.getTerm() != null) {
                    if (maxTerm == null || detail.getTerm().compareTo(maxTerm) > 0) {
                        maxTerm = detail.getTerm();
                    }
                }
            }

            // 上次清仓时间：term为最大值的记录中的最新交易时间
            // 建仓时间：term为空的记录中的最早交易时间
            Date lastClearTime = null;
            Date buildPositionTime = null;

            for (TradeDetail detail : tradeDetails) {
                // 计算上次清仓时间：term为最大值的最新交易时间
                if (maxTerm != null && maxTerm.equals(detail.getTerm())) {
                    if (lastClearTime == null || detail.getTradeTime().after(lastClearTime)) {
                        lastClearTime = detail.getTradeTime();
                    }
                }
                // 计算建仓时间：term为空的最早交易时间
                if (detail.getTerm() == null) {
                    if (buildPositionTime == null || detail.getTradeTime().before(buildPositionTime)) {
                        buildPositionTime = detail.getTradeTime();
                    }
                }
            }
            stockPosition.setLastClearTime(lastClearTime);
            stockPosition.setBuildPositionTime(buildPositionTime);
            
            // 计算当前成本，使用lastClearTime下一天的零点零分1秒作为开始时间
            Date nextDayStart = null;
            if (lastClearTime != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(lastClearTime);
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 1);
                calendar.set(Calendar.MILLISECOND, 0);
                nextDayStart = calendar.getTime();
            }
            BigDecimal currentCost = calculateCurrentCost(stockPosition.getCode(), nextDayStart);
            stockPosition.setCurrentCost(currentCost);
        }
        return stockPositions;
    }
    
    /**
     * 计算当前成本
     * @param code 股票代码
     * @param lastClearTime 上次清仓时间
     * @return 当前成本
     */
    private BigDecimal calculateCurrentCost(String code, Date lastClearTime) {
        List<TradeDetail> tradeDetails;
        if (lastClearTime != null) {
            tradeDetails = tradeDetailMapper.getTradesAfterDayAndCode(lastClearTime, code);
        } else {
            tradeDetails = tradeDetailMapper.getByCode(code);
        }
        
        BigDecimal buyAmount = BigDecimal.ZERO; // 买入总金额（含手续费）
        BigDecimal sellAmount = BigDecimal.ZERO; // 卖出总金额（含税费和手续费）
        int buyVol = 0; // 买入总数量
        int sellVol = 0; // 卖出总数量
        
        for (TradeDetail detail : tradeDetails) {
            if (detail.getOpt()) { // 买入
                BigDecimal amount = detail.getPrice().multiply(BigDecimal.valueOf(detail.getVol()));
                BigDecimal fee = detail.getCommission().add(detail.getTransFee());
                buyAmount = buyAmount.add(amount).add(fee);
                buyVol += detail.getVol();
            } else { // 卖出
                BigDecimal amount = detail.getPrice().multiply(BigDecimal.valueOf(detail.getVol()));
                BigDecimal fee = detail.getCommission().add(detail.getTax()).add(detail.getTransFee());
                sellAmount = sellAmount.add(amount).add(fee);
                sellVol += detail.getVol();
            }
        }
        
        int netVol = buyVol - sellVol;
        if (netVol <= 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal netAmount = buyAmount.subtract(sellAmount);
        log.info("计算当前成本，股票代码：{}，上次清仓时间：{}，买入总金额：{}，卖出总金额：{}，买入总数量：{}，卖出总数量：{}，净数量：{}，净金额：{}",
                code, lastClearTime, buyAmount, sellAmount, buyVol, sellVol, netVol, netAmount);
        return netAmount.divide(BigDecimal.valueOf(netVol), 2, BigDecimal.ROUND_HALF_UP);
    }

    @Override
    public void computeAllDayPosition() {
        stockPositionDayMapper.deleteAll();
        stockPositionMapper.deleteAll();
        stockHistoryPositionMapper.deleteAll();
        CostResp costResp = ComputeProfitDay.computeStockProfitDays(tradeDetailMapper.getAllTrades(), quotation);
        saveData(costResp);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<TradeDetail> computeAllDayPositions(List<String> codes) {
        stockPositionDayMapper.deleteByCodes(codes);

        stockHistoryPositionMapper.deleteByCodes(codes);

        stockPositionMapper.deleteByCodes(codes);

        CostResp costResp = ComputeProfitDay.computeStockProfitDays(tradeDetailMapper.getByCodes(codes), quotation);

        saveData(costResp);

        return costResp.getErrors();

    }

    private void saveData(CostResp costResp) {
        List<TradeDetail> tradeDetails = costResp.getUpdates();
        List<StockPosition> stockPositions = costResp.getCurrentStockPositions();

        List<StockHistoryPosition> stockHistoryPositions = costResp.getStockHistoryPositions();

        Map<String, List<StockPositionDay>> stockPositionDayMap = costResp.getStockPositionDayMap();

        for (TradeDetail td : tradeDetails) {
            tradeDetailMapper.modifySelective(td);
        }

        if (!org.springframework.util.CollectionUtils.isEmpty(stockPositions)) {
            for (StockPosition sp : stockPositions) {
                StockPosition exist = stockPositionMapper.getByCode(sp.getCode());
                if(sp.getVol()>0&&costResp.getClearCodes().contains(sp.getCode())){
                    costResp.getClearCodes().remove(sp.getCode());
                }
                if (exist != null) {
                    exist.setCost(sp.getCost());
                    exist.setVol(sp.getVol());
                    stockPositionMapper.modify(exist);
                } else {
                    stockPositionMapper.save(sp);
                }
            }
        }

        if(!CollectionUtils.isEmpty(costResp.getClearCodes())){
            stockPositionMapper.deleteByCodes(costResp.getClearCodes());
        }

        if (!org.springframework.util.CollectionUtils.isEmpty(stockHistoryPositions)) {
            stockHistoryPositionMapper.saveBatch(stockHistoryPositions);
        }
        if (!CollectionUtils.isEmpty(stockPositionDayMap)) {
            List<StockPositionDay> all = new ArrayList<>();
            for (String day : stockPositionDayMap.keySet()) {
                all.addAll(stockPositionDayMap.get(day));
            }
            stockPositionDayMapper.saveBatch(new ArrayList<>(all));
        }
    }

    @Override
    public void computeCodePosition(String code, String startDay) {
        StockPositionDay stockPositionDay = null;
        List<TradeDetail> tradeDetails = null;
        if (StringUtils.hasLength(startDay)) {

            stockPositionMapper.deleteByCode(code);
            stockPositionDayMapper.deleteAfterDayByCode(code, startDay);
            stockHistoryPositionMapper.deleteAfterDayByCode(code, startDay);

            String maxDay = stockPositionDayMapper.getMaxDayBeforeDay(DateUtils.preDay(startDay));
            stockPositionDay = stockPositionDayMapper.getByDayAndCodeEqual(maxDay, code);
            tradeDetails = tradeDetailMapper.getTradesAfterDayAndCode(DateUtils.parseStrToDate(startDay, DateUtils.DATE_FORMAT_YYYY_MM_DD), code);
        } else {
            stockPositionMapper.deleteByCode(code);
            stockPositionDayMapper.deleteByCode(code);
            stockHistoryPositionMapper.deleteByCode(code);
            tradeDetails = tradeDetailMapper.getByCode(code);
        }

        CostResp costResp = ComputeProfitDay.computeStockProfitDays(code, startDay, null, stockPositionDay, tradeDetails, quotation);


        saveData(costResp);
    }

    @Override
    public void computeDayPosition(String startDay) {
        Map<String, List<TradeDetail>> stockPositionDays = new HashMap<>();
        List<TradeDetail> tradeDetails = null;
        if (StringUtils.hasLength(startDay)) {
            tradeDetails = tradeDetailMapper.getTradesAfterDay(DateUtils.parseStrToDate(startDay, DateUtils.DATE_FORMAT_YYYY_MM_DD));
            Set<String> codes = stockPositionMapper.getPositionCodes();

            //聚合某日后各股票的交易记录
            for (TradeDetail td : tradeDetails) {
                codes.add(td.getCode());
                if (stockPositionDays.containsKey(td.getCode())) {
                    stockPositionDays.get(td.getCode()).add(td);
                } else {
                    List<TradeDetail> tds = new ArrayList<>();
                    tds.add(td);
                    stockPositionDays.put(td.getCode(), tds);
                }
            }


            String maxDay = stockPositionDayMapper.getMaxDayBeforeDay(DateUtils.preDay(startDay));
            for (String code : codes) {

                CostResp costResp = ComputeProfitDay.computeStockProfitDays(code, startDay, null, stockPositionDayMapper.getByDayAndCodeEqual(maxDay, code), stockPositionDays.get(code), quotation);
                stockPositionDayMapper.deleteAfterDayByCode(code, startDay);
                stockHistoryPositionMapper.deleteAfterDayByCode(code, startDay);
                saveData(costResp);
            }
        }
    }


    @Override
    public List<StockPositionDay> historyDayPosition(String day) {
        return stockPositionDayMapper.getByDay(stockPositionDayMapper.getMaxDayBeforeDay(day));
    }

    @Override
    public Map<String, StockPositionDay> historyCodePosition(String code) {
        Map<String, StockPositionDay> history = new TreeMap<>();
        List<StockPositionDay> stockPositionDays = stockPositionDayMapper.getByDay(code);
        for(StockPositionDay day:stockPositionDays){
            history.put(day.getDay(),day);
        }
        return history;
    }

    @Override
    public StockPositionDay historyPositionByCode(String day, String code) {
        return stockPositionDayMapper.getByDayAndCodeEqual(stockPositionDayMapper.getMaxDayBeforeDay(day), code);
    }

    @Override
    public PageResponse<TradeDetailVO> getTermTrades(StockHistoryPositionQO detail, Integer pageNo, Integer pageSize, String sortField, String sortOrder) {

        PageHelper.startPage(pageNo, pageSize);

        List<StockHistoryPosition> stockProfits = null;

        // 列名映射：驼峰转下划线
        Map<String, String> sortMap = new HashMap<>();
        if (sortField != null && sortOrder != null) {
            String columnName = camelToSnake(sortField);
            sortMap.put(columnName, sortOrder);
        } else {
            sortMap.put("close_time", "desc");
        }

        stockProfits = stockHistoryPositionMapper.queryAndOrder(detail, sortMap);

        Page<StockHistoryPosition> page = (Page<StockHistoryPosition>) stockProfits;
        List<TradeDetailVO> tradeDetailVOS = new ArrayList<>();
        List<TradeDetail> tradeDetails = tradeDetailMapper.getAllTrades();
        Map<String, List<TradeDetail>> detailMap = new HashMap<>();
        tradeDetails.forEach(td -> {
            if (detailMap.containsKey(td.getTerm() + td.getCode())) {
                detailMap.get(td.getTerm() + td.getCode()).add(td);
            } else {
                List<TradeDetail> tradeDetails1 = new ArrayList<>();
                tradeDetails1.add(td);
                detailMap.put(td.getTerm() + td.getCode(), tradeDetails1);
            }
        });
        for (StockHistoryPosition stockProfit : stockProfits) {
            TradeDetail tradeDetail = new TradeDetail();
            tradeDetail.setTerm(stockProfit.getTerm());


            TradeDetailVO tradeDetailVO = new TradeDetailVO();
            tradeDetailVO.setCode(stockProfit.getCode());
            tradeDetailVO.setCount(stockProfit.getTradeCount());
            tradeDetailVO.setOpenTime(stockProfit.getOpenTime());
            tradeDetailVO.setCloseTime(stockProfit.getCloseTime());
            tradeDetailVO.setExpend(stockProfit.getExpend());
            tradeDetailVO.setIncome(stockProfit.getIncome());
            tradeDetailVO.setHoldDays(stockProfit.getHoldTime());
            tradeDetailVO.setDetails(detailMap.get(stockProfit.getTerm() + stockProfit.getCode()));
            if (stockProfit.getProfit() != null) {
                tradeDetailVO.setProfit(stockProfit.getProfit().doubleValue());
            }
            if (stockProfit.getProfitRate() != null) {
                tradeDetailVO.setProfitRate(stockProfit.getProfitRate().doubleValue());
            }
            tradeDetailVOS.add(tradeDetailVO);

        }

        return new PageResponse<>(tradeDetailVOS, page.getPageNum(), page.getTotal());
    }

    /**
     * 驼峰命名转下划线命名
     */
    private String camelToSnake(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    result.append('_');
                }
                result.append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }


}
