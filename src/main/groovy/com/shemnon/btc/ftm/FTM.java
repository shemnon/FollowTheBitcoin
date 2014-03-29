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

package com.shemnon.btc.ftm;

import com.shemnon.btc.analysis.Pyramid;
import com.shemnon.btc.analysis.SpendAndChange;
import com.shemnon.btc.model.*;
import com.shemnon.btc.coinbase.CBAddress;
import com.shemnon.btc.coinbase.CBTransaction;
import com.shemnon.btc.coinbase.CoinBaseAPI;
import com.shemnon.btc.coinbase.CoinBaseOAuth;
import com.shemnon.btc.view.GraphViewMXGraph;
import com.shemnon.btc.view.ZoomPane;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WritableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Duration;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static javafx.beans.binding.Bindings.isEmpty;
import static javafx.beans.binding.Bindings.not;

/**
 * 
 * Created by shemnon on 4 Mar 2014.
 */
public class FTM {

    @FXML HBox boxHeader;
    @FXML VBox boxRightSide;
    @FXML VBox boxSide;
    @FXML Button buttonLogin;
    @FXML Button buttonLogout;
    @FXML CheckBox checkTxBtc;
    @FXML CheckBox checkTxCoins;
    @FXML CheckBox checkTxDate;
    @FXML CheckBox checkTxHash;
    @FXML CheckBox checkTxHeight;
    @FXML CheckBox checkTxUsd;
    @FXML CheckBox checkUnspentBtc;
    @FXML CheckBox checkUnspentHash;
    @FXML CheckBox checkUnspentUsd;
    @FXML ChoiceBox<String> choiceType;
    @FXML CheckBox helpCheckbox;
    @FXML AnchorPane helpPane;
    @FXML Label labelProgressBacklog;
    @FXML ToggleGroup lines;
    @FXML HBox loginPane;
    @FXML AnchorPane mapPane;
    @FXML AnchorPane paneLogin;
    @FXML ProgressIndicator progressIndicator;
    @FXML RadioButton radioLineAddr;
    @FXML RadioButton radioLineBTC;
    @FXML RadioButton radioLineNone;
    @FXML RadioButton radioLineUSD;
    @FXML TextField textSearch;
    @FXML SplitMenuButton tipButton;
    @FXML Label tipLabel;
    @FXML HBox tipPanel;
    @FXML ToggleButton toggleHelp;
    @FXML ToggleButton toggleRightSidebar;
    @FXML ToggleButton toggleSidebar;
    @FXML TreeView<IBase> treeViewEntries;
    @FXML WebView webViewLogin;

    TreeItem<IBase> treeRoot = new TreeItem<>(new JsonBaseLabel(""));
    TreeItem<IBase> coinbaseTreeLabel = new TreeItem<>(new JsonBaseLabel("Coinbase Entries"));
    TreeItem<IBase> coinbaseAddresses = new TreeItem<>(new JsonBaseLabel("Addresses"));
    TreeItem<IBase> coinbaseTransactions = new TreeItem<>(new JsonBaseLabel("Transactions"));
    CoinBaseOAuth coinBaseAuth;
    CoinBaseAPI coinBaseAPI;
    Duration slideTime = Duration.millis(400);
    Timeline sidebarTimeline;
    Timeline rightSidebarTimeline;
    Timeline tipbarTimeline;
    ExecutorService offThreadExecutor = Executors.newSingleThreadExecutor();
    ObservableList<Future<?>> futures = FXCollections.observableArrayList();
    ObjectProperty<IBase> menuSelectedItem = new SimpleObjectProperty<>(null);
    MenuItem miExpand = new MenuItem("Expand");
    MenuItem miExpandOutputs = new MenuItem("Expand Outputs");
    MenuItem miExpandAllOutputs = new MenuItem("Expand All Outputs");
    MenuItem miExpandInputs = new MenuItem("Expand Inputs");
    MenuItem miExpandAllInputs = new MenuItem("Expand All Inputs");
    MenuItem miClimbPyramid = new MenuItem("Climb Pyramid");
    MenuItem miDescendPyramid = new MenuItem("Descend Pyramid");
    MenuItem miClimbSNC = new MenuItem("Climb Spend and Change");
    MenuItem miDescendSNC = new MenuItem("Descend Spend and Change");
    MenuItem miRemoveTX = new MenuItem("Remove Transaction");
    MenuItem miPruneUpwards = new MenuItem("Prune Tx and Inputs");
    MenuItem miPruneDownwards = new MenuItem("Prune Tx and Outputs");
    ContextMenu nodeContextMenu = new ContextMenu(
            miExpand,
            new SeparatorMenuItem(),
            miExpandOutputs,
            miExpandAllOutputs,
            new SeparatorMenuItem(),
            miExpandInputs,
            miExpandAllInputs,
            new SeparatorMenuItem(),
            miClimbPyramid,
            miDescendPyramid,
            new SeparatorMenuItem(),
            miClimbSNC,
            miDescendSNC,
            new SeparatorMenuItem(),
            miRemoveTX,
            miPruneUpwards,
            miPruneDownwards
    );
    private GraphViewMXGraph gv;
    private ZoomPane zp;

    public void offThread(Runnable r) {
        Future<?> f = offThreadExecutor.submit(() -> {
            try {
                r.run();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        });
        futures.add(f);
        Timeline t = new Timeline(new KeyFrame(Duration.millis(100),
                event -> {
                    futures.removeIf(Future::isDone);
                    if (futures.isEmpty()) {
                        labelProgressBacklog.setText("");
                    } else {
                        labelProgressBacklog.setText(Integer.toString(futures.size()));
                    }
                }
        ));
        t.setCycleCount(Animation.INDEFINITE);
        t.play();
    }

    @FXML
    public void newHash(ActionEvent event) {
        String type = choiceType.getValue();
        String hash = textSearch.getText();
        offThread(() -> {
            IBase jd;
            switch (type) {
                case "Transaction":
                    jd = ITx.query(hash);
                    break;
                case "Address":
                    jd = IAddress.query(hash);
                    break;
                case "Block":
                    jd = IBlock.query(hash);
                    break;
                default:
                    return;
            }
            IBase jb = jd;
            expandObject(jb);
            Platform.runLater(() -> {
                gv.layout();
                gv.rebuildGraph(true);
                zp.layout();
                zp.centerOnNode(gv.getNode(jb));
            });
        });
    }

    private void expandBlock(IBlock bi) {
        if (Platform.isFxApplicationThread()) {
            offThread(() -> expandBlock(bi));
        } else {
            bi.getTXs().forEach(this::expandTransaction);
        }
    }

    private void expandTransaction(ITx tx) {
        if (Platform.isFxApplicationThread()) {
            offThread(() -> expandTransaction(tx));
        } else {
            expandInputs(tx);
            expandOutputs(tx);
        }
    }

    private void expandOutputs(ITx tx) {
        //todo check flag to visualize unspent outputs
        tx.getOutputs().forEach(gv::addCoin);
        gv.updateExpanded();
    }

    private void expandInputs(ITx tx) {
        tx.getInputs().forEach(gv::addCoin);
        gv.updateExpanded();
    }

    private void expandAddress(IAddress ai) {
        String address = ai.getAddress();
        if (address == null) return;
        
        if (Platform.isFxApplicationThread()) {
            offThread(() -> expandAddress(ai));
        } else {
            ai.getTXs().forEach(tx -> {
                Consumer<? super ICoin> maybeShowCoin = coin ->
                {
                    if (address.equals(coin.getAddr())) {
                        gv.addCoin(coin);
                    }
                };
                tx.getInputs().forEach(maybeShowCoin);
                tx.getOutputs().forEach(maybeShowCoin);
            });
            gv.updateExpanded();
        }
    }

    @FXML
    public void onReset(ActionEvent event) {
        gv.reset();
        gv.layout();
        gv.rebuildGraph(false);
    }

    @FXML
    public void onCenter(ActionEvent event) {
        zp.center();
    }

    @FXML
    public void onZoomToFit(ActionEvent event) {
        zp.fit();
    }

    @FXML
    public void onDeZoom(ActionEvent event) {
        zp.zoomOneToOne();
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
        hideRightSidebar();
        toggleRightSidebar.setSelected(false);
    }

    private void hideSidebar() {
        sidebarTimeline.setRate(-1);
        Duration time = sidebarTimeline.getCurrentTime();
        sidebarTimeline.stop();
        sidebarTimeline.playFrom(time);
    }

    @FXML
    public void toggleRightSidebar(ActionEvent event) {
        if (toggleRightSidebar.isSelected()) {
            showRightSidebar();
        } else {
            hideRightSidebar();
        }
    }

    private void showRightSidebar() {
        rightSidebarTimeline.setRate(1);
        Duration time = rightSidebarTimeline.getCurrentTime();
        rightSidebarTimeline.stop();
        rightSidebarTimeline.playFrom(time);
        hideSidebar();
        toggleSidebar.setSelected(false);
    }

    private void hideRightSidebar() {
        rightSidebarTimeline.setRate(-1);
        Duration time = rightSidebarTimeline.getCurrentTime();
        rightSidebarTimeline.stop();
        rightSidebarTimeline.playFrom(time);
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
    
    @FXML
    void closeHelp(ActionEvent event) {
        helpPane.setVisible(false);
    }
    
    @FXML
    void toggleHelp(ActionEvent event) {
        helpPane.setVisible(!helpPane.isVisible());
    }


    public void updateCoinbaseData() {
        String token = coinBaseAuth.getAccessToken();
        //noinspection StatementWithEmptyBody
        if (token == null) {
            // do nothing 
        } else if (token.isEmpty()) {
            Platform.runLater(() -> {
                // expand famous transactions
                TreeItem<IBase> famousTransactions = treeViewEntries.getRoot().getChildren().get(1);
                famousTransactions.getChildren().forEach(ti -> ti.setExpanded(true));
                famousTransactions.setExpanded(true);

                //JavaFX bug workaround
                treeViewEntries.setShowRoot(true);
                treeViewEntries.setShowRoot(false);
                
                showSidebar();
                toggleSidebar.setSelected(true);
            });
        } else {
            String un = coinBaseAPI.getUserName();
    
            List<CBAddress> addresses = coinBaseAPI.getAddresses();
            List<CBTransaction> transactions = coinBaseAPI.getTransactions();
    
            // scrub in-coinbase transaction since they supply no blockchain hash
            transactions.removeIf(trans -> (trans.getHash() == null));
    
            Platform.runLater(() -> {
                buttonLogout.setText("Logout " + un);
                // expand coinbase children
                // expand coinbase
                coinbaseTreeLabel.setExpanded(true);
                coinbaseAddresses.setExpanded(true);
                coinbaseTransactions.setExpanded(true);

                //noinspection Convert2Diamond,Convert2MethodRef
                coinbaseAddresses.getChildren().setAll(addresses.stream()
                        .map(addr -> new TreeItem<IBase>(addr))
                        .collect(Collectors.toList())
                );
                //noinspection Convert2Diamond,Convert2MethodRef
                coinbaseTransactions.getChildren().setAll(transactions.stream()
                        .map(tx -> new TreeItem<IBase>(tx))
                        .collect(Collectors.toList())
                );
                
                //JavaFX bug workaround
                treeViewEntries.setShowRoot(true);
                treeViewEntries.setShowRoot(false);

                showSidebar();
                toggleSidebar.setSelected(true);
            });
        }
    }

    public void expandObject(Object jbo) {
        if (Platform.isFxApplicationThread()) {
            offThread(() -> expandObject(jbo));
        } else {
            // thunk out coinbase to blockchain
            Object jb = jbo;
            if (jb instanceof CBAddress) {
                jb = IAddress.query(((CBAddress) jb).getAddress());
            } else if (jb instanceof CBTransaction) {
                jb = ITx.query(((CBTransaction) jb).getHash());
            }

            // expand blockchain 
            if (jb instanceof IAddress) {
                expandAddress((IAddress) jb);
            } else if (jb instanceof ITx) {
                expandTransaction((ITx) jb);
            } else if (jb instanceof IBlock) {
                expandBlock((IBlock) jb);
            } else if (jb instanceof ICoin) {
                gv.addCoin((ICoin) jb);
                gv.updateExpanded();
            } else {
                System.out.println("playSound(StandardSounds.SAD_TROMBONE)");
            }
        }
    }

    private void updateHighlights() {
    }

    private void expandSelected(ActionEvent event) {
        offThread(() -> {
            expandObject(menuSelectedItem.get());
            graphNeedsUpdating(true);
        });
    }

    private void expandOutputs(ActionEvent event) {
        offThread(() -> {
            expandOutputs((ITx) menuSelectedItem.get());
            graphNeedsUpdating(true);
        });
    }

    private void expandAllOutputs(ActionEvent event) {
        offThread(() -> {
            for (ITx tx : gv.findUnexpandedOutputTX((ITx) menuSelectedItem.get())) {
                expandOutputs(tx);
            }
            gv.updateExpanded();
            graphNeedsUpdating(true);
        });
    }

    private void expandInputs(ActionEvent event) {
        offThread(() -> {
            Object o = menuSelectedItem.get();
            if (o instanceof ICoin) {
                gv.addCoin((ICoin) o);
            } else if (o instanceof ITx) {
                expandInputs((ITx) o);
            }
            gv.updateExpanded();
            graphNeedsUpdating(true);
        });
    }

    private void expandAllInputs(ActionEvent event) {
        offThread(() -> {
            Object o = menuSelectedItem.get();
            if (o instanceof ICoin) {
                gv.addCoin((ICoin)o);
                o = ((ICoin)o).getSourceTX();
            }
            if (o instanceof ITx) {
                for (ITx tx : gv.findUnexpandedInputTX((ITx) o)) {
                    expandInputs(tx);
                }
            }
            gv.updateExpanded();
            graphNeedsUpdating(true);
        });
    }

    private void climbPyramid(ActionEvent event) {
        offThread(() -> {
            Object o = menuSelectedItem.get();
            if (o instanceof ITx) {
                for (ITx tx : Pyramid.climbPyramid((ITx) o, 20)) {
                    expandInputs(tx);
                }
            }
            gv.updateExpanded();
            graphNeedsUpdating(true);
        });
    }

    private void descendPyramid(ActionEvent event) {
        offThread(() -> {
            Object o = menuSelectedItem.get();
            if (o instanceof ITx) {
                for (ITx tx : Pyramid.descendPyramid((ITx) o, 2)) {
                    expandOutputs(tx);
                }
            }
            gv.updateExpanded();
            graphNeedsUpdating(true);
        });
    }

    private void climbSNC(ActionEvent event) {
        offThread(() -> {
            Object o = menuSelectedItem.get();
            if (o instanceof ITx) {
                for (ITx tx : SpendAndChange.climbSpendAndChange((ITx) o, 20)) {
                    expandTransaction(tx);
                }
            }
            gv.updateExpanded();
            graphNeedsUpdating(true);
        });
    }

    private void descendSND(ActionEvent event) {
        offThread(() -> {
            Object o = menuSelectedItem.get();
            if (o instanceof ITx) {
                for (ITx tx : SpendAndChange.descendSpendAndChange((ITx) o, 20)) {
                    expandTransaction(tx);
                }
            }
            gv.updateExpanded();
            graphNeedsUpdating(true);
        });
    }

    private void removeSelected(ActionEvent event) {
        offThread(() -> {
            Object o = menuSelectedItem.get();
            if (o instanceof ICoin) {
                gv.removeCoinAsNode((ICoin) o);
            } else if (o instanceof ITx) {
                gv.removeTX((ITx) o);
            }
            gv.updateExpanded();
            graphNeedsUpdating(true);
        });
    }

    private void pruneUpwards(ActionEvent event) {
        offThread(() -> {
            Object o = menuSelectedItem.get();
            if (o instanceof ICoin) {
                gv.removeTXAndAllInputs(((ICoin)o).getSourceTX());
                gv.removeCoinAsNode((ICoin) o);
            } else if (o instanceof ITx) {
                gv.removeTXAndAllInputs((ITx) o);
            }
            gv.updateExpanded();
            graphNeedsUpdating(true);
        });
    }

    private void pruneDownwards(ActionEvent event) {
        offThread(() -> {
            Object o = menuSelectedItem.get();
            if (o instanceof ITx) {
                gv.removeTXAndAllOutputs((ITx) o);
            }
            gv.updateExpanded();
            graphNeedsUpdating(true);
        });
    }


    @FXML
    void initialize() {
        try {
            miExpand.setOnAction(this::expandSelected);
            miExpandOutputs.setOnAction(this::expandOutputs);
            miExpandAllOutputs.setOnAction(this::expandAllOutputs);
            miExpandInputs.setOnAction(this::expandInputs);
            miExpandAllInputs.setOnAction(this::expandAllInputs);
            miClimbPyramid.setOnAction(this::climbPyramid);
            miDescendPyramid.setOnAction(this::descendPyramid);
            miClimbSNC.setOnAction(this::climbSNC);
            miDescendSNC.setOnAction(this::descendSND);
            miRemoveTX.setOnAction(this::removeSelected);
            miPruneUpwards.setOnAction(this::pruneUpwards);
            miPruneDownwards.setOnAction(this::pruneDownwards);

            menuSelectedItem.addListener((obv, newN, oldN) -> {
                if (oldN instanceof ITx) {
                    miExpand.setDisable(false); // TODO check expanded
                    miExpandOutputs.setDisable(false); // TODO check expanded
                    miExpandAllOutputs.setDisable(false);
                    miExpandInputs.setDisable(false); // TODO check expanded
                    miExpandAllInputs.setDisable(false);
                    miClimbPyramid.setDisable(false);
                    miDescendPyramid.setDisable(false);
                    miClimbSNC.setDisable(false);
                    miDescendSNC.setDisable(false);
                    miRemoveTX.setDisable(false);
                    miPruneUpwards.setDisable(false);
                    miPruneDownwards.setDisable(false);
                } else if (oldN instanceof ICoin) {
                    miExpand.setDisable(false); // TODO check expanded
                    miExpandOutputs.setDisable(true); 
                    miExpandAllOutputs.setDisable(true);
                    miExpandInputs.setDisable(true); 
                    miExpandAllInputs.setDisable(false);
                    miClimbPyramid.setDisable(true); //FIXME this can be done
                    miDescendPyramid.setDisable(true);
                    miClimbSNC.setDisable(true); //FIXME this can be done
                    miDescendSNC.setDisable(true);
                    miRemoveTX.setDisable(false);
                    miPruneUpwards.setDisable(false);
                    miPruneDownwards.setDisable(true);
                } else {
                    miExpand.setDisable(true);
                    miExpandOutputs.setDisable(true);
                    miExpandAllOutputs.setDisable(true);
                    miExpandInputs.setDisable(true);
                    miExpandAllInputs.setDisable(true);
                    miClimbPyramid.setDisable(true);
                    miDescendPyramid.setDisable(true);
                    miClimbSNC.setDisable(true);
                    miDescendSNC.setDisable(true);
                    miRemoveTX.setDisable(true);
                    miPruneUpwards.setDisable(true);
                    miPruneDownwards.setDisable(true);
                }
            });

            progressIndicator.visibleProperty().bind(not(isEmpty(futures)));

            //noinspection unchecked
            treeRoot.getChildren().setAll(coinbaseTreeLabel, FamousEntries.createFamousTree());
            //noinspection unchecked
            coinbaseTreeLabel.getChildren().setAll(coinbaseAddresses, coinbaseTransactions);

            treeViewEntries.setRoot(treeRoot);

            treeViewEntries.setCellFactory(list -> new CoinbaseDataCell(jd -> {
                expandObject(jd);
                offThread(() -> Platform.runLater(() -> {
                    gv.layout();
                    gv.rebuildGraph(true);
                    zp.layout();
                    zp.centerOnNode(gv.getNode(jd));
                }));
            }));
            treeViewEntries.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            treeViewEntries.getSelectionModel().getSelectedItems().addListener((InvalidationListener) event -> updateHighlights());

            gv = new GraphViewMXGraph((event, tx) -> {
                if (event.isPopupTrigger() || (event.getButton() == MouseButton.SECONDARY && event.getClickCount() == 1)) {
                    menuSelectedItem.setValue(tx);
                    nodeContextMenu.show(event.getPickResult().getIntersectedNode(), event.getScreenX(), event.getScreenY());
                } else if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2 && tx instanceof ITx) {
                    expandTransaction((ITx) tx);
                    graphNeedsUpdating(true);
                }
            });
            zp = new ZoomPane(gv.getGraphPane());
            mapPane.getChildren().setAll(zp);
            AnchorPane.setTopAnchor(zp, 0.0);
            AnchorPane.setRightAnchor(zp, 0.0);
            AnchorPane.setBottomAnchor(zp, 0.0);
            AnchorPane.setLeftAnchor(zp, 0.0);

            coinBaseAuth = new CoinBaseOAuth(webViewLogin);

            //can't bind because we want a weak push
            coinBaseAuth.visualAuthInProgressProperty().addListener((obv, o, n) -> paneLogin.setVisible(n));

            buttonLogin.managedProperty().bind(buttonLogin.visibleProperty());
            buttonLogout.managedProperty().bind(buttonLogout.visibleProperty());

            buttonLogout.visibleProperty().bind(
                    Bindings.and(
                        Bindings.isNotNull(coinBaseAuth.accessTokenProperty()),
                        Bindings.notEqual("", coinBaseAuth.accessTokenProperty())));
            buttonLogin.visibleProperty().bind(
                    Bindings.and(
                            Bindings.isNotNull(coinBaseAuth.accessTokenProperty()),
                            Bindings.equal("", coinBaseAuth.accessTokenProperty())));

            WritableValue<Number> leftOffset = new WritableValue<Number>() {
                @Override
                public Number getValue() {
                    return AnchorPane.getLeftAnchor(boxHeader);
                }

                @Override
                public void setValue(Number value) {
                    AnchorPane.setLeftAnchor(boxHeader, value.doubleValue());
                }
            };
            
            WritableValue<Number> rightOffset = new WritableValue<Number>() {
                @Override
                public Number getValue() {
                    return AnchorPane.getRightAnchor(boxHeader);
                }

                @Override
                public void setValue(Number value) {
                    AnchorPane.setRightAnchor(boxHeader, value.doubleValue());
                }
            };
            
            sidebarTimeline = new Timeline(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(boxSide.translateXProperty(), 0, Interpolator.EASE_BOTH),
                            new KeyValue(leftOffset, 0, Interpolator.EASE_BOTH)),
                    new KeyFrame(slideTime,
                            new KeyValue(boxSide.translateXProperty(), 250, Interpolator.EASE_BOTH),
                            new KeyValue(leftOffset, 250, Interpolator.EASE_BOTH))
            );
            sidebarTimeline.setAutoReverse(false);

            rightSidebarTimeline = new Timeline(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(boxRightSide.translateXProperty(), 0, Interpolator.EASE_BOTH),
                            new KeyValue(rightOffset, 0, Interpolator.EASE_BOTH)),
                    new KeyFrame(slideTime,
                            new KeyValue(boxRightSide.translateXProperty(), -250, Interpolator.EASE_BOTH),
                            new KeyValue(rightOffset, 250, Interpolator.EASE_BOTH))
            );
            rightSidebarTimeline.setAutoReverse(false);
            
            tipbarTimeline = new Timeline(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(tipPanel.translateYProperty(), 0, Interpolator.EASE_BOTH)),
                    new KeyFrame(slideTime,
                            new KeyValue(tipPanel.translateYProperty(), -40, Interpolator.EASE_BOTH))
            );
            tipbarTimeline.setAutoReverse(false);
            
            new Timeline(new KeyFrame(Duration.minutes(1), e -> showTipBar())).play();

            gv.showTxHashProperty().bindBidirectional(checkTxHash.selectedProperty());
            gv.showTxBitcoinProperty().bindBidirectional(checkTxBtc.selectedProperty());
            gv.showTxUSDProperty().bindBidirectional(checkTxUsd.selectedProperty());
            gv.showTxDateProperty().bindBidirectional(checkTxDate.selectedProperty());
            gv.showTxHeightProperty().bindBidirectional(checkTxHeight.selectedProperty());
            gv.showTxCoinCountProperty().bindBidirectional(checkTxCoins.selectedProperty());
            gv.showCoinNodeHashProperty().bindBidirectional(checkUnspentHash.selectedProperty());
            gv.showCoinNodeBitcoinProperty().bindBidirectional(checkUnspentBtc.selectedProperty());
            gv.showCoinNodeUSDProperty().bindBidirectional(checkUnspentUsd.selectedProperty());
            
            gv.layoutFlags().forEach(bp ->
                    bp.addListener((obv, o, n) -> {
                        gv.resizeGraphNodes();
                        gv.layout();
                        gv.rebuildGraph(false);
                    })
            );

            radioLineBTC.selectedProperty().addListener((obv, o, n) -> {
                if (n) {
                    changeLineSummary(true, false, false);
                }
            });
            radioLineUSD.selectedProperty().addListener((obv, o, n) -> {
                if (n) {
                    changeLineSummary(false, true, false);
                }
            });
            radioLineAddr.selectedProperty().addListener((obv, o, n) -> {
                if (n) {
                    changeLineSummary(false, false, true);
                }
            });
            radioLineNone.selectedProperty().addListener((obv, o, n) -> {
                if (n) {
                    changeLineSummary(false, false, false);
                }
            });
            
            coinBaseAPI = new CoinBaseAPI(coinBaseAuth, false, false);
            coinBaseAuth.accessTokenProperty().addListener(change -> offThread(this::updateCoinbaseData));
            offThread(() -> coinBaseAuth.checkTokens(true, false));
            
            helpCheckbox.selectedProperty().addListener((obv, o, n) -> coinBaseAuth.saveToken(Boolean.toString(n), "hideHelp"));
            
            String hideHelp = coinBaseAuth.loadToken("hideHelp");
            if (Boolean.valueOf(hideHelp)) {
                helpCheckbox.setSelected(true);
                toggleHelp.setSelected(false);
                helpPane.setVisible(false);
            } else {
                helpCheckbox.setSelected(false);
                toggleHelp.setSelected(true);
                helpPane.setVisible(true);
            }

        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    private void changeLineSummary(boolean btc, boolean usd, boolean addr) {
        ICoin.setShowEdgeBTC(btc);
        ICoin.setShowEdgeUSD(!btc && usd);
        ICoin.setShowEdgeAddr(!btc && !usd && addr);
        
        gv.resizeGraphNodes();
        gv.layout();
        gv.rebuildGraph(false);
    }

    private void graphNeedsUpdating(boolean animate) {
        offThread(() -> Platform.runLater(() -> {
            gv.layout();
            gv.rebuildGraph(animate);
        }));
    }


    private void showTipBar() {
        String tip = coinBaseAuth.loadToken("tip");
        if (tip != null && !tip.isEmpty()) {
            tipPanel.getChildren().remove(tipButton);
            tipLabel.setText("CryptoCrumb thanks you for your tip of " + tip);
            new Timeline(new KeyFrame(Duration.seconds(60), e -> hideTipBar())).play();
        } 
        tipbarTimeline.setRate(1);
        Duration time = tipbarTimeline.getCurrentTime();
        tipbarTimeline.stop();
        tipbarTimeline.playFrom(time);
        
    }

    public void hideTipBar(ActionEvent e) {
        hideTipBar();
    }

    private void hideTipBar() {
        tipbarTimeline.setRate(-1);
        Duration time = tipbarTimeline.getCurrentTime();
        tipbarTimeline.stop();
        tipbarTimeline.playFrom(time);
    }

    
    static final Map<String, String> tipMap = new TreeMap<>();
    static {
        tipMap.put("One Fiat US Dollar - $1.00",        "https://coinbase.com/checkouts/b25f15cccb7f69329dd640b27d092e5c");
        tipMap.put("Coffee - $1.38",                    "https://coinbase.com/checkouts/ee47e101a1d76f4180e40e6ca06266f3");
        tipMap.put("Tea - $1.62",                       "https://coinbase.com/checkouts/3c812a86bd866ad8c5353e0e402d9e2c");
        tipMap.put("Beer - $3.64",                      "https://coinbase.com/checkouts/75daf25fa8e45e17f347cb16579703a1");
        tipMap.put("Gold (reddit gold) - $3.99",        "https://coinbase.com/checkouts/f5aa3242de75abb42705e206320d9f98");
        tipMap.put("Dessert-like Coffee - $4.69",       "https://coinbase.com/checkouts/413dfa5da984e9ef73b4be5c4e444122");
        tipMap.put("A Pony (Twilight Sparkle) - $5.99", "https://coinbase.com/checkouts/bf657c6ac19609c18e5d1cbf0ad5abe1");
        tipMap.put("Merkle's Root Beer - $7.99",        "https://coinbase.com/checkouts/eab36e4a02658c5e4b23f9b10673105e");
    }
    
    public void tip(ActionEvent actionEvent) {
        String tip = coinBaseAuth.loadToken("tip");
        
        final String s;
        Object source = actionEvent.getSource();
        if (source instanceof SplitMenuButton) {
            s = ((SplitMenuButton)source).getText();
        } else if (source instanceof MenuItem) {
            s = ((MenuItem)source).getText();
        } else {
            hideTipBar();
            return;
        }
        
        String url = tipMap.get(s);
        if (url == null) return;
        
        WebEngine webEngine = webViewLogin.getEngine();

        @SuppressWarnings("unchecked") 
        ChangeListener<? super Worker.State>[] tipListener = new ChangeListener[1];
        
        tipListener[0] = (ov, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                String location = webEngine.getLocation();
                if (location.startsWith("http://cryptocrumb.com/success")) {
                    String[] stuff = s.split(" - ");
                    coinBaseAuth.saveToken(stuff[0], "tip");
                    webEngine.getLoadWorker().stateProperty().removeListener(tipListener[0]);
                }
            }
        };
        
        webEngine.getLoadWorker().stateProperty().addListener(tipListener[0]);
        webEngine.load(url);
        paneLogin.setVisible(true);

    }
}
