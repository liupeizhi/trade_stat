package com.doorway.tradememo.event;

import lombok.Data;

/**
 * Note
 * Author:liupz
 * Date:2022/4/3
 */
@Data
public class SettleEvent {
    private String day;
    private String code;
}
