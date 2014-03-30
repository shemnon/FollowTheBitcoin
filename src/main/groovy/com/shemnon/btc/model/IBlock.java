package com.shemnon.btc.model;

import com.shemnon.btc.bitcore.BCBlock;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.List;
import java.util.function.Function;

/**
 * 
 * Created by shemnon on 16 Mar 2014.
 */
public interface IBlock extends IBase {

    static ObjectProperty<Function<String, IBlock>> generator = new SimpleObjectProperty<>(BCBlock::query);

    public static IBlock query(String hash) {
        return generator.get().apply(hash);
    }

    List<ITx> getTXs();
    
    String getHash();
    
    int getHeight();
}
