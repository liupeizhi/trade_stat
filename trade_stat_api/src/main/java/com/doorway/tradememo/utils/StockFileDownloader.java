package com.doorway.tradememo.utils;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;

public class StockFileDownloader {
    public void download(String originalCode,String fromDate,String toDate,String folder) {
        try {

            String code="";
            if(originalCode.startsWith("6")) {
                code="0"+originalCode;
            }else {
                code="1"+originalCode;
            }

            String raw="http://quotes.money.163.com/service/chddata.html?code={0}&start={1}&end={2}&fields=TCLOSE;HIGH;LOW;TOPEN;LCLOSE;CHG;PCHG;TURNOVER;VOTURNOVER;VATURNOVER;TCAP;MCAP";
            Object[] arr={code,fromDate,toDate};
            String urlPath = MessageFormat.format(raw, arr);

            URL url = new URL(urlPath);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                File dir = new File(folder);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File file = new File(dir, originalCode+".txt");
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buf = new byte[1024 * 8];
                int len = -1;
                while ((len = inputStream.read(buf)) != -1) {
                    fos.write(buf, 0, len);
                }
                fos.flush();
                fos.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}