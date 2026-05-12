package com.doorway.tradememo.service;

import com.doorway.tradememo.domain.TradeDetail;
import com.doorway.tradememo.resp.PageResponse;
import com.doorway.tradememo.vo.DashBoard;
import com.doorway.tradememo.vo.TradePoint;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 交易详情
 * Author:liupz
 * Date:2022/3/26
 */
public interface ITradeDetailService {

    /**
     * 批量保存交易记录
     * @param tradeDetails
     * @return
     */
    List<TradeDetail> saveBatch(List<TradeDetail> tradeDetails);

    List<String> getAllCodes();

    /**
     * 保存单条交易记录
     * @param tradeDetail
     * @return
     */
    int save(TradeDetail tradeDetail);

    /**
     * 修改单条交易记录
     * @param tradeDetail
     * @return
     */
    int mod(TradeDetail tradeDetail);

    /**
     * 删除交易记录
     * @param id
     * @return
     */
    int delById(String id);

    /**
     * 批量删除交易记录
     * @param id
     * @return
     */
    int delByIds(List<String> id);



    /**
     * 计算全部股票收益
     * @return
     */
    List<TradeDetail> getAllTradesByCode(String code);

    /**
     * 查询交易记录
     * @param code
     * @param startTime
     * @param endTime
     * @param opt
     * @param clear
     * @param pageSize
     * @param pageNo
     * @param sortMap
     * @return
     */
    PageResponse<TradeDetail> queryTrades(String code,
                                          Date startTime,
                                          Date endTime,
                                          Integer opt,
                                          Integer clear,
                                          Integer pageSize,
                                          Integer pageNo,
                                          Map<String, String> sortMap);

    /**
     * 计算区间内的股票交易点
     * @param code
     * @param start
     * @param end
     * @return
     */
    List<TradePoint> getTradePointByTerm(String code, String term);




}
