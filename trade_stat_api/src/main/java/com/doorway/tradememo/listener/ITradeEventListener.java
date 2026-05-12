package com.doorway.tradememo.listener;

import com.doorway.tradememo.event.TradeEvent;

/**
 * Note
 * Author:liupz
 * Date:2022/4/3
 */
public interface ITradeEventListener<T> {

    void handleEvent(TradeEvent<T> tradeEvent);

}
