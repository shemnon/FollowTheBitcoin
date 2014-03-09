package com.shemnon.btc.ftm

import groovy.json.JsonOutput

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter

/**
 * Created by shemnon on 1 Mar 2014.
 */
class JsonBase {

    static final DateFormat JSON_DATE = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
    
    def jsonSeed
    
    String toString() {
        return ""
    }
    
    static String shortHash(String hash) {
        return "${hash[0..5]}...${hash[-5..-1]}"
    }

    String dumpJson() {
        return "{\"type\":\"${this.class.simpleName}\", \"value\":${JsonOutput.toJson(jsonSeed)}}"
    }

}
