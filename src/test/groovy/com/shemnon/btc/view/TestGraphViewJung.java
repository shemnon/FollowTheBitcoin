package com.shemnon.btc.view;

import com.shemnon.btc.blockchaininfo.CoinInfo;
import com.shemnon.btc.blockchaininfo.OfflineData;
import com.shemnon.btc.blockchaininfo.TXInfo;
import com.shemnon.btc.graph.TransactionCoinGraphJung;
import com.sun.javafx.application.PlatformImpl;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

/**
 * 
 * Created by shemnon on 3 Mar 2014.
 */
public class TestGraphViewJung {

    @BeforeClass
    public static void loadOfflineData() {
        OfflineData.loadOfflineData();
    }

    @Test
    public void testSimpleGraph() throws InterruptedException {
        PlatformImpl.startup(() -> {});
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() ->  {
            TransactionCoinGraphJung tcg = new TransactionCoinGraphJung();
    
            TXInfo tx42 = TXInfo.query("3a1b9e330d32fef1ee42f8e86420d2be978bbe0dc5862f17da9027cf9e11f8c4");
            tcg.addTransaction(tx42);
            tx42.getOutputs().forEach(tcg::addCoin);
            tx42.getInputs().forEach(tcg::addCoin);
            
            tcg.layout();
    
            GraphViewJung<TXInfo, CoinInfo> gv = new GraphViewJung<>();
            gv.setGraph(tcg.getGraph());
            gv.setLayout(tcg.getLayout());
            Pane pane = new Pane();
            gv.setGraphPane(pane);
            gv.rebuildGraph();
            
            pane.setScaleX(0.5);
            pane.setScaleY(0.5);
            ScrollPane sp = new ScrollPane(pane);
            sp.setPannable(true);
            
            Scene scene = new Scene(sp);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setOnHiding(e -> latch.countDown());
            stage.setOnCloseRequest(e -> {latch.countDown(); stage.close(); });
            
            stage.show();
        });
        latch.await();
    }
}
