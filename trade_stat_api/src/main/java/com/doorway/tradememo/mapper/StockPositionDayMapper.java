package com.doorway.tradememo.mapper;

import com.doorway.tradememo.domain.StockPosition;
import com.doorway.tradememo.domain.StockPositionDay;
import com.doorway.tradememo.utils.domain.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by Mybatis Generator on 2022/04/04
 */
@Mapper
@Component
public interface StockPositionDayMapper extends BaseMapper<StockPositionDay, String> {

    @Delete("delete from  stock_position_day")
    int deleteAll();

    @Delete("delete from  stock_position_day where code = #{code}")
    int deleteByCode(@Param("code") String code);
    @Delete({"<script>",
            " delete ",
            " FROM stock_position_day WHERE   code  in ",
            "<foreach item='item' index='index' collection='codes' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            "</script>"})
    int deleteByCodes(@Param("codes") List<String> codes);

    @Delete("delete from  stock_position_day where code = #{code} and day >=#{day}")
    int deleteAfterDayByCode(@Param("code") String code, @Param("day") String day);

    @Delete("delete from  stock_position_day where day >=#{day}")
    int deleteAfterDay(@Param("day") String day);


    @Select("select * from stock_position_day where day=(select max(day) from stock_position_day where day<=#{day}) ")
    List<StockPositionDay> getByDay(@Param("day") String day);

    @Select("select * from stock_position_day  where code=#{code} order by day asc")
    List<StockPositionDay> getByCode(@Param("code") String code);





    @Select("select * from stock_position_day where day=#{day} and code=#{code} order by day asc")
    StockPositionDay getByDayAndCodeEqual(@Param("day") String day, @Param("code") String code);

    @Select("select * from stock_position_day where day<=#{day} and code=#{code} order by day asc")
    List<StockPositionDay> getByDayAndCode(@Param("day") String day, @Param("code") String code);

    @Select("select * from stock_position_day where day<=#{day} order by day asc")
    List<StockPositionDay> getBeforeDay(@Param("day") String day);

    @Select("select * from stock_position_day where day>=#{day} order by day asc")
    List<StockPositionDay> getAfterDay(@Param("day") String day);


    List<StockPositionDay> getBetweenDay(@Param("start") String start, @Param("end") String end, @Param("code") String code);

    @Select("select * from stock_position_day where day=#{day} and code=#{code} and vol=#{vol}")
    StockPositionDay getByDayCodeVol(@Param("day") String day, @Param("code") String code, @Param("vol") Integer vol);



    @Select("select max(day) from stock_position_day where day<=#{day} ")
    String getMaxDayBeforeDay(@Param("day") String day);

    @Select("select max(day) from stock_position_day ")
    String getMaxDay();

    @Select("select sum(day_profit) from stock_position_day ")
    Double getTotalProfit();

    @Select("select code,sum(day_profit) profit from stock_position_day group by code")
    List<Map<String, Object>> getStockProfits();

    List<Map<String, Object>> getStockPeriodProfits(@Param("period") String period, @Param("code") String code, @Param("start") String start, @Param("end") String end);


}