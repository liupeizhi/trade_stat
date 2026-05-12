package com.doorway.tradememo.controller;

import com.doorway.tradememo.resp.CommonResponse;
import com.doorway.tradememo.service.ICapitalFlowService;
import com.doorway.tradememo.vo.CapitalFlowVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 首页数据
 * Author:liupz
 * Date:2022/3/26
 */
@Api(value = "资金流水、佣金、税费",tags = "资金流水、佣金、税费")
@RestController
@RequestMapping(value = "/capital_flow")
public class CapitalFlowController {

    @Autowired
    private ICapitalFlowService capitalFlowService;


    @ApiOperation(value="全部总流水")
    @GetMapping("/total")
    public CommonResponse<CapitalFlowVO> total()  {
        return new CommonResponse<>(capitalFlowService.getTotalFlow());
    }

    @ApiOperation(value="个股总流水")
    @GetMapping("/stock_flows")
    public CommonResponse<Map<String, CapitalFlowVO>> stockFlows()  {
        return new CommonResponse<>(capitalFlowService.getDistStockFlow());
    }

    @ApiOperation(value="本周流水")
    @GetMapping("/this_week_flows")
    public CommonResponse<CapitalFlowVO> getThisWeekFlow()  {
        return new CommonResponse<>(capitalFlowService.getThisWeekFlow());
    }


    @ApiOperation(value="本月流水")
    @GetMapping("/this_month_flows")
    public CommonResponse<CapitalFlowVO> getThisMonthFlow()  {
        return new CommonResponse<>(capitalFlowService.getThisMonthFlow());
    }

    @ApiOperation(value="周期流水")
    @GetMapping("/stat_flows")
    public CommonResponse<Map<String, CapitalFlowVO>> statFlows(@RequestParam(value = "code",required = false)String code, @RequestParam("period")String period, @RequestParam(value = "start",required = false)String start, @RequestParam(value = "end",required = false)String end)  {
        return new CommonResponse<>(capitalFlowService.getStockFlowStatics(code,period,start,end));
    }

    @ApiOperation(value="周期股票流水分布")
    @GetMapping("/stat_flows_stocks")
    public CommonResponse<Map<String, Map<String,CapitalFlowVO>>> statFlowsStocks(@RequestParam(value = "code",required = false)String code, @RequestParam("period")String period, @RequestParam(value = "start",required = false)String start, @RequestParam(value = "end",required = false)String end)  {
        return new CommonResponse<>(capitalFlowService.getDistStockFlowStatics(code,period,start,end));
    }

}
