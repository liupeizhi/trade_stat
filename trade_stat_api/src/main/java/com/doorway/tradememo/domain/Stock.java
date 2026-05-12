package com.doorway.tradememo.domain;

import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
* Created by Mybatis Generator on 2022/03/29
*/
@Data
public class Stock implements Serializable {

    private static final long serialVersionUID = -1L;

    //股票编码
    /** code : 股票编码**/
    private String code;
    //股票名称
    /** name : 股票名称**/
    private String name;
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
        sb.append(", code=").append(code);
        sb.append(", name=").append(name);
        sb.append(", createdTime=").append(createdTime);
        sb.append(", updatedTime=").append(updatedTime);
        sb.append("]");
        return sb.toString();
    }
}