package com.shemnon.btc.blockchaininfo

import com.shemnon.btc.coinbase.CBPriceHistory
import com.shemnon.btc.ftm.JsonBase

import java.util.concurrent.ConcurrentHashMap

/**
 * There is no such thing as a coin, this is an abstraction of tx input/output
 * Created by shemnon on 2 Mar 2014.
 */
class CoinInfo extends JsonBase {
    
    static Map<String, CoinInfo> coincache = new ConcurrentHashMap()
    
    static boolean btcInSummary = true;
    static boolean usdInSummary = false;
    static boolean addrInSummary = false;
    
    String compkey
    boolean toAddrChecked = false
    def targetTX
    
    private CoinInfo(def seed) {
        jsonSeed = seed
        compkey = coinKey(seed)
        coincache[compkey] = this
    }

    public static String coinKey(seed) {
        "$seed.tx_index#$seed.n"
    }

    static CoinInfo query(String coinKey) {
        if (!coincache.containsKey(coinKey)) {
            TXInfo.query(coinKey.tx_index)
            // rely on pre-cacheing to fill the result
        }
        return coincache[coinKey]
    }

    static CoinInfo preCache(def j) {
        coincache[coinKey(j)] ?: new CoinInfo(j)
    }
    
    public String getAddr() {
        return jsonSeed.addr
    }
    
    public double getValue() {
        return jsonSeed.value.longValue() / 100000000.0
    }

    public long getValueSatoshi() {
        return jsonSeed.value.longValue()
    }
    
    public double getValueUSD() {
        CBPriceHistory.instance.getPrice(sourceTX.timeMs).orElse(0.0) * value
    }
    
    public TXInfo getSourceTX() {
         return TXInfo.query(jsonSeed.tx_index)
    }
    
    public TXInfo getTargetTX() {
        if (!toAddrChecked) {
            AddressInfo addr = AddressInfo.query(addr)
            targetTX = addr.TXs.find { tx -> tx.inputs.find {coin -> coin.compkey == compkey } }
            toAddrChecked = true
        }
        return targetTX
    }
    
    public String toString() {
        if (btcInSummary) {
            return BTC_FORMAT.format(getValue())
        } else if (usdInSummary) {
            return USD_FORMAT.format(getValueUSD())
        } else if (addrInSummary) {
            return shortHash(getAddr())
        } else {
            return "";
        }
    }

}
