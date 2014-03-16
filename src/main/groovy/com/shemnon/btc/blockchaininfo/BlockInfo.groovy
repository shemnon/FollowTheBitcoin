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

package com.shemnon.btc.blockchaininfo

import com.shemnon.btc.ftm.JsonBase
import com.shemnon.btc.model.IBlock
import com.shemnon.btc.model.ITx
import groovy.json.JsonSlurper

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by shemnon on 1 Mar 2014.
 */
public class BlockInfo extends JsonBase implements IBlock {

    static Map<String, IBlock> blockcache = new ConcurrentHashMap<>()

    private BlockInfo(def json) {
        jsonSeed = json
        blockcache[jsonSeed.hash] = this
        blockcache[jsonSeed.block_index] = this
    }
    
    static BlockInfo query(String blockid) {
        BlockInfo bi = blockcache[blockid]
        if (bi == null) {
            if (blockid.length() == 64) {
                URL tx = new URL("http://blockchain.info/rawblock/$blockid")
                def slurper = new JsonSlurper()
                bi = new BlockInfo(slurper.parseText(tx.text))
            } else {
                URL tx = new URL("http://blockchain.info/block-height/$blockid?format=json")
                def slurper = new JsonSlurper()
                bi = new BlockInfo(slurper.parseText(tx.text).blocks[0])
            }                 
            
            TXInfo.preCache(bi.jsonSeed.txs)
        }
        return bi
    }

    static BlockInfo fromJson(String jsonString) {
        Map bk = new JsonSlurper().parseText(jsonString)
        fromJson(bk)
    }

    static BlockInfo fromJson(Map bk) {
        blockcache[bk.hash] ?: new BlockInfo(bk)
    }

    List<ITx> getTXs() {
        return jsonSeed.tx.collect {tx -> TXInfo.query(tx.hash) }
    }

}
