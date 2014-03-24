package com.shemnon.btc.model;

import javafx.application.Platform;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

/**
 * 
 * Created by shemnon on 16 Mar 2014.
 */
public interface IBase {

    public static final DateFormat JSON_DATE = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
    public static final NumberFormat BTC_FORMAT = new DecimalFormat("\u0e3f ###,##0.### ### ###");
    public static final NumberFormat USD_FORMAT = new DecimalFormat("$###,###,##0.00");


    static String shortHash(String hash) {
        int len = hash.length();
        if (len > 10) {
            return hash.substring(0, 6) + "..." + hash.substring(len-4);
        } else {
            return hash;
        }
    }
    
    public static void checkOffThread() {
        if (Platform.isFxApplicationThread()) {
            new RuntimeException("Network Query on Thread").printStackTrace();
        }
    }
    
}
