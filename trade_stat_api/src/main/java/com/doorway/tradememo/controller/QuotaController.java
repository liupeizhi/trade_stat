package com.doorway.tradememo.controller;

import com.doorway.tradememo.domain.StockQuotation;
import com.doorway.tradememo.mapper.TradeDetailMapper;
import com.doorway.tradememo.resp.CommonResponse;
import com.doorway.tradememo.service.IStockInfoService;
import com.doorway.tradememo.service.IStockQuotationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * K线数据
 * Author:liupz
 * Date:2022/4/15
 */
@Api(value = "行情数据",tags = {"行情数据"})
@RestController
@RequestMapping(value = "/quota")
public class QuotaController {

    @Autowired
    private IStockQuotationService stockQuotationService;

    @ApiOperation(value="获取股票日K线数据")
    @GetMapping("/kline/day")
    public CommonResponse<List<StockQuotation>> klineDayData(@RequestParam("code") String code, @RequestParam("start") String start, @RequestParam("end") String end) {
        return new CommonResponse<>(stockQuotationService.getStockDayKlineByRange(code,start,end,0));
    }


    @ApiOperation(value="获取某日行情")
    @GetMapping("/data/{code}/{day}")
    public CommonResponse<StockQuotation> getQuotationByCodeDay(@PathVariable("code")String code, @PathVariable("day")String day)  {
        return new CommonResponse<>(stockQuotationService.getStockQuotationAtDay(code,day));
    }

    @ApiOperation(value="获取最新行情")
    @GetMapping("/data/latest/{code}")
    public CommonResponse<Double> getLatestQuotationByCodeDay(@PathVariable("code")String code)  {
        return new CommonResponse<>(stockQuotationService.getLatestPrice(code));
    }



}
