package com.doorway.tradememo.service;

import com.doorway.tradememo.vo.CapitalFlowVO;

import java.util.List;
import java.util.Map;

/**
 * 资金流水
 * Author:liupz
 * Date:2022/5/28
 */
public interface ICapitalFlowService {

    /**
     * 获取总支出
     * @return totalFlow
     */
    CapitalFlowVO getTotalFlow();


    /**
     * 获取每股流水分布
     *
     * @return <stockCode,flow>
     */
    Map<String,CapitalFlowVO> getDistStockFlow();

    /**
     * 当周总流水
     * @return flow
     */
    CapitalFlowVO getThisWeekFlow();

    /**
     * 当月总流水
     * @return flow
     */
    CapitalFlowVO getThisMonthFlow();


    /**
     * 历史每周期累计流水
     * [time,data]
     * @param code stockCode
     * @param period 周期
     * @return <time,flow>
     */
    Map<String, CapitalFlowVO> getStockFlowStatics(String code, String period, String start, String end);

    /**
     * 获取历史周期按股票分布的累计流水
     * [time,[code,data]]
     * @param code
     * @param period
     * @param start
     * @param end
     * @return
     */
    Map<String, Map<String,CapitalFlowVO>> getDistStockFlowStatics(String code, String period, String start, String end);

}
