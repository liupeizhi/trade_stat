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
public class Profit implements Serializable {

    private static final long serialVersionUID = -1L;

    //
    /** id : **/
    private String id;
    //日期
    /** day : 日期**/
    private String day;
    //总盈亏
    /** total_profit : 总盈亏**/
    private String totalProfit;
    //当日盈亏
    /** profit : 当日盈亏**/
    private BigDecimal profit;
    //用户id
    /** user_id : 用户id**/
    private String userId;
    //租户号
    /** tenant_id : 租户号**/
    private String tenantId;
    //创建人
    /** created_by : 创建人**/
    private String createdBy;
    //创建时间
    /** created_time : 创建时间**/
    private Date createdTime;
    //更新人
    /** updated_by : 更新人**/
    private String updatedBy;
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
        sb.append(", totalProfit=").append(totalProfit);
        sb.append(", profit=").append(profit);
        sb.append(", userId=").append(userId);
        sb.append(", tenantId=").append(tenantId);
        sb.append(", createdBy=").append(createdBy);
        sb.append(", createdTime=").append(createdTime);
        sb.append(", updatedBy=").append(updatedBy);
        sb.append(", updatedTime=").append(updatedTime);
        sb.append("]");
        return sb.toString();
    }
}