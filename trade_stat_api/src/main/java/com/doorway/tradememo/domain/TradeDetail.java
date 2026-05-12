package com.doorway.tradememo.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
* Created by Mybatis Generator on 2022/04/05
*/
@Data
public class TradeDetail implements Serializable {

    private static final long serialVersionUID = -1L;

    //主键
    /** id : 主键**/
    private String id;
    //用户主键
    /** user_id : 用户主键**/
    private String userId;
    //交易时间
    /** trade_time : 交易时间**/
    private Date tradeTime;
    //交易方向：1=buy;0:sell
    /** opt : 交易方向：1=buy;0:sell**/
    private Boolean opt;
    //成交价格
    /** price : 成交价格**/
    private BigDecimal price;
    //股票代码
    /** code : 股票代码**/
    private String code;
    //成交数量
    /** vol : 成交数量**/
    private Integer vol;
    //佣金
    /** commission : 佣金**/
    private BigDecimal commission;
    //税费
    /** tax : 税费**/
    private BigDecimal tax;
    //成交日
    /** day : 成交日**/
    private String day;
    //成交月
    /** month : 成交月**/
    private String month;
    //成交年
    /** year : 成交年**/
    private String year;
    //券商id
    /** broker_id : 券商id**/
    private String brokerId;
    //创建时间
    /** created_time : 创建时间**/
    private Date createdTime;
    //更新时间
    /** updated_time : 更新时间**/
    private Date updatedTime;
    //过户费
    /** trans_fee : 过户费**/
    private BigDecimal transFee;
    //第几期持仓
    /** term : 第几期持仓**/
    private String term;
    //
    /** row_key : **/
    private String rowKey;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append(", id=").append(id);
        sb.append(", userId=").append(userId);
        sb.append(", tradeTime=").append(tradeTime);
        sb.append(", opt=").append(opt);
        sb.append(", price=").append(price);
        sb.append(", code=").append(code);
        sb.append(", vol=").append(vol);
        sb.append(", commission=").append(commission);
        sb.append(", tax=").append(tax);
        sb.append(", day=").append(day);
        sb.append(", month=").append(month);
        sb.append(", year=").append(year);
        sb.append(", brokerId=").append(brokerId);
        sb.append(", createdTime=").append(createdTime);
        sb.append(", updatedTime=").append(updatedTime);
        sb.append(", transFee=").append(transFee);
        sb.append(", term=").append(term);
        sb.append(", rowKey=").append(rowKey);
        sb.append("]");
        return sb.toString();
    }
}