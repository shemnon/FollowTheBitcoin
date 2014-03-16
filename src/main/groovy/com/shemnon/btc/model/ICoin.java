package com.shemnon.btc.model;

import com.shemnon.btc.blockchaininfo.CoinInfo;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * 
 * Created by shemnon on 16 Mar 2014.
 */
public interface ICoin extends IBase {

    static BooleanProperty showEdgeBTC = new SimpleBooleanProperty(true);
    static BooleanProperty showEdgeUSD = new SimpleBooleanProperty(true);
    static BooleanProperty showEdgeAddr = new SimpleBooleanProperty(true);
    
    public static void setShowEdgeBTC(boolean set) {
        showEdgeBTC.set(set);
    }

    public static void setShowEdgeUSD(boolean set) {
        showEdgeUSD.set(set);
    }

    public static void setShowEdgeAddr(boolean set) {
        showEdgeAddr.set(set);
    }

    public static ICoin query(String hash) {
        return CoinInfo.query(hash);
    }

    String getCompkey();

    ITx getSourceTX();

    ITx getTargetTX();

    String getAddr();

    double getValueUSD();

    double getValue();

    long getValueSatoshi();
}
