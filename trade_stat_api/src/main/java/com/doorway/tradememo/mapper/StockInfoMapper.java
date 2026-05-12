package com.doorway.tradememo.mapper;

import com.doorway.tradememo.domain.StockInfo;
import com.doorway.tradememo.utils.domain.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

/**
* Created by Mybatis Generator on 2022/04/01
*/
@Mapper
@Component
public interface StockInfoMapper extends BaseMapper<StockInfo, String> {

    @Delete("delete from  stock_info")
    int deleteAll();

    @Select("select * from stock_info where code like #{code} or name like #{name} or spelling like #{spelling}")
    List<StockInfo> suggestStocks(@Param("code") String code,@Param("name") String name,@Param("spelling") String spelling);

    @Select("select * from stock_info where market in ('sh','sz') and type in('GP-A','QDII-ETF','ETF','GP-A-KCB')")
    List<StockInfo> getStocks();

    @Select("select * from stock_info where market in ('sh','sz') and type in('GP-A','QDII-ETF','ETF','GP-A-KCB') and code = #{code}")
    StockInfo getByCode(String code);

    @Select({"<script>",
            " SELECT ",
            " * ",
            " FROM stock_info WHERE  market in ('sh','sz') and type in('GP-A','QDII-ETF','ETF','GP-A-KCB')  and code  in ",
            "<foreach item='item' index='index' collection='codes' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            "</script>"})
    List<StockInfo> getByCodes(List<String> codes);

}