package com.doorway.tradememo.service;


import com.doorway.tradememo.domain.TradeDetail;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 批量导入交易记录
 * Author:liupz
 * Date:2022/3/26
 */

public interface IReportParser {

    List<TradeDetail> parseFile(File tradeFile) throws IOException;

}
