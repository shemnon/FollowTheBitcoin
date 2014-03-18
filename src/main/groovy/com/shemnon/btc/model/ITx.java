package com.shemnon.btc.model;


import com.shemnon.btc.bitcore.BCTx;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.List;
import java.util.function.Function;

/**
 * 
 * Created by shemnon on 16 Mar 2014.
 */
public interface ITx extends IBase {
    static ObjectProperty<Function<String, ITx>> generator = new SimpleObjectProperty<>(BCTx::query);

    public static ITx query(String hash) {
        return generator.get().apply(hash);
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
