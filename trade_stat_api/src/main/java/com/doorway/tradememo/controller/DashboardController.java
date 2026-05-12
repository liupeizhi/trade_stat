package com.doorway.tradememo.controller;

import com.doorway.tradememo.resp.CommonResponse;
import com.doorway.tradememo.service.IDashBoardService;
import com.doorway.tradememo.service.ITradeDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 首页数据
 * Author:liupz
 * Date:2022/3/26
 */
@RestController
public class DashboardController {

    @Autowired
    private IDashBoardService dashBoardService;

    @GetMapping("/dashboard")
    public CommonResponse dashboard()  {
        return new CommonResponse<>(dashBoardService.getDashBoard());
    }

}
