package com.doorway.tradememo.controller;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 当日操作获得收益或者减少亏损就是操作成功
 * 所有操作
 * Author:liupz
 * Date:2022/8/14
 */
@Api(value = "胜率管理", tags = {"胜率管理"})
@RestController
@RequestMapping(value = "/win")
public class WinController {


}
