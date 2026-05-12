package com.doorway.tradememo.event;

/**
 * Note
 * Author:liupz
 * Date:2022/4/3
 */
public enum TradeEventEnum {
    OPEN("开仓", 1), CLOSE("清仓", 0), ADD("加仓", 2), REDUCE("减仓", 3),SETTLE("当日结算", 4);
    // 成员变量
    private String name;
    private int value;
    // 构造方法
    TradeEventEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }
    // 普通方法
    public static String getByValue(int index) {
        for (TradeEventEnum c : TradeEventEnum.values()) {
            if (c.getValue() == index) {
                return c.name;
            }
        }
        return null;
    }

    // 普通方法
    public static TradeEventEnum getByName(String name) {
        for (TradeEventEnum c : TradeEventEnum.values()) {
            if (c.getName().equals(name)) {
                return c;
            }
        }
        return null;
    }

    // get set 方法
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String toString(){
        return name;
    }
}
