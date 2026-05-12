package com.doorway.tradememo.service;

import java.util.Map;

/**
 * 持股时长
 * Author:liupz
 * Date:2022/5/28
 */
public interface IHoldDaysService {

    /**
     * 每只股票的持股天数
     *
     * @return <stockCode,days>
     */
    Map<String,Integer> getAllStockHoldDays();

    /**
     * 某只股票的持股天数
     *
     * @return times
     */
    Integer getStockHoldDays(String code);


}
