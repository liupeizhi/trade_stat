package com.doorway.tradememo.mapper;

import com.doorway.tradememo.domain.StockHistoryPosition;
import com.doorway.tradememo.domain.TradeDetail;
import com.doorway.tradememo.req.StockHistoryPositionQO;
import com.doorway.tradememo.utils.domain.BaseMapper;
import com.doorway.tradememo.vo.CapitalFlowVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Mybatis Generator on 2022/03/29
 */
@Mapper
@Component
public interface TradeDetailMapper extends BaseMapper<TradeDetail, String> {

    @Select("select code from trade_detail group by code")
    List<String> getAllCodes();

    @Select("select sum(price*vol+tax+commission+trans_fee) flow,sum(commission) commission,sum(trans_fee) trans_fee,sum(tax) tax from trade_detail")
    Map<String,Object> getAllFlow();

    @Select("select sum(price*vol+tax+commission+trans_fee) from trade_detail where opt=#{opt}")
    BigDecimal getAllFlowByOpt(@Param("opt")Boolean opt);

    @Select("select count(1) from trade_detail")
    Long getAllTradeCount();

    @Select("select code,sum(price*vol+tax+commission+trans_fee) flow,sum(tax) tax,sum(commission) commission,sum(trans_fee) trans_fee from trade_detail group by code")
    List<Map<String,Object>> getAllStockFlow();

    @Select("select code,sum(price*vol+tax+commission+trans_fee) flow  from trade_detail where opt=#{opt} group by code ")
    List<Map<String,Object>> getAllStockFlowByOpt(@Param("opt")Boolean opt);


    @Select("select sum(price*vol+tax+commission+trans_fee) flow,sum(tax) tax,sum(commission) commission,sum(trans_fee) trans_fee from trade_detail where day<=#{end} and day>=#{start}")
    Map<String,Object> getRangeFlow(@Param("start")String start, @Param("end")String end);

    @Select("select sum(price*vol+tax+commission+trans_fee) flow  from trade_detail where day<=#{end} and day>=#{start} and opt=#{opt}")
    Map<String,Object> getRangeFlowByOpt(@Param("start")String start, @Param("end")String end,@Param("opt")Boolean opt);


    @Select("select count(1) from trade_detail where day<=#{end} and day>=#{start}")
    Long getRangeTradeTimes(@Param("start")String start, @Param("end")String end);


    @Select("select code,count(1) times  from trade_detail group by code order by times desc")
    List<Map<String,Object>> getAllStockTradeCounts();


    List<CapitalFlowVO> getSumStockFlows(@Param("period") String period, @Param("code") String code, @Param("start") String start, @Param("end") String end,@Param("opt") Boolean opt);

    List<CapitalFlowVO> getDistSumStockFlows(@Param("period") String period, @Param("code") String code, @Param("start") String start, @Param("end") String end,@Param("opt") Boolean opt);


    List<Map<String,Object>> getTradeTimes(@Param("period") String period,@Param("code") String code,@Param("start") String start,@Param("end") String end);

    @Select("select * from trade_detail where code = #{code} order by trade_time asc")
    List<TradeDetail> getByCode(@Param("code") String code);


    @Select({"<script>",
            " SELECT ",
            " * ",
            " FROM trade_detail WHERE   code  in ",
            "<foreach item='item' index='index' collection='codes' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            "</script>"})
    List<TradeDetail> getByCodes(@Param("codes") List<String> codes);


    @Select("select * from trade_detail where row_key=#{rowKey}")
    TradeDetail findByKey(@Param("rowKey") String rowKey);

    @Select("<script>"
            + "SELECT * FROM trade_detail WHERE row_key IN "
            + "<foreach item='item' index='index' collection='rowKeys' open='(' separator=',' close=')'>"
            + "#{item}"
            + "</foreach>"
            + "</script>")
    List<TradeDetail> findByKeys(@Param("rowKeys") Set<String> rowKeys);



    @Select("select * from trade_detail where day <= #{day} and code=#{code} order by trade_time asc")
    List<TradeDetail> getTradesLessThanDayAndCode(@Param("day") String day, @Param("code") String code);

    @Select("select * from trade_detail order by trade_time asc")
    List<TradeDetail> getAllTrades();

    @Select("select * from trade_detail where code = #{code} and trade_time <= #{tradeTime} and term is null order by trade_time asc")
    List<TradeDetail> getTradesEmpty(@Param("code") String code, @Param("tradeTime") Date tradeTime);


    @Select("select * from trade_detail where term is null order by trade_time asc")
    List<TradeDetail> getNoClearTrades();

    @Select("select * from trade_detail where  code = #{code} and term is null order by trade_time asc")
    List<TradeDetail> getNoClearTradesByCode(@Param("code") String code);

    @Select("select * from trade_detail where day <= #{day} order by trade_time asc")
    List<TradeDetail> getTradesEmptyDay(@Param("day") String day);


    List<TradeDetail> queryAndOrder(@Param(value = "code") String code,
                                             @Param(value = "startTime") Date startTime,
                                             @Param(value = "endTime") Date endTime,
                                             @Param(value = "opt") Integer opt,
                                             @Param(value = "clear") Integer clear, @Param("sortMap") Map<String, String> sortMap);


    /**
     * 获得特定时间之前的特定股票的交易记录
     * @param tradeTime
     * @param code
     * @return
     */
    @Select("select * from trade_detail where trade_time <= #{tradeTime} and code =  #{code}")
    List<TradeDetail> getTradesBeforeDayAndCode(@Param("tradeTime") Date tradeTime, @Param("code") String code);

    @Select("select * from trade_detail where trade_time >= #{tradeTime} and code =  #{code}")
    List<TradeDetail> getTradesAfterDayAndCode(@Param("tradeTime") Date tradeTime, @Param("code") String code);

    @Select("select * from trade_detail where trade_time >= #{tradeTime}")
    List<TradeDetail> getTradesAfterDay(@Param("tradeTime") Date tradeTime);


    @Select("select * from trade_detail where day <= #{end} and day >=  #{start} and code = #{code} order by trade_time asc")
    List<TradeDetail> getTradesBetweenDays(@Param("start") String start,@Param("end") String end, @Param("code") String code);


    @Select("select * from trade_detail where term = #{term}")
    List<TradeDetail> getTradesByTerm(@Param("term") String term);


}