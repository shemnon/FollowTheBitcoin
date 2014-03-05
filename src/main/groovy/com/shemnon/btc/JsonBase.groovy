package com.shemnon.btc

import groovy.json.JsonOutput

/**
 * Created by shemnon on 1 Mar 2014.
 */
class JsonBase {
    
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
