/*
 * Trail of Bitcoin
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
