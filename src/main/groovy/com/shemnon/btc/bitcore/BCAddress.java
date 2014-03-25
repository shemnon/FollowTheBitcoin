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

package com.shemnon.btc.bitcore;

import com.shemnon.btc.model.IAddress;
import com.shemnon.btc.model.IBase;
import com.shemnon.btc.model.ITx;

import javax.json.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 *
 * Created by shemnon on 1 Mar 2014.
 */
public class BCAddress extends BitcoreBase implements IAddress {

    static Map<String, BCAddress> addrcache = new ConcurrentHashMap<>();

    String[] txids;
    private String addrStr;

    private BCAddress() {}

    public static BCAddress query(String addr) {
        if (addr == null) return null;
        if (addrcache.containsKey(addr)) {
            return addrcache.get(addr);
        } else {
            IBase.checkOffThread();

            try (InputStream is = new URL(urlbase + "/addr/" + addr).openStream()) {
                return fromStream(is);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    static BCAddress fromJson(String jsonString) {
        try (InputStream is = new ByteArrayInputStream(jsonString.getBytes("UTF-8"))) {
            return fromStream(is);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    static BCAddress fromStream(InputStream is) {
        try (JsonReader rdr = Json.createReader(is)) {
            BCAddress addr = new BCAddress();
            JsonObject obj = rdr.readObject();
            JsonArray transactions = obj.getJsonArray("transactions");
            addr.txids = transactions.getValuesAs(JsonString.class).stream().map(JsonString::getString).toArray(String[]::new);
            addr.addrStr = obj.getString("addrStr");

            
            addrcache.put(addr.addrStr, addr);
            return addr;
        }
    }

    public List<ITx> getTXs() {
        return Arrays.stream(txids).map(BCTx::query).collect(Collectors.toList());
    }


    public String getAddress() {
        return addrStr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BCAddress bcAddress = (BCAddress) o;

        return !(addrStr != null ? !addrStr.equals(bcAddress.addrStr) : bcAddress.addrStr != null);

    }

    @Override
    public int hashCode() {
        return addrStr != null ? addrStr.hashCode() : 0;
    }
}
