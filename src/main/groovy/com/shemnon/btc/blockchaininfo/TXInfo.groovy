package com.shemnon.btc.blockchaininfo

import com.shemnon.btc.JsonBase
import groovy.json.JsonSlurper

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by shemnon on 28 Feb 2014.
 */
public class TXInfo extends JsonBase {
    
    static Map<String, TXInfo> txcache = new ConcurrentHashMap<>()
    
    List<CoinInfo> inputs
    List<CoinInfo> outputs
    
    private TXInfo(def json) {
        jsonSeed = json
        inputs =  coinbase ? [] :jsonSeed.inputs*.prev_out.collect {j -> CoinInfo.preCache(j)}
        outputs = jsonSeed.out.collect {j -> CoinInfo.preCache(j)}

        txcache[hash] = this
        txcache[txIndex as String] = this
    }
    
    static TXInfo query(Integer txid) {
        query(txid as String)
    }
    
    static TXInfo query(String txid) {
        if (txcache.containsKey(txid)) {
            return txcache[txid]
        } else {
            TXInfo tx = new TXInfo(new JsonSlurper().parseText(
                    new URL("http://blockchain.info/rawtx/$txid").text));
            return tx 
        }
    }
    
    static TXInfo fromJson(String jsonString) {
        Map tj = new JsonSlurper().parseText(jsonString)
        fromJson(tj)
    }
    
    static TXInfo fromJson(Map tj) {
        txcache[tj.hash] ?: new TXInfo(tj)
    }
    
    static void preCache (def txs) {
        txs.each {j -> txcache[j.hash] = new TXInfo(j)}        
    }

    boolean isCoinbase() {
        List inputs = jsonSeed.inputs
        inputs?.size() == 1 && inputs[0].size() == 0
    }

    List<String> getInputAddresses() {
        inputs*.jsonSeed*.addr
    }

    List<String> getOutputAddresses() {
        outputs*.jsonSeed*.addr
    }
    
    double getInputValue() {
        return inputValueSatoshi / 100000000.0
    }

    long getInputValueSatoshi() {
        if (coinbase) {
            0 
        } else {
            jsonSeed.inputs*.prev_out?.value*.longValue()?.sum() 
        }
    }

    long getOutputValueSatoshi() {
        jsonSeed.out*.value*.longValue()?.sum()
    }
    
    double getOutputValue() {
        outputValueSatoshi / 100000000.0
    }

    long getFeePaidSatoshi() {
        if (coinbase) {
            return 0;
        } else {
            inputValueSatoshi - outputValueSatoshi
        } 
    }
    
    double getFeePaid() {
        if (coinbase) {
            return 0;
        } else {
            inputValue - outputValue
        } 
    }
    
    BlockInfo getBlock() {
        return BlockInfo.query(blockHeight as String)
    }

    public String getHash() {
        jsonSeed.hash
    }
    
    public int getBlockHeight() {
        jsonSeed.block_height
    }
    
    public int getTxIndex() {
        jsonSeed.tx_index
    }
    
    public List<CoinInfo> getCoins() {
        return inputs + outputs
    }
}
