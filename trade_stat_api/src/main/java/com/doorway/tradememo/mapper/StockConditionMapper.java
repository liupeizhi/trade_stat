package com.doorway.tradememo.mapper;

import com.doorway.tradememo.domain.StockQuotation;
import com.doorway.tradememo.utils.domain.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

/**
* Created by Mybatis Generator on 2022/04/03
*/
@Mapper
@Component
public interface StockConditionMapper extends BaseMapper<StockQuotation, String> {

    @Select("select * from stock_condition where code=#{code} and day<=#{end} and day >=#{start} order by day asc")
    List<StockQuotation> getByStartEnd(@Param("code") String code, @Param("start")String start, @Param("end")String end);
}