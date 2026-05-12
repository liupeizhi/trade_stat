package com.doorway.tradememo.mapper;

import com.doorway.tradememo.domain.StockHistoryPosition;
import com.doorway.tradememo.domain.StockPosition;
import com.doorway.tradememo.utils.domain.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
* Created by Mybatis Generator on 2022/03/29
*/
@Mapper
@Component
public interface StockPositionMapper extends BaseMapper<StockPosition, String> {
    @Delete("delete from stock_position")
     int deleteAll();



    @Delete({"<script>",
            " delete ",
            " FROM stock_position WHERE   code  in ",
            "<foreach item='item' index='index' collection='codes' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            "</script>"})
    int deleteByCodes(@Param("codes") List<String> codes);



    @Delete("delete from  stock_position where code=#{code}")
    int deleteByCode(@Param("code") String code);

    @Select("select * from stock_position where code=#{code}")
    StockPosition getByCode(@Param("code") String code);

    @Select("select distinct(code) from stock_position ")
    Set<String> getPositionCodes();

}