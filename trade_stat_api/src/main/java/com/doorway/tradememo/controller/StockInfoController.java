package com.doorway.tradememo.controller;

import com.doorway.tradememo.domain.StockInfo;
import com.doorway.tradememo.resp.CommonResponse;
import com.doorway.tradememo.service.IStockInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * Note
 * Author:liupz
 * Date:2022/4/3
 */
@Api(value = "股票数据",tags = {"股票数据"})
@RestController
public class StockInfoController {

    @Autowired
    private IStockInfoService stockInfoService;

    @ApiOperation(value="获取所有股票信息")
    @GetMapping("/stockInfo")
    public CommonResponse<List<StockInfo>> stockInfo()  {
        return new CommonResponse<List<StockInfo>>(stockInfoService.getStockInfos());
    }
    @ApiOperation(value="获取历史持仓股票信息")
    @GetMapping("/historyStocks")
    public CommonResponse<List<StockInfo>> historyStocks()  {
        return new CommonResponse<List<StockInfo>>(stockInfoService.historyStocks());
    }

    @ApiOperation(value="根据拼音代码查询股票信息")
    @GetMapping("/suggestStocks")
    public CommonResponse<List<StockInfo>> suggestStock(@Param("name")String name)  {
        return new CommonResponse<List<StockInfo>>(stockInfoService.suggestStock(name));
    }


    @GetMapping("/stockInfo.js")
    public String stockInfoJS()  {
        return stockInfoService.getStockInfoJS();
    }

    @GetMapping("/stockInfo/sync")
    public void sync()  {
         stockInfoService.sync();
    }

    @GetMapping("/stockInfo/syncNewStocks")
    public void syncNewStocks() throws IOException {
        stockInfoService.syncNewStocks();
    }


}
