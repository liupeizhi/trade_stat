package com.doorway.tradememo.service;

import com.doorway.tradememo.domain.DividendInfo;
import com.doorway.tradememo.domain.StockQuotation;

import java.util.List;
import java.util.Map;

/**
 * 股票行情
 * Author:liupz
 * Date:2022/4/3
 */
public interface IStockQuotationService {

    /**
     * 获取日K数据
     * @param code
     * @param start
     * @param end
     * @param fqt 1:前复权，0：不复权，2：后复权
     * @return
     */
    List<StockQuotation> getStockDayKlineByRange(String code, String start, String end,Integer fqt);

    /**
     * 获取某只股票的某天的行情
     * @param code
     * @param day
     * @return
     */
    StockQuotation getStockQuotationAtDay(String code, String day);


    /**
     * 获取某只股票的历史增发配股分红信息
     * @param code
     * @return
     */
    Map<String, DividendInfo> getDividendInfo(String code);

    List<String> getTradeDays();

    /**
     * 获取股票最新价格
     * @param code
     * @return
     */
     Double getLatestPrice(String code);

    /**
     * 同步股票行情
     * @param code
     * @param start
     * @param end
     * @return
     */
    List<StockQuotation> syncQuotations(String code, String start, String end,Integer fqt);





}
