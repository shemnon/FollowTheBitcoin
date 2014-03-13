/*
 * Trail of Bitcoin
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

import com.shemnon.btc.ftm.JsonBase
import groovy.json.JsonSlurper

import java.util.concurrent.ConcurrentHashMap

/**
 * Created by shemnon on 1 Mar 2014.
 */
class AddressInfo extends JsonBase {

    static Map<String, AddressInfo> addrcache = new ConcurrentHashMap<>()

    private AddressInfo(def json) {
        jsonSeed = json

        addrcache[jsonSeed.hash160] = this
        addrcache[jsonSeed.address] = this

        // unspent outputs?
        TXInfo.preCache(jsonSeed.txs)

    }
        
    static AddressInfo query(String addr) {
        if (addrcache.containsKey(addr)) {
            return addrcache[addr]
        } else {
            URL addrurl = new URL("http://blockchain.info/rawaddr/$addr")
            def slurper = new JsonSlurper()
            AddressInfo ai = new AddressInfo(slurper.parseText(addrurl.text))
            
            // unspent outputs?
            // Aggressively seed input txes in coins?
            TXInfo.preCache(ai.jsonSeed.txs)
            
            return ai
        }
    }

    static AddressInfo fromJson(String jsonString) {
        Map aj = new JsonSlurper().parseText(jsonString)
        fromJson(aj)
    }

    static AddressInfo fromJson(Map aj) {
        addrcache[aj.address] ?: new AddressInfo(aj)
    }

    List<TXInfo> getTXs() {
        return jsonSeed.txs.collect {tx -> TXInfo.query(tx.hash) }
    }
    
//    def getBalance() {
//        return txs.collect() { tx ->
//            tx.in
//        }
//    }
    
    def getAddress() {
        jsonSeed.address
    }
    
    def getHash160() {
        jsonSeed.hash160
    }
    
}
