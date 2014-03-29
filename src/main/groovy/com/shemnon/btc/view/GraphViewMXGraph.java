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

package com.shemnon.btc.view;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxGraphView;
import com.shemnon.btc.coinbase.CBTransaction;
import com.shemnon.btc.ftm.JsonBase;
import com.shemnon.btc.model.*;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import javax.swing.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.BiConsumer;

/**
 * 
 * Created by shemnon on 3 Mar 2014.
 */
public class GraphViewMXGraph {
    
    
    BiConsumer<MouseEvent, IBase> clickHandler;

    mxGraph mxGraph;
    mxIGraphModel mxGraphModel;
    private mxGraphView mxGraphView;
    mxGraphLayout mxGraphLayout;

    Map<String, Object> keyToVertexCell = new ConcurrentHashMap<>();
    Map<String, Object> keyToEdgeCell = new ConcurrentHashMap<>();
    Map<String, Pane> keyToVertexNode = new ConcurrentHashMap<>();
    Map<String, List<Line>> keyToEdgeNode = new ConcurrentHashMap<>();
    Map<String, Label> keyToEdgeLabel = new ConcurrentHashMap<>();

    Pane graphPane;
    
    BooleanProperty showTxHash = new SimpleBooleanProperty(true);
    BooleanProperty showTxBitcoin = new SimpleBooleanProperty(true);
    BooleanProperty showTxUSD = new SimpleBooleanProperty(false);
    BooleanProperty showTxDate = new SimpleBooleanProperty(true);
    BooleanProperty showTxHeight = new SimpleBooleanProperty(true);
    BooleanProperty showTxCoinCount = new SimpleBooleanProperty(false);
    
    BooleanProperty showCoinNodeHash = new SimpleBooleanProperty(true);
    BooleanProperty showCoinNodeBitcoin = new SimpleBooleanProperty(true);
    BooleanProperty showCoinNodeUSD = new SimpleBooleanProperty(true);
    
    Timeline animatingTimeline;

    public GraphViewMXGraph(BiConsumer<MouseEvent, IBase> clickHandler) {
        this.clickHandler = clickHandler;

        mxGraph = new mxGraph();
        mxGraphModel = mxGraph.getModel();
        mxGraphView = mxGraph.getView();

        mxGraphLayout = new mxHierarchicalLayout(mxGraph, SwingConstants.NORTH);
        
        graphPane = new Pane();
    }

    public Object addTransaction(ITx tx) {
        try {
            String key = tx.getHash();
            Object o = keyToVertexCell.get(key);
            if (o == null || !mxGraphModel.isVertex(o)) {
                Node n = getFXNodeForVertexCell(tx);
                o = mxGraph.insertVertex(mxGraph.getDefaultParent(), tx.getHash(), tx, 0, 0, n.prefWidth(0), n.prefHeight(0));
                keyToVertexCell.put(key, o);
            }
            return o;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(((JsonBase)tx).dumpJson());
            throw e;
        }
    }

    public Object addCoinAsNode(ICoin coin) {
        try {
            String key = coin.getCompkey();
            Object o = keyToVertexCell.get(key);
            if (o == null || !mxGraphModel.isVertex(o)) {
                Node n = getFXNodeForVertexCell(coin);
                o = mxGraph.insertVertex(mxGraph.getDefaultParent(), coin.getCompkey(), coin, 0, 0, n.prefWidth(0), n.prefHeight(0));
                keyToVertexCell.put(key, o);
            }
            return o;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(((JsonBase)coin).dumpJson());
            throw e;
        }
    }

    public Object addCoin(ICoin ci) {
        try {
            String key = ci.getCompkey();
            Object o = keyToEdgeCell.get(key);
            if ((o == null || !mxGraphModel.isEdge(o)) && ci.getSourceTXID() != null) {
                ITx fromTX = ci.getSourceTX();
                ITx toTX = ci.getTargetTX();

                Object from = addTransaction(fromTX);
                Object to;
                if (toTX == null) {
                    to = addCoinAsNode(ci);
                } else {
                    to = addTransaction(toTX);
                }

                o = mxGraph.insertEdge(mxGraph.getDefaultParent(), key, ci, from, to);
                keyToEdgeCell.put(key, o);
            }
            return o;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void updateExpanded() {
        keyToVertexCell.values().forEach(cell -> {
            Object v = ((mxCell)cell).getValue();
            if (v instanceof ITx) {
                updateExpanded((ITx) v);
            }
        });
    }
    
    private void updateExpanded(ITx tx) {
        if (tx == null) return;
        
        Node n = keyToVertexNode.get(tx.getHash());
        if (n == null) return;

        boolean inputsExpanded = tx.getInputs().stream().allMatch(ci -> {
            String srcTXID = ci.getSourceTXID();
            boolean exists = srcTXID != null && keyToVertexCell.containsKey(srcTXID);
            if (exists && !keyToEdgeCell.containsKey(ci.getCompkey())) {
                addCoin(ci);
            }
            return exists;
        });

        boolean outputsExpanded = tx.getOutputs().stream().allMatch(ci -> {
            String targetTXID = ci.getTargetTXID();
            if (targetTXID == null) {
                boolean exists = keyToVertexCell.containsKey(ci.getCompkey());
                if (exists && !keyToEdgeCell.containsKey(ci.getCompkey())) {
                    addCoin(ci);
                }
                return exists;
            } else {
                boolean exists = keyToVertexCell.containsKey(targetTXID);
                if (exists && !keyToEdgeCell.containsKey(ci.getCompkey())) {
                    addCoin(ci);
                }
                return exists;
            }
        });

        Platform.runLater(() -> { 
            if (inputsExpanded && outputsExpanded) {
                if (!n.getStyleClass().contains("expanded")) {
                    n.getStyleClass().add("expanded");
                }
            } else {
                while (n.getStyleClass().remove("expanded"));
            }
        });
    }

    public void resizeGraphNodes() {
        for (Map.Entry<String, Object> e : keyToVertexCell.entrySet()) {
            if (e.getValue() instanceof mxCell) {
                String key = e.getKey();
                if (key.contains("#")) {
                    recreateUnspentOutput(ICoin.query(key));
                } else {
                    recreateTXNode(ITx.query(key));
                }
                
                Pane p = keyToVertexNode.get(e.getKey());
                ((mxCell) e.getValue()).getGeometry().setWidth(p.getWidth());
                ((mxCell) e.getValue()).getGeometry().setHeight(p.getHeight());
            }
        }
    }
    
    public void layout() {
        mxGraphLayout.execute(mxGraph.getDefaultParent());
    }

    public mxRectangle getGraphBounds() {
        return mxGraphView.getGraphBounds();
    }

    public void rebuildGraph(boolean animate) {
        mxRectangle bounds = mxGraphView.getGraphBounds();
        LinkedList<Node> newKids = new LinkedList<>();
        Set<KeyValue> animations = new CopyOnWriteArraySet<>();
        List<Node> nodesToDelete = new ArrayList<>();

        if (animatingTimeline != null && animatingTimeline.getStatus() == Animation.Status.RUNNING) {
            animatingTimeline.jumpTo(animatingTimeline.getTotalDuration().subtract(Duration.ONE));
        }
        
        mxGraphView.getStates().entrySet().forEach(
                entry -> {
                    mxCell cell = (mxCell) entry.getKey();
                    if (cell.isEdge()) {
                        String key = ((ICoin)cell.getValue()).getCompkey();

                        mxCellState cellState = entry.getValue();
                        List<Line> newLines = new ArrayList<>(cellState.getAbsolutePointCount() - 1);
                        List<Line> prevEdge = null;
                        Label prevLabel = null;
                        if (animate) {
                            prevEdge = keyToEdgeNode.get(key);
                            prevLabel = keyToEdgeLabel.get(key);
                        }
                        if (prevEdge != null && prevEdge.size() == 0) {
                            prevEdge = null;
                        }

                        // add the line label and fade in/move
                        if (cellState.getLabel().isEmpty()) {
                            if (prevLabel != null) {
                                nodesToDelete.add(prevLabel);
                                keyToEdgeLabel.remove(key, prevLabel);
                            }
                        }  else {
                            mxRectangle labelPoint = cellState.getLabelBounds();
                            if (prevLabel == null) {
                                prevLabel  = new Label(cellState.getLabel());
                                prevLabel.getStyleClass().add("edgeLabel");

                                prevLabel.resizeRelocate(labelPoint.getX(), labelPoint.getY(), labelPoint.getWidth(), labelPoint.getHeight());
                                prevLabel.setOpacity(0.0);
                                animations.add(new KeyValue(prevLabel.opacityProperty(), 1.0, Interpolator.EASE_IN));

                            } else {
                                prevLabel.setText(cellState.getLabel());
                                animations.add(
                                        new KeyValue(prevLabel.layoutXProperty(), labelPoint.getX(), Interpolator.EASE_BOTH));
                                animations.add(
                                        new KeyValue(prevLabel.layoutYProperty(), labelPoint.getY(), Interpolator.EASE_BOTH)
                                );
                            }
                            keyToEdgeLabel.put(key, prevLabel);
                            newKids.add(0, prevLabel);
                        }
                        
                        // move the lines and fade in/create
                        for (int i = 1; i < cellState.getAbsolutePointCount(); i ++) {
                            mxPoint prevPoint = cellState.getAbsolutePoint(i-1);
                            mxPoint nextPoint = cellState.getAbsolutePoint(i);
                            if (prevEdge != null) {
                                Line l;
                                if (prevEdge.size() >= i) {
                                    l = prevEdge.get(i-1);
                                } else {
                                    // if this is a new line, animate it from the start point
                                    Line lastLine = prevEdge.get(prevEdge.size() - 1);
                                    l = new Line(lastLine.getEndX(), lastLine.getEndY(), 
                                            lastLine.getEndX(), lastLine.getEndY());
                                    l.setFill(Color.BLACK);
                                }
                                newLines.add(l);
                                animations.add(new KeyValue(l.startXProperty(), prevPoint.getX(), Interpolator.EASE_BOTH));
                                animations.add(new KeyValue(l.startYProperty(), prevPoint.getY(), Interpolator.EASE_BOTH));
                                animations.add(new KeyValue(l.endXProperty(), nextPoint.getX(), Interpolator.EASE_BOTH));
                                animations.add(new KeyValue(l.endYProperty(), nextPoint.getY(), Interpolator.EASE_BOTH));
                            } else {
                                Line l = new Line(prevPoint.getX(), prevPoint.getY(), nextPoint.getX(), nextPoint.getY());
                                l.setFill(Color.BLACK);
                                newLines.add(l);
                                l.setOpacity(0.0);
                                animations.add(new KeyValue(l.opacityProperty(), 1.0, Interpolator.EASE_IN));
                            }
                        }
                        // if we have less lines than before, animate them into the end point
                        if (animate && prevEdge != null && prevEdge.size() > newLines.size()) {
                            mxPoint end = cellState.getAbsolutePoint(cellState.getAbsolutePointCount() - 1);
                            for (int i = newLines.size(); i < prevEdge.size(); i++) {
                                Line l = prevEdge.get(i);
                                animations.add(new KeyValue(l.startXProperty(), end.getX(), Interpolator.EASE_BOTH));
                                animations.add(new KeyValue(l.startYProperty(), end.getY(), Interpolator.EASE_BOTH));
                                animations.add(new KeyValue(l.endXProperty(), end.getX(), Interpolator.EASE_BOTH));
                                animations.add(new KeyValue(l.endYProperty(), end.getY(), Interpolator.EASE_BOTH));
                                nodesToDelete.add(l);
                            }
                        }
                        keyToEdgeNode.put(key, newLines);
                        newKids.addAll(0, newLines);
                    } else if (cell.isVertex()) {
                        mxGeometry geom = cell.getGeometry();
                        
                        Object o =  cell.getValue();
                        String key = null;
                        if (o instanceof ICoin) {
                            key = ((ICoin)o).getCompkey();
                        } else if (o instanceof ITx) {
                            key = ((ITx)o).getHash();
                        }
                        boolean setInitialValue = !keyToVertexNode.containsKey(key);
                        
                        Node node = getFXNodeForVertexCell(cell.getValue());

                        if (setInitialValue || (node.getLayoutX() == 0 && node.getLayoutY() == 0)) {
                            node.setLayoutX(geom.getX());
                            node.setLayoutY(geom.getY());
                            node.setOpacity(0);
                            animations.add(new KeyValue(node.opacityProperty(), 1.0, Interpolator.EASE_IN));
                        } else {
                            animations.add(
                                    new KeyValue(node.layoutXProperty(), geom.getX(), Interpolator.EASE_BOTH));
                            animations.add(
                                    new KeyValue(node.layoutYProperty(), geom.getY(), Interpolator.EASE_BOTH)
                            );
                        }
                        
                        newKids.addLast(node);
                    }
                }
        );
        graphPane.getChildren().setAll(newKids);
        Timeline t = new Timeline(new KeyFrame(Duration.millis(400),
                "move",
                finishedEvent -> { graphPane.getChildren().removeAll(nodesToDelete); },
                animations));
        t.play();
        animatingTimeline = t;
    }

    private Node getFXNodeForVertexCell(Object value) {
        Node node;
        if (value instanceof ITx) {
            node = createTXNode((ITx)value);
        } else if (value instanceof ICoin) {
            node = createUnspentOutput((ICoin)value);
        } else {
            node = new Rectangle(100, 100);
            ((Rectangle)node).setFill(Color.ORANGE);
        }
        return node;
    }

    public Pane getGraphPane() {
        return graphPane;
    }
    
    public void expandTransaction(ITx tx) {
        tx.getOutputs().forEach(this::addCoin);
        tx.getInputs().forEach(this::addCoin);
        
        layout();
        rebuildGraph(true);
    }
    
    Pane createUnspentOutput(ICoin coin) {
        if (keyToVertexCell.containsKey(coin.getCompkey())) {
            return keyToVertexNode.get(coin.getCompkey());
        } else {
            VBox box = new VBox();
            box.getStyleClass().add("coin");

            box.setOnMouseClicked(event -> clickHandler.accept(event, coin));

            fillUnspentOutput(coin, box);
           
            keyToVertexNode.put(coin.getCompkey(), box);
            
            return box;
        }
    }

    Pane recreateUnspentOutput(ICoin ci) {
        if (keyToVertexNode.containsKey(ci.getCompkey())) {
            Pane p = keyToVertexNode.get(ci.getCompkey());
            p.getChildren().clear();
            fillUnspentOutput(ci, p);
            return p;
        } else {
            return createUnspentOutput(ci);
        }
    }

    private void fillUnspentOutput(ICoin coin, Pane box) {
        if (showCoinNodeHash.get()) {
            box.getChildren().add(new Text(IBase.shortHash(coin.getAddr())));
        }
        if (showCoinNodeUSD.get()) {
            box.getChildren().add(new Text(IBase.USD_FORMAT.format(coin.getValueUSD())));
        }
        if (showCoinNodeBitcoin.get()) {
            box.getChildren().add(new Text(IBase.BTC_FORMAT.format(coin.getValue())));
        }
        box.autosize();
    }

    Pane recreateTXNode(ITx tx) {
        if (keyToVertexNode.containsKey(tx.getHash())) {
            Pane p = keyToVertexNode.get(tx.getHash());
            p.getChildren().clear();
            fillTXNode(tx, p);
            return p;
        } else {
            return createTXNode(tx);
        }
    }

    Pane createTXNode(ITx tx) {
        if (keyToVertexNode.containsKey(tx.getHash())) {
            return keyToVertexNode.get(tx.getHash());
        } else {
            VBox box = new VBox();
            box.getStyleClass().setAll("tx");

            box.setOnMouseClicked(event -> clickHandler.accept(event, tx));

            fillTXNode(tx, box);

            // This eliminates jiggly text at non 1.0 scale
            //box.setCache(true);
            //box.setCacheHint(CacheHint.QUALITY);
            
            keyToVertexNode.put(tx.getHash(), box);
            
            if (tx.getInputs().get(0).isCoinbase()) {
                box.getStyleClass().add("coinbase");
            }
            
            return box;
        }
    }

    private void fillTXNode(ITx tx, Pane box) {
        if (showTxHash.get()) {
            box.getChildren().add(new Text(IBase.shortHash(tx.getHash())));
        }
        if (showTxBitcoin.get()) {
            box.getChildren().add(new Text(IBase.BTC_FORMAT.format(tx.getOutputValue())));
        }
        if (showTxUSD.get()) {
            if (tx.isConfirmed()) {
                box.getChildren().add(new Text(IBase.USD_FORMAT.format(tx.getOutputValueUSD())));
            } else {
                //TODO add spot prince to CBAPI
                box.getChildren().add(new Text(" ?? "));
            }
        }
        if (showTxDate.get()) {
            box.getChildren().add(new Text(tx.getTimeString()));
        }
        if (showTxHeight.get()) {
            int height = tx.getBlockHeight();
            box.getChildren().add(new Text((height < 0) ? "Unconfirmed" : ("Block # " + tx.getBlockHeight())));
        }
        if (showTxCoinCount.get()) {
            box.getChildren().add(new Text(tx.getInputs().size() + " in " + tx.getOutputs().size() + " out " + tx.getUnspentCoins().size() + " unspent"));
        }
        box.autosize();
    }

    public void reset() {
        mxGraph = new mxGraph();
        mxGraphModel = mxGraph.getModel();
        mxGraphView = mxGraph.getView();

        mxGraphLayout = new mxHierarchicalLayout(mxGraph, SwingConstants.NORTH);

        keyToVertexCell = new ConcurrentHashMap<>();
        keyToEdgeCell = new ConcurrentHashMap<>();
        keyToVertexNode = new ConcurrentHashMap<>();
        //edgeToNode = new ConcurrentHashMap<>();
        
        graphPane.getChildren().clear();
    }
    
    public void removeTX(ITx tx) {
        mxGraph.removeCells(tx.getOutputs().stream().map(coin -> keyToEdgeCell.get(coin.getCompkey())).toArray());
        mxGraph.removeCells(new Object[] {keyToVertexCell.get(tx.getHash())});
        tx.getOutputs().forEach(ICoin -> {
            keyToEdgeCell.remove(ICoin.getCompkey());
            keyToEdgeNode.remove(ICoin.getCompkey());
            keyToEdgeLabel.remove(ICoin.getCompkey());
        });
        mxGraph.removeCells(tx.getInputs().stream().map(coin -> keyToEdgeCell.get(coin.getCompkey())).toArray());
        mxGraph.removeCells(new Object[] {keyToVertexCell.get(tx.getHash())});
        tx.getInputs().forEach(ICoin -> {
            keyToEdgeCell.remove(ICoin.getCompkey());
            keyToEdgeNode.remove(ICoin.getCompkey());
            keyToEdgeLabel.remove(ICoin.getCompkey());
        });
        keyToVertexCell.remove(tx.getHash());
        keyToVertexNode.remove(tx.getHash());
    }

    public void removeTXAndAllInputs(ITx tx) {
        tx.getInputs().forEach(coin -> {
            if (keyToEdgeCell.containsKey(coin.getCompkey())) {
                removeTXAndAllInputs(coin.getSourceTX());
            }
        });
        removeTX(tx);
    }

    public void removeTXAndAllOutputs(ITx tx) {
        tx.getOutputs().forEach(coin -> {
            if (keyToEdgeCell.containsKey(coin.getCompkey())) {
                removeTXAndAllOutputs(coin.getTargetTX());
            }
        });
        removeTX(tx);
    }

    public void removeCoinAsNode(ICoin ci) {
        mxGraph.removeCells(new Object[] {keyToEdgeCell.get(ci.getCompkey()), 
                                          keyToVertexCell.get(ci.getCompkey())});
        keyToEdgeCell.remove(ci.getCompkey());
        keyToVertexCell.remove(ci.getCompkey());
    }


    public Collection<ITx> findUnexpandedOutputTX(ITx tx) {
        Set<ITx> outputs = new HashSet<>();
        findUnexpandedOutputTX(tx, outputs);
        return outputs;
    }
    
    void findUnexpandedOutputTX(ITx tx, Collection<ITx> collector) {
        // not the most efficient, some nodes could be considered multiple times for diamond merges,
        // but since this is a DAG there are no loops.
        tx.getOutputs().forEach(coin -> {
            String targetID = coin.getTargetTXID();
            if (targetID != null && keyToVertexCell.containsKey(targetID)) {
                findUnexpandedOutputTX(ITx.query(targetID), collector);
            } else {
                collector.add(tx);
            }
        });
    }
    
    public Collection<ITx> findUnexpandedInputTX(ITx tx) {
        Set<ITx> inputs = new HashSet<>();
        findUnexpandedInputTX(tx, inputs);
        return inputs;
    }
    
    void findUnexpandedInputTX(ITx tx, Collection<ITx> collector) {
        // not the most efficient, some nodes could be considered multiple times for diamond TXes,
        // but since this is a DAG there are no loops.
        tx.getInputs().forEach(coin -> {
            String targetID = coin.getTargetTXID();
            if (targetID != null && keyToVertexCell.containsKey(targetID)) {
                findUnexpandedInputTX(ITx.query(targetID), collector);
            } else {
                collector.add(tx);
            }
        });
    }
    
    public Node getNode(IBase jb) {
        if (jb instanceof ITx) {
            return keyToVertexNode.get(((ITx) jb).getHash());
        } else if (jb instanceof ICoin) {
            return keyToVertexNode.get(((ICoin) jb).getCompkey());
        } else if (jb instanceof CBTransaction) {
            return keyToVertexNode.get(((CBTransaction) jb).getHash());
        } else if (jb instanceof IBlock) {
            // focus on the coinbase TX
            return keyToVertexNode.get(((IBlock) jb).getTXs().get(0).getHash());
        } else if (jb instanceof IAddress) {
            // return the first one, truely arbitrary
            List<ITx> txs = ((IAddress) jb).getTXs();
            if (txs.isEmpty()) return null;
            return keyToVertexNode.get(txs.get(0).getHash());
        } else {
            return null;
        }
    }

    public boolean getShowTxHash() {
        return showTxHash.get();
    }

    public BooleanProperty showTxHashProperty() {
        return showTxHash;
    }

    public void setShowTxHash(boolean showTxHash) {
        this.showTxHash.set(showTxHash);
    }

    public boolean getShowTxBitcoin() {
        return showTxBitcoin.get();
    }

    public BooleanProperty showTxBitcoinProperty() {
        return showTxBitcoin;
    }

    public void setShowTxBitcoin(boolean showTxBitcoin) {
        this.showTxBitcoin.set(showTxBitcoin);
    }

    public boolean getShowTxUSD() {
        return showTxUSD.get();
    }

    public BooleanProperty showTxUSDProperty() {
        return showTxUSD;
    }

    public void setShowTxUSD(boolean showTxUSD) {
        this.showTxUSD.set(showTxUSD);
    }

    public boolean getShowTxDate() {
        return showTxDate.get();
    }

    public BooleanProperty showTxDateProperty() {
        return showTxDate;
    }

    public void setShowTxDate(boolean showTxDate) {
        this.showTxDate.set(showTxDate);
    }

    public boolean getShowTxHeight() {
        return showTxHeight.get();
    }

    public BooleanProperty showTxHeightProperty() {
        return showTxHeight;
    }

    public void setShowTxHeight(boolean showTxHeight) {
        this.showTxHeight.set(showTxHeight);
    }

    public boolean getShowTxCoinCount() {
        return showTxCoinCount.get();
    }

    public BooleanProperty showTxCoinCountProperty() {
        return showTxCoinCount;
    }

    public void setShowTxCoinCount(boolean showTxCoinCount) {
        this.showTxCoinCount.set(showTxCoinCount);
    }

    public boolean getShowCoinNodeHash() {
        return showCoinNodeHash.get();
    }

    public BooleanProperty showCoinNodeHashProperty() {
        return showCoinNodeHash;
    }

    public void setShowCoinNodeHash(boolean showCoinNodeHash) {
        this.showCoinNodeHash.set(showCoinNodeHash);
    }

    public boolean getShowCoinNodeBitcoin() {
        return showCoinNodeBitcoin.get();
    }

    public BooleanProperty showCoinNodeBitcoinProperty() {
        return showCoinNodeBitcoin;
    }

    public void setShowCoinNodeBitcoin(boolean showCoinNodeBitcoin) {
        this.showCoinNodeBitcoin.set(showCoinNodeBitcoin);
    }

    public boolean getShowCoinNodeUSD() {
        return showCoinNodeUSD.get();
    }

    public BooleanProperty showCoinNodeUSDProperty() {
        return showCoinNodeUSD;
    }

    public void setShowCoinNodeUSD(boolean showCoinNodeUSD) {
        this.showCoinNodeUSD.set(showCoinNodeUSD);
    }
    
    public List<BooleanProperty> layoutFlags() {
        return Arrays.asList(
                showTxHash,
                showTxBitcoin,
                showTxUSD,
                showTxDate,
                showTxHeight,
                showTxCoinCount,
                showCoinNodeHash,
                showCoinNodeBitcoin,
                showCoinNodeUSD
        );
    }
}
