package com.shemnon.btc.blockchaininfo;

import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * Created by shemnon on 2 Mar 2014.
 */
public class CoinInfoTest {

    @Test
    public void testTXsSpent() {
        AddressInfo.fromJson("{\n" +
                "\t\"hash160\":\"064a4d51e32b234c3e64a244d04b68bd00eb27db\",\n" +
                "\t\"address\":\"1aG4kJWoXaY9Dv8WNenMBQxyWxkc31q5B\",\n" +
                "\t\"n_tx\":2,\n" +
                "\t\"total_received\":5000000000,\n" +
                "\t\"total_sent\":5000000000,\n" +
                "\t\"final_balance\":0,\n" +
                "\t\"txs\":[{\"result\":0,\"block_height\":100010,\"time\":1293628047,\"inputs\":[{\"prev_out\":{\"n\":0,\"value\":5000000000,\"addr\":\"1aG4kJWoXaY9Dv8WNenMBQxyWxkc31q5B\",\"tx_index\":237958,\"type\":0}}],\"vout_sz\":2,\"relayed_by\":\"0.0.0.0\",\"hash\":\"b746a638255dac5820c1ee3b0c7bf9a99e64789ce0bdd9475aeaa64c2d80c73a\",\"vin_sz\":1,\"tx_index\":238350,\"ver\":1,\"out\":[{\"n\":0,\"value\":4840000000,\"addr\":\"1FSMu9pE1iTHQJwxfdwGajUpGQkGwok3GQ\",\"tx_index\":238350,\"type\":0},{\"n\":1,\"value\":160000000,\"addr\":\"1BMXXcqKMtywM8QLFBwktedi2kdARwxYoV\",\"tx_index\":238350,\"type\":0}],\"size\":193},{\"result\":-5000000000,\"block_height\":99890,\"time\":1293569878,\"inputs\":[{}],\"vout_sz\":1,\"relayed_by\":\"0.0.0.0\",\"hash\":\"9f0846638ff870074a31a7975c18e1659a9ec668a573cd8336283ae9fd8da460\",\"vin_sz\":1,\"tx_index\":237958,\"ver\":1,\"out\":[{\"n\":0,\"value\":5000000000,\"addr\":\"1aG4kJWoXaY9Dv8WNenMBQxyWxkc31q5B\",\"tx_index\":237958,\"type\":0}],\"size\":134}]\n" +
                "}");
        TXInfo tx1 = TXInfo.fromJson("{\"double_spend\":false,\"block_height\":99890,\"time\":1293569878,\"inputs\":[{}],\"vout_sz\":1,\"relayed_by\":\"0.0.0.0\",\"hash\":\"9f0846638ff870074a31a7975c18e1659a9ec668a573cd8336283ae9fd8da460\",\"vin_sz\":1,\"tx_index\":237958,\"ver\":1,\"out\":[{\"n\":0,\"value\":5000000000,\"addr\":\"1aG4kJWoXaY9Dv8WNenMBQxyWxkc31q5B\",\"tx_index\":237958,\"type\":0}],\"size\":134}");
        TXInfo tx2 = TXInfo.fromJson("{\"double_spend\":false,\"block_height\":100010,\"time\":1293628047,\"inputs\":[{\"prev_out\":{\"n\":0,\"value\":5000000000,\"addr\":\"1aG4kJWoXaY9Dv8WNenMBQxyWxkc31q5B\",\"tx_index\":237958,\"type\":0}}],\"vout_sz\":2,\"relayed_by\":\"0.0.0.0\",\"hash\":\"b746a638255dac5820c1ee3b0c7bf9a99e64789ce0bdd9475aeaa64c2d80c73a\",\"vin_sz\":1,\"tx_index\":238350,\"ver\":1,\"out\":[{\"n\":0,\"value\":4840000000,\"addr\":\"1FSMu9pE1iTHQJwxfdwGajUpGQkGwok3GQ\",\"tx_index\":238350,\"type\":0},{\"n\":1,\"value\":160000000,\"addr\":\"1BMXXcqKMtywM8QLFBwktedi2kdARwxYoV\",\"tx_index\":238350,\"type\":0}],\"size\":193}");

        CoinInfo coin = tx1.getOutputs().get(0);

        Assert.assertSame(tx1, coin.getSourceTX());
        Assert.assertSame(tx2, coin.getTargetTX());
    }

    @Test
    public void testTXsUnspent() {
        AddressInfo address = AddressInfo.fromJson("{\n" +
                "\t\"hash160\":\"35d31d1dbc974770d456df632a44656a89bae808\",\n" +
                "\t\"address\":\"15ubicBBWFnvoZLT7GiU2qxjRaKJPdkDMG\",\n" +
                "\t\"n_tx\":1,\n" +
                "\t\"total_received\":5000000000,\n" +
                "\t\"total_sent\":0,\n" +
                "\t\"final_balance\":5000000000,\n" +
                "\t\"txs\":[{\"result\":0,\"block_height\":4,\"time\":1231470988,\"inputs\":[{}],\"vout_sz\":1,\"relayed_by\":\"0.0.0.0\",\"hash\":\"df2b060fa2e5e9c8ed5eaf6a45c13753ec8c63282b2688322eba40cd98ea067a\",\"vin_sz\":1,\"tx_index\":5,\"ver\":1,\"out\":[{\"n\":0,\"value\":5000000000,\"addr\":\"15ubicBBWFnvoZLT7GiU2qxjRaKJPdkDMG\",\"tx_index\":5,\"type\":0}],\"size\":134}]\n" +
                "}");
        TXInfo tx = TXInfo.fromJson("{\"double_spend\":false,\"block_height\":4,\"time\":1231470988,\"inputs\":[{}],\"vout_sz\":1,\"relayed_by\":\"0.0.0.0\",\"hash\":\"df2b060fa2e5e9c8ed5eaf6a45c13753ec8c63282b2688322eba40cd98ea067a\",\"vin_sz\":1,\"tx_index\":5,\"ver\":1,\"out\":[{\"n\":0,\"value\":5000000000,\"addr\":\"15ubicBBWFnvoZLT7GiU2qxjRaKJPdkDMG\",\"tx_index\":5,\"type\":0}],\"size\":134}");

        CoinInfo coin = tx.getOutputs().get(0);

        Assert.assertSame(tx, coin.getSourceTX());
        Assert.assertNull(coin.getTargetTX());
        Assert.assertNull(coin.getTargetTX());
    }

}
