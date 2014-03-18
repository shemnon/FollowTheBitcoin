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

package com.shemnon.btc.bitcore

import com.shemnon.btc.coinbase.CBPriceHistory
import com.shemnon.btc.model.IBlock
import com.shemnon.btc.model.ICoin
import com.shemnon.btc.model.ITx
import groovy.json.JsonSlurper

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by shemnon on 28 Feb 2014.
 */
public class BCTx extends BitcoreBase implements ITx {
    
    static Map<String, BCTx> txcache = new ConcurrentHashMap<>()
    
    List<ICoin> inputs
    List<ICoin> outputs
    
    protected static final DateFormat dateFormat =  new SimpleDateFormat("yy-MM-dd HH:mm");
    
    private BCTx(def json) {
        jsonSeed = json
        inputs =  coinbase ? [] :jsonSeed.vin.collect {j -> BCCoin.cacheInput(j, hash)}
        outputs = jsonSeed.vout.collect {j -> BCCoin.cacheOutput(j, hash)}

        txcache[hash] = this
    }
    
    static BCTx query(String txid) {
        if (txid == null) {
            return null
        } else if (txcache.containsKey(txid)) {
            return txcache[txid]
        } else {
            checkOffThread()
            BCTx tx = new BCTx(new JsonSlurper().parseText(
                    new URL("$urlbase/tx/$txid").text));
            return tx
        }
    }
    
    static BCTx fromJson(String jsonString) {
        fromJson(new JsonSlurper().parseText(jsonString))
    }
    
    static BCTx fromJson(Map tj) {
        txcache[tj.txid] ?: new BCTx(tj)
    }
    
    boolean isCoinbase() {
        jsonSeed.isCoinBase
    }

    List<String> getInputAddresses() {
        inputs*.jsonSeed*.addr
    }

    List<String> getOutputAddresses() {
        outputs*.jsonSeed*.scriptPubKey*.addresses
    }
    
    double getInputValue() {
        jsonSeed.valueIn ?: 0
    }

    long getInputValueSatoshi() {
        inputValue * 100000000 
    }
    
    double getInputValueUSD() {
        CBPriceHistory.instance.getPrice(timeMs).orElse(0.0) * inputValue
    }

    long getOutputValueSatoshi() {
        outputValue * 100000000
    }
    
    double getOutputValue() {
        jsonSeed.valueOut
    }

    double getOutputValueUSD() {
        CBPriceHistory.instance.getPrice(timeMs).orElse(0.0) * outputValue
    }


    long getFeePaidSatoshi() {
        feePaid * 100000000
    }
    
    double getFeePaid() {
        jsonSeed.fees ?: 0
    }
    
    double getFeePaidUSD() {
        CBPriceHistory.instance.getPrice(timeMs).orElse(0.0) * feePaid
    }
    
    IBlock getBlock() {
        BCBlock.query(jsonSeed.blockhash)
    }

    public String getHash() {
        jsonSeed.txid
    }
    
    public int getBlockHeight() {
        block.height
    }
    
    public List<ICoin> getCoins() {
        return inputs + outputs
    }
    
    public List<ICoin> getUnspentCoins() {
        return outputs.findAll {c -> !c.spent }
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
