package com.shemnon.btc.ftm;

import com.shemnon.btc.blockchaininfo.AddressInfo;
import com.shemnon.btc.blockchaininfo.BlockInfo;
import com.shemnon.btc.blockchaininfo.TXInfo;
import com.shemnon.btc.coinbase.CBAddress;
import com.shemnon.btc.coinbase.CBTransaction;
import com.shemnon.btc.coinbase.CoinBaseAPI;
import com.shemnon.btc.coinbase.CoinBaseOAuth;
import com.shemnon.btc.view.*;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.util.Duration;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static javafx.beans.binding.Bindings.isEmpty;
import static javafx.beans.binding.Bindings.not;

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
    @FXML HBox loginPane;
    @FXML Pane mapPane;
    @FXML AnchorPane paneLogin;
    @FXML ProgressIndicator progressIndicator;
    @FXML TextField textSearch;
    @FXML ToggleButton toggleSidebar;
    @FXML TreeView<JsonBase> treeViewEntries;
    @FXML WebView webViewLogin;

    TreeItem<JsonBase> treeRoot = new TreeItem<>(new JsonBaseLabel(""));
    TreeItem<JsonBase> coinbaseTreeLabel = new TreeItem<>(new JsonBaseLabel("Coinbase Entries"));
    TreeItem<JsonBase> coinbaseAddresses = new TreeItem<>(new JsonBaseLabel("Addresses"));
    TreeItem<JsonBase> coinbaseTransactions = new TreeItem<>(new JsonBaseLabel("Transactions"));

    private GraphViewMXGraph gv;
    
    CoinBaseOAuth coinBaseAuth;
    CoinBaseAPI coinBaseAPI;
    
    Duration slideTime = Duration.millis(400);

    Timeline sidebarTimeline;
    
    ExecutorService offThreadExecutor = Executors.newSingleThreadExecutor();
    ObservableList<Future<?>> futures = FXCollections.observableArrayList();
    
    public void offThread(Runnable r) {
        Future<?> f = offThreadExecutor.submit(r);
        futures.add(f);
        Timeline t = new Timeline(new KeyFrame(Duration.millis(100),
                event -> 
                    futures.removeIf( it -> it.isDone())
                ));
        t.setCycleCount(Animation.INDEFINITE);
        t.play();
    }

    @FXML
    public void newHash(ActionEvent event) {
        switch (choiceType.getValue()) {
            case "Transaction":
                TXInfo tx = TXInfo.query(textSearch.getText());
                expandTransaction(tx);
                break;
            case "Address":
                AddressInfo ai = AddressInfo.query(textSearch.getText());
                expandAddress(ai);
                break;
            case "Block":
                BlockInfo bi = BlockInfo.query(textSearch.getText());
                expandBlock(bi);
                break;
        }
        graphNeedsUpdating();
    }

    private void expandBlock(BlockInfo bi) {
        if (Platform.isFxApplicationThread()) {
            offThread(() -> expandBlock(bi));
        } else {
            bi.getTXs().forEach(this::expandTransaction);
        }
    }

    private void expandTransaction(TXInfo tx) {
        if (Platform.isFxApplicationThread()) {
            offThread(() -> expandTransaction(tx));
        } else {
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
        }
    }

    private void expandAddress(AddressInfo ai) {
        if (Platform.isFxApplicationThread()) {
            offThread(() -> expandAddress(ai));
        } else {
            ai.getTXs().forEach(this::expandTransaction);
        }
    }

    @FXML
    public void onReset(ActionEvent event) {
        gv.reset();
        gv.layout();
        gv.rebuildGraph();
    }
    
    @FXML
    public void onCenter(ActionEvent event) {
        System.out.println("Center");
    }

    @FXML
    public void toggleSidebar(ActionEvent event) {
        if (toggleSidebar.isSelected()) {
            showSidebar();
        } else {
            hideSidebar();
        }
    }

    private void showSidebar() {
        sidebarTimeline.setRate(1);
        Duration time = sidebarTimeline.getCurrentTime();
        sidebarTimeline.stop();
        sidebarTimeline.playFrom(time);
    }

    private void hideSidebar() {
        sidebarTimeline.setRate(-1);
        Duration time = sidebarTimeline.getCurrentTime();
        sidebarTimeline.stop();
        sidebarTimeline.playFrom(time);
        
    }

    @FXML
    void loginToCoinbase(ActionEvent event) {
        coinBaseAuth.checkTokens(true, true);
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

    
    public void updateCoinbaseData() {
        String un = coinBaseAPI.getUserName();
        
        List<CBAddress> addresses = coinBaseAPI.getAddresses();
        List<CBTransaction> transactions = coinBaseAPI.getTransactions();
        
        // scrub in-coinbase transaction since they supply no blockchain hash
        transactions.removeIf(trans -> (trans.getHash() == null));

        Platform.runLater(() -> {
            buttonLogout.setText("Logout " + un);
            //noinspection Convert2Diamond,Convert2MethodRef
            coinbaseAddresses.getChildren().setAll(addresses.stream()
                    .map(addr -> new TreeItem<JsonBase>(addr))
                    .collect(Collectors.toList())
            );
            //noinspection Convert2Diamond,Convert2MethodRef
            coinbaseTransactions.getChildren().setAll(transactions.stream()
                    .map(tx -> new TreeItem<JsonBase>(tx))
                    .collect(Collectors.toList())
            );
            showSidebar();
            toggleSidebar.setSelected(true);
        });
    }
    
    public void expandObject(JsonBase jbo) {
        if (Platform.isFxApplicationThread()) {
            offThread(() -> expandObject(jbo));
        } else {
            // thunk out coinbase to blockchain
            JsonBase jb = jbo;
            if (jb instanceof CBAddress) {
               jb = AddressInfo.query(((CBAddress)jb).getAddress()); 
            } else if (jb instanceof CBTransaction) {
                jb = TXInfo.query(((CBTransaction)jb).getHash());
            }
            
            // expand blockchain 
            if (jb instanceof AddressInfo) {
                expandAddress((AddressInfo)jb);
            } else if (jb instanceof TXInfo) {
                expandTransaction((TXInfo)jb);
            } else if (jb instanceof BlockInfo) {
                expandBlock((BlockInfo)jb);
            } else {
                System.out.println("playSound(StandardSounds.SAD_TROMBONE)");
            }
        }
    }

    private void updateHilights() {
    }
    
    
    @FXML
    void initialize() {
        try {
            progressIndicator.visibleProperty().bind(not(isEmpty(futures)));
            
            //noinspection unchecked
            treeRoot.getChildren().setAll(coinbaseTreeLabel, FamousEntries.createFamousTree());
            //noinspection unchecked
            coinbaseTreeLabel.getChildren().setAll(coinbaseAddresses, coinbaseTransactions);
            
            treeViewEntries.setRoot(treeRoot);

            treeViewEntries.setCellFactory(list -> new CoinbaseDataCell(jd -> {
                expandObject(jd);
                graphNeedsUpdating();
            }));
            treeViewEntries.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            treeViewEntries.getSelectionModel().getSelectedItems().addListener((InvalidationListener) event -> updateHilights());
            
            gv = new GraphViewMXGraph((event, tx) -> {
                if (event.getClickCount() == 2) {
                    expandTransaction(tx);
                    graphNeedsUpdating();
                }
            });
            ZoomPane zp = new ZoomPane(gv.getGraphPane());
            mapPane.getChildren().setAll(zp);
            
            coinBaseAuth = new CoinBaseOAuth(webViewLogin);
            
            //can't bind because we want a weak push
            coinBaseAuth.visualAuthInProgressProperty().addListener((obv, o, n) -> paneLogin.setVisible(n));

            buttonLogin.managedProperty().bind(buttonLogin.visibleProperty());
            buttonLogout.managedProperty().bind(buttonLogout.visibleProperty());

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

            coinBaseAPI = new CoinBaseAPI(coinBaseAuth, false, false);
            coinBaseAuth.accessTokenProperty().addListener(change -> offThread(this::updateCoinbaseData));
            offThread(() -> coinBaseAuth.checkTokens(true, false));
            
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    private void graphNeedsUpdating() {
        offThread(() -> Platform.runLater(() -> {
            gv.layout();
            gv.rebuildGraph();
        }));
    }
}
