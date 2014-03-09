package com.shemnon.btc;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by shemnon on 8 Mar 2014.
 */
public class JsonBaseLabel extends JsonBase {

    public JsonBaseLabel(String label) {
        jsonSeed = ['label': label]
    }
    
    public String getLabel() {
        return jsonSeed.label
    }
}
