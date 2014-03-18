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
public class BCBlockTest {
    
    @Test
    public void testSmallBlock() {
       BCBlock bi = BCBlock.fromJson( "{\"hash\":\"000000009a4aed3e8ba7a978c6b50fea886fb496d66e696090a91d527200b002\",\"confirmations\":289639,\"size\":1400,\"height\":1296,\"version\":1,\"merkleroot\":\"3817167ba82293f033012dda3a13d581efd76e060e27564687c90a14667b6889\",\"tx\":[\"9faa93334f9561ef4133a97b29235f1c28de9c077e0d8fc39643c4d8baf44138\",\"59bf8acbc9d60dfae841abecc3882b4181f2bdd8ac6c1d94001165ab3aef50b0\"],\"time\":1232582734,\"nonce\":248236551,\"bits\":\"1d00ffff\",\"difficulty\":1,\"chainwork\":\"0000000000000000000000000000000000000000000000000000051105110511\",\"previousblockhash\":\"0000000046d234a513bcba7c73d6c9c2b2b43dace9033838b3eead334a7d39c1\",\"nextblockhash\":\"00000000c909f46277e6748d6f987d6d6b395f0b68cd7b4d50a2286a917fc365\",\"reward\":50,\"isMainChain\":true}");

        assertEquals(2, bi.getTXs().size());
        assertEquals(0, bi.getTXs().get(0).getInputs().size());
        assertEquals(0.0, bi.getTXs().get(0).getInputValue(), BCTxTest.SUB_SATOSHI);
        assertEquals(50.0, bi.getTXs().get(0).getOutputValue(), BCTxTest.SUB_SATOSHI);
        assertEquals(0, bi.getTXs().get(0).getInputValueSatoshi());
        assertEquals(50_000_000_00L, bi.getTXs().get(0).getOutputValueSatoshi());
        assertEquals(10, bi.getTXs().get(1).getInputs().size());
        assertEquals(500.0, bi.getTXs().get(1).getInputValue(), BCTxTest.SUB_SATOSHI);
        assertEquals(500.0, bi.getTXs().get(1).getOutputValue(), BCTxTest.SUB_SATOSHI);
        assertEquals(500_000_000_00L, bi.getTXs().get(1).getInputValueSatoshi());
        assertEquals(500_000_000_00L, bi.getTXs().get(1).getOutputValueSatoshi());
        
    }
}
