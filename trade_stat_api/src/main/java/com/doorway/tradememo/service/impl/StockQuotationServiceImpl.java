package com.doorway.tradememo.service.impl;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.doorway.tradememo.domain.DividendInfo;
import com.doorway.tradememo.domain.StockInfo;
import com.doorway.tradememo.domain.StockQuotation;
import com.doorway.tradememo.mapper.StockConditionMapper;
import com.doorway.tradememo.service.IStockInfoService;
import com.doorway.tradememo.service.IStockQuotationService;
import com.doorway.tradememo.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.DateUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.*;

import static com.doorway.tradememo.utils.DateUtils.DATE_FORMAT_YYYY_MM_DD;

/**
 * Note
 * Author:liupz
 * Date:2022/4/3
 */
@Service("StockConditionService")
@Slf4j
public class StockQuotationServiceImpl implements IStockQuotationService {


    @Autowired
    private StockConditionMapper stockConditionMapper;

    @Autowired
    private IStockInfoService stockInfoService;

    private static Map<String,Map<String, DividendInfo>> DVID_MAP = new HashMap<>();
    private static Map<String,StockQuotation> QUOTA_MAP = new HashMap<>();
    private static Map<String,Double> PRICE_MAP = new HashMap<>();
    @Override
    public List<StockQuotation> getStockDayKlineByRange(String code, String start, String end,Integer fqt) {

//        List<StockQuotation> stockQuotations = stockConditionMapper.getByStartEnd(code,start,end);
//        if(!CollectionUtils.isEmpty(stockQuotations)){
//            return stockQuotations;
//        }else{
            return syncQuotations(code,start,end,fqt);
//        }
    }

    @Override
    public StockQuotation getStockQuotationAtDay(String code, String day) {
        if(QUOTA_MAP.containsKey(code+day)){
            return QUOTA_MAP.get(code+day);
        }else {
            StockQuotation stockQuotation = new StockQuotation();
            stockQuotation.setCode(code);
            stockQuotation.setDay(day);
            List<StockQuotation> conditions = stockConditionMapper.getByPojo(stockQuotation);
            if (!CollectionUtils.isEmpty(conditions)) {
                return conditions.get(0);
            } else {
                log.info("没有找到行情信息,远程爬取行情：" + code + ",day=" + day);
                List<StockQuotation> stockQuotations = syncQuotations(code, day, day, 0);
                if (!CollectionUtils.isEmpty(stockQuotations)) {
                    conditions = stockConditionMapper.getByPojo(stockQuotation);
                    if (!CollectionUtils.isEmpty(conditions)) {
                        log.info("远程爬取行情成功：" + code + ",day=" + day);
                        QUOTA_MAP.put(code+day,conditions.get(0));
                        return conditions.get(0);
                    }
                    QUOTA_MAP.put(code+day,stockQuotations.get(0));
                    log.info("远程爬取行情成功：" + code + ",day=" + day);
                    return stockQuotations.get(0);
                } else {
                    log.info("远程爬取行情失败：" + code + ",day=" + day);
                    return null;
                }

            }
        }
    }

    @Override
    public Map<String, DividendInfo> getDividendInfo(String code) {
        if(DVID_MAP.containsKey(code)){
            return DVID_MAP.get(code);
        }else {
            Map<String, DividendInfo> result = new HashMap<>();
            StockInfo stockInfo = stockInfoService.getStockInfo(code);
            if (stockInfo == null) {
                return new HashMap<>();
            }

            code = stockInfo.getMarket().equals("sz") ? "SZ" + code : "SH" + code;

            String raw = "http://push2.eastmoney.com/api/qt/stock/cqcx/get?id=" + code + "&ut=e1e6871893c6386c5ff6967026016627";


            String urlPath = raw;

            log.info("远程获取除权数据：" + urlPath);

            String conditionJson = HttpUtil.get(urlPath);
            JSONObject object = JSONObject.parseObject(conditionJson);
            if (object.containsKey("data") && object.getJSONObject("data") != null && object.getJSONObject("data").containsKey("records")) {
                JSONArray records = object.getJSONObject("data").getJSONArray("records");
                for (Object record : records) {
                    DividendInfo dividendInfo = new DividendInfo();
                    BeanUtils.copyProperties(record, dividendInfo);
                    JSONObject r = (JSONObject) record;
                    dividendInfo.setDate(r.getString("date"));
                    dividendInfo.setCxbl(r.getDoubleValue("cxbl"));
                    dividendInfo.setGgflag(r.getInteger("ggflag"));
                    dividendInfo.setPgbl(r.getDoubleValue("pgbl"));
                    dividendInfo.setPghg(r.getDoubleValue("pghg"));
                    dividendInfo.setPgjg(r.getDoubleValue("pgjg"));
                    dividendInfo.setPxbl(r.getDoubleValue("pxbl"));
                    dividendInfo.setSgbl(r.getDoubleValue("sgbl"));
                    dividendInfo.setType(r.getInteger("type"));
                    dividendInfo.setZfbl(r.getDoubleValue("zfbl"));
                    dividendInfo.setZfgs(r.getDoubleValue("zfgs"));
                    dividendInfo.setZfjg(r.getDoubleValue("zfjg"));
                    dividendInfo.setZzbl(r.getDoubleValue("zzbl"));
                    result.put(dividendInfo.getDate(), dividendInfo);
                }
            }
            DVID_MAP.put(code,result);
            return result;
        }

    }

    @Override
    public List<String> getTradeDays() {
        List<String> days = new ArrayList<>();
        String raw = "http://push2his.eastmoney.com/api/qt/stock/kline/get?fields1=f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f11,f12,f13&fields2=f51,f52,f53,f54,f55,f56,f57,f58,f59,f60,f61&beg=0&end="+DateUtils.parseDateToStr(new Date(), DateUtils.DATE_FORMAT_YYYYMMDD)+"&ut=fa5fd1943c7b386f172d6893dbfba10b&rtntype=6&secid=1.000001&klt=101&fqt=1&cb=jsonp1655635169785";
        String conditionJson = HttpUtil.get(raw);
        conditionJson = conditionJson.substring("jsonp1655635169785".length()+1,conditionJson.length()-2);
        JSONObject object = JSONObject.parseObject(conditionJson);


        if (object.containsKey("data") && object.getJSONObject("data")!=null&&object.getJSONObject("data").containsKey("klines")) {
            JSONArray klines = object.getJSONObject("data").getJSONArray("klines");
            for (Object k : klines) {
                String record = (String) k;
                String[] records = record.split(",");
                days.add(records[0]);
            }
        }

        return days;
    }

    @Override
    public Double getLatestPrice(String code){
        if(PRICE_MAP.containsKey(code)){
            return PRICE_MAP.get(code);
        }else {
            StockInfo stockInfo = stockInfoService.getStockInfo(code);
            if (stockInfo == null) {
                return 0.0;
            }

            code = stockInfo.getMarket().equals("sz") ? "0." + code : "1." + code;

            String rurl = "http://push2his.eastmoney.com/api/qt/stock/trends2/get?fields1=f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f11,f12,f13&ut=fa5fd1943c7b386f172d6893dbfba10b&secid=" + code + "&fields2=f51,f52,f53,f54,f55,f56,f57,f58&ndays=1&iscr=0&iscca=0";
            String conditionJson = HttpUtil.get(rurl);
//        log.info(code+","+conditionJson);
            JSONObject object = JSONObject.parseObject(conditionJson);
            if (object != null && object.containsKey("data") && object.getJSONObject("data") != null && object.getJSONObject("data").containsKey("trends")) {
                JSONArray jsonArray = object.getJSONObject("data").getJSONArray("trends");
                if (jsonArray == null || jsonArray.isEmpty()) {
                    log.warn("获取最新价格失败，trends为空：code={}", code);
                    return 0.0;
                }
                String latest = (String) jsonArray.get(jsonArray.size() - 1);
                PRICE_MAP.put(code,Double.parseDouble(latest.split(",")[2]));
                return Double.parseDouble(latest.split(",")[2]);
            }

            return 0.0;
        }
    }


    @Override
    public List<StockQuotation> syncQuotations(String code, String start, String end,Integer fqt) {

        List<StockQuotation> stockQuotations = new ArrayList<>();


        String raw = "http://push2his.eastmoney.com/api/qt/stock/kline/get?fields1=f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f11,f12,f13&fields2=f51,f52,f53,f54,f55,f56,f57,f58,f59,f60,f61&beg={0}&end={1}&ut=fa5fd1943c7b386f172d6893dbfba10b&rtntype=6&secid={2}&klt=101&fqt="+fqt+"&cb=jsonp1655635169785";

        StockInfo stockInfo = stockInfoService.getStockInfo(code);
        if(stockInfo==null){
            return stockQuotations;
        }else{
            code = stockInfo.getMarket().equals("sz") ? "0." + code : "1." + code;
        }

        start = DateUtils.parseDateToStr(DateUtils.parseStrToDate(start, DATE_FORMAT_YYYY_MM_DD), DateUtils.DATE_FORMAT_YYYYMMDD);

        end = DateUtils.parseDateToStr(DateUtils.parseStrToDate(end, DATE_FORMAT_YYYY_MM_DD), DateUtils.DATE_FORMAT_YYYYMMDD);

        Object[] arr = { start,end,code};
        String urlPath = MessageFormat.format(raw, arr);

        log.info("远程获取K线数据："+urlPath);

        String conditionJson = HttpUtil.get(urlPath);
        conditionJson = conditionJson.substring("jsonp1655635169785".length()+1,conditionJson.length()-2);
        JSONObject object = JSONObject.parseObject(conditionJson);


        if (object.containsKey("data") && object.getJSONObject("data")!=null&&object.getJSONObject("data").containsKey("klines")) {
            JSONArray klines = object.getJSONObject("data").getJSONArray("klines");
            if (klines.size() == 0) {
                log.info("没有找到行情信息：" + code + ",st=" + start + ",end=" + end);
                return stockQuotations;
            }

            // 读表头,日期,股票代码,名称,收盘价,最高价,最低价,开盘价,前收盘,涨跌额,涨跌幅,换手率,成交量,成交金额,
                for (Object k : klines) {
                    String record = (String) k;
                    String[] records = record.split(",");

                    // 读一整行

                    StockQuotation stockQuotation = new StockQuotation();
                    stockQuotation.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                    stockQuotation.setCode(code.substring(2));
                    stockQuotation.setClose(BigDecimal.valueOf(Double.parseDouble(records[2])));
                    stockQuotation.setHigh(BigDecimal.valueOf(Double.parseDouble(records[3])));
                    stockQuotation.setLow(BigDecimal.valueOf(Double.parseDouble(records[4])));
                    stockQuotation.setOpen(BigDecimal.valueOf(Double.parseDouble(records[1])));

                    stockQuotation.setOffsetValue(BigDecimal.valueOf(Double.parseDouble(records[9])));
                    stockQuotation.setOffsetRate(BigDecimal.valueOf(Double.parseDouble(records[8])));
                    stockQuotation.setTurnoverRate(BigDecimal.valueOf(Double.parseDouble(records[10])));
                    stockQuotation.setTurnover(BigDecimal.valueOf(Double.parseDouble(records[5]) * 100));
                    stockQuotation.setTurnoverValue(BigDecimal.valueOf(Double.parseDouble(records[6])));

                    stockQuotation.setCreatedTime(new Date());
                    stockQuotation.setDay(records[0]);



//                    StockQuotation req = new StockQuotation();
//                    req.setDay(stockQuotation.getDay());
//                    req.setCode(stockQuotation.getCode());
//                    List<StockQuotation> exists = stockConditionMapper.getByPojo(req);
//                    if(!CollectionUtils.isEmpty(exists)){
//                        log.info("存在相同的行情数据，不覆盖："+ stockQuotation.getCode()+","+ stockQuotation.getDay());
//                    }else{
                        stockQuotations.add(stockQuotation);
//                    }

                }

                if (!CollectionUtils.isEmpty(stockQuotations)) {
//                    stockConditionMapper.saveBatch(stockQuotations);
                } else {
                    log.info("没有找到行情信息：" + code + ",st=" + start + ",end=" + end);
                }

        }


        return stockQuotations;
    }





    public static void main(String[] args) {

//        String raw = "http://push2his.eastmoney.com/api/qt/stock/kline/get?fields1=f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f11,f12,f13&fields2=f51,f52,f53,f54,f55,f56,f57,f58,f59,f60,f61&beg=0&end=20500101&ut=fa5fd1943c7b386f172d6893dbfba10b&rtntype=6&secid=1.000001&klt=101&fqt=1&cb=jsonp1648970963130";
//        Object[] arr = {"0688118", "20220401", "20220402"};
//        String urlPath = MessageFormat.format(raw, arr);

        String code = "688118";
        String start = "2022-04-01";
        String end = "2022-04-02";
        String raw = "http://push2his.eastmoney.com/api/qt/stock/kline/get?fields1=f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f11,f12,f13&fields2=f51,f52,f53,f54,f55,f56,f57,f58,f59,f60,f61&beg={0}&end={1}&ut=fa5fd1943c7b386f172d6893dbfba10b&rtntype=6&secid={2}&klt=101&fqt=1&cb=jsonp1648970963130";
        code = code.startsWith("6") ? "1." + code : "0." + code;
        start = DateUtils.parseDateToStr(DateUtils.parseStrToDate(start, DATE_FORMAT_YYYY_MM_DD), DateUtils.DATE_FORMAT_YYYYMMDD);
        end = DateUtils.parseDateToStr(DateUtils.parseStrToDate(end, DATE_FORMAT_YYYY_MM_DD), DateUtils.DATE_FORMAT_YYYYMMDD);
        Object[] arr = {start, end, code};
        String urlPath = "http://push2his.eastmoney.com/api/qt/stock/trends2/get?fields1=f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f11,f12,f13&ut=fa5fd1943c7b386f172d6893dbfba10b&secid=1.600588&fields2=f51,f52,f53,f54,f55,f56,f57,f58&ndays=1&iscr=0&iscca=0";
        String conditionJson = HttpUtil.get(urlPath);


        log.info(conditionJson.substring("jsonp1648970963130".length() + 1, conditionJson.length() - 2));
    }
}
