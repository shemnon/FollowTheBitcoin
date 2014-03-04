package com.shemnon.btc.graph;

import com.shemnon.btc.blockchaininfo.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * Created by shemnon on 2 Mar 2014.
 */
public class TransactionCoinGraphMXGraphTest {
    
    @BeforeClass
    public static void loadOfflineData() {
        OfflineData.loadOfflineData();
    }
    
    @Test
    public void test42() {
        TransactionCoinGraphMXGraph tcg = new TransactionCoinGraphMXGraph();
        
        TXInfo tx42 = TXInfo.query("3a1b9e330d32fef1ee42f8e86420d2be978bbe0dc5862f17da9027cf9e11f8c4"); 
        tcg.addTransaction(tx42);
        tx42.getOutputs().forEach(tcg::addCoin);
        tx42.getInputs().forEach(tcg::addCoin);
        
        tcg.layout();
        
        tcg.getTransactionVertexes().forEach(c -> System.out.println(c.getGeometry()));
        System.out.println("---");
        tcg.getCoinEdges().forEach(c -> System.out.println(c.getAbsolutePoints()));
        System.out.println("---");
        System.out.println("---");

        tcg.getTransactionVertexes().forEach(System.out::println);
        System.out.println("---");
        tcg.getCoinEdges().forEach(System.out::println);

        System.out.println("---");
        System.out.println(tcg.getGraphBounds());
    }
}
