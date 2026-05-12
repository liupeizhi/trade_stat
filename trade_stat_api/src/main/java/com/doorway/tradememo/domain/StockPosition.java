package com.doorway.tradememo.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
* Created by Mybatis Generator on 2022/03/29
*/
@Data
public class StockPosition implements Serializable {

    private static final long serialVersionUID = -1L;

    //主键
    /** id : 主键**/
    private String id;
    //用户
    /** user_id : 用户**/
    private String userId;
    //股票编码
    /** code : 股票编码**/
    private String code;
    //股票数量
    /** vol : 股票数量**/
    private Integer vol;
    //成本
    /** cost : 成本**/
    private BigDecimal cost;
    //当前价格
    /** price : 当前价格**/
    private BigDecimal price;
    //创建时间
    /** created_time : 创建时间**/
    private Date createdTime;
    //更新时间
    /** updated_time : 更新时间**/
    private Date updatedTime;
    //上次清仓时间
    /** last_clear_time : 上次清仓时间**/
    private Date lastClearTime;
    //当前成本
    /** current_cost : 当前成本**/
    private BigDecimal currentCost;
    //建仓时间
    /** build_position_time : 建仓时间**/
    private Date buildPositionTime;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = " + hashCode());
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append(", id=").append(id);
        sb.append(", userId=").append(userId);
        sb.append(", code=").append(code);
        sb.append(", vol=").append(vol);
        sb.append(", cost=").append(cost);
        sb.append(", price=").append(price);
        sb.append(", createdTime=").append(createdTime);
        sb.append(", updatedTime=").append(updatedTime);
        sb.append(", lastClearTime=").append(lastClearTime);
        sb.append(", currentCost=").append(currentCost);
        sb.append(", buildPositionTime=").append(buildPositionTime);
        sb.append("]");
        return sb.toString();
    }
}