package com.doorway.tradememo.domain;

import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
* Created by Mybatis Generator on 2022/03/29
*/
@Data
public class TradeMemo implements Serializable {

    private static final long serialVersionUID = -1L;

    //
    /** id : **/
    private String id;
    //
    /** trade_id : **/
    private String tradeId;
    //
    /** user_id : **/
    private String userId;
    //
    /** title : **/
    private String title;
    //
    /** content : **/
    private String content;
    //租户号
    /** tenant_id : 租户号**/
    private String tenantId;
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
        sb.append(", tradeId=").append(tradeId);
        sb.append(", userId=").append(userId);
        sb.append(", title=").append(title);
        sb.append(", content=").append(content);
        sb.append(", tenantId=").append(tenantId);
        sb.append(", createdTime=").append(createdTime);
        sb.append(", updatedTime=").append(updatedTime);
        sb.append("]");
        return sb.toString();
    }
}