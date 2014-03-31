package com.shemnon.btc.bitcore;

import com.shemnon.btc.model.IAddress;
import com.shemnon.btc.model.IBlock;
import com.shemnon.btc.model.ICoin;
import com.shemnon.btc.model.ITx;

/**
 * 
 * Created by shemnon on 16 Mar 2014.
 */
public class BitcoreBase { 
    
    protected static String urlbase = "http://live.bitcore.io/api";
    
    public static void useBitCore() {
        useBitCore("http://live.bitcore.io/api");
    }
    
    public static void useBitCore(String url) {
        urlbase = url;
        IAddress.generator.set(BCAddress::query);
        IBlock.generator.set(BCBlock::query);
        ICoin.generator.set(BCCoin::query);
        ITx.generator.set(BCTx::query);
    }
    
}
