package com.shemnon.btc.blockchaininfo;

import groovy.json.JsonSlurper;

import java.net.URL;

/**
 * Created by shemnon on 1 Mar 2014.
 */
public class BlockInfo {

    def jsonSeed

    static BlockInfo query(String blockid) {
        println " len:'${blockid.length()} block'$blockid' "
        if (blockid.length() == 64) {
            URL tx = new URL("http://blockchain.info/rawblock/$blockid")
            def slurper = new JsonSlurper()
            return new BlockInfo(jsonSeed:slurper.parseText(tx.text))
        } else {
            URL tx = new URL("http://blockchain.info/block-height/$blockid?format=json")
            def slurper = new JsonSlurper()
            return new BlockInfo(jsonSeed:slurper.parseText(tx.text).blocks[0])
        }
    }
    
    String toString() {
        println jsonSeed
    }
    
    List<TXInfo> getTXs() {
        return jsonSeed.tx.collect({tx -> new TXInfo(jsonSeed: tx)})
    }

    def keys() {
        return jsonSeed.keySet();
    }
    
}
