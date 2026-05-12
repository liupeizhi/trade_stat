package com.doorway.tradememo.service;

import com.doorway.tradememo.vo.BacktestResultVO;
import com.doorway.tradememo.vo.BacktestStrategyVO;

import java.util.List;

/**
 * 交易回测服务接口
 * Author: liupeizhi
 * Date: 2026-02-06
 */
public interface IBacktestService {

    /**
     * 获取支持的策略列表
     * @return 策略列表
     */
    List<BacktestStrategyVO> getStrategies();

    /**
     * 执行回测
     * @param code 股票代码
     * @param strategy 策略名称
     * @param params 策略参数
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 回测结果
     */
    BacktestResultVO runBacktest(String code, String strategy, String params, String startDate, String endDate);
}
