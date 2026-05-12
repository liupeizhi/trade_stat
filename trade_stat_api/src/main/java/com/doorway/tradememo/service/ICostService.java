package com.doorway.tradememo.service;

import java.util.Map;

/**
 * 收益
 * Author:liupz
 * Date:2022/4/9
 */
public interface ICostService {

    /**
     * 某只证券的成本线历史
     * @return
     */
    Map<String,Double> getCostLine(String code);

    /**
     * 某只证券的成本线历史
     * @return
     */
    Map<String,Double> getCostLineByTerm(String code,String term);


}
