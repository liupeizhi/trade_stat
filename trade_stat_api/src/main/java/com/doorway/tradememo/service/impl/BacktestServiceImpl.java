package com.doorway.tradememo.service.impl;

import com.doorway.tradememo.service.IBacktestService;
import com.doorway.tradememo.vo.BacktestResultVO;
import com.doorway.tradememo.vo.BacktestStrategyVO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 交易回测服务实现
 * Author: liupeizhi
 * Date: 2026-02-06
 */
@Service
public class BacktestServiceImpl implements IBacktestService {

    @Override
    public List<BacktestStrategyVO> getStrategies() {
        List<BacktestStrategyVO> strategies = new ArrayList<>();

        // 1. 均线交叉策略
        BacktestStrategyVO maCrossStrategy = new BacktestStrategyVO();
        maCrossStrategy.setName("ma_cross");
        maCrossStrategy.setDescription("均线交叉策略：短期均线上穿长期均线买入，短期均线下穿长期均线卖出");
        
        List<BacktestStrategyVO.StrategyParam> maCrossParams = new ArrayList<>();
        
        BacktestStrategyVO.StrategyParam shortPeriodParam = new BacktestStrategyVO.StrategyParam();
        shortPeriodParam.setName("short_period");
        shortPeriodParam.setDescription("短期均线周期");
        shortPeriodParam.setDefaultValue("5");
        shortPeriodParam.setType("number");
        maCrossParams.add(shortPeriodParam);
        
        BacktestStrategyVO.StrategyParam longPeriodParam = new BacktestStrategyVO.StrategyParam();
        longPeriodParam.setName("long_period");
        longPeriodParam.setDescription("长期均线周期");
        longPeriodParam.setDefaultValue("20");
        longPeriodParam.setType("number");
        maCrossParams.add(longPeriodParam);
        
        maCrossStrategy.setParams(maCrossParams);
        strategies.add(maCrossStrategy);

        // 2. MACD策略
        BacktestStrategyVO macdStrategy = new BacktestStrategyVO();
        macdStrategy.setName("macd");
        macdStrategy.setDescription("MACD策略：MACD金叉买入，死叉卖出");
        
        List<BacktestStrategyVO.StrategyParam> macdParams = new ArrayList<>();
        
        BacktestStrategyVO.StrategyParam fastPeriodParam = new BacktestStrategyVO.StrategyParam();
        fastPeriodParam.setName("fast_period");
        fastPeriodParam.setDescription("快速EMA周期");
        fastPeriodParam.setDefaultValue("12");
        fastPeriodParam.setType("number");
        macdParams.add(fastPeriodParam);
        
        BacktestStrategyVO.StrategyParam slowPeriodParam = new BacktestStrategyVO.StrategyParam();
        slowPeriodParam.setName("slow_period");
        slowPeriodParam.setDescription("慢速EMA周期");
        slowPeriodParam.setDefaultValue("26");
        slowPeriodParam.setType("number");
        macdParams.add(slowPeriodParam);
        
        BacktestStrategyVO.StrategyParam signalPeriodParam = new BacktestStrategyVO.StrategyParam();
        signalPeriodParam.setName("signal_period");
        signalPeriodParam.setDescription("信号周期");
        signalPeriodParam.setDefaultValue("9");
        signalPeriodParam.setType("number");
        macdParams.add(signalPeriodParam);
        
        macdStrategy.setParams(macdParams);
        strategies.add(macdStrategy);

        // 3. KDJ策略
        BacktestStrategyVO kdjStrategy = new BacktestStrategyVO();
        kdjStrategy.setName("kdj");
        kdjStrategy.setDescription("KDJ策略：K线从下向上穿越D线买入，K线从上向下穿越D线卖出");
        
        List<BacktestStrategyVO.StrategyParam> kdjParams = new ArrayList<>();
        
        BacktestStrategyVO.StrategyParam periodParam = new BacktestStrategyVO.StrategyParam();
        periodParam.setName("period");
        periodParam.setDescription("KDJ周期");
        periodParam.setDefaultValue("9");
        periodParam.setType("number");
        kdjParams.add(periodParam);
        
        BacktestStrategyVO.StrategyParam kParam = new BacktestStrategyVO.StrategyParam();
        kParam.setName("k_period");
        kParam.setDescription("K线周期");
        kParam.setDefaultValue("3");
        kParam.setType("number");
        kdjParams.add(kParam);
        
        BacktestStrategyVO.StrategyParam dParam = new BacktestStrategyVO.StrategyParam();
        dParam.setName("d_period");
        dParam.setDescription("D线周期");
        dParam.setDefaultValue("3");
        dParam.setType("number");
        kdjParams.add(dParam);
        
        kdjStrategy.setParams(kdjParams);
        strategies.add(kdjStrategy);

        return strategies;
    }

    @Override
    public BacktestResultVO runBacktest(String code, String strategy, String params, String startDate, String endDate) {
        // 这里实现回测逻辑
        // 由于需要历史价格数据，这里暂时返回模拟数据
        BacktestResultVO result = new BacktestResultVO();
        result.setCode(code);
        result.setName("模拟股票");
        result.setTimeRange(startDate + " ~ " + endDate);
        result.setStrategy(strategy);

        // 模拟回测结果
        result.setTotalReturn(new BigDecimal("0.35"));
        result.setAnnualizedReturn(new BigDecimal("0.25"));
        result.setMaxDrawdown(new BigDecimal("0.15"));
        result.setSharpeRatio(new BigDecimal("1.8"));
        result.setWinRate(new BigDecimal("0.65"));
        result.setTradeCount(25);
        result.setAvgHoldDays(new BigDecimal("15"));

        // 模拟资金曲线
        List<Map<String, Object>> equityCurve = new ArrayList<>();
        double baseEquity = 100000;
        double currentEquity = baseEquity;
        
        // 生成365天的资金曲线数据
        for (int i = 0; i < 365; i++) {
            Map<String, Object> point = new HashMap<>();
            point.put("date", "2025-" + String.format("%02d", (i / 30) + 1) + "-" + String.format("%02d", (i % 30) + 1));
            // 模拟资金波动
            currentEquity *= (1 + (Math.random() * 0.02 - 0.008));
            point.put("equity", currentEquity);
            point.put("value", currentEquity / baseEquity);
            equityCurve.add(point);
        }
        result.setEquityCurve(equityCurve);

        // 模拟交易信号
        List<BacktestResultVO.TradeSignal> tradeSignals = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            BacktestResultVO.TradeSignal signal = new BacktestResultVO.TradeSignal();
            signal.setDate("2025-" + String.format("%02d", (i * 3) + 1) + "-15");
            signal.setType(i % 2 == 0 ? "buy" : "sell");
            signal.setPrice(new BigDecimal(100 + i * 5));
            signal.setVolume(100);
            signal.setPosition(i % 2 == 0 ? 100 : 0);
            tradeSignals.add(signal);
        }
        result.setTradeSignals(tradeSignals);

        return result;
    }
}
