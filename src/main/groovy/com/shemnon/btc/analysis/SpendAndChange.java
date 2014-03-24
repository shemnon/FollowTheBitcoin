/*
 *  Follow the Bitcoin Analysis
 *  Copyright (C) 2014 Danno Ferrin
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.shemnon.btc.analysis;

import com.shemnon.btc.model.ICoin;
import com.shemnon.btc.model.ITx;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Created by shemnon on 18 Mar 2014.
 */
public class SpendAndChange {

    public static boolean isSpendAndChange(ITx tx) {
        // idiot check
        if (tx == null) return false;

        // Spend and change must have:
        // * 2 outputs
        // * one output is 10x more than the other

        List<ICoin> outputs = tx.getOutputs();
        if (outputs.size() != 2) return false;

        for (int i = 0; i < outputs.size(); i++) {
            for (int j = i+1; j < outputs.size(); j++) {
                double ratio = outputs.get(i).getValue() / outputs.get(j).getValue();
                if (ratio > 0.1 && ratio < 10.0) return false;
            }
        }

        return true;
    }

    public static List<ITx> climbSpendAndChange(ITx tx, int limit) {
        List<ITx> upwards = new ArrayList<>();
        ITx step = tx;
        while (step != null && (limit-- > 0)) {
            if (!isSpendAndChange(step)) break;
            upwards.add(step);
            ICoin coin = step.getInputs().stream().reduce((c1, c2) -> {
                try {
                    if (c1.getValue() > c2.getValue()) {
                        return c1;
                    } else {
                        return c2;
                    }
                } catch (Throwable e) {
                    System.out.println(":((");
                    e.printStackTrace();
                    throw e;
                }
            }).orElseGet(() -> {
                try {
                    return tx.getOutputs().get(0);
                } catch (Throwable e) {
                    System.out.println(":((");
                    e.printStackTrace();
                    return null;
                }
            });
            step = coin.getSourceTX();
            System.out.println("limit=" + limit);
            System.out.print(".");
        }
        System.out.println();
        System.out.println(":)");
        return upwards;
    }

    public static List<ITx> descendSpendAndChange(ITx tx, int limit) {
        try {
            List<ITx> downwards = new ArrayList<>();
            ITx step = tx;
            System.out.println(":)");
            while (step != null && (limit-- > 0)) {
                if (!isSpendAndChange(step)) break;
                downwards.add(step);
                ICoin coin = step.getOutputs().stream().reduce((c1, c2) -> {
                    try {
                        if (c1.getValue() > c2.getValue()) {
                            return c1;
                        } else {
                            return c2;
                        }
                    } catch (Throwable e) {
                        System.out.println(":((");
                        e.printStackTrace();
                        throw e;
                    }
                }).orElseGet(() -> {
                    try {
                        return tx.getOutputs().get(0);
                    } catch (Throwable e) {
                        System.out.println(":((");
                        e.printStackTrace();
                        return null;
                    }
                });
                step = coin.getTargetTX();
                System.out.print(".");
            }
            System.out.println("limit=" + limit);
            return downwards;
        } catch (Throwable e) {
            System.out.println(":(");
            e.printStackTrace();
            throw e;
        }
    }
    
}
