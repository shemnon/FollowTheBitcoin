package com.shemnon.btc.blockchaininfo;

import com.shemnon.btc.ftm.JsonBase;
import com.shemnon.btc.model.IAddress;
import com.shemnon.btc.model.IBlock;
import com.shemnon.btc.model.ICoin;
import com.shemnon.btc.model.ITx;
import groovy.json.JsonOutput;

/**
 * 
 * Created by shemnon on 17 Mar 2014.
 */
public class BlockchainInfoBase extends JsonBase {

    public static void useBlockchainInfo() {
        IAddress.generator.set(AddressInfo::query);
        IBlock.generator.set(BlockInfo::query);
        ICoin.generator.set(CoinInfo::query);
        ITx.generator.set(TXInfo::query);
    }

    public String dump() {
        return "{\"type\":\"" + getClass().getSimpleName() + "\", \"value\":" + JsonOutput.toJson(getJsonSeed()) + "}}";
    }

}
