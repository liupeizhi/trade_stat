package com.doorway.tradememo.service;

import com.doorway.tradememo.domain.StockInfo;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 股票基本信息
 * Author:liupz
 * Date:2022/4/3
 */
public interface IStockInfoService {


    /**
     * 获取所有股票信息
     * @return
     */
    List<StockInfo> getStockInfos();

    /**
     * 历史清仓股票
     * @return
     */
    List<StockInfo> historyStocks();

    /**
     * 批量获取股票信息
     * @param codes
     * @return
     */
    List<StockInfo> getStockInfos(List<String> codes);

    /**
     * 模糊查询股票信息
     * @param codeName
     * @return
     */
    List<StockInfo> suggestStock(String codeName);

    /**
     * 获取某天新发行的股票
     * @param day
     * @return
     * @throws IOException
     */
    List<String> getNewStocks(String day) throws IOException;

    /**
     * 获取股票信息
     * @param code
     * @return
     */
    StockInfo getStockInfo(String code);

    /**
     * 同步股票信息
     */
    void sync();

    /**
     * 同步股票信息
     */
    void sync(String code);

    /**
     * 同步当天新发行股票
     * @throws IOException
     */
    void syncNewStocks() throws IOException;

    /**
     * 获取股票信息js文件
     * @return
     */
    String getStockInfoJS();





}
