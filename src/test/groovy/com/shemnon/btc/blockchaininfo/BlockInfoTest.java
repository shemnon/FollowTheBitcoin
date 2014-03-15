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

package com.shemnon.btc.blockchaininfo;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 
 * Created by shemnon on 2 Mar 2014.
 */
public class BlockInfoTest {
    
    @Test
    public void testSmallBlock() {
       BlockInfo bi = BlockInfo.fromJson( "\n" +
                "\n" +
                "{\n" +
                "\t\"hash\":\"000000009a4aed3e8ba7a978c6b50fea886fb496d66e696090a91d527200b002\",\n" +
                "\t\"ver\":1,\t\n" +
                "\t\"prev_block\":\"0000000046d234a513bcba7c73d6c9c2b2b43dace9033838b3eead334a7d39c1\",\n" +
                "\t\"mrkl_root\":\"3817167ba82293f033012dda3a13d581efd76e060e27564687c90a14667b6889\",\n" +
                "\t\"time\":1232582734,\n" +
                "\t\"bits\":486604799,\n" +
                "    \"fee\":0,\n" +
                "    \"nonce\":248236551,\n" +
                "\t\"n_tx\":2,\n" +
                "\t\"size\":1400,\n" +
                "\t\"block_index\":1297,\n" +
                "\t\"main_chain\":true,\n" +
                "\t\"height\":1296,\n" +
                "\t\"received_time\":1232582734,\n" +
                "\t\"relayed_by\":\"127.0.0.1\",\n" +
                "\t\n" +
                "\t\"tx\":[{\"time\":1232582734,\"inputs\":[{}],\"vout_sz\":1,\"relayed_by\":\"0.0.0.0\",\"hash\":\"9faa93334f9561ef4133a97b29235f1c28de9c077e0d8fc39643c4d8baf44138\",\"vin_sz\":1,\"tx_index\":1320,\"ver\":1,\"out\":[{\"n\":0,\"value\":5000000000,\"addr\":\"125JeyLib4bzAkqFbcidrNqwcty6S51yCu\",\"tx_index\":1320,\"type\":0}],\"size\":134},{\"time\":1232582734,\"inputs\":[{\"prev_out\":{\"n\":0,\"value\":5000000000,\"addr\":\"19QiFoYFBf8bo6STnvdCD21ASskDGkpaSQ\",\"tx_index\":786,\"type\":0}},{\"prev_out\":{\"n\":0,\"value\":5000000000,\"addr\":\"1qvpfJXzAzKJvkbBFRVEtNZw6yU31orDL\",\"tx_index\":1147,\"type\":0}},{\"prev_out\":{\"n\":0,\"value\":5000000000,\"addr\":\"1HaEeDfAWeo4hopjYMAx1At75uhY614C9X\",\"tx_index\":1161,\"type\":0}},{\"prev_out\":{\"n\":0,\"value\":5000000000,\"addr\":\"1CsZEWqUk95GggXg1mvxP6qWv95bCvVaZs\",\"tx_index\":959,\"type\":0}},{\"prev_out\":{\"n\":0,\"value\":5000000000,\"addr\":\"15Eto1LeCTkZGkPwr3H2BS8Nu1yadfeEH8\",\"tx_index\":469,\"type\":0}},{\"prev_out\":{\"n\":0,\"value\":5000000000,\"addr\":\"19NaaYCw1UBQd2gFfc77bKnVesA5d7fFzM\",\"tx_index\":1118,\"type\":0}},{\"prev_out\":{\"n\":0,\"value\":5000000000,\"addr\":\"1HG3byV85t3wiZ4uazZJvntRQT2Rmw4rbm\",\"tx_index\":828,\"type\":0}},{\"prev_out\":{\"n\":0,\"value\":5000000000,\"addr\":\"12V3D1ytYGZgYTgmuNh8JLAiKMF9chUoDd\",\"tx_index\":328,\"type\":0}},{\"prev_out\":{\"n\":0,\"value\":5000000000,\"addr\":\"1Ktq4ujHAwk8Utk1AGzCCq2aFBRbdkMeh7\",\"tx_index\":1019,\"type\":0}},{\"prev_out\":{\"n\":0,\"value\":5000000000,\"addr\":\"1DGgqqQHfwGX28FzenC2DgqX2fzgU99urQ\",\"tx_index\":965,\"type\":0}}],\"vout_sz\":1,\"relayed_by\":\"0.0.0.0\",\"hash\":\"59bf8acbc9d60dfae841abecc3882b4181f2bdd8ac6c1d94001165ab3aef50b0\",\"vin_sz\":10,\"tx_index\":1321,\"ver\":1,\"out\":[{\"n\":0,\"value\":50000000000,\"addr\":\"12higDjoCCNXSA95xZMWUdPvXNmkAduhWv\",\"tx_index\":1321,\"type\":0}],\"size\":1185}]\n" +
                " }");

        assertEquals(2, bi.getTXs().size());
        assertEquals(0, bi.getTXs().get(0).getInputs().size());
        assertEquals(0.0, bi.getTXs().get(0).getInputValue(), TxInfoTest.SUB_SATOSHI);
        assertEquals(50.0, bi.getTXs().get(0).getOutputValue(), TxInfoTest.SUB_SATOSHI);
        assertEquals(0, bi.getTXs().get(0).getInputValueSatoshi());
        assertEquals(50_000_000_00L, bi.getTXs().get(0).getOutputValueSatoshi());
        assertEquals(10, bi.getTXs().get(1).getInputs().size());
        assertEquals(500.0, bi.getTXs().get(1).getInputValue(), TxInfoTest.SUB_SATOSHI);
        assertEquals(500.0, bi.getTXs().get(1).getOutputValue(), TxInfoTest.SUB_SATOSHI);
        assertEquals(500_000_000_00L, bi.getTXs().get(1).getInputValueSatoshi());
        assertEquals(500_000_000_00L, bi.getTXs().get(1).getOutputValueSatoshi());
        
    }
}
