package com.doorway.tradememo.controller;

import com.doorway.tradememo.resp.CommonResponse;
import com.doorway.tradememo.service.ICapitalFlowService;
import com.doorway.tradememo.service.ITradeTimesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 首页数据
 * Author:liupz
 * Date:2022/3/26
 */
@Api(value = "交易次数",tags = "交易次数")
@RestController
@RequestMapping(value = "/trade_times")
public class TradeTimesController {

    @Autowired
    private ITradeTimesService tradeTimesService;


    @ApiOperation(value="总次数")
    @GetMapping("/total")
    public CommonResponse total()  {
        return new CommonResponse<>(tradeTimesService.getTotalTimes());
    }

    @ApiOperation(value="个股次数")
    @GetMapping("/stock_times")
    public CommonResponse getAllStockTradeTimes()  {
        return new CommonResponse<>(tradeTimesService.getAllStockTradeTimes());
    }

    @ApiOperation(value="本周次数")
    @GetMapping("/this_week_times")
    public CommonResponse getThisWeekTradeTimes()  {
        return new CommonResponse<>(tradeTimesService.getThisWeekTradeTimes());
    }


    @ApiOperation(value="本月次数")
    @GetMapping("/this_month_times")
    public CommonResponse getThisMonthTradeTimes()  {
        return new CommonResponse<>(tradeTimesService.getThisMonthTradeTimes());
    }

    @ApiOperation(value="历史汇总次数")
    @GetMapping("/stat_times")
    public CommonResponse getStockTradeTimes(@RequestParam(value = "code",required = false)String code,@RequestParam("period")String period,@RequestParam(value = "start",required = false)String start,@RequestParam(value = "end",required = false)String end)  {
        return new CommonResponse<>(tradeTimesService.getStockTradeTimes(code,period,start,end));
    }


}
