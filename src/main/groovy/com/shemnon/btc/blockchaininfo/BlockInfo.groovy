package com.shemnon.btc.blockchaininfo

import com.shemnon.btc.ftm.JsonBase
import groovy.json.JsonSlurper

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by shemnon on 1 Mar 2014.
 */
public class BlockInfo extends JsonBase {

    static Map<String, BlockInfo> blockcache = new ConcurrentHashMap<>()

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

    List<TXInfo> getTXs() {
        return jsonSeed.tx.collect {tx -> TXInfo.query(tx.hash) }
    }

}
