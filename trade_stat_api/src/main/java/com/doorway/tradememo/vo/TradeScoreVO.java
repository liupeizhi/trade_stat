package com.doorway.tradememo.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 交易评分VO
 * Author: liupeizhi
 * Date: 2026-02-06
 */
@Data
public class TradeScoreVO {
    /**
     * 交易记录ID
     */
    private Long tradeId;

    /**
     * 股票代码
     */
    private String code;

    /**
     * 交易时间
     */
    private String tradeTime;

    /**
     * 操作类型（买入/卖出）
     */
    private Integer opt;

    /**
     * 总评分（0-100）
     */
    private Integer totalScore;

    /**
     * 评分维度明细
     */
    private Map<String, Integer> dimensionScores;

    /**
     * 评分说明
     */
    private String scoreDescription;

    /**
     * 收益率
     */
    private BigDecimal profitRate;

    /**
     * 持仓天数
     */
    private Integer holdDays;

    /**
     * 评分统计相关字段
     */
    private Integer avgScore;
    private Integer highScore;
    private Integer lowScore;
    private Integer scoreCount;
    private Map<String, Integer> scoreDistribution;
}
