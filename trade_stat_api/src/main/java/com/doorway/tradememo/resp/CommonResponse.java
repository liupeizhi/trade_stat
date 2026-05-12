package com.doorway.tradememo.resp;

import lombok.Data;

/**
 * Note
 * Author:liupz
 * Date:2022/3/26
 */
@Data
public class CommonResponse<T> {
    private Integer code;
    private String message;


    private T data;

    public CommonResponse(){
        code = 0;
        message = "success";
    }

    public CommonResponse(Integer code,String message){
        this.code = code;
        this.message = message;
    }
    public CommonResponse(T data){
        code = 0;
        message = "success";
        this.data = data;
    }
}
