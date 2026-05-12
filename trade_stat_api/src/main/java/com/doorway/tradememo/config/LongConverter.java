package com.doorway.tradememo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 */
/*@Deprecated*/
public class LongConverter implements Converter<String, Long> {

    @Override
    public Long convert(String value) {

        if(value == null || value.trim().equals("") || value.equalsIgnoreCase("null")) {
            return null;
        }

        value = value.trim();

        try {
           return Long.parseLong(value);
        } catch (Exception e) {
            throw new RuntimeException(String.format("parser %s to Long fail", value));
        }
    }
}
