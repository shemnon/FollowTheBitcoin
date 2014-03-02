package com.shemnon.btc.blockchaininfo

import groovy.json.JsonSlurper;

/**
 * Created by shemnon on 28 Feb 2014.
 */
public class TXInfo {
    
    def jsonSeed
    
    static TXInfo query(String txid) {
        URL tx = new URL("http://blockchain.info/rawtx/$txid")
        def slurper = new JsonSlurper()
        return new TXInfo(jsonSeed:slurper.parseText(tx.text))
    }
    
    List<String> getInputAddresses() {
        jsonSeed.inputs*.prev_out.addr
    }

    List<String> getOutputAddresses() {
        jsonSeed.out*.addr
    }
    
    Number getInputValue() {
        if (coinbase) {
            0 
        } else {
            jsonSeed.inputs*.prev_out?.value?.sum() / 100000000.0 
        }
    }

    boolean isCoinbase() {
        def inputs = jsonSeed.inputs
        inputs.size() == 1 && inputs[0].size() == 0
    }
    
    Number getOutputValue() {
        jsonSeed.out*.value?.sum() / 100000000.0
    }

    Number getFeePaid() {
        if (coinbase) {
            return 0;
        } else {
            inputValue - outputValue
        } 
    }
    
    BlockInfo getBlock() {
        return BlockInfo.query(jsonSeed.block_height as String)
    }
    
    def keys() {
        return jsonSeed.keySet();
    }
}
