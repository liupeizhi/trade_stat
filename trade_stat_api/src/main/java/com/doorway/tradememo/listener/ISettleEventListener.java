package com.doorway.tradememo.listener;

import com.doorway.tradememo.event.SettleEvent;

/**
 * Note
 * Author:liupz
 * Date:2022/4/3
 */
public interface ISettleEventListener {
    void handleEvent(SettleEvent settleEvent);
}
