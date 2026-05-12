package com.doorway.tradememo.vo;

import lombok.Data;

import java.util.Date;

/**
 * Note
 * Author:liupz
 * Date:2022/4/9
 */
@Data
public class ProfitVO {
    private double expend;
    private double income;
    private double price;
    private double profit;
    private double dayProfit;
    private double dayProfitRate;
    private double sumProfit;
    private double profitRate;
    private double cost;
    private int vol;
    private String day;
    private String code;
    private Date date;


}
