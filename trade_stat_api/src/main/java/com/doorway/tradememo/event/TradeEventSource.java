package com.doorway.tradememo.event;

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
public  class TradeEventSource {

    private List<ITradeEventListener> eventListeners = new ArrayList<>();

    public TradeEventSource(){

    }

   public void addEventListener(ITradeEventListener eventListener){
       eventListeners.add(eventListener);
   }

    public void removeEventListener(ITradeEventListener eventListener){
        eventListeners.remove(eventListener);
    }

    public void triggerEvent(TradeEvent event){
        eventListeners.forEach(el->el.handleEvent(event));
    }

}
