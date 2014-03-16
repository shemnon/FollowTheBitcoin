package com.shemnon.btc.model;

import com.shemnon.btc.blockchaininfo.AddressInfo;

import java.util.List;

/**
 * 
 * Created by shemnon on 16 Mar 2014.
 */
public interface IAddress extends IBase {
    public static IAddress query(String hash) {
        return AddressInfo.query(hash);
    }

    List<ITx> getTXs();

    String getAddress();
}
