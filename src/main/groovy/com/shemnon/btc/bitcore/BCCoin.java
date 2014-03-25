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

package com.shemnon.btc.bitcore;

import com.shemnon.btc.coinbase.CBPriceHistory;
import com.shemnon.btc.model.IBase;
import com.shemnon.btc.model.ICoin;
import com.shemnon.btc.model.ITx;

import javax.json.JsonNumber;
import javax.json.JsonObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * There is no such thing as a coin, this is an abstraction of tx input/output
 * Created by shemnon on 2 Mar 2014.
 */
public class BCCoin extends BitcoreBase implements ICoin {
    
    static Map<String, BCCoin> coincache = new ConcurrentHashMap<>();
    
    String compkey;
    
    String sourceTxID;
    String coinbase;
    String spendTxID;
    int sourceN;
    String addr;
    double value;
    
    private BCCoin() {
    }

    public static BCCoin query(String coinKey) {
        if (coinKey == null) return null;
        if (!coincache.containsKey(coinKey)) {
            IBase.checkOffThread();
            BCTx.query(coinKey.split("#")[0]);
        }
        return coincache.get(coinKey);
    }

    static BCCoin cacheFromInput(JsonObject j, String spendTxID) {
        String txid = j.getString("txid", null);
        if (txid == null) {
            txid = "@" + j.getString("coinbase");
        }
        int n = j.getInt("vout", j.getInt("sequence", -1));
        String key = txid + "#" + n;
        BCCoin ci = coincache.get(key);
        if (ci == null) {
            ci = new BCCoin();
            if (txid.startsWith("@")) {
                ci.coinbase = txid.substring(1);
            } else {
                ci.sourceTxID = txid;
            }
            ci.spendTxID = spendTxID;
            ci.sourceN = n;
            ci.addr = j.getString("addr", null);
            try {
                JsonNumber num = j.getJsonNumber("value");
                if (num != null) {
                    ci.value = num.doubleValue();
                }
            } catch (Exception e) {
                System.out.println(j);
                e.printStackTrace();
            }
            ci.compkey = key;
            coincache.put(key, ci);
        }
        return ci;
    }
    
    static BCCoin cacheFromOutput(JsonObject j, String sourceTxID) {
        int n = j.getInt("n");
        String key = sourceTxID + "#" + n;
        BCCoin ci = coincache.get(key);
        if (ci == null) {
            ci = new BCCoin();
            ci.sourceTxID = sourceTxID;
            ci.spendTxID = j.getString("spentTxId", null);
            ci.sourceN = n;
            ci.addr = j.getJsonObject("scriptPubKey").getJsonArray("addresses").getString(0);
            try {
                ci.value = j.getJsonNumber("value").doubleValue();
            } catch (Exception e) {
                System.out.println(j);
                e.printStackTrace();
            }
            ci.compkey = key;
            coincache.put(key, ci);
        }
        return ci;
    }
    
    
    public String getAddr() {
        return addr;
    }
    
    public String getCompkey() {
        return compkey;
    }
    
    public double getValue() {
        return value;
    }
    
    public long getValueSatoshi() {
        return (long)(value * 100000000L);
    }
    
    public double getValueUSD() {
        if (getSourceTX().isConfirmed()) {
            return CBPriceHistory.getInstance().getPrice(getSourceTX().getTimeMs()).orElse(0.0) * value;
        } else {
            return -1.0;
        }
    }
    
    public String getSourceTXID() {
         return sourceTxID;
    }
    
    public String getTargetTXID() {
        return spendTxID;
    }
    
    public ITx getSourceTX() {
         return BCTx.query(sourceTxID);
    }
    
    public ITx getTargetTX() {
        return BCTx.query(spendTxID);
    }
    
    public boolean isSpent() {
        return spendTxID != null;
    }
    
    public String toString() {
        if (showEdgeBTC.get()) {
            return BTC_FORMAT.format(getValue());
        } else if (showEdgeUSD.get()) {
            return USD_FORMAT.format(getValueUSD());
        } else if (showEdgeAddr.get()) {
            return IBase.shortHash(getAddr());
        } else {
            return "";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BCCoin bcCoin = (BCCoin) o;

        return !(compkey != null ? !compkey.equals(bcCoin.compkey) : bcCoin.compkey != null);

    }

    @Override
    public int hashCode() {
        return compkey != null ? compkey.hashCode() : 0;
    }
}
