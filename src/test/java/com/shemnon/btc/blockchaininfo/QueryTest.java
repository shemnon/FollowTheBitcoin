package com.shemnon.btc.blockchaininfo;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * 
 * Created by shemnon on 2 Mar 2014.
 */
public class QueryTest {
    
    @Test
    public void queryAddressByAddress() {
        AddressInfo ai = AddressInfo.query("1BGbGFBhsXYq6kTyjSC9AHRe1dhe76tD6i");

        assertEquals("1BGbGFBhsXYq6kTyjSC9AHRe1dhe76tD6i", ai.getAddress());
        assertEquals("70a419336ae604ddf73e4f8199f1d03b3c2f260b", ai.getHash160());

        ai.getTxs().forEach(tx ->
                assertEquals(1, tx.getOutputs().get(0).getValueSatoshi())
        );
    }

    @Test
    public void queryAddressByHash160() {
        AddressInfo ai = AddressInfo.query("70a419336ae604ddf73e4f8199f1d03b3c2f260b");

        assertEquals("1BGbGFBhsXYq6kTyjSC9AHRe1dhe76tD6i", ai.getAddress());
        assertEquals("70a419336ae604ddf73e4f8199f1d03b3c2f260b", ai.getHash160());
        
        ai.getTxs().forEach(tx ->
                assertEquals(1, tx.getOutputs().get(0).getValueSatoshi())
        );
    }
    
    //TODO tx.query by hash
    //TODO tx.query by tx_index
    //TODO tx.query then block
    //TODO block.query by hash
    //TODO block.query by height
    
    //TODO coin.gettx
    
    
}
