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

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * 
 * Created by shemnon on 2 Mar 2014.
 */
public class QueryTest {
    
    @Test
    public void queryAddressByAddress() {
        BCAddress ai = BCAddress.query("1BGbGFBhsXYq6kTyjSC9AHRe1dhe76tD6i");

        assertEquals("1BGbGFBhsXYq6kTyjSC9AHRe1dhe76tD6i", ai.getAddress());

        ai.getTXs().forEach(tx ->
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
