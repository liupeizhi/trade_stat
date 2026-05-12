package com.doorway.tradememo.vo;

import lombok.Data;

/**
 * Note
 * Author:liupz
 * Date:2022/8/18
 */
@Data
public class StockView {
    private String code;
    private String name;
    private Double capitalFlow;
    private Double profit;
    private Integer holdDays;
    private Long tradeTimes;
}
