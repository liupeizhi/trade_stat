package com.doorway.tradememo.utils;

import com.alibaba.fastjson.JSONObject;
import com.doorway.tradememo.domain.*;
import com.doorway.tradememo.service.IStockQuotationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

import static com.doorway.tradememo.utils.DateUtils.DATE_FORMAT_YYYY;
import static com.doorway.tradememo.utils.DateUtils.DATE_FORMAT_YYYY_MM;

/**
 * Note
 * Author:liupz
 * Date:2022/4/4
 */
@Slf4j
public class ComputeProfitDay {

    public static CostResp computeStockProfitDays(String code, String startDay, String endDay, StockPositionDay initialDay, List<TradeDetail> tds, IStockQuotationService stockQuotationService) {
        CostResp costResp = new CostResp();
        //按天分类交易记录，带排序
        Map<String, List<TradeDetail>> dayTrades = new TreeMap<>();
        if(!CollectionUtils.isEmpty(tds)) {
            //按天分类交易记录
            tds.forEach(td -> {
                if (dayTrades.containsKey(td.getDay())) {
                    dayTrades.get(td.getDay()).add(td);
                } else {
                    List<TradeDetail> tradeDetails = new ArrayList<>();
                    tradeDetails.add(td);
                    dayTrades.put(td.getDay(), tradeDetails);
                }
            });
        }


        //计算起止日期
        List<String> days = new ArrayList<>(dayTrades.keySet());
        if (startDay == null) {
            startDay = days.get(0);
        }
        if (endDay == null) {
            endDay = DateUtils.parseDateToStr(new Date(), DateUtils.DATE_FORMAT_YYYY_MM_DD);
        }
        log.info("开始时间："+startDay+",结束时间："+endDay+"，code="+code);

        //获取起止日期内的股票行情
        List<StockQuotation> stockQuotations = stockQuotationService.getStockDayKlineByRange(code, startDay, endDay, 0);

        //行情放入日期映射
        Map<String, StockQuotation> stringStockQuotationMap = new HashMap<>();
        stockQuotations.forEach(s -> stringStockQuotationMap.put(s.getDay(), s));

        //除权除息信息，日期代表除权日，除权日前一个交易日计算除权后的结果
        Map<String, DividendInfo> dividendInfoMap = stockQuotationService.getDividendInfo(code);

        //每天仓位信息
        Map<String, StockPositionDay> dayPosition = new HashMap<>();


        List<String> seDays = DateUtils.getDays(startDay, endDay);
        //数量
        int vol = 0;

        List<TradeDetail> errors = new ArrayList<>();
        List<StockHistoryPosition> stockHistoryPositions = new ArrayList<>();
        List<TradeDetail> updates = new ArrayList<>();
        List<TradeDetail> terms = new ArrayList<>();

        //对交易记录列表进行排序
        for (int j = 0; j < seDays.size(); j++) {
            //当天日期
            String day = seDays.get(j);

            //昨天日期
            String nextDay = null;
            if (j < seDays.size() - 1) {
                nextDay = seDays.get(j + 1);
            }
            log.info("处理" + day + "日数据");

            StockPositionDay currentDayPosition = new StockPositionDay();


            StockQuotation stockQuotation = stringStockQuotationMap.get(day);
            if (stockQuotation == null) {
                log.info("股票代码：" + code + "，没有找到" + day + "的行情,可能是非交易日");
                if (initialDay != null && initialDay.getVol() != null && initialDay.getVol() > 0) {
                    BeanUtils.copyProperties(initialDay, currentDayPosition);
                    currentDayPosition.setDayProfit(BigDecimal.valueOf(0));
                    currentDayPosition.setDay(day);
                    currentDayPosition.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                    //没有清仓，前一个交易日有处理除权除息
                    if (dividendInfoMap.containsKey(nextDay)) {
                        //当天有除权除息
                        DividendInfo dividendInfo = dividendInfoMap.get(nextDay);
                        if (dividendInfo.getType() == 1) {
                            //增加分红收入
                            currentDayPosition.setTotalIncome(BigDecimal.valueOf(currentDayPosition.getTotalIncome().doubleValue() + dividendInfo.getPxbl() * currentDayPosition.getVol()));
                            currentDayPosition.setTermIncome(BigDecimal.valueOf(currentDayPosition.getTermIncome().doubleValue() + dividendInfo.getPxbl() * currentDayPosition.getVol()));
                            currentDayPosition.setDayProfit(BigDecimal.valueOf(dividendInfo.getPxbl() * currentDayPosition.getVol()));
                            currentDayPosition.setTotalProfit(BigDecimal.valueOf(currentDayPosition.getTotalProfit().doubleValue() + dividendInfo.getPxbl() * currentDayPosition.getVol()));
                            currentDayPosition.setAvgCost(BigDecimal.valueOf((currentDayPosition.getTotalExpend().doubleValue() - currentDayPosition.getTotalIncome().doubleValue()) / currentDayPosition.getVol()));
                            log.info("当天派息：" + day + ",派息1：" + currentDayPosition.getDayProfit());

                        }

                    }

                    //重置初始状态
                    initialDay = currentDayPosition;
                    dayPosition.put(day, currentDayPosition);
                }

                continue;
            }

            double open = stockQuotation.getOpen().doubleValue();
            double close = stockQuotation.getClose().doubleValue();

            //上个交易日收盘价
            double lastClose = 0;
            if (initialDay != null) {
                lastClose = initialDay.getClosePrice().doubleValue();
            } else {
                lastClose = open;
            }


            List<TradeDetail> v = dayTrades.get(day);
            if (CollectionUtils.isEmpty(v)) {
                log.info("股票代码：" + code + "，没有找到" + day + "的交易记录");
                //当日没有交易记录
                if (initialDay != null && initialDay.getVol() != null && initialDay.getVol() > 0) {
                    BeanUtils.copyProperties(initialDay, currentDayPosition);
                    //当日收盘价减去平均成本
                    currentDayPosition.setTotalProfit(BigDecimal.valueOf((close - currentDayPosition.getAvgCost().doubleValue()) * currentDayPosition.getVol()));
                    //当日收盘价减去上日收盘价
                    currentDayPosition.setDayProfit(BigDecimal.valueOf((close - lastClose) * currentDayPosition.getVol()));

                    currentDayPosition.setDay(day);

                    currentDayPosition.setClosePrice(BigDecimal.valueOf(close));

                    currentDayPosition.setId(UUID.randomUUID().toString().replaceAll("-", ""));

                    //没有清仓，处理除权除息
                    if (dividendInfoMap.containsKey(nextDay)) {
                        //前一天有除权除息
                        DividendInfo dividendInfo = dividendInfoMap.get(nextDay);
                        //增加收入
                        currentDayPosition.setTotalIncome(BigDecimal.valueOf(currentDayPosition.getTotalIncome().doubleValue() + dividendInfo.getPxbl() * currentDayPosition.getVol()));
                        currentDayPosition.setTermIncome(BigDecimal.valueOf(currentDayPosition.getTermIncome().doubleValue() + dividendInfo.getPxbl() * currentDayPosition.getVol()));
                        //当天分红计入
                        currentDayPosition.setDayProfit(BigDecimal.valueOf(currentDayPosition.getDayProfit().doubleValue() + dividendInfo.getPxbl() * currentDayPosition.getVol()));

                        currentDayPosition.setTotalProfit(BigDecimal.valueOf(currentDayPosition.getTotalProfit().doubleValue() + dividendInfo.getPxbl() * currentDayPosition.getVol()));
                        currentDayPosition.setAvgCost(BigDecimal.valueOf((currentDayPosition.getTotalExpend().doubleValue() - currentDayPosition.getTotalIncome().doubleValue()) / currentDayPosition.getVol()));

                        log.info("当天派息：" + day + ",派息2：" + dividendInfo.getPxbl() * currentDayPosition.getVol());

                    }

                    dayPosition.put(day, currentDayPosition);
                    //重置初始状态
                    initialDay = currentDayPosition;
                }
                continue;
            }


            //总支出
            double expend = 0;
            //总收入
            double income = 0;
            //总平均成本
            double cost = 0;
            //总收益
            double profit = 0;


            //当期支出
            double termExpend = 0;
            //当期收入
            double termIncome = 0;
            //当期平均成本
            double termCost = 0;

            int tradeTimes = 0;


            //支出
            double dayExpend = 0;
            //收入
            double dayIncome = 0;
            //平均成本
            double dayCost = 0;


            v.sort((u1, u2) -> {
                long diff = u1.getTradeTime().getTime() - u2.getTradeTime().getTime();
                if (diff > 0) {
                    return 1;
                } else if (diff < 0) {
                    return -1;
                }
                return 0; //相等为0
            }); //


            //处理昨日交易结果数据
            if (initialDay != null) {
                vol = initialDay.getVol();

                expend = initialDay.getTotalExpend().doubleValue();

                income = initialDay.getTotalIncome().doubleValue();

                dayExpend = initialDay.getVol() * lastClose;

                if (initialDay.getTermExpend() != null) {
                    termIncome = initialDay.getTermIncome().doubleValue();
                    termExpend = initialDay.getTermExpend().doubleValue();
                    tradeTimes += initialDay.getTermTradeTimes();

                }
                if (initialDay.getClear() != null && initialDay.getClear()) {
                    termIncome = 0;
                    termExpend = 0;
                    tradeTimes = 0;
                    initialDay.setTermOpenTime(null);
                    currentDayPosition.setClear(false);
                }

            }


            for (int i = 0; i < v.size(); i++) {

                //计算每天的交易记录
                TradeDetail td = v.get(i);
                code = td.getCode();
                terms.add(td);
                if (initialDay == null || initialDay.getTermOpenTime() == null) {
                    currentDayPosition.setTermOpenTime(td.getTradeTime());
                } else {
                    currentDayPosition.setTermOpenTime(initialDay.getTermOpenTime());
                }

                tradeTimes++;
                log.info("交易次数：" + tradeTimes);
                currentDayPosition.setTermTradeTimes(tradeTimes);


                if (td.getOpt()) {
                    vol += td.getVol();

                    expend += td.getVol() * td.getPrice().doubleValue();

                    dayExpend += td.getVol() * td.getPrice().doubleValue();

                    termExpend += td.getVol() * td.getPrice().doubleValue();

                    if (td.getCommission() != null) {
                        expend += td.getCommission().doubleValue();
                        dayExpend += td.getCommission().doubleValue();
                        termExpend += td.getCommission().doubleValue();
                    }
                    if (td.getTransFee() != null) {
                        expend += td.getTransFee().doubleValue();
                        dayExpend += td.getTransFee().doubleValue();
                        termExpend += td.getTransFee().doubleValue();
                    }
                    if (td.getTax() != null) {
                        expend += td.getTax().doubleValue();
                        dayExpend += td.getTax().doubleValue();
                        termExpend += td.getTax().doubleValue();
                    }

                    cost = (expend - income) / vol;

                    dayCost = (dayExpend - dayIncome) / vol;

                    log.info("时间:{},买入:{}，数量:{},价格:{},收入:{},支出：{},平均成本：{}，持股数量：{}", td.getTradeTime(), td.getCode(), td.getVol(), td.getPrice().doubleValue(), income, expend, cost, vol);
                } else {
                    vol -= td.getVol();
                    //减仓量超过持仓数量
                    if (vol < 0) {
                        log.error("出现不合法的交易链，最终持仓为负数（循环中）");
                        errors.add(td);
                        break;
                    }

                    income += td.getVol() * td.getPrice().doubleValue();

                    dayIncome += td.getVol() * td.getPrice().doubleValue();

                    termIncome += td.getVol() * td.getPrice().doubleValue();

                    if (td.getCommission() != null) {
                        expend += td.getCommission().doubleValue();
                        dayExpend += td.getCommission().doubleValue();
                        termExpend += td.getCommission().doubleValue();
                    }
                    if (td.getTransFee() != null) {
                        expend += td.getTransFee().doubleValue();
                        dayExpend += td.getTransFee().doubleValue();
                        termExpend += td.getTransFee().doubleValue();
                    }
                    if (td.getTax() != null) {
                        expend += td.getTax().doubleValue();
                        dayExpend += td.getTax().doubleValue();
                        termExpend += td.getTax().doubleValue();
                    }

                    if (vol > 0) {
                        cost = (expend - income) / vol;
                        dayCost = (dayExpend - dayIncome) / vol;
                        log.info("时间:{},卖出:{}，数量:{},价格:{},收入：{},支出：{},平均成本：{}，持股数量：{}", td.getTradeTime(), code, td.getVol(), td.getPrice().doubleValue(), income, expend, cost, vol);
                    }
                }
            }

            currentDayPosition.setCode(code);
            currentDayPosition.setAvgCost(BigDecimal.valueOf(cost));
            currentDayPosition.setDay(day);
            currentDayPosition.setMonth(day.substring(0,7));
            currentDayPosition.setYear(day.substring(0,4));

            currentDayPosition.setClosePrice(BigDecimal.valueOf(close));


            double dayProfit = 0;
            double totalProfit = 0;


            currentDayPosition.setTermExpend(BigDecimal.valueOf(termExpend));
            currentDayPosition.setTermIncome(BigDecimal.valueOf(termIncome));


            currentDayPosition.setTotalExpend(BigDecimal.valueOf(expend));
            currentDayPosition.setTotalIncome(BigDecimal.valueOf(income));

            currentDayPosition.setVol(vol);


            if (vol > 0) {
                dayProfit = (close - dayCost) * vol;
                totalProfit = (close - cost) * vol;
            } else {
                //清仓了
                dayProfit = dayIncome - dayExpend;
                totalProfit = income - expend;

//                currentDayPosition.setTermProfit(BigDecimal.valueOf(termIncome - termExpend));
//                currentDayPosition.setTermProfitRate(BigDecimal.valueOf((termIncome - termExpend) / termExpend));

//                currentDayPosition.setTermCloseTime(v.get(v.size() - 1).getTradeTime());

//                currentDayPosition.setTermHoldDays(DateUtils.getDistanceDays(currentDayPosition.getTermOpenTime(), currentDayPosition.getTermCloseTime()));

                StockHistoryPosition stockHistoryPosition = new StockHistoryPosition();
                stockHistoryPosition.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                stockHistoryPosition.setOpenTime(currentDayPosition.getTermOpenTime());
                stockHistoryPosition.setCloseTime(v.get(v.size() - 1).getTradeTime());
                stockHistoryPosition.setCode(code);
                stockHistoryPosition.setExpend(currentDayPosition.getTermExpend());
                stockHistoryPosition.setIncome(currentDayPosition.getTermIncome());
                stockHistoryPosition.setProfit(BigDecimal.valueOf(termIncome - termExpend));
                log.info(termIncome+"-"+termExpend+"-");
                if(termExpend!=0) {
                    stockHistoryPosition.setProfitRate(BigDecimal.valueOf((termIncome - termExpend) / termExpend));
                }
                stockHistoryPosition.setHoldTime(DateUtils.getDistanceDays(currentDayPosition.getTermOpenTime(), stockHistoryPosition.getCloseTime()));
                stockHistoryPosition.setTradeCount(currentDayPosition.getTermTradeTimes());
                stockHistoryPosition.setTerm(DateUtils.parseDateToStr(currentDayPosition.getTermOpenTime(), DateUtils.DATE_FORMAT_YYYY_MM_DD));
                stockHistoryPositions.add(stockHistoryPosition);
                //TODO 计算当期利润，交易次数，持仓时长
                log.info("清仓了：" + JSONObject.toJSONString(currentDayPosition));

                for (TradeDetail td : terms) {
                    TradeDetail ready2Update = new TradeDetail();
                    ready2Update.setId(td.getId());
                    ready2Update.setTerm(DateUtils.parseDateToStr(currentDayPosition.getTermOpenTime(), DateUtils.DATE_FORMAT_YYYY_MM_DD));
                    //记录需要更新的交易
                    updates.add(ready2Update);

                }
                currentDayPosition.setClear(true);
                List<String> clearCodes = new ArrayList<>();
                clearCodes.add(code);
                costResp.setClearCodes(clearCodes);

                terms.clear();
            }


            currentDayPosition.setTotalProfit(BigDecimal.valueOf(totalProfit));
            currentDayPosition.setDayProfit(BigDecimal.valueOf(dayProfit));

            currentDayPosition.setCreatedTime(new Date());
            currentDayPosition.setId(UUID.randomUUID().toString().replaceAll("-", ""));


            if (dividendInfoMap.containsKey(nextDay)) {

                //当天有除权除息
                DividendInfo dividendInfo = dividendInfoMap.get(nextDay);
                if (dividendInfo.getType() == 1) {
                    currentDayPosition.setTotalIncome(BigDecimal.valueOf(currentDayPosition.getTotalIncome().doubleValue() + dividendInfo.getPxbl() * currentDayPosition.getVol()));
                    currentDayPosition.setTermIncome(BigDecimal.valueOf(currentDayPosition.getTermIncome().doubleValue() + dividendInfo.getPxbl() * currentDayPosition.getVol()));

                    currentDayPosition.setDayProfit(BigDecimal.valueOf(dayProfit + dividendInfo.getPxbl() * currentDayPosition.getVol()));
                    currentDayPosition.setTotalProfit(BigDecimal.valueOf(currentDayPosition.getTotalProfit().doubleValue() + dividendInfo.getPxbl() * currentDayPosition.getVol()));

                    log.info("当天除权：" + day + ",分红：" + currentDayPosition.getDayProfit());
                }

            }

            dayPosition.put(day, currentDayPosition);

            log.info(code + ",昨日盈利：" + profit + ",今日盈利：" + dayProfit + ",总盈利：" + totalProfit);

            initialDay = currentDayPosition;
        }
        if (vol > 0) {
            List<StockPosition> currentStockPositions = new ArrayList<>();
            StockPosition stockPosition = new StockPosition();
            stockPosition.setId(UUID.randomUUID().toString().replaceAll("-", ""));
            stockPosition.setVol(vol);
            stockPosition.setCode(code);
            stockPosition.setCost(initialDay.getAvgCost());
            stockPosition.setCreatedTime(new Date());
            currentStockPositions.add(stockPosition);
            costResp.setCurrentStockPositions(currentStockPositions);
        }

        costResp.setErrors(errors);

        costResp.setStockHistoryPositions(stockHistoryPositions);
        costResp.setUpdates(updates);
        for (String day : dayPosition.keySet()) {
            List<StockPositionDay> stockPositionDays = new ArrayList<>();
            stockPositionDays.add(dayPosition.get(day));
            costResp.getStockPositionDayMap().put(day, stockPositionDays);
        }
        return costResp;
    }

    public static CostResp computeStockProfitDays(List<TradeDetail> tds, IStockQuotationService stockQuotationService) {
        CostResp resp = new CostResp();
        Map<String, List<TradeDetail>> dayTrades = new TreeMap<>();
        tds.forEach(td -> {
            if (dayTrades.containsKey(td.getCode())) {
                dayTrades.get(td.getCode()).add(td);
            } else {
                List<TradeDetail> tradeDetails = new ArrayList<>();
                tradeDetails.add(td);
                dayTrades.put(td.getCode(), tradeDetails);
            }
        });

        for (String code : dayTrades.keySet()) {
            CostResp res = computeStockProfitDays(code, null, null, null, dayTrades.get(code), stockQuotationService);
            resp.getCurrentStockPositions().addAll(res.getCurrentStockPositions());
            resp.getErrors().addAll(res.getErrors());
            resp.getStockHistoryPositions().addAll(res.getStockHistoryPositions());
            resp.getUpdates().addAll(res.getUpdates());
            resp.getClearCodes().addAll(res.getClearCodes());
            for (String day : res.getStockPositionDayMap().keySet()) {
                if (resp.getStockPositionDayMap().containsKey(day)) {
                    resp.getStockPositionDayMap().get(day).addAll(res.getStockPositionDayMap().get(day));
                } else {
                    resp.getStockPositionDayMap().put(day, res.getStockPositionDayMap().get(day));
                }
            }
        }
        return resp;
    }
}
