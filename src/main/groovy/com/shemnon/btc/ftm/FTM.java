package com.shemnon.btc.ftm;

import com.shemnon.btc.blockchaininfo.AddressInfo;
import com.shemnon.btc.blockchaininfo.BlockInfo;
import com.shemnon.btc.blockchaininfo.TXInfo;
import com.shemnon.btc.coinbase.CoinBaseAPI;
import com.shemnon.btc.coinbase.CoinBaseOAuth;
import com.shemnon.btc.view.*;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.util.Duration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**     
 * 
 * Created by shemnon on 4 Mar 2014.
 */
public class FTM {

    @FXML HBox boxHeader;
    @FXML VBox boxSide;
    @FXML Button buttonLogin;
    @FXML Button buttonLogout;
    @FXML ChoiceBox<String> choiceType;
    @FXML Label labelUser;
    @FXML ListView<String> listAddresses;
    @FXML HBox loginPane;
    @FXML Pane mapPane;
    @FXML AnchorPane paneLogin;
    @FXML TextField textSearch;
    @FXML ToggleButton toggleSidebar;
    @FXML WebView webViewLogin;

    private GraphViewMXGraph gv;
    
    CoinBaseOAuth coinBaseAuth;
    CoinBaseAPI coinBaseAPI;
    
    Duration slideTime = Duration.millis(400);

    Timeline sidebarTimeline;
    
    ExecutorService offThread = Executors.newSingleThreadExecutor();

    @FXML
    public void newHash(ActionEvent event) {
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

    @FXML
    public void onReset(ActionEvent event) {
        System.out.println("Reset");
    }
    @FXML
    public void onCenter(ActionEvent event) {
        System.out.println("Center");
    }

    @FXML
    public void toggleSidebar(ActionEvent event) {
        if (toggleSidebar.isSelected()) {
            slideSidebarIn();
        } else {
            slideSidebarOut();
        }
    }

    private void slideSidebarIn() {
        sidebarTimeline.setRate(1);
        Duration time = sidebarTimeline.getCurrentTime();
        sidebarTimeline.stop();
        sidebarTimeline.playFrom(time);
    }

    private void slideSidebarOut() {
        sidebarTimeline.setRate(-1);
        Duration time = sidebarTimeline.getCurrentTime();
        sidebarTimeline.stop();
        sidebarTimeline.playFrom(time);
        
    }

    @FXML
    void loginToCoinbase(ActionEvent event) {
        coinBaseAuth.requestLogin();
    }

    @FXML
    void logoutOfCoinbase(ActionEvent event) {
        coinBaseAuth.clearTokens();
    }

    @FXML
    void closeWebView(ActionEvent event) {
        coinBaseAuth.setVisualAuthInProgress(false);
        paneLogin.setVisible(false);
    }

    
    public void updateUserName() {
        String un = coinBaseAPI.getUserName();
        Platform.runLater(() -> labelUser.setText("Coinbase user: " + un));
    }
    
    @FXML
    void initialize() {
        try {
            gv = new GraphViewMXGraph();
            ZoomPane zp = new ZoomPane(gv.getGraphPane());
            mapPane.getChildren().setAll(zp);
            
            coinBaseAuth = new CoinBaseOAuth(webViewLogin);
            
            //can't bind because we want a weak push
            coinBaseAuth.visualAuthInProgressProperty().addListener((obv, o, n) -> {System.out.println("vis - " + n); paneLogin.setVisible(n);});

            buttonLogin.managedProperty().bind(buttonLogin.visibleProperty());
            buttonLogout.managedProperty().bind(buttonLogout.visibleProperty());
            labelUser.managedProperty().bind(labelUser.visibleProperty());

            coinBaseAuth.accessTokenProperty().addListener(change -> 
                offThread.submit(this::updateUserName)
            );
            
            labelUser.visibleProperty().bind(Bindings.isNotNull(coinBaseAuth.accessTokenProperty()));
            buttonLogout.visibleProperty().bind(Bindings.isNotNull(coinBaseAuth.accessTokenProperty()));
            buttonLogin.visibleProperty().bind(Bindings.isNull(coinBaseAuth.accessTokenProperty()));
    
            sidebarTimeline = new Timeline(
                    new KeyFrame(Duration.ZERO,
                              new KeyValue(boxSide.translateXProperty(), 0, Interpolator.EASE_BOTH),
                              new KeyValue(boxHeader.translateXProperty(), 0, Interpolator.EASE_BOTH)),        
                    new KeyFrame(slideTime,
                              new KeyValue(boxSide.translateXProperty(), 250, Interpolator.EASE_BOTH),
                              new KeyValue(boxHeader.translateXProperty(), 250, Interpolator.EASE_BOTH))        
                );
            sidebarTimeline.setAutoReverse(false);

            coinBaseAPI = new CoinBaseAPI(coinBaseAuth, false);
            updateUserName();
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }
}
