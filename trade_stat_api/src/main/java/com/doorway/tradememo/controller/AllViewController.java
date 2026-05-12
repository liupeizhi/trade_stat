package com.doorway.tradememo.controller;

import com.doorway.tradememo.domain.StockInfo;
import com.doorway.tradememo.resp.CommonResponse;
import com.doorway.tradememo.service.IDashBoardService;
import com.doorway.tradememo.service.IPositionService;
import com.doorway.tradememo.service.IStockInfoService;
import com.doorway.tradememo.vo.StockView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Note
 * Author:liupz
 * Date:2022/8/18
 */
@Api(value = "股票全景图", tags = "股票全景图")
@RestController
@RequestMapping(value = "/all_view")
public class AllViewController {

    @Autowired
    IDashBoardService dashBoardService;
    @Autowired
    IPositionService positionService;
    @Autowired
    private IStockInfoService stockInfoService;
    @ApiOperation(value = "全部总流水")
    @GetMapping("/total")
    public CommonResponse<List<StockView>> allView() {
        return new CommonResponse<>(dashBoardService.allView());
    }





}
