package com.doorway.tradememo.controller;

import com.doorway.tradememo.resp.CommonResponse;
import com.doorway.tradememo.service.IBacktestService;
import com.doorway.tradememo.vo.BacktestResultVO;
import com.doorway.tradememo.vo.BacktestStrategyVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 交易回测控制器
 * Author: liupeizhi
 * Date: 2026-02-06
 */
@Api(value = "交易回测", tags = {"交易回测"})
@RestController
@RequestMapping(value = "/backtest")
public class BacktestController {

    @Autowired
    private IBacktestService backtestService;

    /**
     * 获取支持的策略列表
     *
     * @return 策略列表
     */
    @ApiOperation(value = "获取支持的策略列表")
    @GetMapping("/strategies")
    public CommonResponse<List<BacktestStrategyVO>> getStrategies() {
        return new CommonResponse<List<BacktestStrategyVO>>(backtestService.getStrategies());
    }

    /**
     * 执行回测
     *
     * @param code      股票代码
     * @param strategy  策略名称
     * @param params    策略参数
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 回测结果
     */
    @ApiOperation(value = "执行回测")
    @GetMapping("/run")
    public CommonResponse<BacktestResultVO> runBacktest(
            @RequestParam("code")
            @ApiParam(value = "股票代码")
                    String code,
            @RequestParam("strategy")
            @ApiParam(value = "策略名称")
                    String strategy,
            @RequestParam(value = "params", required = false)
            @ApiParam(value = "策略参数")
                    String params,
            @RequestParam("startDate")
            @ApiParam(value = "开始日期：yyyy-MM-dd")
                    String startDate,
            @RequestParam("endDate")
            @ApiParam(value = "结束日期：yyyy-MM-dd")
                    String endDate) {
        return new CommonResponse<BacktestResultVO>(backtestService.runBacktest(code, strategy, params, startDate, endDate));
    }
}
