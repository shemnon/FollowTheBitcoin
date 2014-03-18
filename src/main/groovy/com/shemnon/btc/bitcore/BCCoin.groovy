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
import com.shemnon.btc.model.IBase
import com.shemnon.btc.model.ICoin
import com.shemnon.btc.model.ITx
import javafx.scene.text.Text

import java.util.concurrent.ConcurrentHashMap

/**
 * There is no such thing as a coin, this is an abstraction of tx input/output
 * Created by shemnon on 2 Mar 2014.
 */
class BCCoin extends BitcoreBase implements ICoin {
    
    static Map<String, BCCoin> coincache = new ConcurrentHashMap()
    
    String compkey
    
    String sourceTxID
    String spendTxID
    String sourceN
    String addr
    double value
    
    private BCCoin() {
    }

    public static String coinKey(def seed) {
        "$seed.tx#$seed.n"
    }

    static BCCoin query(String coinKey) {
        if (!coincache.containsKey(coinKey)) {
            checkOffThread()
            BCTx.query(coinKey.split('#')[0])
        }
        return coincache[coinKey]
    }

    static BCCoin cacheInput(def j, def spendTxID) {
        BCCoin ci = coincache["$j.txid#$j.vout"]
        if (ci == null) {
            ci = new BCCoin()
            ci.sourceTxID = j.txid
            ci.spendTxID = spendTxID
            ci.sourceN = j.vout
            ci.addr = j.addr
            ci.value = j.value
            ci.compkey = "$j.txid#$j.vout"
            coincache[ci.compkey] = ci
        }
        return ci;
    }
    
    static BCCoin cacheOutput(def j, def sourceTxID) {
        BCCoin ci = coincache["$sourceTxID#$j.n"]
        if (ci == null) {
            ci = new BCCoin()
            ci.sourceTxID = sourceTxID
            ci.spendTxID = j.spentTxId
            ci.sourceN = j.n
            ci.addr = j.scriptPubKey.addresses[0]
            ci.value = j.value
            ci.compkey = "$sourceTxID#$j.n"
            coincache[ci.compkey] = ci
        }
        return ci;
    }
    
    public long getValueSatoshi() {
        return value * 100000000
    }
    
    public double getValueUSD() {
        if (tx.isConfirmed()) {
            CBPriceHistory.instance.getPrice(sourceTX.timeMs).orElse(0.0) * value
        } else {
            //TODO add spot prince to CBAPI
            box.getChildren().add(new Text(" ?? "));
        }
    }
    
    public ITx getSourceTX() {
         return BCTx.query(sourceTxID)
    }
    
    public ITx getTargetTX() {
        BCTx.query(spendTxID)
    }
    
    public boolean isSpent() {
        spendTxID
    }
    
    public String toString() {
        if (showEdgeBTC.get()) {
            return BTC_FORMAT.format(getValue());
        } else if (showEdgeUSD.get()) {
            return USD_FORMAT.format(getValueUSD());
        } else if (showEdgeAddr.get()) {
            return shortHash(getAddr());
        } else {
            return "";
        }
    }

}
