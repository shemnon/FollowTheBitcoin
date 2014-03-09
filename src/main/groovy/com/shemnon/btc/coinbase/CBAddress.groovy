package com.shemnon.btc.coinbase

import com.shemnon.btc.JsonBase;

/**
 * Created by shemnon on 5 Mar 2014.
 */
public class CBAddress extends JsonBase {

    CBAddress(def seed) {
        jsonSeed = seed;
    }
    
    String getLabel() {
        return jsonSeed.label
    }

    String getAddress() {
        return jsonSeed.address
    }

    Date getCreatedAt() {
        if (jsonSeed.created_at) {
            return JSON_DATE.parse(jsonSeed.created_at)
        } else {
            return null;
        }
    }
}
