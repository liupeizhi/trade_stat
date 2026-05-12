package com.doorway.tradememo.utils;

import com.doorway.tradememo.domain.StockHistoryPosition;
import com.doorway.tradememo.domain.StockPosition;
import com.doorway.tradememo.domain.StockPositionDay;
import com.doorway.tradememo.domain.TradeDetail;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Note
 * Author:liupz
 * Date:2022/4/4
 */
@Data
public class CostResp {
    List<TradeDetail> updates = new ArrayList<>();
    List<TradeDetail> errors = new ArrayList<>();
    List<StockHistoryPosition> stockHistoryPositions = new ArrayList<>();
    List<StockPosition> currentStockPositions = new ArrayList<>();
    List<String> clearCodes = new ArrayList<>();
    Map<String, List<StockPositionDay>> stockPositionDayMap = new HashMap<>();
    Map<String, String> result = new HashMap<>();
}
