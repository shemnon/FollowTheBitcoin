package com.shemnon.btc.analysis;

import com.shemnon.btc.model.ICoin;
import com.shemnon.btc.model.ITx;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * Created by shemnon on 18 Mar 2014.
 */
public class Pyramid {
    
    
    public static boolean isPyramidStep(ITx tx) {
        // idiot check
        if (tx == null) return false;
        
        // pyramid steps must have:
        // * 1 input
        // * 2 or more outputs
        // * each output has no worse than a 2:1 to each other output
        
        if (tx.getInputs().size() > 1) return false;
        
        List<ICoin> outputs = tx.getOutputs();
        if (outputs.size() < 2) return false;
        
        for (int i = 0; i < outputs.size(); i++) {
            for (int j = i+1; j < outputs.size(); j++) {
                double ratio = outputs.get(i).getValue() / outputs.get(j).getValue();
                if (ratio < 0.5 || ratio > 2.0) return false;
            }
        }
        
        return true;
    }
    
    public static List<ITx> climbPyramid(ITx tx, int limit) {
        List<ITx> upwards = new ArrayList<>();
        ITx step = tx;
        while (step != null && (limit-- > 0)) {
            if (!isPyramidStep(step)) break;
            upwards.add(step);
            step = step.getInputs().get(0).getSourceTX();
        }
        return upwards;
    }

    public static List<ITx> descendPyramid(ITx tx, int limit) {
        Deque<ITx> parents = new LinkedList<>();
        parents.add(tx);
        parents.add(null);
        List<ITx> results = new ArrayList<>();
        if (isPyramidStep(tx)) {
            results.add(tx);
        }
        while (!parents.isEmpty()) {
            ITx parent = parents.removeFirst();
            if (parent == null) {
                // null is the full row marker
                if (limit-- < 1) {
                    break;
                }
                parents.addLast(null);
            } else {
                parent.getOutputs().forEach(coin -> {
                            ITx otx = coin.getTargetTX();
                            if (Pyramid.isPyramidStep(otx)) {
                                results.add(otx);
                                parents.addLast(otx);
                            }
                        }
                );
            }
        }
        return results;        
    }
}
