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

import com.shemnon.btc.coinbase.CBPriceHistory;
import com.shemnon.btc.model.IBase;
import com.shemnon.btc.model.IBlock;
import com.shemnon.btc.model.ICoin;
import com.shemnon.btc.model.ITx;

import javax.json.Json;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 
 * Created by shemnon on 28 Feb 2014.
 */
public class BCTx extends BitcoreBase implements ITx {

    static Map<String, BCTx> txcache = new ConcurrentHashMap<>();

    protected static final DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm");

    private String txid;
    List<ICoin> inputs;
    List<ICoin> outputs;
    String blockhash;
    private long timeMs;

    private double outputValue;
    private double inputValue;
    private double fees;
    
    private int confirmations;

    private BCTx() {}

    public static BCTx query(String txid) {
        if (txid == null) return null;
        if (txcache.containsKey(txid)) {
            return txcache.get(txid);
        } else {
            IBase.checkOffThread();

            try (InputStream is = new URL(urlbase + "/tx/" + txid).openStream()) {
                return fromStream(is);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    static BCTx fromJson(String jsonString) {
        try (InputStream is = new ByteArrayInputStream(jsonString.getBytes("UTF-8"))) {
            return fromStream(is);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    static BCTx fromStream(InputStream is) {
        JsonObject obj = null;
        try (JsonReader rdr = Json.createReader(is)) {
            BCTx tx = new BCTx();
            obj = rdr.readObject();
            
            tx.txid = obj.getString("txid");
            
            tx.inputs = obj.getJsonArray("vin").stream()
                    .map(jv -> BCCoin.cacheFromInput((JsonObject)jv, tx.txid))
                    .collect(Collectors.toList()); 
            
            tx.outputs = obj.getJsonArray("vout").stream()
                    .map(jv -> BCCoin.cacheFromOutput((JsonObject) jv, tx.txid))
                    .collect(Collectors.toList()); 
            
            tx.blockhash = obj.getString("blockhash");
            tx.confirmations = obj.getInt("confirmations");
            
            JsonNumber num = obj.getJsonNumber("time");
            tx.timeMs = num == null ? 0 : (num.longValue() * 1000L);
            
            num = obj.getJsonNumber("valueOut");
            tx.outputValue = (num == null) ? 0.0 : num.doubleValue();
            
            num = obj.getJsonNumber("valueIn");
            tx.inputValue = (num == null) ? 0.0 : num.doubleValue();
            
            num = obj.getJsonNumber("fees");
            tx.fees = (num == null) ? 0.0 : num.doubleValue();
            
            txcache.put(tx.txid, tx);
            return tx;
        } catch (Exception e){
            e.printStackTrace();
            System.out.println(obj);
            return null;
        }
    }

    public List<ICoin> getInputs() {
        return inputs;
    }
    
    public List<ICoin> getOutputs() {
        return outputs;
    }


    List<String> getInputAddresses() {
        return inputs.stream().map(ICoin::getAddr).collect(Collectors.toList());
    }

    List<String> getOutputAddresses() {
        return outputs.stream().map(ICoin::getAddr).collect(Collectors.toList()); 
    }
    
    public double getInputValue() {
        return inputValue;
    }

    public long getInputValueSatoshi() {
        return (long)(inputValue * 100000000); 
    }
    
    public double getInputValueUSD() {
        return CBPriceHistory.getInstance().getPrice(timeMs).orElse(0.0) * inputValue;
    }

    public long getOutputValueSatoshi() {
        return (long)(outputValue * 100000000);
    }
    
    public double getOutputValue() {
        return outputValue;
    }

    public double getOutputValueUSD() {
        return CBPriceHistory.getInstance().getPrice(timeMs).orElse(0.0) * outputValue;
    }


    long getFeePaidSatoshi() {
        return (long)(fees * 100000000);
    }
    
    double getFeePaid() {
        return fees;
    }
    
    double getFeePaidUSD() {
        return CBPriceHistory.getInstance().getPrice(timeMs).orElse(0.0) * fees;
    }
    
    public boolean isConfirmed() {
        return confirmations > 0;
    }
    
    IBlock getBlock() {
        return BCBlock.query(blockhash);
   }

    public String getHash() {
        return txid;
    }
    
    public int getBlockHeight() {
        IBlock block = getBlock();
        return (block != null) ? block.getHeight() : -1;
    }
    
    public List<ICoin> getUnspentCoins() {
        return outputs.stream().filter(c -> !c.isSpent()).collect(Collectors.toList());
    }
    
    public long getTimeMs() {
        return timeMs;
    }
    
    public String getTimeString() {
        if (timeMs > 0) {
            return dateFormat.format(timeMs);
        } else {
            return "?";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BCTx bcTx = (BCTx) o;

        return !(txid != null ? !txid.equals(bcTx.txid) : bcTx.txid != null);

    }

    @Override
    public int hashCode() {
        return txid != null ? txid.hashCode() : 0;
    }

    @Override
    public String dump() {
        return "{\"type\":\"BCBlock\", \"txid\":\"" + txid + "\"}";
    }
    
}
