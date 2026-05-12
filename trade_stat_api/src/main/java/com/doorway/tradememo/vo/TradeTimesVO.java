package com.doorway.tradememo.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * Note
 * Author:liupz
 * Date:2022/7/7
 */
@Data
public class TradeTimesVO implements Serializable {
    private String time;
    private Integer times;
    private Integer sumTimes;
}
