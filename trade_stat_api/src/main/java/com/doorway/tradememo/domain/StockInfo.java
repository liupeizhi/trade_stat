package com.doorway.tradememo.domain;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
* Created by Mybatis Generator on 2022-04-19
*/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockInfo implements Serializable {

    private static final long serialVersionUID = -1L;

    //
    /** id : **/
    private Integer id;
    //
    /** code : **/
    private String code;
    //
    /** name : **/
    private String name;
    //
    /** spelling : **/
    private String spelling;
    //
    /** market : **/
    private String market;
    //
    /** type : **/
    private String type;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append(", id=").append(id);
        sb.append(", code=").append(code);
        sb.append(", name=").append(name);
        sb.append(", spelling=").append(spelling);
        sb.append(", market=").append(market);
        sb.append(", type=").append(type);
        sb.append("]");
        return sb.toString();
    }
}