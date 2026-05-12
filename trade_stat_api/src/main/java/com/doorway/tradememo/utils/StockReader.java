package com.doorway.tradememo.utils;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class StockReader {
    public void readFrom(String filePathname) {
        Map<String, String> map = new TreeMap<String, String>();

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePathname), "UTF-8"));
            String line = null;
            while ((line = br.readLine()) != null) {
                String[] arr = line.split("\\t+");

                if (isStockCode(arr[0])) {
                    map.put(arr[0], arr[1]);
                }
            }
            br.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        PrintWriter out;
        try {
            out = new PrintWriter("C:\\Users\\ufo\\Desktop\\output.txt");

            int index = 0;
            for (String code : map.keySet()) {
                index++;
                String name = map.get(code);
                String raw = " insert into stock(id,code,name) values (''{0}'',''{1}'',''{2}'');";
                Object[] arr = { String.valueOf(index), code, name };
                String sql = MessageFormat.format(raw, arr);
                out.println(sql);
            }

            out.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private boolean isStockCode(String str) {
        return Pattern.matches("\\d{6}", str);
    }

    public static void main(String[] args) {
        StockReader sr = new StockReader();
        sr.readFrom("C:\\new_tdx\\T0002\\export\\沪深Ａ股20200224.txt");
    }
}