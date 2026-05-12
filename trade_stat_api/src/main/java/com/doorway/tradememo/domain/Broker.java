package com.doorway.tradememo.domain;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
* Created by Mybatis Generator on 2022/03/29
*/
@Data
public class Broker implements Serializable {

    private static final long serialVersionUID = -1L;

    //主键
    /** id : 主键**/
    private String id;
    //名称
    /** name : 名称**/
    private String name;
    //佣金比例(commission_rate)
    /** com_rate : 佣金比例(commission_rate)**/
    private BigDecimal comRate;
    //是否免五
    /** five_free : 是否免五**/
    private Boolean fiveFree;
    //开户地址
    /** adress : 开户地址**/
    private String adress;
    //联系人电话
    /** telephone : 联系人电话**/
    private String telephone;
    //沪A账号
    /** sh_account : 沪A账号**/
    private String shAccount;
    //深A账号
    /** sz_account : 深A账号**/
    private String szAccount;
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
        sb.append(", name=").append(name);
        sb.append(", comRate=").append(comRate);
        sb.append(", fiveFree=").append(fiveFree);
        sb.append(", adress=").append(adress);
        sb.append(", telephone=").append(telephone);
        sb.append(", shAccount=").append(shAccount);
        sb.append(", szAccount=").append(szAccount);
        sb.append(", createdTime=").append(createdTime);
        sb.append(", updatedTime=").append(updatedTime);
        sb.append("]");
        return sb.toString();
    }
}