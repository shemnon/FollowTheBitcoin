package com.shemnon.btc.bitcore

import com.shemnon.btc.ftm.JsonBase
import com.shemnon.btc.model.IAddress
import com.shemnon.btc.model.IBlock
import com.shemnon.btc.model.ICoin
import com.shemnon.btc.model.ITx

import java.util.function.Function

/**
 * Created by shemnon on 16 Mar 2014.
 */
class BitcoreBase extends JsonBase {
    
    protected static String urlbase = "http://live.bitcore.io/api"
    
    public static void useBitCore(String url = "http://live.bitcore.io/api") {
        urlbase = url;
        IAddress.generator.set({ h -> BCAddress.query(h) } as Function<String, IAddress>);
        IBlock.generator.set({ h -> BCBlock.query(h) } as Function<String, IBlock>);
        ICoin.generator.set({ h -> BCCoin.query(h) } as Function<String, ICoin>);
        ITx.generator.set({ h -> BCTx.query(h) } as Function<String, ITx>);
    }
    
}
