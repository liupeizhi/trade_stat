package com.doorway.tradememo.service.impl;

/**
 * Note
 * Author:liupz
 * Date:2022/4/3
 */

import com.alibaba.fastjson.JSONObject;
import com.doorway.tradememo.cache.LocalCache;
import com.doorway.tradememo.domain.StockInfo;
import com.doorway.tradememo.mapper.StockHistoryPositionMapper;
import com.doorway.tradememo.mapper.StockInfoMapper;
import com.doorway.tradememo.service.IStockInfoService;
import com.doorway.tradememo.utils.DateUtils;
import com.doorway.tradememo.utils.OkHttp3Sample;
import com.doorway.tradememo.utils.UnicodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("stockInfo")
@Slf4j
public class StockInfoServiceImpl implements IStockInfoService {
    @Autowired
    private StockInfoMapper stockInfoMapper;


    @Autowired
    private StockHistoryPositionMapper stockHistoryPositionMapper;

    @Override
    public List<StockInfo> getStockInfos() {
        if (LocalCache.STOCK_INFO_CACHE.size() == 0) {
            List<StockInfo> stockInfos = stockInfoMapper.getStocks();
            stockInfos.forEach(s -> LocalCache.STOCK_INFO_CACHE.put(s.getCode(), s));
        } else {
            return new ArrayList<>(LocalCache.STOCK_INFO_CACHE.asMap().values());
        }
        return new ArrayList<>(LocalCache.STOCK_INFO_CACHE.asMap().values());

    }

    @Override
    public List<StockInfo> historyStocks() {
        if(stockHistoryPositionMapper.getCodes()!=null&&stockHistoryPositionMapper.getCodes().size()>0) {
            return stockInfoMapper.getByCodes(stockHistoryPositionMapper.getCodes());
        }else{
            return new ArrayList<>();
        }
    }

    @Override
    public List<StockInfo> getStockInfos(List<String> codes) {

        return stockInfoMapper.getByIds(codes);
    }

    @Override
    public List<StockInfo> suggestStock(String codeName) {

        if (LocalCache.STOCK_KEYWORD_CACHE.size() == 0) {
            List<StockInfo> stockInfos = stockInfoMapper.getAll();
            stockInfos.forEach(s -> {
                LocalCache.STOCK_KEYWORD_CACHE.put(s.getCode() + s.getName() + s.getSpelling(), s);
            });
        }
        List<StockInfo> stockInfos = new ArrayList<>();
        int i = 0;
        for (StockInfo stockInfo : LocalCache.STOCK_KEYWORD_CACHE.asMap().values()) {
            if (stockInfo.getSpelling().contains(codeName)
                    || stockInfo.getName().contains(codeName)
                    || stockInfo.getCode().contains(codeName)) {
                stockInfos.add(stockInfo);
                i++;
                if (i > 10) {
                    break;
                }
            }
        }

        return stockInfos;
    }

    @Override
    public List<String> getNewStocks(String day) throws IOException {
        Document document = Jsoup.connect("https://stock.sohu.com/upload/ipo/new_stock_calendar.shtml").get();
        Elements codes = document.select("tr td:nth-child(2)");
        Elements days = document.select("tr td:nth-child(3)");
        List<String> newStocks = new ArrayList<>();
        for (int i = 1; i < codes.size(); i++) {
            String d = days.get(i).text();
            String code = codes.get(i).text();
            if (d.equals(day)) {
                newStocks.add(code);
            }
        }
        return newStocks;
    }

    @Override
    public StockInfo getStockInfo(String code) {
        return stockInfoMapper.getByCode(code);
    }

    @Override
    public void sync() {
        for (int i = 0; i < 999999; i++) {
            String code = new DecimalFormat("000000").format(i);
            //005023
            List<StockInfo> stockInfos = getStockInfosRemote(code);

            if (!CollectionUtils.isEmpty(stockInfos)) {
                stockInfoMapper.saveBatch(stockInfos);
            } else {
                log.info("没有找到行情：" + code);
            }
        }


    }

    @Override
    public void sync(String code) {
        List<StockInfo> stockInfos = getStockInfosRemote(code);
        if (!CollectionUtils.isEmpty(stockInfos)) {
            stockInfoMapper.saveBatch(stockInfos);
        } else {
            log.info("没有找到行情：" + code);
        }
    }

    private List<StockInfo> getStockInfosRemote(String code) {
        String response = UnicodeUtils.unicodeStr2String(OkHttp3Sample.HTTP_CLIENT.get("https://smartbox.gtimg.cn/s3/?q=" + code + "&t=all"));
        String stock = response.substring("v_hint=\"".length(), response.length() - 1);
        log.info(stock);
        String[] stocks = stock.split("\\^");
        List<StockInfo> stockInfos = new ArrayList<>();
        for (String s : stocks) {
            String[] fields = s.split("~");
            if (fields.length != 5) {
                continue;
            }
            StockInfo stockInfo = new StockInfo();
            stockInfo.setMarket(fields[0]);
            stockInfo.setCode(fields[1]);
            stockInfo.setName(fields[2]);
            stockInfo.setSpelling(fields[3]);
            stockInfo.setType(fields[4]);
            List<StockInfo> existing = stockInfoMapper.getByPojo(stockInfo);
            if (!CollectionUtils.isEmpty(existing)) {
                log.info("存在相同记录：" + JSONObject.toJSONString(stockInfo));
            } else {
                stockInfos.add(stockInfo);
            }
        }
        return stockInfos;
    }

    @Override
    public void syncNewStocks() throws IOException {
        List<String> stocks = getNewStocks(DateUtils.parseDateToStr(new Date(), DateUtils.DATE_FORMAT_YYYY_MM_DD));
        if (!CollectionUtils.isEmpty(stocks)) {
            for (String code : stocks) {
                List<StockInfo> stockInfos = getStockInfosRemote(code);
                if (!CollectionUtils.isEmpty(stockInfos)) {
                    stockInfoMapper.saveBatch(stockInfos);
                } else {
                    log.info("没有找到行情：" + code);
                }
            }

        }
    }

    @Override
    public String getStockInfoJS() {
        List<StockInfo> stockInfos = stockInfoMapper.getAll();
        StringBuilder stockJs = new StringBuilder();
        stockJs.append("var stockInfos=\"");
        for (StockInfo stockInfo : stockInfos) {
            stockJs.append("$" + stockInfo.getCode());
            stockJs.append("," + stockInfo.getName());
            stockJs.append("," + stockInfo.getSpelling());
            stockJs.append("," + stockInfo.getMarket());
            stockJs.append("," + stockInfo.getType());
        }
        stockJs.append("\"");
        return stockJs.toString();
    }

    public static void main(String[] args) {


        log.info(UnicodeUtils.unicodeStr2String(OkHttp3Sample.HTTP_CLIENT.get("https://smartbox.gtimg.cn/s3/?q=513530&t=all")));
        ;
    }

}
