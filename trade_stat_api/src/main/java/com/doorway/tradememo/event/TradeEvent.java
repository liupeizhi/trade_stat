package com.doorway.tradememo.event;

import lombok.Data;

/**
 * Note
 * Author:liupz
 * Date:2022/4/3
 */
@Data
public class TradeEvent<T> {
    TradeEventEnum type;
    private T data;
}
