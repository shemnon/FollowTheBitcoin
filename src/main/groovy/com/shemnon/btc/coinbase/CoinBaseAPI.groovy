package com.shemnon.btc.coinbase

import groovy.json.JsonSlurper;

/**
 * Created by shemnon on 5 Mar 2014.
 */
public class CoinBaseAPI {
    
    CoinBaseOAuth auth
    boolean attemptReAuth

    CoinBaseAPI(CoinBaseOAuth auth, boolean attemptReAuth = true) {
        this.auth = auth
        this.attemptReAuth = attemptReAuth
    }

    String getUserName() {
        if (auth.checkTokens(attemptReAuth)) {
            URL addressesURL = new URL("https://coinbase.com/api/v1/users?access_token=" + auth.accessToken)
            URLConnection connection = addressesURL.openConnection()

            JsonSlurper slurper = new JsonSlurper()
            def results = slurper.parse(connection.inputStream.newReader())
            return results.users[0].user.name
        } else {
            return null
        }
    }
    
    List<CBAddress> getAddresses() {
        if (auth.checkTokens(attemptReAuth)) {
            URL addressesURL = new URL("https://coinbase.com/api/v1/addresses?access_token=" + auth.accessToken)
            URLConnection connection = addressesURL.openConnection()
    
            JsonSlurper slurper = new JsonSlurper()
            def results = slurper.parse(connection.inputStream.newReader())
            return results.addresses.collect({j -> new CBAddress(j)})
        } else {
            return null
        }
    }
}
