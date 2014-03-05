package com.shemnon.btc.ftm;

import com.shemnon.btc.blockchaininfo.AddressInfo;
import com.shemnon.btc.blockchaininfo.BlockInfo;
import com.shemnon.btc.blockchaininfo.TXInfo;
import com.shemnon.btc.view.*;
import javafx.event.ActionEvent;

/**     
 * 
 * Created by shemnon on 4 Mar 2014.
 */
public class FTM extends FTMGenerated {

    private GraphViewMXGraph gv;

    @Override
    public void newHash(ActionEvent event) {
        super.newHash(event);
        
        switch (choiceType.getValue()) {
            case "Transaction":
                TXInfo tx = TXInfo.query(textSearch.getText());
                gv.addTransaction(tx);
                tx.getInputs().forEach(coin -> {
                    gv.addTransaction(coin.getSourceTX());
                    gv.addCoin(coin);
                });
                tx.getOutputs().forEach(coin -> {
                    if (coin.getTargetTX() != null) {
                        gv.addTransaction(coin.getTargetTX());
                        gv.addCoin(coin);
                    }
                });
                break;
            case "Address":
                AddressInfo ai = AddressInfo.query(textSearch.getText());
                ai.getTXs().forEach(atx -> {
                    gv.addTransaction(atx);
                    atx.getInputs().forEach(coin -> {
                        gv.addTransaction(coin.getSourceTX());
                        gv.addCoin(coin);
                    });
                    atx.getOutputs().forEach(coin -> {
                        if (coin.getTargetTX() != null) {
                            gv.addTransaction(coin.getTargetTX());
                            gv.addCoin(coin);
                        }
                    });
                });
                break;
            case "Block":
                BlockInfo bi = BlockInfo.query(textSearch.getText());
                bi.getTXs().forEach(btx -> {
                    gv.addTransaction(btx);
                    btx.getInputs().forEach(coin -> {
                        gv.addTransaction(coin.getSourceTX());
                        gv.addCoin(coin);
                    });
                    btx.getOutputs().forEach(coin -> {
                        if (coin.getTargetTX() != null) {
                            gv.addTransaction(coin.getTargetTX());
                            gv.addCoin(coin);
                        }
                    });
                });
                break;
        }
        gv.layout();
        gv.rebuildGraph();
    }

    @Override
    public void onReset(ActionEvent event) {
        super.onReset(event);
    }

    @Override
    public void onCenter(ActionEvent event) {
        super.onCenter(event);
    }

    @Override
    void initialize() {
        super.initialize();

        gv = new GraphViewMXGraph();
        ZoomPane zp = new ZoomPane(gv.getGraphPane());
        mapPane.getChildren().setAll(zp);
    }
}
