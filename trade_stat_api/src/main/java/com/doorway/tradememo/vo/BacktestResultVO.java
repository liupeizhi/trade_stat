package com.doorway.tradememo.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 回测结果VO
 * Author: liupeizhi
 * Date: 2026-02-06
 */
@Data
public class BacktestResultVO {
    /**
     * 股票代码
     */
    private String code;

    /**
     * 股票名称
     */
    private String name;

    /**
     * 回测时间范围
     */
    private String timeRange;

    /**
     * 策略名称
     */
    private String strategy;

    /**
     * 总收益率
     */
    private BigDecimal totalReturn;

    /**
     * 年化收益率
     */
    private BigDecimal annualizedReturn;

    /**
     * 最大回撤
     */
    private BigDecimal maxDrawdown;

    /**
     * 夏普比率
     */
    private BigDecimal sharpeRatio;

    /**
     * 胜率
     */
    private BigDecimal winRate;

    /**
     * 交易次数
     */
    private Integer tradeCount;

    /**
     * 平均持仓天数
     */
    private BigDecimal avgHoldDays;

    /**
     * 资金曲线数据
     */
    private List<Map<String, Object>> equityCurve;

    /**
     * 交易信号点
     */
    private List<TradeSignal> tradeSignals;

    /**
     * 交易信号
     */
    @Data
    public static class TradeSignal {
        /**
         * 日期
         */
        private String date;

        /**
         * 信号类型（buy/sell）
         */
        private String type;

        /**
         * 价格
         */
        private BigDecimal price;

        /**
         * 数量
         */
        private Integer volume;

        /**
         * 持仓
         */
        private Integer position;
    }
}
