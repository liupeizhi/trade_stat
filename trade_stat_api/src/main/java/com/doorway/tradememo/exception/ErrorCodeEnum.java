package com.doorway.tradememo.exception;

/**
 * Note
 * Author:liupz
 * Date:2022/4/5
 */
public enum ErrorCodeEnum implements BaseErrorInfoInterface{

    // 数据操作错误定义
    SUCCESS(0, "成功!"),
    NULLPOINTER(1,"空指针异常!"),
    INVALID_PARAM(2,"参数不合法!"),
    DUPLICATE_RECORD(3,"存在重复记录!"),
    NOT_FOUND(4, "未找到该资源!"),
    INTERNAL_SERVER_ERROR(5, "服务器内部错误!"),
    ERROR_RECORDS(7,"数据不正确"),
    SERVER_BUSY(6,"服务器正忙，请稍后再试!");

    /** 错误码 */
    private Integer code;

    /** 错误描述 */
    private String message;

    ErrorCodeEnum(Integer resultCode, String resultMsg) {
        this.code = resultCode;
        this.message = resultMsg;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
