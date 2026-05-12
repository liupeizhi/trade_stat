package com.doorway.tradememo.exception;

/**
 * Note
 * Author:liupz
 * Date:2022/4/5
 */

public interface BaseErrorInfoInterface {
    /** 错误码*/
    Integer getCode();

    /** 错误描述*/
    String getMessage();
}

