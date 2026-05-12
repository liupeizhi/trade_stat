package com.doorway.tradememo.controller;

import com.doorway.tradememo.resp.CommonResponse;
import com.doorway.tradememo.service.IHoldDaysService;
import com.doorway.tradememo.service.IProfitService;
import com.doorway.tradememo.vo.ProfitVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Note
 * Author:liupz
 * Date:2022/4/9
 */
@Api(value = "持股时长",tags = "持股时长")
@RestController
@RequestMapping("/hold_days")
public class HoldDaysController {

    @Autowired
    IHoldDaysService holdDaysService;

    @ApiOperation("各证券总的持股时长")
    @GetMapping("/all")
    public CommonResponse<Map<String,Integer>> getAllStockHoldDays() {
        return new CommonResponse<>(holdDaysService.getAllStockHoldDays());
    }

    @ApiOperation("单只证券总的持股时长")
    @GetMapping("/stock")
    public CommonResponse<Integer> getAllStockHoldDays(@RequestParam("code")String code) {
        return new CommonResponse<>(holdDaysService.getStockHoldDays(code));
    }


}
