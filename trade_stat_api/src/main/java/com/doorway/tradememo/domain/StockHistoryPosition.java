package com.doorway.tradememo.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
* Created by Mybatis Generator on 2022/04/03
*/
@Data
public class StockHistoryPosition implements Serializable {

    private static final long serialVersionUID = -1L;

    //
    /** id : **/
    private String id;
    //
    /** user_id : **/
    private String userId;
    //代码
    /** code : 代码**/
    private String code;
    //当日收益
    /** profit : 当日收益**/
    private BigDecimal profit;
    //第几期持仓（以开仓日期为准）
    /** term : 第几期持仓（以开仓日期为准）**/
    private String term;
    //租户号
    /** tenant_id : 租户号**/
    private String tenantId;
    //创建时间
    /** created_time : 创建时间**/
    private Date createdTime;
    //更新时间
    /** updated_time : 更新时间**/
    private Date updatedTime;
    //耗费资金
    /** expend : 耗费资金**/
    private BigDecimal expend;
    //取回资金
    /** income : 取回资金**/
    private BigDecimal income;
    //盈利百分比
    /** profit_rate : 盈利百分比**/
    private BigDecimal profitRate;
    //持仓时长
    /** hold_time : 持仓时长**/
    private Integer holdTime;
    //开仓时间
    /** open_time : 开仓时间**/
    private Date openTime;
    //清仓时间
    /** close_time : 清仓时间**/
    private Date closeTime;
    //交易次数
    /** trade_count : 交易次数**/
    private Integer tradeCount;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append(", id=").append(id);
        sb.append(", userId=").append(userId);
        sb.append(", code=").append(code);
        sb.append(", profit=").append(profit);
        sb.append(", term=").append(term);
        sb.append(", tenantId=").append(tenantId);
        sb.append(", createdTime=").append(createdTime);
        sb.append(", updatedTime=").append(updatedTime);
        sb.append(", expend=").append(expend);
        sb.append(", income=").append(income);
        sb.append(", profitRate=").append(profitRate);
        sb.append(", holdTime=").append(holdTime);
        sb.append(", openTime=").append(openTime);
        sb.append(", closeTime=").append(closeTime);
        sb.append(", tradeCount=").append(tradeCount);
        sb.append("]");
        return sb.toString();
    }
}