package com.shemnon.btc.model;

import com.shemnon.btc.bitcore.BCAddress;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.List;
import java.util.function.Function;

/**
 * 
 * Created by shemnon on 16 Mar 2014.
 */
public interface IAddress extends IBase {
    
    static final String BITCOIN_ADDRESS_REGEX = "[abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ123456789]{27,35}";
    
    static ObjectProperty<Function<String, IAddress>> generator = new SimpleObjectProperty<>(BCAddress::query);
    
    public static IAddress query(String hash) {
        return generator.get().apply(hash);
    }

    List<ITx> getTXs();

    String getAddress();
}
