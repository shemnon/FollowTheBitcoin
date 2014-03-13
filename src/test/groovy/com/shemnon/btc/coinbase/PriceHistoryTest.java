package com.shemnon.btc.coinbase;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

/**
 * 
 * Created by shemnon on 12 Mar 2014.
 */
public class PriceHistoryTest {

    @Test
    public void page1() throws IOException, ParseException {
        Assert.assertEquals(646.29, CBPriceHistory.getInstance().getPrice(1394653140000L).orElse(0.0), 0.001);  // 2014-03-12T15:38:21-04:00,646.29
    } 

    @Test
    public void page3() throws IOException, ParseException {
        Assert.assertEquals(574.10, CBPriceHistory.getInstance().getPrice(1393450200000L).orElse(0.0), 0.001); //2014-02-26T13:29:49-08:00,574.1
        Assert.assertEquals(646.29, CBPriceHistory.getInstance().getPrice(1394653140000L).orElse(0.0), 0.001);  // 2014-03-12T15:38:21-04:00,646.29
    } 
}
