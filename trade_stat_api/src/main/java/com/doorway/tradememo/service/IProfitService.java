package com.doorway.tradememo.service;

import com.doorway.tradememo.vo.ProfitVO;

import java.util.List;
import java.util.Map;

/**
 * 收益
 * Author:liupz
 * Date:2022/4/9
 */
public interface IProfitService {


    /**
     * 历史总收益
     */
    ProfitVO getBeforeTodayProfits();

    ProfitVO getTodayProfits();

    /**
     * 当周总收益
     * @return flow
     */
    ProfitVO getThisWeekProfit();

    /**
     * 当月总收益
     * @return flow
     */
    ProfitVO getThisMonthProfit();

    /**
     * 股票收益分布
     * @return <code,profit>
     */
    Map<String,Double> getStockProfits();


    /**
     * 每股每周期收益
     * @param code stockCode
     * @param period 周期
     * @return <time,flow>
     */
    Map<String,ProfitVO> getStockPeriodProfits(String code,String period,String start,String end);

    Map<String,List<ProfitVO>> getStockPositionDays(String code, String start, String end);



}
