package com.shemnon.btc.model;

import com.shemnon.btc.blockchaininfo.BlockInfo;

import java.util.List;

/**
 * 
 * Created by shemnon on 16 Mar 2014.
 */
public interface IBlock extends IBase {
    public static IBlock query(String hash) {
        return BlockInfo.query(hash);
    }

    List<ITx> getTXs();
}
