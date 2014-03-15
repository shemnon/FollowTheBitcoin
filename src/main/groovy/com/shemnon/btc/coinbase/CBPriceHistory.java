/*
 * Follow the  Bitcoin
 * Copyright (C) 2014  Danno Ferrin
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package com.shemnon.btc.coinbase;

import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;
import com.shemnon.btc.ftm.JsonBase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.Optional;

/**
 * 
 * Created by shemnon on 12 Mar 2014.
 */
public class CBPriceHistory {
    RangeMap<Long, Double> priceMap = TreeRangeMap.create();
    long lastDateQueried = Long.MAX_VALUE;
    long lastPageQueried = 0;
    
    private static final CBPriceHistory INSTANCE = new CBPriceHistory();
    
    private CBPriceHistory() {}
    
    public static CBPriceHistory getInstance() {
        return INSTANCE;
    }

    private void loadPrices(long stoppingPoint) throws IOException, ParseException {
        while (lastDateQueried > stoppingPoint) { // hard coded date: 2011-07-18T00:00:00
            URL priceQuery = new URL("https://coinbase.com/api/v1/prices/historical?page=" + ++lastPageQueried);
            BufferedReader br = new BufferedReader(new InputStreamReader(priceQuery.openStream()));
            String line;
            while ((line = br.readLine()) != null) {
                String[] entry = line.split(",");
                if (entry.length == 2) {
                    Date date = JsonBase.JSON_DATE.parse(entry[0]);
                    double value = Double.parseDouble(entry[1]);
                    priceMap.put(Range.closedOpen(date.getTime(), lastDateQueried), value);
                    lastDateQueried = date.getTime();
                }
            }
        }
    }
    
    public Optional<Double> getPrice(long date) {
        try {
            loadPrices(date);
            Double price = priceMap.get(date);
            if (price == null) {
                return Optional.empty();
            } else {
                return Optional.of(price);
            }
        } catch (IOException e) {
            return Optional.empty();
        } catch (ParseException e) {
            return Optional.empty();
        }
    }
}
