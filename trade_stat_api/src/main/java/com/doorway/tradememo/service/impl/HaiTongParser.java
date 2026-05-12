package com.doorway.tradememo.service.impl;

import com.doorway.tradememo.domain.TradeDetail;
import com.doorway.tradememo.service.IReportParser;
import com.doorway.tradememo.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.doorway.tradememo.utils.DateUtils.DATE_TIME_FORMAT_YYYYMMDD_HH_MI_SS;

/**
 * Note
 * Author:liupz
 * Date:2022/3/26
 */
@Service("haitong")
@Slf4j
public class HaiTongParser implements IReportParser {
    @Override
    public List<TradeDetail> parseFile(File tradeFile) throws IOException {
        List<TradeDetail> tradeDetails = new ArrayList<>();


        FileInputStream fileInputStream = new FileInputStream(tradeFile);
        //获取工作簿
        Workbook workbook=new XSSFWorkbook(fileInputStream);
        //获取第一张表
        Sheet sheetAt = workbook.getSheetAt(0);
        //获取标题内容
        Row rowTitle = sheetAt.getRow(0);

        if (rowTitle!=null){
            //一定要掌握
            //获取一行里面有多少列
            int cells = rowTitle.getPhysicalNumberOfCells();
            for (int cellNum = 0; cellNum < cells ; cellNum++) {
                //获取他的每一列
                Cell cell = rowTitle.getCell(cellNum);
                //根据列获取每一个单元格的数据
                String cellValue = cell.getStringCellValue();
                System.out.print(cellValue+" | ");
            }
        }

        //获取表中的内容
        //获取总共有多少行数据
        int rows = sheetAt.getPhysicalNumberOfRows();
        for(int i=1;i<rows;i++){
            //获取第一行
            Row row = sheetAt.getRow(i);

            if (row!=null) {
                TradeDetail tradeDetail = new TradeDetail();
                tradeDetail.setId(UUID.randomUUID().toString().replaceAll("-",""));
                Cell date = row.getCell(0);
                date.setCellType(Cell.CELL_TYPE_STRING);
                Cell time = row.getCell(1);
                //交易时间
                Date tTime = DateUtils.parseStrToDate(date.toString()+" "+time.toString(),DATE_TIME_FORMAT_YYYYMMDD_HH_MI_SS);
                if(tTime == null){
                    log.info("没有找到时间");
                    continue;
                }
                tradeDetail.setTradeTime(tTime);
                //股票代码
                String code = row.getCell(2).toString();

                tradeDetail.setCode(code.replaceAll("\\.0",""));

                tradeDetail.setOpt(row.getCell(4).toString().contains("买"));
                int vol = Double.valueOf(row.getCell(5).toString()).intValue();
                tradeDetail.setVol(vol);
                if(vol<=0){
                    log.info("交易量不合法");
                    continue;
                }
                tradeDetail.setPrice(BigDecimal.valueOf(Double.parseDouble(row.getCell(6).toString())));
                tradeDetail.setCommission(BigDecimal.valueOf(Double.parseDouble(row.getCell(8).toString())));
                tradeDetail.setTax(BigDecimal.valueOf(Double.parseDouble(row.getCell(9).toString())));
                tradeDetail.setTransFee(BigDecimal.valueOf(Double.parseDouble(row.getCell(10).toString())));
                tradeDetails.add(tradeDetail);


                //获取每一行总共有多少列
//                int cells = row.getPhysicalNumberOfCells();
//                for (int cellNum = 0; cellNum < cells ; cellNum++) {
//                    System.out.print("【" + (i + 1) + "-" + (cellNum + 1) + "】");
//                    //读取第一行的第一个单元格
//                    Cell cell = row.getCell(cellNum);
//                    if (cell!=null){
//                        //获取每一个单元格的类型
//                        int cellType = cell.getCellType();
//                        String cellValue="";
//                        switch (cellType){
//                            case Cell.CELL_TYPE_STRING: //字符串
//                                System.out.print("[String]");
//                                cellValue = cell.getStringCellValue();
//                                break;
//                            case Cell.CELL_TYPE_BOOLEAN: //布尔
//                                System.out.print("[boolean]");
//                                cellValue = String.valueOf(cell.getBooleanCellValue());
//                                break;
//                            case Cell.CELL_TYPE_BLANK: //空
//                                System.out.print("[空]");
//                                break;
//                            case Cell.CELL_TYPE_NUMERIC: //数字（日期、普通数字）
//                                if (DateUtil.isCellDateFormatted(cell)){
//                                    System.out.print("[日期]");
//                                    Date date = cell.getDateCellValue();
//                                    cellValue = new DateTime(date).toString("yyyy-MM-dd");
//                                }else {
//                                    //不是日期格式，防止数字过长。
//                                    System.out.print("[转换为字符串]");
//                                    cell.setCellType(Cell.CELL_TYPE_STRING);
//                                    cellValue=cell.toString();
//                                }
//                                break;
//                            case Cell.CELL_TYPE_ERROR://
//                                System.out.print("数据类型错误");
//                                break;
//                        }
//
//                        System.out.println(cellValue);
//                    }
//
//                }

            }
        }

        //关闭流
        fileInputStream.close();
        return tradeDetails;
    }
}
