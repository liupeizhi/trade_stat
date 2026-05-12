package com.doorway.tradememo.cache;

import com.doorway.tradememo.domain.StockInfo;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

/**
 * Note
 * Author:liupz
 * Date:2022/4/19
 */
public class LocalCache {

    public static final Cache<String, StockInfo> STOCK_INFO_CACHE = CacheBuilder
            .newBuilder().maximumSize(1000000)
            .expireAfterWrite(2, TimeUnit.DAYS) // 根据写入时间过期
            .build();

    public static final Cache<String, StockInfo> STOCK_KEYWORD_CACHE = CacheBuilder
            .newBuilder().maximumSize(1000000)
            .expireAfterWrite(2, TimeUnit.DAYS) // 根据写入时间过期
            .build();

}
