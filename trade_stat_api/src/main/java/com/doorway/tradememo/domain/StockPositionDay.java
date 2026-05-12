package com.doorway.tradememo.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
* Created by Mybatis Generator on 2022-07-02
*/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockPositionDay implements Serializable {

    private static final long serialVersionUID = -1L;

    //
    /** id : **/
    private String id;
    //
    /** day : **/
    private String day;
    private String month;
    private String year;
    //
    /** code : **/
    private String code;
    //
    /** vol : **/
    private Integer vol;
    //
    /** avg_cost : **/
    private BigDecimal avgCost;
    //
    /** close_price : **/
    private BigDecimal closePrice;
    //
    /** created_time : **/
    private Date createdTime;
    //
    /** updated_time : **/
    private Date updatedTime;
    //
    /** total_profit : **/
    private BigDecimal totalProfit;
    //
    /** day_profit : **/
    private BigDecimal dayProfit;
    //
    /** term_trade_times : **/
    private Integer termTradeTimes;
    //
    /** term_open_time : **/
    private Date termOpenTime;
    //
    /** term_expend : **/
    private BigDecimal termExpend;
    //
    /** term_income : **/
    private BigDecimal termIncome;
    //
    /** total_expend : **/
    private BigDecimal totalExpend;
    //
    /** total_income : **/
    private BigDecimal totalIncome;

    private Boolean clear;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append(", id=").append(id);
        sb.append(", day=").append(day);
        sb.append(", code=").append(code);
        sb.append(", vol=").append(vol);
        sb.append(", avgCost=").append(avgCost);
        sb.append(", closePrice=").append(closePrice);
        sb.append(", createdTime=").append(createdTime);
        sb.append(", updatedTime=").append(updatedTime);
        sb.append(", totalProfit=").append(totalProfit);
        sb.append(", dayProfit=").append(dayProfit);
        sb.append(", termTradeTimes=").append(termTradeTimes);
        sb.append(", termOpenTime=").append(termOpenTime);
        sb.append(", termExpend=").append(termExpend);
        sb.append(", termIncome=").append(termIncome);
        sb.append(", totalExpend=").append(totalExpend);
        sb.append(", totalIncome=").append(totalIncome);
        sb.append("]");
        return sb.toString();
    }
}