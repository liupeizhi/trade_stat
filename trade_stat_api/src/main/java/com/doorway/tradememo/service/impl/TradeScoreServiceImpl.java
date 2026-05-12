package com.doorway.tradememo.service.impl;

import com.doorway.tradememo.domain.TradeDetail;
import com.doorway.tradememo.service.ITradeScoreService;
import com.doorway.tradememo.vo.TradeScoreVO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 交易评分服务实现
 * Author: liupeizhi
 * Date: 2026-02-06
 */
@Service
public class TradeScoreServiceImpl implements ITradeScoreService {

    @Override
    public TradeScoreVO getTradeScore(Long tradeId) {
        // TODO: 从数据库获取交易记录
        // 这里先返回模拟数据
        TradeScoreVO vo = new TradeScoreVO();
        vo.setTradeId(tradeId);
        vo.setCode("600519");
        vo.setTradeTime("2026-02-01 10:00:00");
        vo.setOpt(1); // 1: 买入
        vo.setProfitRate(new BigDecimal("0.15"));
        vo.setHoldDays(30);
        
        // 计算评分
        calculateScore(vo);
        
        return vo;
    }

    @Override
    public List<TradeScoreVO> batchGetTradeScore(List<TradeDetail> tradeDetails) {
        List<TradeScoreVO> result = new ArrayList<>();
        for (TradeDetail detail : tradeDetails) {
            TradeScoreVO vo = new TradeScoreVO();
            // 类型转换：String -> Long
            try {
                vo.setTradeId(detail.getId() != null ? Long.parseLong(detail.getId()) : null);
            } catch (NumberFormatException e) {
                vo.setTradeId(null);
            }
            vo.setCode(detail.getCode());
            vo.setTradeTime(detail.getTradeTime().toString());
            // 类型转换：Boolean -> Integer
            vo.setOpt(detail.getOpt() != null ? (detail.getOpt() ? 1 : 0) : null);
            
            // 这里需要根据实际情况计算收益率和持仓天数
            // 暂时使用模拟数据
            vo.setProfitRate(new BigDecimal(Math.random() * 0.3 - 0.1));
            vo.setHoldDays((int) (Math.random() * 60) + 1);
            
            calculateScore(vo);
            result.add(vo);
        }
        return result;
    }

    @Override
    public TradeScoreVO getScoreStats() {
        TradeScoreVO stats = new TradeScoreVO();
        stats.setAvgScore(75);
        stats.setHighScore(95);
        stats.setLowScore(40);
        stats.setScoreCount(120);
        
        // 评分分布
        Map<String, Integer> distribution = new HashMap<>();
        distribution.put("90-100", 15);
        distribution.put("80-89", 30);
        distribution.put("70-79", 45);
        distribution.put("60-69", 20);
        distribution.put("0-59", 10);
        stats.setScoreDistribution(distribution);
        
        return stats;
    }

    /**
     * 计算交易评分
     * @param vo 交易评分VO
     */
    private void calculateScore(TradeScoreVO vo) {
        Map<String, Integer> dimensionScores = new HashMap<>();
        
        // 1. 收益评分（40分）
        int profitScore = calculateProfitScore(vo.getProfitRate());
        dimensionScores.put("收益评分", profitScore);
        
        // 2. 持仓时间评分（20分）
        int holdTimeScore = calculateHoldTimeScore(vo.getHoldDays());
        dimensionScores.put("持仓时间评分", holdTimeScore);
        
        // 3. 时机评分（20分）
        int timingScore = calculateTimingScore(vo.getOpt(), vo.getProfitRate());
        dimensionScores.put("时机评分", timingScore);
        
        // 4. 风险控制评分（20分）
        int riskScore = calculateRiskScore(vo.getProfitRate());
        dimensionScores.put("风险控制评分", riskScore);
        
        // 计算总评分
        int totalScore = profitScore + holdTimeScore + timingScore + riskScore;
        vo.setTotalScore(totalScore);
        vo.setDimensionScores(dimensionScores);
        
        // 生成评分说明
        generateScoreDescription(vo);
    }

    /**
     * 计算收益评分（0-40分）
     * @param profitRate 收益率
     * @return 收益评分
     */
    private int calculateProfitScore(BigDecimal profitRate) {
        if (profitRate == null) return 20;
        
        double rate = profitRate.doubleValue();
        if (rate >= 0.3) return 40;
        if (rate >= 0.2) return 35;
        if (rate >= 0.1) return 30;
        if (rate >= 0) return 25;
        if (rate >= -0.05) return 20;
        if (rate >= -0.1) return 15;
        if (rate >= -0.2) return 10;
        return 5;
    }

    /**
     * 计算持仓时间评分（0-20分）
     * @param holdDays 持仓天数
     * @return 持仓时间评分
     */
    private int calculateHoldTimeScore(Integer holdDays) {
        if (holdDays == null) return 10;
        
        // 合理持仓时间：7-30天
        if (holdDays >= 7 && holdDays <= 30) return 20;
        if (holdDays >= 3 && holdDays <= 6) return 15;
        if (holdDays >= 31 && holdDays <= 60) return 15;
        if (holdDays == 1 || holdDays == 2) return 10;
        if (holdDays > 60) return 10;
        return 5;
    }

    /**
     * 计算时机评分（0-20分）
     * @param opt 操作类型（1:买入，0:卖出）
     * @param profitRate 收益率
     * @return 时机评分
     */
    private int calculateTimingScore(Integer opt, BigDecimal profitRate) {
        if (opt == null || profitRate == null) return 10;
        
        double rate = profitRate.doubleValue();
        // 买入时机：如果是买入后上涨，评分高
        if (opt == 1) {
            if (rate > 0) return 20;
            if (rate > -0.05) return 15;
            if (rate > -0.1) return 10;
            return 5;
        }
        // 卖出时机：如果是卖出后下跌，评分高
        else {
            if (rate > 0) return 20;
            if (rate > -0.05) return 15;
            if (rate > -0.1) return 10;
            return 5;
        }
    }

    /**
     * 计算风险控制评分（0-20分）
     * @param profitRate 收益率
     * @return 风险控制评分
     */
    private int calculateRiskScore(BigDecimal profitRate) {
        if (profitRate == null) return 10;
        
        double rate = profitRate.doubleValue();
        // 收益率波动越小，风险控制评分越高
        if (Math.abs(rate) <= 0.05) return 20;
        if (Math.abs(rate) <= 0.1) return 15;
        if (Math.abs(rate) <= 0.2) return 10;
        return 5;
    }

    /**
     * 生成评分说明
     * @param vo 交易评分VO
     */
    private void generateScoreDescription(TradeScoreVO vo) {
        StringBuilder description = new StringBuilder();
        
        int totalScore = vo.getTotalScore();
        if (totalScore >= 90) {
            description.append("优秀的交易操作！");
        } else if (totalScore >= 80) {
            description.append("良好的交易操作。");
        } else if (totalScore >= 70) {
            description.append("不错的交易操作，有改进空间。");
        } else if (totalScore >= 60) {
            description.append("一般的交易操作，需要注意改进。");
        } else {
            description.append("较差的交易操作，建议分析原因。");
        }
        
        // 根据评分维度给出具体建议
        Map<String, Integer> dimensionScores = vo.getDimensionScores();
        if (dimensionScores != null) {
            if (dimensionScores.get("收益评分") < 25) {
                description.append(" 建议关注收益情况，优化买入时机。");
            }
            if (dimensionScores.get("持仓时间评分") < 15) {
                description.append(" 建议合理控制持仓时间。");
            }
            if (dimensionScores.get("时机评分") < 15) {
                description.append(" 建议提高对市场时机的判断能力。");
            }
            if (dimensionScores.get("风险控制评分") < 15) {
                description.append(" 建议加强风险控制意识。");
            }
        }
        
        vo.setScoreDescription(description.toString());
    }
}
