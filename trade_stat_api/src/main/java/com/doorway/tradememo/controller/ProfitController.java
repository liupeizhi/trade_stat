package com.doorway.tradememo.controller;

import com.doorway.tradememo.resp.CommonResponse;
import com.doorway.tradememo.service.IProfitService;
import com.doorway.tradememo.service.ITradeDetailService;
import com.doorway.tradememo.vo.ProfitVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Note
 * Author:liupz
 * Date:2022/4/9
 */
@Api(value = "收益情况",tags = "收益情况")
@RestController(value = "/profit")
public class ProfitController {

    @Autowired
    IProfitService profitService;

    @Autowired
    ITradeDetailService tradeDetailService;

    @ApiOperation("T-1总收益")
    @GetMapping("/before_today")
    public CommonResponse<ProfitVO> currentProfits() {
        return new CommonResponse<>(profitService.getBeforeTodayProfits());
    }

    @ApiOperation("T日收益")
    @GetMapping("/today")
    public CommonResponse<ProfitVO> todayProfits() {
        return new CommonResponse<>(profitService.getTodayProfits());
    }

    @ApiOperation("今日以前收益分布")
    @GetMapping("/dist")
    public CommonResponse<Map<String,Double>> currentProfitsAll() {
        return new CommonResponse<>(profitService.getStockProfits());
    }

    @ApiOperation(value="今日以前周期收益")
    @GetMapping("/profit/stats")
    public CommonResponse<Map<String,ProfitVO>> statProfits(@RequestParam(value = "code",required = false)String code, @RequestParam("period")String period, @RequestParam(value = "start",required = false)String start, @RequestParam(value = "end",required = false)String end)  {
        return new CommonResponse<>(profitService.getStockPeriodProfits(code,period,start,end));
    }

    @ApiOperation(value="历史仓位收益")
    @GetMapping("/profit/history")
    public CommonResponse<Map<String,List<ProfitVO>>> statProfitsDist(@RequestParam(value = "code",required = false)String code, @RequestParam(value = "start",required = false)String start, @RequestParam(value = "end",required = false)String end)  {
        return new CommonResponse<>(profitService.getStockPositionDays(code,start,end));
    }


}
