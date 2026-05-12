package com.doorway.tradememo.service.impl;

import com.doorway.tradememo.domain.StockPosition;
import com.doorway.tradememo.mapper.StockHistoryPositionMapper;
import com.doorway.tradememo.service.*;
import com.doorway.tradememo.vo.CapitalFlowVO;
import com.doorway.tradememo.vo.DashBoard;
import com.doorway.tradememo.vo.StockView;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Note
 * Author:liupz
 * Date:2022/8/14
 */
@Component
public class DashBoardServiceImpl implements IDashBoardService {
    @Autowired
    private ICapitalFlowService capitalFlowService;
    @Autowired
    private IProfitService profitService;
    @Autowired
    private ITradeTimesService tradeTimesService;
    @Autowired
    private IPositionService positionService;
    @Autowired
    private IHoldDaysService holdDaysService;

    @Autowired
    private StockHistoryPositionMapper historyPositionMapper;


    @Override
    public List<StockView> allView() {
        List<StockView> stockViews = new ArrayList<>();
        List<StockPosition> positions = positionService.getCurrentPositions();
        List<String> historyCodes = historyPositionMapper.getCodes();
        Set<String> allCodes = new HashSet<>(historyCodes);

        for(StockPosition stockPosition:positions){
            allCodes.add(stockPosition.getCode());
        }


        Map<String,CapitalFlowVO> capitalFlowVOMap = capitalFlowService.getDistStockFlow();
        Map<String,Long> times = tradeTimesService.getAllStockTradeTimes();
        Map<String, Integer> holdDays = holdDaysService.getAllStockHoldDays();
        Map<String,Double> profit = profitService.getStockProfits();
        for(String code:allCodes){
            StockView stockView = new StockView();
            stockView.setCode(code);
            if(capitalFlowVOMap.containsKey(code)) {
                stockView.setCapitalFlow(Double.parseDouble(capitalFlowVOMap.get(code).getFlow()));
            }
            if(times.containsKey(code)){
                stockView.setTradeTimes(times.get(code));
            }
            if(holdDays.containsKey(code)){
                stockView.setHoldDays(holdDays.get(code));
            }
            if(profit.containsKey(code)){
                stockView.setProfit(profit.get(code));
            }

            stockViews.add(stockView);
        }


        return stockViews;
    }

    @Override
    public DashBoard getDashBoard() {

        DashBoard dashBoard = new DashBoard();
        dashBoard.setTotalProfit(profitService.getBeforeTodayProfits().getProfit());

        dashBoard.setWeekProfit(profitService.getThisWeekProfit().getProfit());
        dashBoard.setMonthProfit(profitService.getThisMonthProfit().getProfit());

        List<StockPosition> positions = positionService.getCurrentPositions();
        double marketValue = 0;
        double currentProfit = 0;
        for (StockPosition p : positions) {
            marketValue += p.getVol() * p.getPrice().doubleValue();
            currentProfit += p.getVol() * (p.getPrice().doubleValue() - p.getCost().doubleValue());
        }
//        List<StockHistoryPosition> stockHistoryPositions = historyPositionMapper.getAll();
        double historyProfit = 0;
//        for(StockHistoryPosition position:stockHistoryPositions){
//            historyProfit+=position.getProfit().doubleValue();
//        }


        dashBoard.setMarketValue(marketValue);
        dashBoard.setCurrentProfit(currentProfit);
        dashBoard.setHistoryProfit(historyProfit);


        dashBoard.setTradeCount(tradeTimesService.getTotalTimes().intValue());
        dashBoard.setWeekTradeCount(tradeTimesService.getThisWeekTradeTimes().intValue());
        dashBoard.setMonthTradeCount(tradeTimesService.getThisMonthTradeTimes().intValue());
        if(StringUtils.hasLength(capitalFlowService.getTotalFlow().getFlow())) {
            dashBoard.setCapitalFlow(Double.parseDouble(capitalFlowService.getTotalFlow().getFlow()));
        }
        if(StringUtils.hasLength(capitalFlowService.getThisWeekFlow().getFlow())) {
            dashBoard.setWeekCapitalFlow(Double.parseDouble(capitalFlowService.getThisWeekFlow().getFlow()));
        }
        if(StringUtils.hasLength(capitalFlowService.getThisMonthFlow().getFlow())) {
            dashBoard.setMonthCapitalFlow(Double.parseDouble(capitalFlowService.getThisMonthFlow().getFlow()));
        }

        Map<String, CapitalFlowVO> capitalFlowVOMap = capitalFlowService.getDistStockFlow();
        Map<String, Double> capitalFlows = new HashMap<>();

        for (String code : capitalFlowVOMap.keySet()) {
            capitalFlows.put(code, Double.parseDouble(capitalFlowVOMap.get(code).getFlow()));
        }

        Map<String, Double> capitalFlowsMap = sortByValue(capitalFlows, true, 10);
        double top10Flow = 0;
        for (String code : capitalFlowsMap.keySet()) {
            top10Flow += Double.parseDouble(capitalFlowVOMap.get(code).getFlow());
        }
        capitalFlowsMap.put("其他", dashBoard.getCapitalFlow() - top10Flow);

        dashBoard.setCapitalFlows(capitalFlowsMap);
        Map<String, Double> profit = profitService.getStockProfits();

        Map<String, Double> profitMap = sortByValue(profit, true, 10);

        dashBoard.setProfitRanks(profitMap);


        Map<String, Long> times = tradeTimesService.getAllStockTradeTimes();
        Map<String, Long> timesMap = sortByValue(times, true, 10);
        long top10Times = 0;
        for (String code : timesMap.keySet()) {
            top10Times += timesMap.get(code);
        }
        timesMap.put("其他", dashBoard.getTradeCount() - top10Times);

        dashBoard.setTradeCounts(timesMap);


        Map<String, Integer> holdTimes = holdDaysService.getAllStockHoldDays();
        Map<String, Integer> holdTimesMap = sortByValue(holdTimes, true, 10);

        dashBoard.setHoldTimes(sortByValue(holdTimesMap,true));


        return dashBoard;
    }

    public static Map sortByValue(Map map, final boolean reverse) {
        List list = new LinkedList(map.entrySet());
        Collections.sort(list, new Comparator() {

            public int compare(Object o1, Object o2) {
                if (reverse) {
                    return -((Comparable) ((Map.Entry) (o1)).getValue())
                            .compareTo(((Map.Entry) (o2)).getValue());
                }
                return ((Comparable) ((Map.Entry) (o1)).getValue())
                        .compareTo(((Map.Entry) (o2)).getValue());
            }
        });

        Map result = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }



    /**
     * Sort map by value
     *
     * @param map    map source
     * @param isDesc 是否降序，true：降序，false：升序
     * @param limit  取前几条
     * @return 已排序map
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map, boolean isDesc, int limit) {
        Map<K, V> result = Maps.newLinkedHashMap();
        if (isDesc) {
            map.entrySet().stream().sorted(Map.Entry.<K, V>comparingByValue().reversed()).limit(limit)
                    .forEach(e -> result.put(e.getKey(), e.getValue()));
        } else {
            map.entrySet().stream().sorted(Map.Entry.<K, V>comparingByValue())
                    .forEachOrdered(e -> result.put(e.getKey(), e.getValue()));
        }
        return result;
    }

    /**
     * Sort map by key
     *
     * @param map    待排序的map
     * @param isDesc 是否降序，true：降序，false：升序
     * @param limit  取前几条
     * @return 已排序map
     */
    public static <K extends Comparable<? super K>, V> Map<K, V> sortByKey(Map<K, V> map, boolean isDesc, int limit) {
        Map<K, V> result = Maps.newLinkedHashMap();
        if (isDesc) {
            map.entrySet().stream().sorted(Map.Entry.<K, V>comparingByKey().reversed()).limit(limit)
                    .forEachOrdered(e -> result.put(e.getKey(), e.getValue()));
        } else {
            map.entrySet().stream().sorted(Map.Entry.<K, V>comparingByKey())
                    .forEachOrdered(e -> result.put(e.getKey(), e.getValue()));
        }
        return result;
    }


}
