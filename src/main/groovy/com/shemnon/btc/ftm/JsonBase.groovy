package com.shemnon.btc.ftm

import groovy.json.JsonOutput

import java.text.DateFormat
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat

/**
 * Created by shemnon on 1 Mar 2014.
 */
class JsonBase {

    public static final DateFormat JSON_DATE = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
    public static final NumberFormat BTC_FORMAT = new DecimalFormat("\u0e3f###,###.### ### ###");
    public static final NumberFormat USD_FORMAT = new DecimalFormat("\$###,###,###.00");
    
    def jsonSeed
    
    String toString() {
        return ""
    }
    
    static String shortHash(String hash) {
        return "${hash[0..6]}...${hash[-4..-1]}"
    }

    String dumpJson() {
        return "{\"type\":\"${this.class.simpleName}\", \"value\":${JsonOutput.toJson(jsonSeed)}}"
    }

}
