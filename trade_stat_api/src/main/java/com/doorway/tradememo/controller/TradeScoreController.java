package com.doorway.tradememo.controller;

import com.doorway.tradememo.resp.CommonResponse;
import com.doorway.tradememo.service.ITradeScoreService;
import com.doorway.tradememo.vo.TradeScoreVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 交易评分控制器
 * Author: liupeizhi
 * Date: 2026-02-06
 */
@Api(value = "交易评分", tags = {"交易评分"})
@RestController
@RequestMapping(value = "/trade_score")
public class TradeScoreController {

    @Autowired
    private ITradeScoreService tradeScoreService;

    /**
     * 获取单笔交易的评分
     *
     * @param tradeId 交易记录ID
     * @return 交易评分
     */
    @ApiOperation(value = "获取单笔交易的评分")
    @GetMapping("/get_score")
    public CommonResponse<TradeScoreVO> getTradeScore(
            @RequestParam("tradeId")
            @ApiParam(value = "交易记录ID")
                    Long tradeId) {
        return new CommonResponse<TradeScoreVO>(tradeScoreService.getTradeScore(tradeId));
    }

    /**
     * 获取用户的评分统计
     *
     * @return 评分统计
     */
    @ApiOperation(value = "获取用户的评分统计")
    @GetMapping("/get_stats")
    public CommonResponse<TradeScoreVO> getScoreStats() {
        return new CommonResponse<TradeScoreVO>(tradeScoreService.getScoreStats());
    }
}
