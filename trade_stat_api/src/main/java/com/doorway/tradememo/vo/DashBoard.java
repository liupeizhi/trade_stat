package com.doorway.tradememo.vo;

import com.doorway.tradememo.domain.StockPosition;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 首页数据
 * Author:liupz
 * Date:2022/3/26
 */
@Data
public class DashBoard {

    //总收益=浮动收益+历史清仓收益
    private double totalProfit;
    //浮动收益
    private double currentProfit;
    //历史清仓收益
    private double historyProfit;

    //总市值
    private double marketValue;
    //总交易次数
    private Integer tradeCount;
    //总流水
    private double capitalFlow;
    //当周收益
    private double weekProfit;
    //当周交易次数
    private Integer weekTradeCount;
    //当周流水
    private double weekCapitalFlow;
    //当月收益
    private double monthProfit;
    //当月交易次数
    private Integer monthTradeCount;
    //当月总流水
    private double monthCapitalFlow;

    //总胜率
    private double winRate;


    //股票流水分布
    private Map<String,Double> capitalFlows;
    //总体收益排名
    private Map<String,Double> profitRanks;
    //股票交易次数分布
    private Map<String,Long> tradeCounts;
    //持股时长排名
    private Map<String,Integer> holdTimes;



    //当前持仓收益
    private Map<String,Double> currentPositionsProfits;

    //历史清仓收益
    private Map<String,Double> historyPositionsProfits;





    //当前仓位市值分布
    private Map<String,Double> currentPositions;

    //历史仓位市值分布
    private Map<String,Double> historyPositions;


}
