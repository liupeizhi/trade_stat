package com.doorway.tradememo.vo;

import com.doorway.tradememo.domain.TradeDetail;
import lombok.Data;

import java.util.List;

/**
 * Note
 * Author:liupz
 * Date:2022/4/16
 */
@Data
public class TradePoint {
    private String day;
    private int vol;
    private Double price;
    private String name;
    private List<TradeDetail> tradeDetails;
}
