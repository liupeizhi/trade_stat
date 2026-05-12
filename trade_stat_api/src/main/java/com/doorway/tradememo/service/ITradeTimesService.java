package com.doorway.tradememo.service;

import com.doorway.tradememo.vo.TradeTimesVO;

import java.util.Map;

/**
 * 交易次数
 * Author:liupz
 * Date:2022/5/28
 */
public interface ITradeTimesService {

    /**
     * 获取历史总次数
     * @return totalFlow
     */
    Long getTotalTimes();

    /**
     * 获取历史每股总次数分布
     *
     * @return <stockCode,profit>
     */
    Map<String,Long> getAllStockTradeTimes();

    /**
     * 当周总次数
     * @return times
     */
    Long getThisWeekTradeTimes();

    /**
     * 当月总次数
     * @return times
     */
    Long getThisMonthTradeTimes();


    /**
     * 每股每周期次数
     * @param code stockCode
     * @param period 周期
     * @return <time,times>
     */
    Map<String, TradeTimesVO> getStockTradeTimes(String code, String period, String start, String end);

    /**
     * 每股每周期累加次数
     * @param code stockCode
     * @param period 周期
     * @return <time,times>
     */
    Map<String,Long> getSumStockTradeTimes(String code,String period,String start,String end);

}
