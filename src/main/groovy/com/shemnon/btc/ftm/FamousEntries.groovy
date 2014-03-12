package com.shemnon.btc.ftm

import com.shemnon.btc.coinbase.CBAddress
import com.shemnon.btc.coinbase.CBTransaction
import javafx.scene.control.TreeItem

/**
 * Created by shemnon on 8 Mar 2014.
 */
class FamousEntries {

    static TreeItem<JsonBase> createFamousTree() {
        TreeItem<JsonBase> addresses = new TreeItem<>(new JsonBaseLabel("Addresses"))
        addresses.children.addAll(
                new TreeItem<>(new CBAddress([
                        label: 'Dorian Nakamoto Donations',
                        address: '1Dorian4RoXcnBv9hnQ4Y2C1an6NJ4UrjX',
                        created_at: ''
                ])),
                new TreeItem<>(new CBAddress([
                        label: 'ESPN \'HiMom\' Sign Guy',
                        address: '1HiMoMgBaAikFHgAt3M4YJtetp4HrnsiXu',
                        created_at: ''
                ])),
                new TreeItem<>(new CBAddress([
                        label: 'Silk Road Seized Coins',
                        address: '1F1tAaz5x1HUXrCNLbtMDqcw6o5GNn4xqX',
                        created_at: ''
                ])),
                new TreeItem<>(new CBAddress([
                        label: 'CryptoCrumb Tip Jar',
                        address: '1CRUMB7EPfZjVPqML1Wk4q8WJdusLufsVr',
                        created_at: ''
                ]))
        )
        //Other tip jars?
        

        TreeItem<JsonBase> transactions = new TreeItem<>(new JsonBaseLabel("Transactions"))
        transactions.children.addAll(
                new TreeItem<>(new CBTransaction(
                        [ notes: 'MtGox 424242.42424242',
                        amount:[amount:'424242.42424242', currency:'BTC'], 
                        created_at: '2011-06-23T06:50:15+00:00', 
                        hsh:'3a1b9e330d32fef1ee42f8e86420d2be978bbe0dc5862f17da9027cf9e11f8c4'])
                ),
                new TreeItem<>(new CBTransaction(
                        [notes: 'MtGox 180K Binary Split',
                        amount:[amount:'180000', currency:'BTC'], 
                        created_at: '2014-03-07T07:24:02+00:00', 
                        hsh:'1dda0f8827518ce4d1d824bf7600f75ec7e199774a090a947c58a65ab63552e3'])
                ),
                new TreeItem<>(new CBTransaction(
                        [notes: 'MtGox 29K Binary Split',
                        amount:[amount:'29999.99', currency:'BTC'], 
                        created_at: '2014-03-07T05:01:08+00:00', 
                        hsh:'02aa71ee0fc00e7cc867b490de78db434f6045e95c3864c607b0dca151186d1e'])
                ),
        )
        // other heists?

        TreeItem<JsonBase> rootLabel = new TreeItem<>(new JsonBaseLabel("Famous Entries"))
        rootLabel.children.addAll(addresses, transactions)
        return rootLabel
    }
    
}
