package com.doorway.tradememo.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Note
 * Author:liupz
 * Date:2022/3/28
 */
@Data
@AllArgsConstructor
public class Stock {
    private Integer id;
    private String code;
    private String name;
}
