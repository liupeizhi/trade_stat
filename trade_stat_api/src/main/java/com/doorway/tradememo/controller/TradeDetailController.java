package com.doorway.tradememo.controller;

import com.doorway.tradememo.domain.TradeDetail;
import com.doorway.tradememo.event.SettleEventSource;
import com.doorway.tradememo.resp.CommonResponse;
import com.doorway.tradememo.resp.PageResponse;
import com.doorway.tradememo.service.IReportParser;
import com.doorway.tradememo.service.ITradeDetailService;
import com.doorway.tradememo.utils.SpringContextUtil;
import com.doorway.tradememo.vo.TradePoint;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Note
 * Author:liupz
 * Date:2022/3/26
 */
@Api(value = "交易记录",tags = {"交易记录"})
@RestController
@RequestMapping(value = "/trade_details")
public class TradeDetailController {

    @Autowired
    private ITradeDetailService tradeDetailService;

    @Autowired
    SettleEventSource settleEventSource;

    @ApiOperation(value="添加交易记录")
    @PostMapping("")
    public CommonResponse<Integer> createTradeDetail(@RequestBody TradeDetail tradeDetail){

        tradeDetail.setCode(tradeDetail.getCode().substring(0, 6));

        return new CommonResponse<Integer>(tradeDetailService.save(tradeDetail));
    }
    @ApiOperation(value="批量上传交易记录")
    @PostMapping("/upload_records")
    public CommonResponse<List<TradeDetail>> uploadFile(@RequestParam("recordFile") MultipartFile file, @RequestParam("broker") String broker) throws IOException {

        File rfile = new File(Objects.requireNonNull(file.getOriginalFilename()));

        IReportParser fileParser = SpringContextUtil.getApplicationContext().getBean(broker, IReportParser.class);

        FileUtils.copyInputStreamToFile(file.getInputStream(), rfile);

        return new CommonResponse<List<TradeDetail>>(tradeDetailService.saveBatch(fileParser.parseFile(rfile)));

    }




    /**
     * 获取某只股票的操作点
     * @param code
     * @return
     */
    @ApiOperation(value="获取某只股票的操作点")
    @GetMapping("/trade_points")
    public CommonResponse<List<TradePoint>> getTradePoint(@RequestParam("code") String code, @RequestParam(value = "term",required = false) String term) {
        return new CommonResponse<>(tradeDetailService.getTradePointByTerm(code,term));
    }



    /**
     * 查询交易记录
     * @param code
     * @return
     */
    @ApiOperation(value="查询交易记录")
    @GetMapping("/raw_query")
    public PageResponse<TradeDetail> query(@RequestParam(value = "code",required = false) String code,
                                           @RequestParam(value = "startTime",required = false) Date startTime,
                                           @RequestParam(value = "endTime",required = false) Date endTime,
                                           @RequestParam(value = "opt",required = false) Integer opt,
                                           @RequestParam(value = "clear",required = false) Integer clear,
                                           @RequestParam(value = "pageSize", required = false, defaultValue = "10")
                                    @ApiParam(value = "每页记录数")
                                            Integer pageSize,
                                           @RequestParam(value = "pageNo", required = false, defaultValue = "1")
                                    @ApiParam(value = "第几页")
                                            Integer pageNo,
                                           @RequestParam(value = "field", required = false, defaultValue = "trade_time")
                                    @ApiParam(value = "根据哪个字段排序")
                                            String sortField,
                                           @RequestParam(value = "order", required = false, defaultValue = "desc")
                                    @ApiParam(value = "如何排序，asc:升序，desc：降序")
                                            String sortOrder) {
        Map<String, String> sortMap = new HashMap<>();
        if(StringUtils.hasLength(sortField)&&StringUtils.hasLength(sortOrder)) {
            sortMap.put(sortField, sortOrder);
        }else{
            sortMap.put("created_time", "desc");
        }

        return tradeDetailService.queryTrades(code,startTime,endTime,opt,clear,pageSize,pageNo,sortMap);
    }





}
