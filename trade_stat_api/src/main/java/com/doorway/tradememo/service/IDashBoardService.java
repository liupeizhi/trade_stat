package com.doorway.tradememo.service;

import com.doorway.tradememo.vo.DashBoard;
import com.doorway.tradememo.vo.StockView;

import java.util.List;

/**
 * Note
 * Author:liupz
 * Date:2022/8/14
 */
public interface IDashBoardService {

    public List<StockView> allView();


    public DashBoard getDashBoard() ;


}
