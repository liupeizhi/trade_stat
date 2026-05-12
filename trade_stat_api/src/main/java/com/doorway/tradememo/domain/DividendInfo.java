package com.doorway.tradememo.domain;

import lombok.Data;

/**
 * Note
 * Author:liupz
 * Date:2022/7/2
 */
@Data
public class DividendInfo {

    /**
     * type：
     * 1:派息 pxbl
     * 2=送股  sgbl
     * 3：送股派息 pxbl sgbl
     *
     * 16：增发 zfbl zfgs zfjg
     */
    private int type;

    private String date;

    //派息比例
    private Double pxbl;
    //送股比例
    private Double sgbl;
    //除息比例
    private Double cxbl;
    //派股比例
    private Double pgbl;

    //派股加个
    private Double pgjg;
    //
    private Double pghg;

    //增发比例
    private Double zfbl;
    //增发股数
    private Double zfgs;
    //增发价格
    private Double zfjg;

    private Integer ggflag;

    private Double zzbl;


}
