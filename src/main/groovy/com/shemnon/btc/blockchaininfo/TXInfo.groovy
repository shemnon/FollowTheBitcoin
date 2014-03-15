/*
 * Follow the  Bitcoin 
 * Copyright (C) 2014  Danno Ferrin
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package com.shemnon.btc.blockchaininfo

import com.shemnon.btc.coinbase.CBPriceHistory
import com.shemnon.btc.ftm.JsonBase
import groovy.json.JsonSlurper

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by shemnon on 28 Feb 2014.
 */
public class TXInfo extends JsonBase {
    
    static Map<String, TXInfo> txcache = new ConcurrentHashMap<>()
    
    List<CoinInfo> inputs
    List<CoinInfo> outputs
    
    protected static final DateFormat dateFormat =  new SimpleDateFormat("yy-MM-dd HH:mm");
    
    private TXInfo(def json) {
        jsonSeed = json
        inputs =  coinbase ? [] :jsonSeed.inputs*.prev_out.collect {j -> CoinInfo.preCache(j, this)}
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
        fromJson(new JsonSlurper().parseText(jsonString))
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
    
    double getInputValueUSD() {
        CBPriceHistory.instance.getPrice(timeMs).orElse(0.0) * inputValue
    }

    long getOutputValueSatoshi() {
        jsonSeed.out*.value*.longValue()?.sum()
    }
    
    double getOutputValue() {
        outputValueSatoshi / 100000000.0
    }

    double getOutputValueUSD() {
        CBPriceHistory.instance.getPrice(timeMs).orElse(0.0) * outputValue
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
    
    double getFeePaidUSD() {
        CBPriceHistory.instance.getPrice(timeMs).orElse(0.0) * feePaid
    }
    
    BlockInfo getBlock() {
        return BlockInfo.query(blockHeight as String)
    }

    public String getHash() {
        jsonSeed.hash
    }
    
    public int getBlockHeight() {
        try {
            jsonSeed.block_height
        } catch (Exception ignore) {
            return 0; // fun fact, the genesis block doesn't have a height value on blockchain.info
        }
    }
    
    public int getTxIndex() {
        jsonSeed.tx_index
    }
    
    public List<CoinInfo> getCoins() {
        return inputs + outputs
    }
    
    public List<CoinInfo> getUnspentCoins() {
        return outputs.findAll {c -> 
            (c.getJsonSeed().containsKey('spent') && c.getJsonSeed().spent == false) || c.getTargetTX() == null}
    }
    
    public long getTimeMs() {
        return jsonSeed.time.longValue() * 1000
    }
    
    public String getTimeString() {
        Integer time = jsonSeed.time;
        if (time != null) {
            return dateFormat.format(timeMs)
        } else {
            return "?";
        }
    }
}
