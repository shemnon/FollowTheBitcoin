package com.shemnon.btc.model;


import com.shemnon.btc.blockchaininfo.TXInfo;

import java.util.List;

/**
 * 
 * Created by shemnon on 16 Mar 2014.
 */
public interface ITx extends IBase {
    public static ITx query(String hash) {
        return TXInfo.query(hash);
    }

    String getHash();

    List<ICoin> getInputs();

    List<ICoin> getOutputs();

    double getInputValue();

    double getInputValueUSD();

    String getTimeString();

    int getBlockHeight();

    List<ICoin> getUnspentCoins();

    double getOutputValue();

    long getInputValueSatoshi();

    long getOutputValueSatoshi();
}
