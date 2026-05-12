package com.doorway.tradememo.service;

import com.doorway.tradememo.domain.TradeDetail;
import com.doorway.tradememo.vo.TradeScoreVO;

import java.util.List;

/**
 * 交易评分服务接口
 * Author: liupeizhi
 * Date: 2026-02-06
 */
public interface ITradeScoreService {

    /**
     * 获取单笔交易的评分
     * @param tradeId 交易记录ID
     * @return 交易评分VO
     */
    TradeScoreVO getTradeScore(Long tradeId);

    /**
     * 批量获取交易评分
     * @param tradeDetails 交易记录列表
     * @return 交易评分VO列表
     */
    List<TradeScoreVO> batchGetTradeScore(List<TradeDetail> tradeDetails);

    /**
     * 获取用户的评分统计
     * @return 评分统计VO
     */
    TradeScoreVO getScoreStats();
}
