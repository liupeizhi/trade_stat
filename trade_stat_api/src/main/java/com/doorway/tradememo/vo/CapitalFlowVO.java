package com.doorway.tradememo.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * Note
 * Author:liupz
 * Date:2022/7/7
 */
@Data
public class CapitalFlowVO implements Serializable {
    //股票编码
    private String code="";

    //时间，YYYY-MM-DD,YYYY-MM等
    private String time="";

    //当前时间的流水(包含佣金税费)
    private String flow="";

    //买股支出（包含佣金）
    private String expense="";
    //卖股收入（包含佣金税费）
    private String income="";
    //税费
    private String tax="";

    //佣金
    private String commission="";

    //交易费
    private String transFee="";



    //至今累计流水(包含佣金税费)
    private String sumFlow="";

    //至今累计税费
    private String sumTax="";

    private String sumCommission="";
    private String sumTransFee="";
    private String sumExpense="";
    private String sumIncome="";

}
