package com.doorway.tradememo.utils;


import com.fasterxml.jackson.databind.JsonNode;

/**
 * 每日交易数据实体类
 * @author ufo
 *
 */
public class DayTransact {
    private long    id;            // ID
    private String  day;        // 日期
    private String  code;        // 代号
    private String  name;        // 名称
    private double  tclose;        // 收盘价
    private double  high;        // 最高价
    private double  low;        // 最低价
    private double  topen;        // 开盘价
    private double  lclose;        // 前日收盘价
    private double  chg;        // 涨跌额
    private double  pchg;        // 涨跌幅
    private double  turnover;    // 换手率
    private long    voturnover;    // 成交量
    private double  vaturnover;    // 成交金额
    private double  tcap;        // 总市值
    private double  mcap;        // 流通市值

    public DayTransact() {

    }

    public DayTransact(JsonNode transNode) {

    }

    public DayTransact(String[] arr) {
        if(arr.length!=15) {
            throw new ArrayIndexOutOfBoundsException("Array size should be 15 but now it is "+arr.length);
        }

        String dataLine=String.join(",", arr);

        day=arr[0];

        try {
            tclose=Double.parseDouble(arr[3]);
        }catch(NumberFormatException ex) {
            throw new NumberFormatException("Can not get tclose from string:"+arr[3]+" dataLine:"+dataLine);
        }

        try {
            high=Double.parseDouble(arr[4]);
        }catch(NumberFormatException ex) {
            throw new NumberFormatException("Can not get high from string:"+arr[4]+" dataLine:"+dataLine);
        }

        try {
            low=Double.parseDouble(arr[5]);
        }catch(NumberFormatException ex) {
            throw new NumberFormatException("Can not get low from string:"+arr[5]+" dataLine:"+dataLine);
        }

        try {
            topen=Double.parseDouble(arr[6]);
        }catch(NumberFormatException ex) {
            throw new NumberFormatException("Can not get topen from string:"+arr[6]+" dataLine:"+dataLine);
        }

        try {
            lclose=Double.parseDouble(arr[7]);
        }catch(NumberFormatException ex) {
            throw new NumberFormatException("Can not get lclose from string:"+arr[7]+" dataLine:"+dataLine);
        }

        try {
            chg=Double.parseDouble(arr[8]);
        }catch(NumberFormatException ex) {
            throw new NumberFormatException("Can not get chg from string:"+arr[8]+" dataLine:"+dataLine);
        }

        try {
            pchg=Double.parseDouble(arr[9]);
        }catch(NumberFormatException ex) {
            throw new NumberFormatException("Can not get pchg from string:"+arr[9]+" dataLine:"+dataLine);
        }

        try {
            turnover=Double.parseDouble(arr[10]);
        }catch(NumberFormatException ex) {
            throw new NumberFormatException("Can not get turnover from string:"+arr[10]+" dataLine:"+dataLine);
        }

        try {
            voturnover=Long.parseLong(arr[11]);
        }catch(NumberFormatException ex) {
            throw new NumberFormatException("Can not get voturnover from string:"+arr[11]+" dataLine:"+dataLine);
        }

        try {
            vaturnover=Double.parseDouble(arr[12]);
        }catch(NumberFormatException ex) {
            throw new NumberFormatException("Can not get vaturnover from string:"+arr[12]+" dataLine:"+dataLine);
        }

        try {
            tcap=Double.parseDouble(arr[13]);
        }catch(NumberFormatException ex) {
            throw new NumberFormatException("Can not get tcap from string:"+arr[13]+" dataLine:"+dataLine);
        }

        try {
            mcap=Double.parseDouble(arr[14]);
        }catch(NumberFormatException ex) {
            throw new NumberFormatException("Can not get mcap from string:"+arr[14]+" dataLine:"+dataLine);
        }

    }

    public String toString() {
        StringBuilder sb=new StringBuilder();
        sb.append("id:"+id);
        sb.append(" 日期day:"+day);
        sb.append(" 代号code:"+code);
        sb.append(" 名称name:"+name);
        sb.append(" 收盘价tclose:"+tclose);
        sb.append(" 最高价high:"+high);
        sb.append(" 最低价low:"+low);
        sb.append(" 开盘价topen:"+topen);
        sb.append(" 前日收盘价lclose:"+lclose);
        sb.append(" 涨跌额chg:"+chg);
        sb.append(" 涨跌幅pchg:"+pchg);
        sb.append(" 换手率turnover:"+turnover);
        sb.append(" 成交量voturnover:"+voturnover);
        sb.append(" 成交金额vaturnover:"+vaturnover);
        sb.append(" 总市值tcap:"+tcap);
        sb.append(" 流通市值mcap:"+mcap);

        return sb.toString();//"code:"+code+" name:"+name+" date:"+day+" tclose:"+tclose;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getDay() {
        return day;
    }
    public void setDay(String day) {
        this.day = day;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public double getTclose() {
        return tclose;
    }
    public void setTclose(double tclose) {
        this.tclose = tclose;
    }
    public double getHigh() {
        return high;
    }
    public void setHigh(double high) {
        this.high = high;
    }
    public double getLow() {
        return low;
    }
    public void setLow(double low) {
        this.low = low;
    }
    public double getTopen() {
        return topen;
    }
    public void setTopen(double topen) {
        this.topen = topen;
    }
    public double getLclose() {
        return lclose;
    }
    public void setLclose(double lclose) {
        this.lclose = lclose;
    }
    public double getChg() {
        return chg;
    }
    public void setChg(double chg) {
        this.chg = chg;
    }
    public double getPchg() {
        return pchg;
    }
    public void setPchg(double pchg) {
        this.pchg = pchg;
    }
    public double getTurnover() {
        return turnover;
    }
    public void setTurnover(double turnover) {
        this.turnover = turnover;
    }
    public long getVoturnover() {
        return voturnover;
    }
    public void setVoturnover(long voturnover) {
        this.voturnover = voturnover;
    }
    public double getVaturnover() {
        return vaturnover;
    }
    public void setVaturnover(double vaturnover) {
        this.vaturnover = vaturnover;
    }
    public double getTcap() {
        return tcap;
    }
    public void setTcap(double tcap) {
        this.tcap = tcap;
    }
    public double getMcap() {
        return mcap;
    }
    public void setMcap(double mcap) {
        this.mcap = mcap;
    }
}
