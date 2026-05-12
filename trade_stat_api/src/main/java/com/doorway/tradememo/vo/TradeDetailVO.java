package com.doorway.tradememo.vo;

import com.doorway.tradememo.domain.TradeDetail;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Note
 * Author:liupz
 * Date:2022/3/27
 */
@Data
public class TradeDetailVO  {
    private Date openTime;
    private Date closeTime;
    /** expend : 耗费资金**/
    private BigDecimal expend;
    //取回资金
    /** income : 取回资金**/
    private BigDecimal income;
    private Integer holdDays;
    private String name;
    private String code;
    private Integer count;
    private Double profit;
    private Double profitRate;
    private List<TradeDetail> details;


}
