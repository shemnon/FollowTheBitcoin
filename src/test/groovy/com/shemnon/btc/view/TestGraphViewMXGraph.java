package com.shemnon.btc.view;

import com.shemnon.btc.blockchaininfo.OfflineData;
import com.shemnon.btc.blockchaininfo.TXInfo;
import com.shemnon.btc.graph.TransactionCoinGraphMXGraph;
import com.sun.javafx.application.PlatformImpl;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 
 * Created by shemnon on 3 Mar 2014.
 */
public class TestGraphViewMXGraph {

    @BeforeClass
    public static void loadOfflineData() {
        OfflineData.loadOfflineData();
    }

    @Test
    public void testSimpleGraph() throws InterruptedException {
        PlatformImpl.startup(() -> {});
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() ->  {
            TransactionCoinGraphMXGraph tcg = new TransactionCoinGraphMXGraph();
    
            TXInfo tx42 = TXInfo.query("3a1b9e330d32fef1ee42f8e86420d2be978bbe0dc5862f17da9027cf9e11f8c4");
            tcg.addTransaction(tx42);
            List<TXInfo> moreTXes = new ArrayList<>();
            tx42.getOutputs().forEach(c -> {
                moreTXes.add(c.getTargetTX());
                tcg.addCoin(c);
            });
            for (int i = 0; i < 5; i++) {
                List<TXInfo> thisTX = new ArrayList<>(moreTXes);
                moreTXes.clear();
                thisTX.forEach((t) -> {
                    if (t != null && t.getOutputs() != null) {
                        t.getOutputs().forEach(c -> {
                            moreTXes.add(c.getTargetTX());
                            tcg.addCoin(c);
                        });
                    }
                });
            }
            tx42.getInputs().forEach(tcg::addCoin);
            
            tcg.layout();
    
            GraphViewMXGraph gv = new GraphViewMXGraph();
            gv.setView(tcg.getGraphView());
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
            stage.setOnCloseRequest(e -> {stage.close(); latch.countDown();});
            
            stage.show();
        });
        latch.await();
    }
}
