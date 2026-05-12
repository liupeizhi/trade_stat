package com.doorway.tradememo.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
* Created by Mybatis Generator on 2022/04/03
*/
@Data
public class StockQuotation implements Serializable {

    private static final long serialVersionUID = -1L;

    //
    /** id : **/
    private String id;
    //
    /** day : **/
    private String day;
    //
    /** high : **/
    private BigDecimal high;
    //
    /** low : **/
    private BigDecimal low;
    //
    /** open : **/
    private BigDecimal open;
    //
    /** close : **/
    private BigDecimal close;
    //
    /** code : **/
    private String code;
    //前收盘
    /** last_close : 前收盘**/
    private BigDecimal lastClose;
    //涨跌额
    /** offset_value : 涨跌额**/
    private BigDecimal offsetValue;
    //涨跌幅
    /** offset_rate : 涨跌幅**/
    private BigDecimal offsetRate;
    //换手率
    /** turnover_rate : 换手率**/
    private BigDecimal turnoverRate;
    //成交量
    /** turnover : 成交量**/
    private BigDecimal turnover;
    //成交金额
    /** turnover_value : 成交金额**/
    private BigDecimal turnoverValue;
    //创建时间
    /** created_time : 创建时间**/
    private Date createdTime;
    //更新时间
    /** updated_time : 更新时间**/
    private Date updatedTime;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append(", id=").append(id);
        sb.append(", day=").append(day);
        sb.append(", high=").append(high);
        sb.append(", low=").append(low);
        sb.append(", open=").append(open);
        sb.append(", close=").append(close);
        sb.append(", code=").append(code);
        sb.append(", lastClose=").append(lastClose);
        sb.append(", offsetValue=").append(offsetValue);
        sb.append(", offsetRate=").append(offsetRate);
        sb.append(", turnoverRate=").append(turnoverRate);
        sb.append(", turnover=").append(turnover);
        sb.append(", turnoverValue=").append(turnoverValue);
        sb.append(", createdTime=").append(createdTime);
        sb.append(", updatedTime=").append(updatedTime);
        sb.append("]");
        return sb.toString();
    }
}