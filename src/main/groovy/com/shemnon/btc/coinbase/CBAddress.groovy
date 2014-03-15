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

import com.shemnon.btc.ftm.JsonBase;

/**
 * Created by shemnon on 5 Mar 2014.
 */
public class CBAddress extends JsonBase {

    CBAddress(def seed) {
        jsonSeed = seed;
    }
    
    String getLabel() {
        return jsonSeed.label ?: ""
    }

    String getAddress() {
        return jsonSeed.address ?: ""
    }

    Date getCreatedAt() {
        if (jsonSeed.created_at) {
            return JSON_DATE.parse(jsonSeed.created_at)
        } else {
            return null;
        }
    }
}
