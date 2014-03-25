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

import com.shemnon.btc.model.IBase;
import com.shemnon.btc.model.IBlock;
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
public class BCBlock extends BitcoreBase implements IBlock {

    static Map<String, BCBlock> blockcache = new ConcurrentHashMap<>();
    private String[] txids;
    private String hash;
    private int height;

    private BCBlock() {}

    public static BCBlock query(String block) {
        if (block == null) return null;
        if (blockcache.containsKey(block)) {
            return blockcache.get(block);
        } else {
            IBase.checkOffThread();

            try (InputStream is = new URL(urlbase + "/block/" + block).openStream()) {
                return fromStream(is);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    static BCBlock fromJson(String jsonString) {
        try (InputStream is = new ByteArrayInputStream(jsonString.getBytes("UTF-8"))) {
            return fromStream(is);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    static BCBlock fromStream(InputStream is) {
        try (JsonReader rdr = Json.createReader(is)) {
            BCBlock block = new BCBlock();
            JsonObject obj = rdr.readObject();
            JsonArray transactions = obj.getJsonArray("tx");
            block.txids = transactions.getValuesAs(JsonString.class).stream().map(JsonString::getString).toArray(String[]::new);
            block.height = obj.getInt("height");
            block.hash = obj.getString("hash");


            blockcache.put(block.hash, block);
            blockcache.put(String.valueOf(block.height), block);
            return block;
        }
    }
    
    public List<ITx> getTXs() {
        return Arrays.stream(txids).map(BCTx::query).collect(Collectors.toList()); 
    }
    
    public int getHeight() {
        return height;
    }
    
    public String getHash() {
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BCBlock bcBlock = (BCBlock) o;

        return !(hash != null ? !hash.equals(bcBlock.hash) : bcBlock.hash != null);

    }

    @Override
    public int hashCode() {
        return hash != null ? hash.hashCode() : 0;
    }
}
