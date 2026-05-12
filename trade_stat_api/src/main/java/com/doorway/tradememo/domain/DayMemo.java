package com.doorway.tradememo.domain;

import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
* Created by Mybatis Generator on 2022/03/29
*/
@Data
public class DayMemo implements Serializable {

    private static final long serialVersionUID = -1L;

    //
    /** id : **/
    private String id;
    //日期
    /** day : 日期**/
    private String day;
    //标题
    /** title : 标题**/
    private String title;
    //内容
    /** content : 内容**/
    private String content;
    //用户ID
    /** user_id : 用户ID**/
    private String userId;
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
        sb.append(", title=").append(title);
        sb.append(", content=").append(content);
        sb.append(", userId=").append(userId);
        sb.append(", createdTime=").append(createdTime);
        sb.append(", updatedTime=").append(updatedTime);
        sb.append("]");
        return sb.toString();
    }
}