package com.doorway.tradememo.event;

import com.doorway.tradememo.listener.ISettleEventListener;
import com.doorway.tradememo.listener.ITradeEventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Note
 * Author:liupz
 * Date:2022/4/3
 */
@Component
public  class SettleEventSource {

    private List<ISettleEventListener> eventListeners = new ArrayList<>();

    public SettleEventSource(){

    }

   public void addEventListener(ISettleEventListener eventListener){
       eventListeners.add(eventListener);
   }

    public void removeEventListener(ISettleEventListener eventListener){
        eventListeners.remove(eventListener);
    }

    public void triggerEvent(SettleEvent event){
        eventListeners.forEach(el->el.handleEvent(event));
    }

}
