package com.shemnon.btc.model;

import com.shemnon.btc.bitcore.BCCoin;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.function.Function;

/**
 * 
 * Created by shemnon on 16 Mar 2014.
 */
public interface ICoin extends IBase {

    static ObjectProperty<Function<String, ICoin>> generator = new SimpleObjectProperty<>(BCCoin::query);

    public static ICoin query(String hash) {
        return generator.get().apply(hash);
    }

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

    String getCompkey();

    ITx getSourceTX();

    ITx getTargetTX();

    String getAddr();

    double getValueUSD();

    double getValue();

    long getValueSatoshi();
    
    boolean isSpent();
}
