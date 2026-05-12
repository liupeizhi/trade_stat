package com.doorway.tradememo.exception;

import com.doorway.tradememo.resp.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Note
 * Author:liupz
 * Date:2022/4/6
 */

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理自定义的业务异常
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value = ServiceException.class)
    @ResponseBody
    public CommonResponse bizExceptionHandler(HttpServletRequest req, ServiceException e){
        log.error("发生业务异常！原因是：{}",e.getMessage());
        return new CommonResponse(e.getCode(),e.getMessage());
    }

    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    @ResponseBody
    public CommonResponse bizExceptionHandler(HttpServletRequest req, MissingServletRequestParameterException e){
        log.error("缺少请求必须参数！原因是：{}",e.getMessage());
        return new CommonResponse(ErrorCodeEnum.INVALID_PARAM.getCode(),ErrorCodeEnum.INVALID_PARAM.getMessage());
    }

    /**
     * 处理空指针的异常
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value =NullPointerException.class)
    @ResponseBody
    public CommonResponse exceptionHandler(HttpServletRequest req, NullPointerException e){
        log.error("发生空指针异常！原因是:",e);
        return new CommonResponse(ErrorCodeEnum.NULLPOINTER.getCode(),ErrorCodeEnum.NULLPOINTER.getMessage());
    }


    /**
     * 处理其他异常
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value =Exception.class)
    @ResponseBody
    public CommonResponse exceptionHandler(HttpServletRequest req, Exception e){
        log.error("未知异常！原因是:",e);
        return new CommonResponse(ErrorCodeEnum.INTERNAL_SERVER_ERROR.getCode(),ErrorCodeEnum.INTERNAL_SERVER_ERROR.getMessage());
    }
}

