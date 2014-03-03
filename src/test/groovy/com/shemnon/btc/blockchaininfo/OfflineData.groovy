package com.shemnon.btc.blockchaininfo

import groovy.json.JsonSlurper;

/**
 * Created by shemnon on 3 Mar 2014.
 */
public class OfflineData {
    
    public static void loadOfflineData() {
        URL url = OfflineData.class.getResource("offline.json")
        def data = new JsonSlurper().parseText(url.text)

        data.each {e ->
            switch (e.type) {
                case 'AddressInfo' :
                    AddressInfo.fromJson(e.value);
                    break;
                case 'BlockInfo' :
                    BlockInfo.fromJson(e.value);
                    break;
                case 'TXInfo' :
                    TXInfo.fromJson(e.value);
                    break;
                default:
                    println "Unknown type $e.type - data not added"
            }
        }
    }
}
