package com.doorway.tradememo.mapper;

import com.doorway.tradememo.domain.Stock;
import com.doorway.tradememo.utils.domain.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
* Created by Mybatis Generator on 2022/03/29
*/
@Mapper
@Component
public interface StockMapper extends BaseMapper<Stock, String> {
}