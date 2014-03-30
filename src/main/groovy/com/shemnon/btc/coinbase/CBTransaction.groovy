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

package com.shemnon.btc.coinbase

import com.shemnon.btc.ftm.JsonBase
import com.shemnon.btc.model.IBase
import groovy.json.JsonOutput;

/**
 * Created by shemnon on 5 Mar 2014.
 */
public class CBTransaction extends JsonBase implements IBase {

    CBTransaction(def seed) {
        jsonSeed = seed;
    }
    
    String getHash() {
        return jsonSeed.hsh
    }

    Date getCreatedAt() {
        return JSON_DATE.parse(jsonSeed.created_at)
    }
    
    String getAmount() {
        return jsonSeed.amount.amount + " " + jsonSeed.amount.currency
    }
    
    boolean isSpend() {
        return jsonSeed.amount.amount[0] == '-'
    }
    
    String getNotes() {
        return jsonSeed.notes
    }

    public String dump() {
        return "{\"type\":\"" + getClass().getSimpleName() + "\", \"value\":" + JsonOutput.toJson(getJsonSeed()) + "}}";
    }


}
