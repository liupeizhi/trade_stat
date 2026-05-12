package com.doorway.tradememo.utils;

import com.alibaba.fastjson.JSONObject;
import com.doorway.tradememo.domain.StockHistoryPosition;
import com.doorway.tradememo.domain.StockPosition;
import com.doorway.tradememo.domain.TradeDetail;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.*;

/**
 * Note
 * Author:liupz
 * Date:2022/4/4
 */
@Slf4j
public class ComputeCost {

    public static CostResp compute(List<TradeDetail> tds) {
        Map<String, List<TradeDetail>> positions = new HashMap<>();

        tds.forEach(td -> {
            if (positions.containsKey(td.getCode())) {
                positions.get(td.getCode()).add(td);
            } else {
                List<TradeDetail> tdd = new ArrayList<>();
                tdd.add(td);
                positions.put(td.getCode(), tdd);
            }
        });

        //待升级的交易记录
        List<TradeDetail> updates = new ArrayList<>();
        //清仓仓位记录
        List<StockHistoryPosition> stockHistoryPositions = new ArrayList<>();
        //当前持仓记录
        List<StockPosition> currentStockPositions = new ArrayList<>();
        //这个周期内每只证券的支出收入成本和数量
        Map<String, String> result = new HashMap<>();

        List<TradeDetail> errors = new ArrayList<>();



        positions.keySet().forEach(code -> {
            List<TradeDetail> tradeDetails = positions.get(code);

            tradeDetails.sort((u1, u2) -> {
                long diff = u1.getTradeTime().getTime() - u2.getTradeTime().getTime();
                if (diff > 0) {
                    return 1;
                } else if (diff < 0) {
                    return -1;
                }
                return 0; //相等为0
            }); // 按年龄排序
            
            
            
            int vol = 0;
            //支出
            double expend = 0;
            //收入
            double income = 0;


            double avg = 0;

            List<TradeDetail> terms = new ArrayList<>();
            for (int i=0;i< tradeDetails.size();i++) {
                TradeDetail td = tradeDetails.get(i);
                terms.add(td);
                if (td.getOpt()) {
                    vol += td.getVol();
                    expend += td.getPrice().doubleValue() * td.getVol();
                    if(td.getCommission()!=null) {
                        expend += td.getCommission().doubleValue();
                    }
                    if(td.getTransFee()!=null) {
                        expend += td.getTransFee().doubleValue();
                    }
                    avg = (expend - income) / vol;
                    log.info("时间:{},买入:{}，数量:{},价格:{},收入:{},支出：{},平均成本：{}，持股数量：{}", td.getTradeTime(),code,td.getVol(),td.getPrice().doubleValue(), income,expend, avg, vol);
                } else {
                    vol -= td.getVol();
                    //减仓量超过持仓数量
                    if(vol < 0 ){
                        log.error("出现不合法的交易链，最终持仓为负数（循环中）");

                        break;
                    }

                    income += td.getVol() * td.getPrice().doubleValue();
                    if(td.getCommission()!=null) {
                        expend += td.getCommission().doubleValue();
                    }
                    if(td.getTransFee()!=null) {
                        expend += td.getTransFee().doubleValue();
                    }
                    if(td.getTax()!=null) {
                        expend += td.getTax().doubleValue();
                    }

                    if (vol == 0) {
                        boolean isFinal = true;
                        if(i!=tradeDetails.size()-1){
                            TradeDetail next = tradeDetails.get(i+1);
                            if(next.getDay().equals(td.getDay())){//说明本日交易还没结束
                                isFinal = false;
                            }
                        }
                        if(isFinal) {
                            //清仓了
                            Date openTime = null;
                            Date closeTime = null;
                            TradeDetail last = null;
                            int count = 0;
                            for (TradeDetail dd : terms) {
                                if (openTime == null) {
                                    openTime = dd.getTradeTime();
                                }
                                TradeDetail ready2Update = new TradeDetail();
                                ready2Update.setId(dd.getId());
                                ready2Update.setTerm(terms.get(0).getDay());
                                //记录需要更新的交易
                                updates.add(ready2Update);
                                last = dd;
                                count++;
                            }
                            if (last != null) {
                                closeTime = last.getTradeTime();
                            }


                            StockHistoryPosition stockProfit = new StockHistoryPosition();
                            stockProfit.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                            stockProfit.setTerm(terms.get(0).getDay());
                            stockProfit.setCode(td.getCode());
                            stockProfit.setProfit(BigDecimal.valueOf(income - expend));
                            stockProfit.setProfitRate(BigDecimal.valueOf((income - expend) / expend));


                            stockProfit.setOpenTime(openTime);
                            stockProfit.setCloseTime(closeTime);
                            try {
                                stockProfit.setHoldTime(DateUtils.getDistanceDays(openTime, closeTime));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if (code.equals("")) {

                            }
                            stockProfit.setTradeCount(count);

                            stockProfit.setIncome(BigDecimal.valueOf(income));
                            stockProfit.setExpend(BigDecimal.valueOf(expend));
                            stockProfit.setCreatedTime(new Date());
                            //记录历史仓位
                            stockHistoryPositions.add(stockProfit);
                            terms.clear();
                            log.info("清仓:{}，盈利：{},盈亏百分比：{}", code, (income - expend), 100 * (income - expend) / expend + "%");
                            expend = 0;
                            income = 0;
                        }
                    }

                    if (vol > 0){

                        avg = (expend - income) / vol;
                        log.info("时间:{},卖出:{}，数量:{},价格:{},收入：{},支出：{},平均成本：{}，持股数量：{}", td.getTradeTime(),code,td.getVol(),td.getPrice().doubleValue(), income, expend, avg, vol);

                    }




                }
            }

            if(vol>0) {
                StockPosition sp = new StockPosition();
                sp.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                sp.setCode(code);
                sp.setCost(BigDecimal.valueOf(avg));
                sp.setVol(vol);
                sp.setCreatedTime(new Date());
                currentStockPositions.add(sp);
                result.put(code, expend + "_" + income + "_" + vol+"_"+avg);
            }
            if (vol < 0){
                log.error("出现不合法的交易链，最终持仓为负数："+ JSONObject.toJSONString(tradeDetails));
                errors.addAll(terms);

                //交易中间出现不合法交易，后续的交易记录标记为错误
               TradeDetail last = terms.get(terms.size()-1);
               boolean found = false;
                for (TradeDetail td : tradeDetails) {
                    if(td.getId().equals(last.getId())){
                        found = true;
                    }
                    if(found){
                        errors.add(td);
                    }
                }

            }
        });


        CostResp costResp = new CostResp();
        costResp.setCurrentStockPositions(currentStockPositions);
        costResp.setResult(result);
        costResp.setStockHistoryPositions(stockHistoryPositions);
        costResp.setUpdates(updates);
        costResp.setErrors(errors);
        return costResp;

    }
}
