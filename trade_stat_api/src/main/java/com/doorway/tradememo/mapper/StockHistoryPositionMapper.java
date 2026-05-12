package com.doorway.tradememo.mapper;

import com.doorway.tradememo.domain.StockHistoryPosition;
import com.doorway.tradememo.domain.TradeDetail;
import com.doorway.tradememo.req.StockHistoryPositionQO;
import com.doorway.tradememo.utils.domain.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
* Created by Mybatis Generator on 2022/03/30
*/
@Mapper
@Component
public interface StockHistoryPositionMapper extends BaseMapper<StockHistoryPosition, String> {

    @Delete("delete from  stock_history_position")
    int deleteAll();

    @Delete({"<script>",
            " delete ",
            " FROM stock_history_position WHERE   code  in ",
            "<foreach item='item' index='index' collection='codes' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            "</script>"})
    int deleteByCodes(@Param("codes") List<String> codes);


    @Delete("delete from  stock_history_position where code=#{code}")
    int deleteByCode(@Param("code") String code);

    @Delete("delete from  stock_history_position where code = #{code} and  date_format( close_time , '%Y-%m-%d' ) >=#{day}")
    int deleteAfterDayByCode(@Param("code") String code, @Param("day") String day);
    @Delete("delete from  stock_history_position where  date_format( close_time , '%Y-%m-%d' ) >=#{day}")
    int deleteAfterDay( @Param("day") String day);

    List<StockHistoryPosition> queryAndOrder(@Param("detail") StockHistoryPositionQO detail, @Param("sortMap") Map<String, String> sortMap);

    @Select("select * from stock_history_position where code=#{code}")
    List<StockHistoryPosition> getByCode(@Param("code") String code);

    @Select("select * from stock_history_position where date_format(close_time,'%Y-%m-%d')<=#{day}")
    List<StockHistoryPosition> getBeforeDay(@Param("day") String day);


    @Select("select code from stock_history_position group by code")
    List<String> getCodes();

    @Select("select code,sum(hold_time) times from stock_history_position group by code")
    List<Map<String,Object>> getCodesHoldTimes();

    @Select("select code,sum(hold_time) times from stock_history_position where code=#{code} group by code")
    Map<String,Object> getCodesHoldTimesByCode(@Param("code") String code);

    @Select("select * from stock_history_position where code=#{code} and term=#{term}")
    StockHistoryPosition getByTerm(@Param("code") String code,@Param("term") String term);

//    @Select("SELECT "
//            + "a.id as 'id',a.create_date as 'createDate',a.content as 'content',"
//            + "a.parent_id as 'parentId',a.first_comment_id as 'firstCommentId',"
//            + "b.id as 'fromUser.id',b.realname as 'fromUser.realname',b.avatar as 'fromUser.avatar',"
//            + "c.id as 'toUser.id',c.realname as 'toUser.realname',c.avatar as 'toUser.avatar' "
//            + "FROM t_demand_comment a "
//            + "LEFT JOIN t_user b ON b.id = a.from_uid "
//            + "LEFT JOIN t_user c ON c.id = a.to_uid "
//            + "WHERE a.demand_id = #{demandId} "
//            + "ORDER BY a.create_date ASC"
//            + "LIMIT #{startNo},#{pageSize}")
//    public List<DemandComment> listDemandComment(@Param("demandId") Long demandId,
//　　　　　　　　　　　　　　　　　　　　　　　　　　　　　@Param("startNo") Integer pageNo,
//　　　　　　　　　　　　　　　　　　　　　　　　　　　　　@Param("pageSize") Integer pageSize);

//    @Select("<script>"
//            + "SELECT "
//            + "a.id as 'id',a.create_date as 'createDate',a.content as 'content',"
//            + "a.parent_id as 'parentId',a.first_comment_id as 'firstCommentId',"
//            + "b.id as 'fromUser.id',b.realname as 'fromUser.realname',b.avatar as 'fromUser.avatar',"
//            + "c.id as 'toUser.id',c.realname as 'toUser.realname',c.avatar as 'toUser.avatar' "
//            + "FROM t_demand_comment a "
//            + "LEFT JOIN t_user b ON b.id = a.from_uid "
//            + "LEFT JOIN t_user c ON c.id = a.to_uid "
//            + "WHERE a.demand_id = #{demandId} "
//            + "ORDER BY a.create_date ASC "
//            + "<if test='startNo!=null and pageSize != null '>"
//            + "LIMIT #{startNo},#{pageSize}"
//            + "</if>"
//            + "</script>")
//    public List<DemandComment> listDemandComment(@Param("demandId") Long demandId,
//　　　　　　　　　　　　　　　　　　　　　　　　　　　　　@Param("startNo") Integer pageNo,
//　　　　　　　　　　　　　　　　　　　　　　　　　　　　　@Param("pageSize") Integer pageSize);

}