/*
 * Follow the Bitcoin
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

import static org.junit.Assert.assertEquals;

/**
 * 
 * Created by shemnon on 2 Mar 2014.
 */

public class TxInfoTest {
    
    static final double SUB_SATOSHI = Math.nextDown(0.000_000_01D);

    @Test
    public void testParse424242() {
        TXInfo txInfo = TXInfo.fromJson("{" +
                "\"double_spend\":false," +
                "\"block_height\":132749," +
                "\"time\":1308811815," +
                "\"inputs\":[" +
                "{\"prev_out\":{\"n\":1,\"value\":5000000000000,\"addr\":\"17dxcM1cdeXPL1rZNJDAFwU2C4H9Xf5VqR\",\"tx_index\":889535,\"type\":0}}," +
                "{\"prev_out\":{\"n\":1,\"value\":5000000000000,\"addr\":\"19H4PcfyPCw8FUh5J1pma5Bb9w2srhYcQ4\",\"tx_index\":889533,\"type\":0}}," +
                "{\"prev_out\":{\"n\":1,\"value\":5000000000000,\"addr\":\"1LfddcwiNnJ7Hk3KpBv5AfcTMD1fCahLqY\",\"tx_index\":889532,\"type\":0}}," +
                "{\"prev_out\":{\"n\":1,\"value\":5000000000000,\"addr\":\"15oGga9EJ7BzdZxFnEcDxQDZvh1dHZ6Zco\",\"tx_index\":889537,\"type\":0}}," +
                "{\"prev_out\":{\"n\":1,\"value\":5000000000000,\"addr\":\"1FfsbQntU2BAkVy2xaaRpSmLEDMEUwPbG1\",\"tx_index\":889534,\"type\":0}}," +
                "{\"prev_out\":{\"n\":1,\"value\":5000000000000,\"addr\":\"1MtyGxTeWmjNhXyBf85JPKL5vrhTRJ7HnD\",\"tx_index\":889483,\"type\":0}}," +
                "{\"prev_out\":{\"n\":1,\"value\":5000000000000,\"addr\":\"14yX4kRDME61BbM7DMnuv1PKUtk8K65J4y\",\"tx_index\":889531,\"type\":0}}," +
                "{\"prev_out\":{\"n\":1,\"value\":4200000000000,\"addr\":\"191sZm1o7WZ1baNqUmRow6RPo293gbeJLW\",\"tx_index\":889539,\"type\":0}}," +
                "{\"prev_out\":{\"n\":1,\"value\":5000000000000,\"addr\":\"1Q4EpJ6eaXDV1mEHx7JgupBuGNi4GBcMbV\",\"tx_index\":889536,\"type\":0}}" +
                "]," +
                "\"vout_sz\":2," +
                "\"relayed_by\":\"0.0.0.0\"," +
                "\"hash\":\"3a1b9e330d32fef1ee42f8e86420d2be978bbe0dc5862f17da9027cf9e11f8c4\"," +
                "\"vin_sz\":9," +
                "\"tx_index\":925838," +
                "\"ver\":1," +
                "\"out\":[" +
                "{\"n\":0,\"value\":1775757575758,\"addr\":\"1Hzpk4eXTbrAfmH2noWrkrgxo6ewH6qncd\",\"tx_index\":925838,\"type\":0}," +
                "{\"n\":1,\"value\":42424242424242,\"addr\":\"1eHhgW6vquBYhwMPhQ668HPjxTtpvZGPC\",\"tx_index\":925838,\"type\":0}" +
                "]," +
                "\"size\":1698}");
        assertEquals(442000, txInfo.getInputValue(), SUB_SATOSHI);
        assertEquals(442000, txInfo.getOutputValue(), SUB_SATOSHI);
        assertEquals(0, txInfo.getFeePaid(), SUB_SATOSHI);

        assertEquals(442_000_000_000_00L, txInfo.getInputValueSatoshi());
        assertEquals(442_000_000_000_00L, txInfo.getOutputValueSatoshi());
        assertEquals(0L, txInfo.getFeePaidSatoshi());

        assertEquals(17757.57575758, txInfo.getOutputs().get(0).getValue(), SUB_SATOSHI);
        assertEquals(1775757575758L, txInfo.getOutputs().get(0).getValueSatoshi());
        assertEquals(424242.42424242, txInfo.getOutputs().get(1).getValue(), SUB_SATOSHI);
        assertEquals(42424242424242L, txInfo.getOutputs().get(1).getValueSatoshi());
    }

    @Test
    public void testParseFeeTrans() {
        TXInfo txInfo = TXInfo.fromJson("{\"double_spend\":false,\"block_height\":288455,\"time\":1393685947,\"inputs\":[{\"prev_out\":{\"n\":19,\"value\":128988620,\"addr\":\"1LFrDULxpGCFLN61miVjoGw59JNQZ6PxU8\",\"tx_index\":114229990,\"type\":0}}," +
                "{\"prev_out\":{\"n\":0,\"value\":81786209,\"addr\":\"1Pm3k1dhJ2ZCUTBvX5YiqyjPCVfc2r2ajL\",\"tx_index\":114230176,\"type\":0}}," +
                "{\"prev_out\":{\"n\":8,\"value\":127753590,\"addr\":\"19gtnRui6L2ucJFM4G41XKmPpMCXp1Pyq4\",\"tx_index\":114228921,\"type\":0}}," +
                "{\"prev_out\":{\"n\":8,\"value\":6000000,\"addr\":\"1PdCBSyDBKoWPUTrVRfYmJmm4vU3vLrCTd\",\"tx_index\":114230127,\"type\":0}}," +
                "{\"prev_out\":{\"n\":10,\"value\":13667224,\"addr\":\"1CLFRYyqRbCju1LjWyyq72xsL7ZHt1ibQ5\",\"tx_index\":114230127,\"type\":0}}," +
                "{\"prev_out\":{\"n\":6,\"value\":66432334,\"addr\":\"16atfQSbR1DDiwQ1eJmQdCDFUvemSzr6JQ\",\"tx_index\":114230176,\"type\":0}}," +
                "{\"prev_out\":{\"n\":10,\"value\":23064531,\"addr\":\"17z9BopWZqWM3T9hAB8MG14aQ8f2ctjTxq\",\"tx_index\":114228939,\"type\":0}}," +
                "{\"prev_out\":{\"n\":10,\"value\":19200397,\"addr\":\"1NpX27L2euokQquyvMQDXXFAbwFxpQPsBM\",\"tx_index\":114228928,\"type\":0}}," +
                "{\"prev_out\":{\"n\":10,\"value\":9100000,\"addr\":\"1FPAYJxL539xqwXZH9quwjivcCvT4w8byD\",\"tx_index\":114230057,\"type\":0}}," +
                "{\"prev_out\":{\"n\":2,\"value\":4864531,\"addr\":\"14KGz2gxx5N4kszMKe8UXcVbyNe9jsXzPJ\",\"tx_index\":114228939,\"type\":0}}," +
                "{\"prev_out\":{\"n\":3,\"value\":129591664,\"addr\":\"1NS1jBm1zcHuL6yAZyk9yDgyznHwSBPo8Z\",\"tx_index\":114228907,\"type\":0}}," +
                "{\"prev_out\":{\"n\":0,\"value\":15100000,\"addr\":\"1PBWqwfKemMiETe7Xy1NoMF3919KnT8Kq3\",\"tx_index\":114228929,\"type\":0}}," +
                "{\"prev_out\":{\"n\":5,\"value\":129220711,\"addr\":\"1Hxjmqe2zhahhSiYyV91UwStmM4op4MMPr\",\"tx_index\":114230006,\"type\":0}}," +
                "{\"prev_out\":{\"n\":4,\"value\":8000000,\"addr\":\"1DdZHcmm7T9ZXeMxb8mcpCJE97ZS1ofWPa\",\"tx_index\":114230127,\"type\":0}}],\"vout_sz\":17,\"relayed_by\":\"127.0.0.1\",\"hash\":\"103bb04aa6da1bc6878295f4f520202fee3c68328e8a984cc572d67e2b9ab1f0\",\"vin_sz\":14,\"tx_index\":114228948,\"ver\":1,\"out\":[{\"n\":0,\"value\":50500877,\"addr\":\"19ZAHvpYb2RWrjFfAVeecaFrchdeTiCFaZ\",\"tx_index\":114228948,\"type\":0}," +
                "{\"n\":1,\"value\":49739434,\"addr\":\"1Hjp95QJYQ8wC5Q2Akvy5HZcRVLAUtUzZ9\",\"tx_index\":114228948,\"type\":0}," +
                "{\"n\":2,\"value\":48155296,\"addr\":\"1KtS5pso6nQeMCetnG9Bkh21bwkcxaXp6e\",\"tx_index\":114228948,\"type\":0}," +
                "{\"n\":3,\"value\":9090000,\"addr\":\"14gbmCzGKPTANHPGbL6j9ukCzALXtuNNjy\",\"tx_index\":114228948,\"type\":0}," +
                "{\"n\":4,\"value\":51908381,\"addr\":\"1AYM3HCuSAWjKorchg6e9udHMXmnUgcjoN\",\"tx_index\":114228948,\"type\":0}," +
                "{\"n\":5,\"value\":12597095,\"addr\":\"1E35MGdjbNVzJPud4SWGgwpzE1WAwsMLQg\",\"tx_index\":114228948,\"type\":0}," +
                "{\"n\":6,\"value\":50627734,\"addr\":\"1PJJXfUws6vCfU8avhc3YcDUkiZtcSxHgN\",\"tx_index\":114228948,\"type\":0}," +
                "{\"n\":7,\"value\":44482037,\"addr\":\"18ypUTvkP5CckHUCi4DcGd6bjXcjGUxvs9\",\"tx_index\":114228948,\"type\":0}," +
                "{\"n\":8,\"value\":51006008,\"addr\":\"1HvKqu2QeAZw75jUMhzyqRBHmqjGWkkiS3\",\"tx_index\":114228948,\"type\":0}," +
                "{\"n\":9,\"value\":53098772,\"addr\":\"15tsuwFS7vZpxvbMy2nBHd7bL4mxxgw2Af\",\"tx_index\":114228948,\"type\":0}," +
                "{\"n\":10,\"value\":45271483,\"addr\":\"1JTt1Dd9Xp6ZZVQ71qnfrips3H4Vq8Ho6D\",\"tx_index\":114228948,\"type\":0}," +
                "{\"n\":11,\"value\":49079740,\"addr\":\"1PSZYHTyVSWPFcBvpDg4LnuWuC51tYejjB\",\"tx_index\":114228948,\"type\":0}," +
                "{\"n\":12,\"value\":51028680,\"addr\":\"1NxsiScxBzzmw3xzKDKpwMGTBvNtmPNa59\",\"tx_index\":114228948,\"type\":0}," +
                "{\"n\":13,\"value\":50973507,\"addr\":\"1Cb7EM9eFqTXmcPBWLunLSJPD9Lh6cRhyk\",\"tx_index\":114228948,\"type\":0}," +
                "{\"n\":14,\"value\":47463101,\"addr\":\"1P2xLD3XPfhCazs4wDQMMNhSDnJ5Mybsy1\",\"tx_index\":114228948,\"type\":0}," +
                "{\"n\":15,\"value\":52879364,\"addr\":\"19g7qTAyi7GRG8YtnBvk84m1LEVEJNPXJH\",\"tx_index\":114228948,\"type\":0}," +
                "{\"n\":16,\"value\":44828302,\"addr\":\"18vWXfiTGAL2KMMt43LALYaPbAtnhM63kR\",\"tx_index\":114228948,\"type\":0}],\"size\":2981}");

        assertEquals(7.62769811, txInfo.getInputValue(), SUB_SATOSHI);
        assertEquals(7.62729811, txInfo.getOutputValue(), SUB_SATOSHI);
        assertEquals(0.0004, txInfo.getFeePaid(), SUB_SATOSHI);

        assertEquals(7_627_698_11L, txInfo.getInputValueSatoshi());
        assertEquals(7_627_298_11L, txInfo.getOutputValueSatoshi());
        assertEquals(      400_00L, txInfo.getFeePaidSatoshi());
    }

    @Test
    public void testParseCoinbase() {
        TXInfo txInfo = TXInfo.fromJson("{\"double_spend\":false,\"block_height\":288455,\"time\":1393686974,\"inputs\":[{}],\"vout_sz\":1,\"relayed_by\":\"198.147.23.40\",\"hash\":\"7e2a3076461d852112e53d42e424bb31fcb2c760560d8a6c4bd1e546d8c0e7e9\",\"vin_sz\":1,\"tx_index\":114230885,\"ver\":1,\"out\":[{\"addr_tag\":\"Eleuthria\",\"n\":0,\"value\":2510897664,\"addr\":\"14cZMQk89mRYQkDEj8Rn25AnGoBi5H6uer\",\"tx_index\":114230885,\"type\":0,\"addr_tag_link\":\"http:\\/\\/bitcoin-otc.com\\/viewgpg.php?nick=Eleuthria\"}],\"size\":168}");

        assertEquals(0, txInfo.getInputValue(), SUB_SATOSHI);
        assertEquals(25.10897664, txInfo.getOutputValue(), SUB_SATOSHI);
        assertEquals(0, txInfo.getFeePaid(), SUB_SATOSHI);
        
        assertEquals(0L, txInfo.getInputValueSatoshi());
        assertEquals(25_108_976_64L, txInfo.getOutputValueSatoshi());
        assertEquals(0L, txInfo.getFeePaidSatoshi());
        
        assertEquals(txInfo.getInputs().size(), 0);
        assertEquals(txInfo.getOutputs().size(), 1);
    }
}
