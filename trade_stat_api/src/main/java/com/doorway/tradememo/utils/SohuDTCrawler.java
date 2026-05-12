package com.doorway.tradememo.utils;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SohuDTCrawler {
    private List<DayTransact> dtList;

    public List<DayTransact> getDtList(){
        return dtList;
    }

    public void download(String originalCode,String name,String fromDate,String toDate) {
        dtList=new ArrayList<>();
        try {
            Document doc=Jsoup.connect(getReqUrl(originalCode,fromDate,toDate)).ignoreContentType(true)
                    .data("query", "Java")
                    .userAgent("Mozilla")
                    .cookie("auth", "token")
                    .timeout(30000)
                    .get();
            String rawText=doc.text();
            String json=rawText.substring(22, rawText.length()-2);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(json);

            JsonNode listNode=node.path("hq");
            Iterator<JsonNode> iterator = listNode.elements();

            while (iterator.hasNext()) {
                JsonNode transNode = iterator.next();

                DayTransact dt=new DayTransact();
                dt.setCode(originalCode);
                dt.setName(name);
                dt.setDay(transNode.get(0).asText());
                dt.setTopen(Double.parseDouble(transNode.get(1).asText()));
                dt.setTclose(Double.parseDouble(transNode.get(2).asText()));
                dt.setChg(Double.parseDouble(transNode.get(3).asText()));
                dt.setPchg(Double.parseDouble(transNode.get(4).asText().replace("%", "")));
                dt.setLow(Double.parseDouble(transNode.get(5).asText()));
                dt.setHigh(Double.parseDouble(transNode.get(6).asText()));
                dt.setVoturnover(Long.parseLong(transNode.get(7).asText()));
                dt.setVaturnover(Double.parseDouble(transNode.get(8).asText()));
                dt.setTurnover(Double.parseDouble(transNode.get(9).asText().replace("%", "")));

                dtList.add(dt);
            }
        }catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private String getReqUrl(String code,String startDate,String endDate) {
        return "http://q.stock.sohu.com/hisHq?code=cn_"+code+"&start="+startDate+"&end="+endDate+"&stat=1&order=D&period=d&callback=historySearchHandler&rt=jsonp";
    }

    public static void main(String[] args) {
        SohuDTCrawler n=new SohuDTCrawler();
        n.download("002101","广东鸿图", "20200401", "20200410");

        for(DayTransact dt:n.getDtList()) {
            System.out.println(dt);
        }
    }
}