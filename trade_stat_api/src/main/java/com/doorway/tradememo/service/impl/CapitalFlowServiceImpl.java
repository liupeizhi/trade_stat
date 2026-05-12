package com.doorway.tradememo.service.impl;

import com.doorway.tradememo.mapper.TradeDetailMapper;
import com.doorway.tradememo.service.ICapitalFlowService;
import com.doorway.tradememo.utils.DateUtils;
import com.doorway.tradememo.vo.CapitalFlowVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * Note
 * Author:liupz
 * Date:2022/5/29
 */
@Component
public class CapitalFlowServiceImpl implements ICapitalFlowService {
    @Autowired
    private TradeDetailMapper tradeDetailMapper;


    @Override
    public CapitalFlowVO getTotalFlow() {
        CapitalFlowVO capitalFlow = new CapitalFlowVO();
        Map<String, Object> flowVOMap = tradeDetailMapper.getAllFlow();
        if(flowVOMap!=null) {
            capitalFlow.setFlow(flowVOMap.get("flow").toString());
            capitalFlow.setExpense(tradeDetailMapper.getAllFlowByOpt(true).toString());
            capitalFlow.setIncome(tradeDetailMapper.getAllFlowByOpt(false).toString());
            capitalFlow.setCommission(flowVOMap.get("commission").toString());
            capitalFlow.setTax(flowVOMap.get("tax").toString());
            capitalFlow.setTransFee(flowVOMap.get("trans_fee").toString());
        }
        return capitalFlow;
    }

    @Override
    public Map<String, CapitalFlowVO> getDistStockFlow() {
        Map<String, CapitalFlowVO> flows = new HashMap<>();
        List<Map<String,Object>> codeFlow =  tradeDetailMapper.getAllStockFlow();
        List<Map<String,Object>> expense =  tradeDetailMapper.getAllStockFlowByOpt(true);
        List<Map<String,Object>> income =  tradeDetailMapper.getAllStockFlowByOpt(false);
        for(Map<String,Object> flow:codeFlow){
            CapitalFlowVO capitalFlow = new CapitalFlowVO();
            capitalFlow.setFlow(flow.get("flow").toString());
            capitalFlow.setCommission(flow.get("commission").toString());
            capitalFlow.setTax(flow.get("tax").toString());
            capitalFlow.setTransFee(flow.get("trans_fee").toString());
            capitalFlow.setCode(flow.get("code").toString());

            flows.put((String)flow.get("code"),capitalFlow);
        }
        for(Map<String,Object> flow:expense){
            String code = flow.get("code").toString();
            if(flows.containsKey(code)){
                flows.get(code).setExpense(flow.get("flow").toString());
            }
        }

        for(Map<String,Object> flow:income){
            String code = flow.get("code").toString();
            if(flows.containsKey(code)){
                flows.get(code).setIncome(flow.get("flow").toString());
            }
        }

        return flows;
    }


    @Override
    public CapitalFlowVO getThisWeekFlow() {
        Map<String,Object> codeFlow =  tradeDetailMapper.getRangeFlow(DateUtils.parseDateToStr(DateUtils.getFirstDayOfWeek(new Date()),DateUtils.DATE_FORMAT_YYYY_MM_DD),DateUtils.parseDateToStr(new Date(),DateUtils.DATE_FORMAT_YYYY_MM_DD));
        Map<String,Object> expense =  tradeDetailMapper.getRangeFlowByOpt(DateUtils.parseDateToStr(DateUtils.getFirstDayOfWeek(new Date()),DateUtils.DATE_FORMAT_YYYY_MM_DD),DateUtils.parseDateToStr(new Date(),DateUtils.DATE_FORMAT_YYYY_MM_DD),true);
        Map<String,Object> income =  tradeDetailMapper.getRangeFlowByOpt(DateUtils.parseDateToStr(DateUtils.getFirstDayOfWeek(new Date()),DateUtils.DATE_FORMAT_YYYY_MM_DD),DateUtils.parseDateToStr(new Date(),DateUtils.DATE_FORMAT_YYYY_MM_DD),false);

        CapitalFlowVO capitalFlow = new CapitalFlowVO();
        if(codeFlow!=null){
            capitalFlow.setFlow(codeFlow.get("flow").toString());
            capitalFlow.setCommission(codeFlow.get("commission").toString());
            capitalFlow.setTax(codeFlow.get("tax").toString());
            capitalFlow.setTransFee(codeFlow.get("trans_fee").toString());
        }
       if(expense!=null) {
           capitalFlow.setExpense(expense.get("flow").toString());
       }
       if(income!=null) {
           capitalFlow.setIncome(income.get("flow").toString());
       }

        return capitalFlow;
    }

    @Override
    public CapitalFlowVO getThisMonthFlow() {
        Map<String,Object> codeFlow =  tradeDetailMapper.getRangeFlow(DateUtils.parseDateToStr(new Date(),"yyyy-MM-01"),DateUtils.parseDateToStr(new Date(),DateUtils.DATE_FORMAT_YYYY_MM_DD));
        Map<String,Object> expense =  tradeDetailMapper.getRangeFlowByOpt(DateUtils.parseDateToStr(new Date(),"yyyy-MM-01"),DateUtils.parseDateToStr(new Date(),DateUtils.DATE_FORMAT_YYYY_MM_DD),true);
        Map<String,Object> income =  tradeDetailMapper.getRangeFlowByOpt(DateUtils.parseDateToStr(new Date(),"yyyy-MM-01"),DateUtils.parseDateToStr(new Date(),DateUtils.DATE_FORMAT_YYYY_MM_DD),false);

        CapitalFlowVO capitalFlow = new CapitalFlowVO();
        if(codeFlow!=null){
            capitalFlow.setFlow(codeFlow.get("flow").toString());
            capitalFlow.setCommission(codeFlow.get("commission").toString());
            capitalFlow.setTax(codeFlow.get("tax").toString());
            capitalFlow.setTransFee(codeFlow.get("trans_fee").toString());
        }
        if(expense!=null) {
            capitalFlow.setExpense(expense.get("flow").toString());
        }
        if(income!=null) {
            capitalFlow.setIncome(income.get("flow").toString());
        }
        return capitalFlow;

    }


    @Override
    public Map<String, CapitalFlowVO> getStockFlowStatics(String code, String period, String start, String end) {
        Map<String, CapitalFlowVO> flows = new TreeMap<>();
        List<CapitalFlowVO> codeFlow = tradeDetailMapper.getSumStockFlows(period,code,start,end,null);
        List<CapitalFlowVO> expense = tradeDetailMapper.getSumStockFlows(period,code,start,end,true);
        List<CapitalFlowVO> income = tradeDetailMapper.getSumStockFlows(period,code,start,end,false);
        double totalFlow = 0;
        double totalComm = 0;
        double totalTax = 0;
        double totalTransFee = 0;
        double totalExpense = 0;
        double totalIncome = 0;
        for(CapitalFlowVO flow:codeFlow){
            totalFlow+=Double.parseDouble(flow.getFlow());
            totalComm+=Double.parseDouble(flow.getCommission());
            totalTax+=Double.parseDouble(flow.getTax());
            totalTransFee+=Double.parseDouble(flow.getTransFee());
            flow.setSumFlow(BigDecimal.valueOf(totalFlow).toPlainString());
            flow.setSumCommission(BigDecimal.valueOf(totalComm).toPlainString());
            flow.setSumTax(BigDecimal.valueOf(totalTax).toPlainString());
            flow.setSumTransFee(BigDecimal.valueOf(totalTransFee).toPlainString());
            flows.put(flow.getTime(),flow);
        }

        for(CapitalFlowVO flow:expense){
            if(flows.containsKey(flow.getTime())){
                flows.get(flow.getTime()).setExpense(flow.getFlow());
                totalExpense +=Double.parseDouble(flow.getFlow());
                flows.get(flow.getTime()).setSumExpense(BigDecimal.valueOf(totalExpense).toPlainString());

            }
        }
        for(CapitalFlowVO flow:income){
            if(flows.containsKey(flow.getTime())){
                flows.get(flow.getTime()).setIncome(flow.getFlow());
                totalIncome +=Double.parseDouble(flow.getFlow());
                flows.get(flow.getTime()).setSumIncome(BigDecimal.valueOf(totalIncome).toPlainString());
            }
        }

        return flows;
    }

    @Override
    public Map<String, Map<String,CapitalFlowVO>> getDistStockFlowStatics(String code, String period, String start, String end) {
        Map<String, Map<String,CapitalFlowVO>> flows = new TreeMap<>();
        List<CapitalFlowVO> codeFlow = tradeDetailMapper.getDistSumStockFlows(period,code,start,end,null);
        List<CapitalFlowVO> expense = tradeDetailMapper.getDistSumStockFlows(period,code,start,end,true);
        List<CapitalFlowVO> income = tradeDetailMapper.getDistSumStockFlows(period,code,start,end,false);
        for(CapitalFlowVO flow:codeFlow){
            if(flows.containsKey(flow.getTime())){
                flows.get(flow.getTime()).put(flow.getCode(),flow);
            }else{
                Map<String,CapitalFlowVO> capitalFlowVOS = new HashMap<>();
                capitalFlowVOS.put(flow.getCode(),flow);
                flows.put(flow.getTime(),capitalFlowVOS);
            }

        }

        for(CapitalFlowVO flow:expense){
            if(flows.containsKey(flow.getTime())){
                for(CapitalFlowVO vo:flows.get(flow.getTime()).values()){
                    if(vo.getCode().equals(flow.getCode())){
                        vo.setExpense(flow.getFlow());
                    }
                }
            }
        }
        for(CapitalFlowVO flow:income){
            if(flows.containsKey(flow.getTime())){
                for(CapitalFlowVO vo:flows.get(flow.getTime()).values()){
                    if(vo.getCode().equals(flow.getCode())){
                        vo.setIncome(flow.getFlow());
                    }
                }
            }
        }

        return flows;
    }
}
