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

import com.shemnon.btc.ftm.JsonBase
import com.shemnon.btc.model.IAddress
import com.shemnon.btc.model.ITx
import groovy.json.JsonSlurper

import java.util.concurrent.ConcurrentHashMap

/**
 * Created by shemnon on 1 Mar 2014.
 */
class BCAddress extends BitcoreBase implements IAddress {

    static Map<String, IAddress> addrcache = new ConcurrentHashMap<>()

    private BCAddress(def json) {
        jsonSeed = json

        addrcache[jsonSeed.addrStr] = this
    }
        
    static BCAddress query(String addr) {
        if (addrcache.containsKey(addr)) {
            return addrcache[addr]
        } else {
            checkOffThread()
            URL addrurl = new URL("$urlbase/addr/$addr")
            def slurper = new JsonSlurper()
            BCAddress ai = new BCAddress(slurper.parseText(addrurl.text))
            
            return ai
        }
    }

    static BCAddress fromJson(String jsonString) {
        Map aj = new JsonSlurper().parseText(jsonString)
        fromJson(aj)
    }

    static BCAddress fromJson(Map aj) {
        addrcache[aj.addrStr] ?: new BCAddress(aj)
    }

    List<ITx> getTXs() {
        return jsonSeed.transactions.collect {tx -> 
            return BCTx.query(tx);
        } 
    }
    
//    def getBalance() {
//        return txs.collect() { tx ->
//            tx.in
//        }
//    }
    
    String getAddress() {
        jsonSeed.addrStr
    }
    
    boolean equals(o) {
        if (o == null) return false;
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        BCAddress bcAddress = (BCAddress) o

        if (address != bcAddress.address) return false

        return true
    }

    int hashCode() {
        return (address != null ? address.hashCode() : 0)
    }
}
