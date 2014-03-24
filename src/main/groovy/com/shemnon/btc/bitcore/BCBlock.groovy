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
import com.shemnon.btc.model.IBlock
import com.shemnon.btc.model.ITx
import groovy.json.JsonSlurper

import java.util.concurrent.ConcurrentHashMap

/**
 * Created by shemnon on 1 Mar 2014.
 */
public class BCBlock extends BitcoreBase implements IBlock {

    static Map<String, IBlock> blockcache = new ConcurrentHashMap<>()
    
    private BCBlock(def json) {
        jsonSeed = json
        blockcache[jsonSeed.hash] = this
        blockcache[jsonSeed.height] = this
    }
    
    static BCBlock query(String blockid) {
        BCBlock bi = blockcache[blockid]
        if (bi == null) {
            checkOffThread()
            bi = new BCBlock(new JsonSlurper().parseText(
                    new URL("$urlbase/block/$blockid").text));
        }
        return bi
    }

    static BCBlock fromJson(String jsonString) {
        Map bk = new JsonSlurper().parseText(jsonString)
        fromJson(bk)
    }

    static BCBlock fromJson(Map bk) {
        blockcache[bk.hash] ?: new BCBlock(bk)
    }

    List<ITx> getTXs() {
        return jsonSeed.tx.collect {tx -> BCTx.query(tx) }
    }
    
    int getHeight() {
        return jsonSeed.height
    }
    
    String getHash() {
        return jsonSeed.hash
    }

    boolean equals(o) {
        if (o == null) return false;
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        BCBlock bcBlock = (BCBlock) o

        if (hash != bcBlock.hash) return false

        return true
    }

    int hashCode() {
        return (hash != null ? hash.hashCode() : 0)
    }
}
