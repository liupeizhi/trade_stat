package com.doorway.tradememo.controller;

import com.doorway.tradememo.domain.StockPosition;
import com.doorway.tradememo.domain.StockPositionDay;
import com.doorway.tradememo.req.StockHistoryPositionQO;
import com.doorway.tradememo.resp.CommonResponse;
import com.doorway.tradememo.resp.PageResponse;
import com.doorway.tradememo.service.IPositionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 仓位相关信息
 * Author:liupz
 * Date:2022/4/9
 */
@Api(value = "仓位管理", tags = {"仓位管理"})
@RestController
@RequestMapping(value = "/position")
public class PositionController {
    @Autowired
    private IPositionService positionService;

    /**
     * 查询当前仓位
     *
     * @return
     */
    @ApiOperation(value = "获取当前仓位")
    @GetMapping("/current_positions")
    public CommonResponse<List<StockPosition>> currentPositions() {
        return new CommonResponse<List<StockPosition>>(positionService.getCurrentPositions());
    }


    /**
     * 查询历史清仓交易仓位
     *
     * @param detail
     * @param pageSize
     * @param pageNo
     * @param sortField
     * @param sortOrder
     * @return
     */
    @ApiOperation(value = "查询历史清仓信息")
    @PostMapping("/stock_history_positions")
    public PageResponse details(
            @RequestBody StockHistoryPositionQO detail,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10")
            @ApiParam(value = "每页记录数")
                    Integer pageSize,
            @RequestParam(value = "pageNo", required = false, defaultValue = "1")
            @ApiParam(value = "第几页")
                    Integer pageNo,
            @RequestParam(value = "field", required = false, defaultValue = "closeTime")
            @ApiParam(value = "根据哪个字段排序")
                    String sortField,
            @RequestParam(value = "order", required = false, defaultValue = "desc")
            @ApiParam(value = "如何排序，asc:升序，desc：降序")
                    String sortOrder) {
        if (StringUtils.hasLength(detail.getCode())) {
            detail.setCode(detail.getCode().substring(0, 6));
        }

        return positionService.getTermTrades(detail, pageNo, pageSize, sortField, sortOrder);
    }


    /**
     * 查看某交易日的仓位
     *
     * @return
     */
    @ApiOperation(value = "查询某天某股票的持仓情况")
    @GetMapping("/day_position")
    public CommonResponse<StockPositionDay> dayHistory(@RequestParam("code")
                                                       @ApiParam(value = "股票代码")
                                                               String code,
                                                       @RequestParam("startDay")
                                                       @ApiParam(value = "哪一天")
                                                               String day) {
        return new CommonResponse<>(positionService.historyPositionByCode(day,code));
    }


    /**
     * 计算区间内的所有交易日的仓位
     *
     * @param start
     * @return
     */
    @ApiOperation(value = "计算某日后某只股票的收益")
    @GetMapping("/compute")
    public CommonResponse computeStockPositionDay(@RequestParam(value = "start")
                                                  @ApiParam(value = "开始日期：yyyy-MM-dd")
                                                          String start,
                                                  @RequestParam(value = "code", required = false)
                                                  @ApiParam(value = "股票代码")
                                                          String code) {
        if (StringUtils.hasLength(code)) {
            positionService.computeCodePosition(code, start);
        } else {
            positionService.computeDayPosition(start);
        }
        return new CommonResponse();
    }

    /**
     * 计算区间内的所有交易日的仓位
     *
     * @return
     */
    @ApiOperation(value = "计算所有股票所有交易日的仓位")
    @GetMapping("/computeAll")
    public CommonResponse computeStockPositionDay() {
        positionService.computeAllDayPosition();
        return new CommonResponse();
    }
}
