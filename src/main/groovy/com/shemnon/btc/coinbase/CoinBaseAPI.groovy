package com.shemnon.btc.coinbase

import groovy.json.JsonSlurper;

/**
 * Created by shemnon on 5 Mar 2014.
 */
public class CoinBaseAPI {
    
    CoinBaseOAuth auth
    boolean attemptRefresh
    boolean attemptLogin

    CoinBaseAPI(CoinBaseOAuth auth, boolean attemptRefresh = true, boolean attemptLogin = true) {
        this.auth = auth
        this.attemptRefresh = attemptRefresh
        this.attemptLogin = attemptLogin
    }

    String getUserName() {
        if (auth.checkTokens(attemptRefresh, attemptLogin)) {
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
        if (auth.checkTokens(attemptRefresh, attemptLogin)) {
            URL addressesURL = new URL("https://coinbase.com/api/v1/addresses?access_token=" + auth.accessToken)
            URLConnection connection = addressesURL.openConnection()
    
            JsonSlurper slurper = new JsonSlurper()
            def results = slurper.parse(connection.inputStream.newReader())
            return results.addresses*.address.collect({j -> new CBAddress(j)})
        } else {
            return null
        }
    }

    List<CBTransaction> getTransactions() {
        if (auth.checkTokens(attemptRefresh, attemptLogin)) {
            URL addressesURL = new URL("https://coinbase.com/api/v1/transactions?access_token=" + auth.accessToken)
            URLConnection connection = addressesURL.openConnection()
    
            JsonSlurper slurper = new JsonSlurper()
            def results = slurper.parse(connection.inputStream.newReader())
            
            // for now we just care about the first page, take what is there
            return results.transactions*.transaction.collect({j -> new CBTransaction(j)})
        } else {
            return null
        }
    }
}
