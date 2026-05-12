package com.doorway.tradememo.req;

import lombok.Data;

/**
 * Note
 * Author:liupz
 * Date:2022/4/6
 */
@Data
public class StockHistoryPositionQO {

    /** user_id : **/
    private String userId;
    //代码
    /** code : 代码**/
    private String code;

    //第几期持仓（以开仓日期为准）
    /** term : 第几期持仓（以开仓日期为准）**/
    private String term;




}
