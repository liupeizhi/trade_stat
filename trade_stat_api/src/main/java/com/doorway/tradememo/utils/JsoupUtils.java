package com.doorway.tradememo.utils;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Note
 * Author:liupz
 * Date:2022/4/20
 */
@Slf4j
public class JsoupUtils {

    public static void main(String[] args) throws IOException {
        Document document = Jsoup.connect("https://stock.sohu.com/upload/ipo/new_stock_calendar.shtml").get();
        Elements codes = document.select("tr td:nth-child(2)");
        Elements days = document.select("tr td:nth-child(3)");
        List<String> newStocks = new ArrayList<>();
        for(int i=1;i<codes.size();i++){
            String day = days.get(i).text();
            String code = codes.get(i).text();
            if(day.equals("2022-04-11")) {
                newStocks.add(code);
            }
        }
    }
}
