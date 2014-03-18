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

import com.shemnon.btc.blockchaininfo.AddressInfo;
import com.shemnon.btc.blockchaininfo.TXInfo;
import com.shemnon.btc.model.ICoin;
import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * Created by shemnon on 2 Mar 2014.
 */
public class BCCoinTest {

    @Test
    public void testTXsSpent() {
        AddressInfo.fromJson("{\"balanceSat\":0,\"totalReceivedSat\":5000000000,\"totalSentSat\":5000000000,\"unconfirmedBalanceSat\":0,\"txApperances\":2,\"unconfirmedTxApperances\":0,\"transactions\":[\"b746a638255dac5820c1ee3b0c7bf9a99e64789ce0bdd9475aeaa64c2d80c73a\",\"9f0846638ff870074a31a7975c18e1659a9ec668a573cd8336283ae9fd8da460\"],\"addrStr\":\"1aG4kJWoXaY9Dv8WNenMBQxyWxkc31q5B\",\"totalSent\":50,\"balance\":0,\"totalReceived\":50,\"unconfirmedBalance\":0}");
        
        TXInfo tx1 = TXInfo.fromJson("{\"txid\":\"9f0846638ff870074a31a7975c18e1659a9ec668a573cd8336283ae9fd8da460\",\"version\":1,\"locktime\":0,\"vin\":[{\"coinbase\":\"044c86041b0123\",\"sequence\":4294967295,\"n\":0}],\"vout\":[{\"value\":50,\"n\":0,\"scriptPubKey\":{\"asm\":\"043eff1e9230d4c79d067db9b6db2187687f74fa1a62c469b597e6f1160d66b537998befa8d6f4c6261e495c42f5e759910c09b464b347b614a036127120364a8b OP_CHECKSIG\",\"reqSigs\":1,\"type\":\"pubkey\",\"addresses\":[\"1aG4kJWoXaY9Dv8WNenMBQxyWxkc31q5B\"]},\"spentTxId\":\"b746a638255dac5820c1ee3b0c7bf9a99e64789ce0bdd9475aeaa64c2d80c73a\",\"spentIndex\":0,\"spentTs\":1293628047}],\"blockhash\":\"0000000000025871b959d5fca203575e2335675eaef03e58b2d06e8e7daccd71\",\"confirmations\":191045,\"time\":1293569878,\"blocktime\":1293569878,\"isCoinBase\":true,\"valueOut\":50,\"size\":134}");
        TXInfo tx2 = TXInfo.fromJson("{\"txid\":\"b746a638255dac5820c1ee3b0c7bf9a99e64789ce0bdd9475aeaa64c2d80c73a\",\"version\":1,\"locktime\":0,\"vin\":[{\"txid\":\"9f0846638ff870074a31a7975c18e1659a9ec668a573cd8336283ae9fd8da460\",\"vout\":0,\"scriptSig\":{\"asm\":\"3046022100f2d278f48956619292655d3bd39641a2e187ff3231c8bc5c2ae3c68bb048978d022100fb5d38e152965f3b6d188bddaa743f6c173f8b8d9a280dd4a505539a4a60611601\"},\"sequence\":4294967295,\"n\":0,\"addr\":\"1aG4kJWoXaY9Dv8WNenMBQxyWxkc31q5B\",\"valueSat\":5000000000,\"value\":50,\"doubleSpentTxID\":null}],\"vout\":[{\"value\":48.4,\"n\":0,\"scriptPubKey\":{\"asm\":\"OP_DUP OP_HASH160 9e5d9b13984a1562813a4a98b934952ae5fb5dcd OP_EQUALVERIFY OP_CHECKSIG\",\"reqSigs\":1,\"type\":\"pubkeyhash\",\"addresses\":[\"1FSMu9pE1iTHQJwxfdwGajUpGQkGwok3GQ\"]},\"spentTxId\":\"18c412ae52c8204f6a1b88739dd7a4afd80072c282cf06a7b5c9d8cc7631b49c\",\"spentIndex\":0,\"spentTs\":1293632191},{\"value\":1.6,\"n\":1,\"scriptPubKey\":{\"asm\":\"OP_DUP OP_HASH160 71930f7b53595b8e1b35628bc39425f4d531ecac OP_EQUALVERIFY OP_CHECKSIG\",\"reqSigs\":1,\"type\":\"pubkeyhash\",\"addresses\":[\"1BMXXcqKMtywM8QLFBwktedi2kdARwxYoV\"]},\"spentTxId\":\"a3abc1476cb7a2433c7f1c0bcbe135fe2cc8e2bba6de723323a473f0d16b0b61\",\"spentIndex\":13,\"spentTs\":1296341672}],\"blockhash\":\"000000000003eaeb80590d76856f7f9d6c2aeb691a6c0cba747d5b523d20b0f2\",\"confirmations\":190925,\"time\":1293628047,\"blocktime\":1293628047,\"valueOut\":50,\"size\":193,\"firstSeenTs\":1293628047,\"valueIn\":50,\"fees\":0}");

        ICoin coin = tx1.getOutputs().get(0);

        Assert.assertSame(tx1, coin.getSourceTX());
        Assert.assertSame(tx2, coin.getTargetTX());
    }

    @Test
    public void testTXsUnspent() {
        AddressInfo address = AddressInfo.fromJson("{\"balanceSat\":5000000000,\"totalReceivedSat\":5000000000,\"totalSentSat\":0,\"unconfirmedBalanceSat\":0,\"txApperances\":1,\"unconfirmedTxApperances\":0,\"transactions\":[\"df2b060fa2e5e9c8ed5eaf6a45c13753ec8c63282b2688322eba40cd98ea067a\"],\"addrStr\":\"15ubicBBWFnvoZLT7GiU2qxjRaKJPdkDMG\",\"totalSent\":0,\"balance\":50,\"totalReceived\":50,\"unconfirmedBalance\":0}");
        
        TXInfo tx = TXInfo.fromJson("{\"txid\":\"df2b060fa2e5e9c8ed5eaf6a45c13753ec8c63282b2688322eba40cd98ea067a\",\"version\":1,\"locktime\":0,\"vin\":[{\"coinbase\":\"04ffff001d011a\",\"sequence\":4294967295,\"n\":0}],\"vout\":[{\"value\":50,\"n\":0,\"scriptPubKey\":{\"asm\":\"04184f32b212815c6e522e66686324030ff7e5bf08efb21f8b00614fb7690e19131dd31304c54f37baa40db231c918106bb9fd43373e37ae31a0befc6ecaefb867 OP_CHECKSIG\",\"reqSigs\":1,\"type\":\"pubkey\",\"addresses\":[\"15ubicBBWFnvoZLT7GiU2qxjRaKJPdkDMG\"]}}],\"blockhash\":\"000000004ebadb55ee9096c9a2f8880e09da59c0d68b1c228da88e48844a1485\",\"confirmations\":290931,\"time\":1231470988,\"blocktime\":1231470988,\"isCoinBase\":true,\"valueOut\":50,\"size\":134}");

        ICoin coin = tx.getOutputs().get(0);

        Assert.assertSame(tx, coin.getSourceTX());
        Assert.assertNull(coin.getTargetTX());
        Assert.assertNull(coin.getTargetTX());
    }

}
