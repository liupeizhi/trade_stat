package com.doorway.tradememo.controller;

import com.doorway.tradememo.resp.CommonResponse;
import com.doorway.tradememo.service.ICapitalFlowService;
import com.doorway.tradememo.service.ICostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 首页数据
 * Author:liupz
 * Date:2022/3/26
 */
@Api(value = "成本中心",tags = "成本中心")
@RestController
@RequestMapping(value = "/cost_line")
public class CostLineController {

    @Autowired
    private ICostService costService;


    @ApiOperation(value="成本历史线")
    @GetMapping("/stock")
    public CommonResponse<Map<String,Double>> getCostLine(@RequestParam(value = "code")String code)  {
        return new CommonResponse<>(costService.getCostLine(code));
    }

    @ApiOperation(value="根据持仓周期查询成本历史线")
    @GetMapping("/stock_term")
    public CommonResponse<Map<String,Double>> getCostLineByTerm(@RequestParam(value = "code")String code, @RequestParam(value = "term")String term)  {
        return new CommonResponse<>(costService.getCostLineByTerm(code,term));
    }


}
