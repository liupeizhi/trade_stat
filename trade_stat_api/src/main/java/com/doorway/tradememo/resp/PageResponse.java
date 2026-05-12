package com.doorway.tradememo.resp;

import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;


@Data
public class PageResponse<T>  {

    private Integer code;
    private String message;

    private Long total;
    private List<T> data;
    private Integer pageNo;

    public PageResponse(List<T> data) {
        super();
        this.code=0;
        this.data = data;

    }
    public PageResponse(List<T> data,Integer pageNo,Long total) {
        super();
        this.code=0;
        this.data = data;
        this.pageNo = pageNo;
        this.total = total;

    }

    public PageResponse(List<?> data, Class<T> clazz) {
        super();
        this.code=0;
        List<T> content = new ArrayList<>();
        if (data != null) data.forEach(o -> {
            T inst = null;
            try {
                inst = clazz.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            BeanUtils.copyProperties(o, inst);
            content.add(inst);
        });

        this.data = content;

    }

    public PageResponse(PageResponse<?> data, Class<T> clazz) {
        super();
        this.code=0;
        List<T> content = new ArrayList<>();
        if (data != null) data.getData().forEach(o -> {
            T inst = null;
            try {
                inst = clazz.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            BeanUtils.copyProperties(inst, o);

            content.add(inst);
        });

        this.data = content;
        this.pageNo = data.getPageNo();
        this.total = data.getTotal();


    }
}
