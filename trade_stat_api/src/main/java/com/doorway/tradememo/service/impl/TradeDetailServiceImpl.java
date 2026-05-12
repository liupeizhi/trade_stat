package com.doorway.tradememo.service.impl;

import com.doorway.tradememo.domain.StockHistoryPosition;
import com.doorway.tradememo.domain.StockPosition;
import com.doorway.tradememo.domain.StockPositionDay;
import com.doorway.tradememo.domain.TradeDetail;
import com.doorway.tradememo.event.SettleEventSource;
import com.doorway.tradememo.event.TradeEventSource;
import com.doorway.tradememo.exception.ErrorCodeEnum;
import com.doorway.tradememo.exception.ServiceException;
import com.doorway.tradememo.mapper.StockHistoryPositionMapper;
import com.doorway.tradememo.mapper.StockPositionDayMapper;
import com.doorway.tradememo.mapper.StockPositionMapper;
import com.doorway.tradememo.mapper.TradeDetailMapper;
import com.doorway.tradememo.resp.PageResponse;
import com.doorway.tradememo.service.IPositionService;
import com.doorway.tradememo.service.IStockQuotationService;
import com.doorway.tradememo.service.ITradeDetailService;
import com.doorway.tradememo.utils.CostResp;
import com.doorway.tradememo.utils.DateUtils;
import com.doorway.tradememo.vo.DashBoard;
import com.doorway.tradememo.vo.TradePoint;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.*;

import static com.doorway.tradememo.utils.DateUtils.*;

/**
 * Note
 * Author:liupz
 * Date:2022/3/26
 */
@Service
@Slf4j
public class TradeDetailServiceImpl implements ITradeDetailService {
    @Autowired
    TradeEventSource tradeEventSource;

    @Autowired
    SettleEventSource settleEventSource;
    @Autowired
    private IPositionService positionService;

    @Autowired
    private TradeDetailMapper tradeDetailMapper;
    @Autowired
    private StockHistoryPositionMapper stockHistoryPositionMapper;
    @Autowired
    private StockPositionMapper stockPositionMapper;

    @Autowired
    private StockPositionDayMapper stockPositionDayMapper;
    @Autowired
    private TradeListener tradeListener;
    @Autowired
    private SettleEventListener settleEventListener;

    @Autowired
    private IStockQuotationService quotationService;

    @PostConstruct
    public void postConstruct() {
//        tradeEventSource.addEventListener(tradeListener);
//        settleEventSource.addEventListener(settleEventListener);
    }

    @Autowired
    public List<String> getAllCodes() {
        return tradeDetailMapper.getAllCodes();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<TradeDetail> saveBatch(List<TradeDetail> tradeDetails) {

        Map<String, TradeDetail> tradeDetailMap = new HashMap<>();
        if (CollectionUtils.isEmpty(tradeDetails)) {
            throw new ServiceException(ErrorCodeEnum.INVALID_PARAM);
        }
        //本地去重
        for (TradeDetail tradeDetail : tradeDetails) {

            if (tradeDetail.getOpt() == null
                    || tradeDetail.getTradeTime() == null
                    || !StringUtils.hasLength(tradeDetail.getCode())
                    || tradeDetail.getVol() == null
                    || tradeDetail.getPrice() == null) {
                throw new ServiceException(ErrorCodeEnum.INVALID_PARAM);
            }


            String key = tradeDetail.getCode() + tradeDetail.getTradeTime() + tradeDetail.getOpt();
            if (tradeDetailMap.containsKey(key)) {
                log.info("请求参数中出现重复交易记录：" + key);
            }
            tradeDetailMap.put(key, tradeDetail);
        }
        List<TradeDetail> exists = tradeDetailMapper.findByKeys(tradeDetailMap.keySet());

        if (!CollectionUtils.isEmpty(exists)) {
            log.info("数据库中存在重复交易记录：" + exists.size());
            exists.forEach(e -> {
                tradeDetailMap.remove(e.getRowKey());
            });
        }

        log.info("此次请求添加：" + tradeDetails.size() + ",实际添加：" + tradeDetailMap.size());
        Set<String> codes = new HashSet<>();
        tradeDetailMap.values().forEach(td -> {
            String key = td.getCode() + td.getTradeTime() + td.getOpt();
            td.setRowKey(key);
            td.setCreatedTime(new Date());
            td.setDay(DateUtils.parseDateToStr(td.getTradeTime(), DATE_FORMAT_YY_MM_DD));
            td.setMonth(DateUtils.parseDateToStr(td.getTradeTime(), DATE_FORMAT_YYYY_MM));
            td.setYear(DateUtils.parseDateToStr(td.getTradeTime(), DATE_FORMAT_YYYY));
            codes.add(td.getCode());

        });

        if (!CollectionUtils.isEmpty(tradeDetailMap)) {
            tradeDetailMapper.saveBatch(new ArrayList<>(tradeDetailMap.values()));
        }


        return new ArrayList<>();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int save(TradeDetail t) {

        if (t.getOpt() == null
                || t.getTradeTime() == null
                || !StringUtils.hasLength(t.getCode())
                || t.getVol() == null
                || t.getPrice() == null) {
            throw new ServiceException(ErrorCodeEnum.INVALID_PARAM);
        }

        // 卖出时校验持仓数量
        if (t.getOpt() != null && !t.getOpt()) {
            StockPosition stockPosition = stockPositionMapper.getByCode(t.getCode());
            if (stockPosition == null || stockPosition.getVol() < t.getVol()) {
                throw new ServiceException(ErrorCodeEnum.INVALID_PARAM.getCode(), "卖出数量超过当前持仓数量");
            }
        }

        Date time = t.getTradeTime();
        String id = UUID.randomUUID().toString().replaceAll("-", "");

        String key = t.getCode() + t.getTradeTime() + t.getOpt();
        TradeDetail existing = tradeDetailMapper.findByKey(key);

        if (existing != null) {
            throw new ServiceException(ErrorCodeEnum.DUPLICATE_RECORD);
        }

        t.setId(id);
        t.setCreatedTime(new Date());
        t.setDay(DateUtils.parseDateToStr(time, DATE_FORMAT_YY_MM_DD));
        t.setMonth(DateUtils.parseDateToStr(time, DATE_FORMAT_YYYY_MM));
        t.setYear(DateUtils.parseDateToStr(time, DATE_FORMAT_YYYY));
        t.setRowKey(key);
        // 如果transFee为null，设置为0
        if (t.getTransFee() == null) {
            t.setTransFee(new BigDecimal(0));
        }

        int res = tradeDetailMapper.save(t);


//        //组装事件
//
//        TradeDetail tradeDetail = tradeDetailMapper.getById(id);
//        TradeEvent<TradeDetail> tradeDetailTradeEvent = new TradeEvent<>();
//
//        tradeDetailTradeEvent.setData(tradeDetail);
//
//        if (tradeDetail.getOpt()) {
//            //买入操作
//            StockPosition code = stockPositionMapper.getByCode(tradeDetail.getCode());
//            if (code == null) {
//                //目前持仓不包含该股票，开仓
//                tradeDetailTradeEvent.setType(TradeEventEnum.OPEN);
//            } else {
//                //目前持仓包含该股票，加仓
//                tradeDetailTradeEvent.setType(TradeEventEnum.ADD);
//            }
//
//        } else {
//            //卖出操作
//
//
//            List<TradeDetail> tds = tradeDetailMapper.getTradesBeforeDayAndCode(tradeDetail.getTradeTime(), tradeDetail.getCode());
//            int vol = computeCount(tds);
//            if (vol == 0) {
//                //数量为零，清仓操作
//                tradeDetailTradeEvent.setType(TradeEventEnum.CLOSE);
//            }
//            if (vol > 0) {
//                //数量不为零，减仓操作
//                tradeDetailTradeEvent.setType(TradeEventEnum.REDUCE);
//            }
//            if (vol < 0) {
//                //数量小于零，数据异常
//                throw new ServiceException(ErrorCodeEnum.ERROR_RECORDS);
//            }
//
//        }
//
//        //发送事件
//        tradeEventSource.triggerEvent(tradeDetailTradeEvent);


        return res;

    }

    private int computeCount(List<TradeDetail> tds) {

        //计算该交易时间之前的该股票的最终数量
        int vol = 0;
        for (TradeDetail td : tds) {
            if (td.getOpt()) {
                vol += td.getVol();
            } else {
                vol -= td.getVol();
            }
        }
        return vol;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int mod(TradeDetail tradeDetail) {

        TradeDetail td = checkPosition(tradeDetail.getId());

        tradeDetail.setTradeTime(null);
        tradeDetail.setCode(null);
        tradeDetail.setTerm(null);
        tradeDetail.setDay(null);
        tradeDetail.setMonth(null);
        tradeDetail.setYear(null);
        tradeDetail.setUpdatedTime(new Date());

        int r = tradeDetailMapper.modifySelective(tradeDetail);

        positionService.computeCodePosition(td.getCode(),null);
        return r;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delById(String id) {
        TradeDetail tradeDetail = checkPosition(id);

        int r = tradeDetailMapper.delById(id);

        positionService.computeCodePosition(tradeDetail.getCode(),null);
        return r;
    }

    private TradeDetail checkPosition(String id) {

        TradeDetail tradeDetail = tradeDetailMapper.getById(id);
        if (tradeDetail == null) {
            throw new IllegalArgumentException("该交易不存在");
        }
        List<TradeDetail> tds = tradeDetailMapper.getByCode(tradeDetail.getCode());

        if (computeCount(tds) < 0) {
            //数量小于零，数据异常
            throw new ServiceException(ErrorCodeEnum.ERROR_RECORDS);
        }

        return tradeDetail;

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delByIds(List<String> ids) {
        List<TradeDetail> g = tradeDetailMapper.getByIds(ids);
        List<String> codes = new ArrayList<>();
        int r = 0;
        if (g != null && g.size() == ids.size()) {

            for(TradeDetail td:g){
                codes.add(td.getCode());
            }

            r = tradeDetailMapper.delByIds(ids);
            g.forEach(t -> positionService.computeCodePosition(t.getCode(),null));
        } else {
            throw new ServiceException(ErrorCodeEnum.NOT_FOUND);
        }

        List<TradeDetail> errors = positionService.computeAllDayPositions(codes);

        if (!CollectionUtils.isEmpty(errors)) {
            throw new ServiceException(ErrorCodeEnum.ERROR_RECORDS);
        }


        return r;
    }


    private void updateRecords(CostResp costResp) {
        List<TradeDetail> tradeDetails = costResp.getUpdates();
        List<StockPosition> stockPositions = costResp.getCurrentStockPositions();

        List<StockHistoryPosition> stockHistoryPositions = costResp.getStockHistoryPositions();

        Map<String, List<StockPositionDay>> stockPositionDayMap = costResp.getStockPositionDayMap();

        for (TradeDetail td : tradeDetails) {
            tradeDetailMapper.modifySelective(td);
        }
        if (!CollectionUtils.isEmpty(stockPositions)) {
            stockPositionMapper.saveBatch(stockPositions);
        }
        if (!CollectionUtils.isEmpty(stockHistoryPositions)) {
            stockHistoryPositionMapper.saveBatch(stockHistoryPositions);
        }
        if(!CollectionUtils.isEmpty(stockPositionDayMap)){
            List<StockPositionDay>  all = new ArrayList<>();
            for(String day:stockPositionDayMap.keySet()){
                all.addAll(stockPositionDayMap.get(day));
            }
            stockPositionDayMapper.saveBatch(new ArrayList<>(all));
        }
    }



    public double currentProfit(List<StockPosition> stockPositions) {
        double profit = 0;
        for (StockPosition sp : stockPositions) {
            profit += (sp.getPrice().doubleValue() - sp.getCost().doubleValue()) * sp.getVol();
        }
        return profit;
    }

    public double marketValue(List<StockPosition> stockPositions) {

        double profit = 0;
        for (StockPosition sp : stockPositions) {
            profit += sp.getPrice().doubleValue() * sp.getVol();
        }
        return profit;
    }

    public double historyProfit(List<StockHistoryPosition> stockProfits) {

        double profit = 0;
        for (StockHistoryPosition sp : stockProfits) {
            profit += sp.getProfit().doubleValue();
        }
        return profit;
    }


    @Override
    public List<TradeDetail> getAllTradesByCode(String code) {

        return tradeDetailMapper.getByCode(code);
    }

    @Override
    public PageResponse<TradeDetail> queryTrades(String code, Date startTime, Date endTime, Integer opt, Integer clear, Integer pageSize,
                                                 Integer pageNo, Map<String, String> sortMap) {
        PageHelper.startPage(pageNo, pageSize);

        List<TradeDetail> tradeDetails = tradeDetailMapper.queryAndOrder(code, startTime, endTime, opt, clear, sortMap);

        Page<TradeDetail> page = (Page<TradeDetail>) tradeDetails;

        return new PageResponse<>(tradeDetails, page.getPageNum(), page.getTotal());
    }

    @Override
    public List<TradePoint> getTradePointByTerm(String code, String term) {

        TradeDetail qo = new TradeDetail();
        qo.setCode(code);
        if(StringUtils.hasLength(term)) {
            qo.setTerm(term);
        }
        List<TradeDetail> tradeDetails = tradeDetailMapper.getByPojo(qo);
        List<TradePoint> tradePoints = new ArrayList<>();
        Map<String, List<TradeDetail>> tradeDetailMap = new TreeMap<>();

        for (TradeDetail td : tradeDetails) {
            if (tradeDetailMap.containsKey(td.getDay())) {
                tradeDetailMap.get(td.getDay()).add(td);
            } else {
                List<TradeDetail> tradeDetails1 = new ArrayList<>();
                tradeDetails1.add(td);
                tradeDetailMap.put(td.getDay(), tradeDetails1);
            }

        }

        StockHistoryPosition stockHistoryPosition =  stockHistoryPositionMapper.getByTerm(code,term);

        List<StockPositionDay> stockPositionDays = stockPositionDayMapper.getByDayAndCode(DateUtils.parseDateToStr(stockHistoryPosition.getCloseTime(), DATE_FORMAT_YYYY_MM_DD), code);

        Map<String, Integer> positionDays = new HashMap<>();

        List<String> days = DateUtils.getDays(DateUtils.parseDateToStr(stockHistoryPosition.getOpenTime(), DATE_FORMAT_YYYY_MM_DD), DateUtils.parseDateToStr(stockHistoryPosition.getCloseTime(), DATE_FORMAT_YYYY_MM_DD));

        for (String day : days) {
            positionDays.put(day, getPositionByDay(stockPositionDays, day));
        }

        tradeDetailMap.forEach((k, v) -> {

            TradePoint tradePoint = new TradePoint();
            tradePoint.setDay(k);
            tradePoint.setPrice(v.get(0).getPrice().doubleValue());


            if (positionDays.get(k) == 0) {
                tradePoint.setName("C");
            } else {
                if (v.size() > 1) {
                    Boolean opt = null;
                    for (TradeDetail t : v) {
                        if (opt == null) {
                            opt = t.getOpt();
                        }
                        if (!t.getOpt().equals(opt)) {
                            tradePoint.setName("T");
                            break;
                        }
                    }
                    if (!StringUtils.hasLength(tradePoint.getName())) {
                        if (opt) {
                            tradePoint.setName("B");
                        } else {
                            tradePoint.setName("S");
                        }
                    }

                } else {
                    if (v.get(0).getOpt()) {
                        tradePoint.setName("B");
                    } else {
                        tradePoint.setName("S");
                    }
                }
            }
            tradePoint.setTradeDetails(v);
            tradePoint.setVol(positionDays.get(k));
            tradePoints.add(tradePoint);
        });

        return tradePoints;
    }

    private Integer getPositionByDay(List<StockPositionDay> stockPositionDays, String day) {

        for (int i = 0; i < stockPositionDays.size(); i++) {
            StockPositionDay stockPositionDay = stockPositionDays.get(i);
            if (day.equals(stockPositionDay.getDay())) {
                return stockPositionDay.getVol();
            }
        }
        return 0;
    }

}
