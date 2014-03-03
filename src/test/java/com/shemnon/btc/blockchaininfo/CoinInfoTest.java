package com.shemnon.btc.blockchaininfo;

import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * Created by shemnon on 2 Mar 2014.
 */
public class CoinInfoTest {
    
    @Test
    public void testTXSources() {
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
        CoinInfo coin42 = txInfo.getOutputs().get(1);

        Assert.assertSame(txInfo, coin42.getSourceTX());
        
    }
}
