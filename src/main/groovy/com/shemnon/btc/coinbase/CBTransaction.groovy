package com.shemnon.btc.coinbase

import com.shemnon.btc.ftm.JsonBase;

/**
 * Created by shemnon on 5 Mar 2014.
 */
public class CBTransaction extends JsonBase {

    CBTransaction(def seed) {
        jsonSeed = seed;
    }
    
    String getHash() {
        return jsonSeed.hsh
    }

    Date getCreatedAt() {
        return JSON_DATE.parse(jsonSeed.created_at)
    }
    
    String getAmount() {
        return jsonSeed.amount.amount + " " + jsonSeed.amount.currency
    }
    
    boolean isSpend() {
        return jsonSeed.amount.amount[0] == '-'
    }
}
