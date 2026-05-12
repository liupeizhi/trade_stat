package com.doorway.tradememo.service;

import com.doorway.tradememo.domain.StockPosition;
import com.doorway.tradememo.domain.StockPositionDay;
import com.doorway.tradememo.domain.TradeDetail;
import com.doorway.tradememo.req.StockHistoryPositionQO;
import com.doorway.tradememo.resp.PageResponse;
import com.doorway.tradememo.vo.TradeDetailVO;

import java.util.List;
import java.util.Map;

/**
 * 仓位
 *
 * Author:liupz
 * Date:2022/4/9
 */
public interface IPositionService {


    /**
     * 当前仓位
     * @return
     */
    List<StockPosition> getCurrentPositions();



    void computeAllDayPosition();

    List<TradeDetail> computeAllDayPositions(List<String> codes);

    void computeCodePosition(String code, String startDay);

    void computeDayPosition(String startDay);

    /**
     * 某日历史仓位
     * @param day yyyy-MM-dd
     * @return
     */
    List<StockPositionDay> historyDayPosition(String day);

    /**
     * 某证券历史持仓记录
     * @return
     */
    Map<String,StockPositionDay> historyCodePosition(String code);


    /**
     * 某证券某天的仓位
     * @param code
     * @param day
     * @return
     */
    StockPositionDay historyPositionByCode(String code,String day);


    /**
     * 历史清仓
     * 每次股数降为0代表一次清仓
     * 每次清仓以建仓日作为标记
     *
     * @param detail
     * @param pageNo
     * @param pageSize
     * @param sortField
     * @param sorOrder
     * @return
     */
    PageResponse<TradeDetailVO> getTermTrades(StockHistoryPositionQO detail, Integer pageNo, Integer pageSize, String sortField, String sorOrder);



}
