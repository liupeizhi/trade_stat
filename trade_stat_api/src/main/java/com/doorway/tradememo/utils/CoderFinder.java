package com.doorway.tradememo.utils;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class CoderFinder {
    List<String> codeList;

    public CoderFinder(String filePathname) {
        codeList=new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePathname), "UTF-8"));
            String line = null;
            while ((line = br.readLine()) != null) {
                String[] arr = line.split("\\t+");

                if(isStockCode(arr[0])) {
                    codeList.add(arr[0]);
                }
            }
            br.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static boolean isStockCode(String code) {
        final String patternStr = "\\d+";
        return Pattern.matches(patternStr, code);
    }

    public List<String> getCodeList() {
        return codeList;
    }

    public static void main(String[] args) {
        long startMs=System.currentTimeMillis();

        CoderFinder cf=new CoderFinder("/Users/liupeizhi/Downloads/StockDataDownloader/stockcodes.txt");
        List<String> ls=cf.getCodeList();
        Collections.sort(ls);

        StockFileDownloader downloader=new StockFileDownloader();

        int index=0;
        for(String code:cf.getCodeList()) {
            downloader.download(code, "20200401", "20200401", "./");
            System.out.println(String.format("#%d %s downloaded.", index,code));
            index++;
        }

        long endMs=System.currentTimeMillis();
        System.out.println("Time elapsed:"+ms2DHMS(startMs,endMs));
    }

    /**
     * change seconds to DayHourMinuteSecond format
     *
     * @param startMs
     * @param endMs
     * @return
     */
    private static String ms2DHMS(long startMs, long endMs) {
        String retval = null;
        long secondCount = (endMs - startMs) / 1000;
        String ms = (endMs - startMs) % 1000 + "ms";

        long days = secondCount / (60 * 60 * 24);
        long hours = (secondCount % (60 * 60 * 24)) / (60 * 60);
        long minutes = (secondCount % (60 * 60)) / 60;
        long seconds = secondCount % 60;

        if (days > 0) {
            retval = days + "d" + hours + "h" + minutes + "m" + seconds + "s";
        } else if (hours > 0) {
            retval = hours + "h" + minutes + "m" + seconds + "s";
        } else if (minutes > 0) {
            retval = minutes + "m" + seconds + "s";
        } else if(seconds > 0) {
            retval = seconds + "s";
        }else {
            return ms;
        }

        return retval + ms;
    }
}