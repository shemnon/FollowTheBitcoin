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
import com.shemnon.btc.blockchaininfo.AddressInfo;
import com.shemnon.btc.blockchaininfo.BlockInfo;
import com.shemnon.btc.coinbase.CBTransaction;
import com.shemnon.btc.ftm.JsonBase;
import com.shemnon.btc.blockchaininfo.CoinInfo;
import com.shemnon.btc.blockchaininfo.TXInfo;
import javafx.animation.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import javax.swing.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

/**
 * 
 * Created by shemnon on 3 Mar 2014.
 */
public class GraphViewMXGraph {
    
    NumberFormat btcFormat = new DecimalFormat("\u0e3f###,###.### ### ###");
    
    BiConsumer<MouseEvent, JsonBase> clickHandler;

    mxGraph mxGraph;
    mxIGraphModel mxGraphModel;
    private mxGraphView mxGraphView;
    mxGraphLayout mxGraphLayout;

    Map<String, Object> keyToVertexCell = new ConcurrentHashMap<>();
    Map<String, Object> keyToEdgeCell = new ConcurrentHashMap<>();
    Map<String, Pane> keyToVertexNode = new ConcurrentHashMap<>();
    Map<String, List<Line>> keyToEdgeNode = new ConcurrentHashMap<>();

    Pane graphPane;
    
    BooleanProperty showTxHash = new SimpleBooleanProperty(true);
    BooleanProperty showTxBitcoin = new SimpleBooleanProperty(true);
    BooleanProperty showTxUSD = new SimpleBooleanProperty(true);
    BooleanProperty showTxDate = new SimpleBooleanProperty(true);
    BooleanProperty showTxHeight = new SimpleBooleanProperty(true);
    BooleanProperty showTxCoinCount = new SimpleBooleanProperty(true);
    
    BooleanProperty showCoinNodeHash = new SimpleBooleanProperty(true);
    BooleanProperty showCoinNodeBitcoin = new SimpleBooleanProperty(true);
    BooleanProperty showCoinNodeUSD = new SimpleBooleanProperty(true);
    
    Timeline animatingTimeline;

    public GraphViewMXGraph(BiConsumer<MouseEvent, JsonBase> clickHandler) {
        this.clickHandler = clickHandler;

        mxGraph = new mxGraph();
        mxGraphModel = mxGraph.getModel();
        mxGraphView = mxGraph.getView();

        mxGraphLayout = new mxHierarchicalLayout(mxGraph, SwingConstants.NORTH);
        
        graphPane = new Pane();
    }

    public Object addTransaction(TXInfo tx) {
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
            System.out.println(tx.dumpJson());
            throw e;
        }
    }

    public Object addCoinAsNode(CoinInfo coin) {
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
            System.out.println(coin.dumpJson());
            throw e;
        }
    }

    public Object addCoin(CoinInfo ci) {
        String key = ci.getCompkey();
        Object o = keyToEdgeCell.get(key);
        if ((o == null || !mxGraphModel.isEdge(o)) && ci.getSourceTX() != null) {
            TXInfo fromTX = ci.getSourceTX();
            TXInfo toTX = ci.getTargetTX();
            
            Object from = addTransaction(fromTX);
            Object to;
            if (toTX == null) {
                to = addCoinAsNode(ci);
            } else {
                to = addTransaction(toTX);
            }

            o = mxGraph.insertEdge(mxGraph.getDefaultParent(), key, ci, from, to);
            keyToEdgeCell.put(key, o);
            updateExpanded(fromTX);
            updateExpanded(toTX);
        }
        return o;
    }

    private void updateExpanded(TXInfo tx) {
        if (tx == null) return;
        
        Node n = keyToVertexNode.get(tx.getHash());
        if (n == null) return;

        boolean inputsExpanded = tx.getInputs().stream().allMatch(ci -> keyToVertexNode.containsKey(ci.getSourceTX().getHash()));

        boolean outputsExpanded = tx.getOutputs().stream().allMatch(ci -> {
            TXInfo target = ci.getTargetTX();
            if (target == null) {
                return keyToVertexNode.containsKey(ci.getCompkey());
            } else {
                return keyToVertexNode.containsKey(target.getHash());
            }
        });

        if (inputsExpanded && outputsExpanded) {
            n.getStyleClass().add("expanded");
        } else {
            n.getStyleClass().remove("expanded");
        }
    }

    public void resizeGraphNodes() {
        for (Map.Entry<String, Object> e : keyToVertexCell.entrySet()) {
            if (e.getValue() instanceof mxCell) {
                String key = e.getKey();
                if (key.contains("#")) {
                    recreateUnspentOutput(CoinInfo.query(key));
                } else {
                    recreateTXNode(TXInfo.query(key));
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
        double dx = bounds.getX();
        double dy = bounds.getY();
        LinkedList<Node> newKids = new LinkedList<>();
        List<KeyValue> animations = new ArrayList<>(keyToVertexCell.size());
        List<Node> nodesToDelete = new ArrayList<>();

        if (animatingTimeline != null && animatingTimeline.getStatus() == Animation.Status.RUNNING) {
            animatingTimeline.jumpTo(animatingTimeline.getTotalDuration().subtract(Duration.ONE));
        }
        
        mxGraphView.getStates().entrySet().forEach(
                entry -> {
                    mxCell cell = (mxCell) entry.getKey();
                    if (cell.isEdge()) {
                        String key = cell.getId();

                        mxCellState cellState = entry.getValue();
                        List<Line> newLines = new ArrayList<>(cellState.getAbsolutePointCount() - 1);
                        List<Line> prevEdge = null;
                        if (animate) {
                            prevEdge = keyToEdgeNode.get(cell.getId());
                        }
                        if (prevEdge != null && prevEdge.size() == 0) {
                            prevEdge = null;
                        }

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
                                animations.add(new KeyValue(l.startXProperty(), prevPoint.getX()));
                                animations.add(new KeyValue(l.startYProperty(), prevPoint.getY()));
                                animations.add(new KeyValue(l.endXProperty(), nextPoint.getX()));
                                animations.add(new KeyValue(l.endYProperty(), nextPoint.getY()));
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
                                animations.add(new KeyValue(l.startXProperty(), end.getX()));
                                animations.add(new KeyValue(l.startYProperty(), end.getY()));
                                animations.add(new KeyValue(l.endXProperty(), end.getX()));
                                animations.add(new KeyValue(l.endYProperty(), end.getY()));
                                nodesToDelete.add(l);
                            }
                        }

                        keyToEdgeNode.put(key, newLines);
                        newKids.addAll(0, newLines);

                    } else if (cell.isVertex()) {
                        mxGeometry geom = cell.getGeometry();
                        
                        String key = cell.getId();
                        boolean setInitialValue = !keyToVertexNode.containsKey(key);
                        
                        Node node = getFXNodeForVertexCell(cell.getValue());

                        if (setInitialValue || (node.getLayoutX() == 0 && node.getLayoutY() == 0)) {
                            node.setLayoutX(geom.getX() - dx);
                            node.setLayoutY(geom.getY() - dy);
                            node.setOpacity(0);
                            animations.add(new KeyValue(node.opacityProperty(), 1.0, Interpolator.EASE_IN));
                        } else {
                            animations.add(
                                    new KeyValue(node.layoutXProperty(), geom.getX() - dx, Interpolator.EASE_BOTH));
                            animations.add(
                                    new KeyValue(node.layoutYProperty(), geom.getY() - dy, Interpolator.EASE_BOTH)
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
        if (value instanceof TXInfo) {
            node = createTXNode((TXInfo)value);
        } else if (value instanceof CoinInfo) {
            node = createUnspentOutput((CoinInfo)value);
        } else {
            node = new Rectangle(100, 100);
            ((Rectangle)node).setFill(Color.ORANGE);
        }
        return node;
    }

    public Pane getGraphPane() {
        return graphPane;
    }
    
    public void expandTransaction(TXInfo tx) {
        tx.getOutputs().forEach(this::addCoin);
        tx.getInputs().forEach(this::addCoin);
        
        layout();
        rebuildGraph(true);
    }
    
    Pane createUnspentOutput(CoinInfo coin) {
        if (keyToVertexCell.containsKey(coin.getCompkey())) {
            return keyToVertexNode.get(coin.getCompkey());
        } else {
            VBox box = new VBox();
            box.getStyleClass().add("coin");

            box.setOnMouseClicked(event -> clickHandler.accept(event, coin));

            fillUnspentOutput(coin, box);
           
            keyToVertexNode.put(coin.getCompkey(), box);
            updateExpanded(coin.getSourceTX());
            
            return box;
        }
    }

    Pane recreateUnspentOutput(CoinInfo ci) {
        if (keyToVertexNode.containsKey(ci.getCompkey())) {
            Pane p = keyToVertexNode.get(ci.getCompkey());
            p.getChildren().clear();
            fillUnspentOutput(ci, p);
            return p;
        } else {
            return createUnspentOutput(ci);
        }
    }

    private void fillUnspentOutput(CoinInfo coin, Pane box) {
        if (showCoinNodeHash.get()) {
            box.getChildren().add(new Text(btcFormat.format(coin.getValue())));
        }
        if (showCoinNodeBitcoin.get()) {
            box.getChildren().add(new Text(JsonBase.shortHash(coin.getAddr())));
        }
        box.autosize();
    }

    Pane recreateTXNode(TXInfo tx) {
        if (keyToVertexNode.containsKey(tx.getHash())) {
            Pane p = keyToVertexNode.get(tx.getHash());
            p.getChildren().clear();
            fillTXNode(tx, p);
            return p;
        } else {
            return createTXNode(tx);
        }
    }

    Pane createTXNode(TXInfo tx) {
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
            updateExpanded(tx);
            
            return box;
        }
    }

    private void fillTXNode(TXInfo tx, Pane box) {
        if (showTxHash.get()) {
            box.getChildren().add(new Text(JsonBase.shortHash(tx.getHash())));
        }
        if (showTxBitcoin.get()) {
            box.getChildren().add(new Text(btcFormat.format(tx.getInputValue())));
        }
        //todo usd
        if (showTxDate.get()) {
            box.getChildren().add(new Text(tx.getTimeString()));
        }
        if (showTxHeight.get()) {
            box.getChildren().add(new Text("Block# " + tx.getBlockHeight()));
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
    
    public void removeTX(TXInfo tx) {
        mxGraph.removeCells(tx.getOutputs().stream().map(coin -> keyToEdgeCell.get(coin.getCompkey())).toArray());
        mxGraph.removeCells(new Object[] {keyToVertexCell.get(tx.getHash())});
        tx.getOutputs().forEach(coinInfo -> {
            keyToEdgeCell.remove(coinInfo.getCompkey());
        });
        mxGraph.removeCells(tx.getInputs().stream().map(coin -> keyToEdgeCell.get(coin.getCompkey())).toArray());
        mxGraph.removeCells(new Object[] {keyToVertexCell.get(tx.getHash())});
        tx.getInputs().forEach(coinInfo -> {
            keyToEdgeCell.remove(coinInfo.getCompkey());
        });
        keyToVertexCell.remove(tx.getHash());
    }

    public void removeCoinAsNode(CoinInfo ci) {
        mxGraph.removeCells(new Object[] {keyToEdgeCell.get(ci.getCompkey()), 
                                          keyToVertexCell.get(ci.getCompkey())});
        keyToEdgeCell.remove(ci.getCompkey());
        keyToVertexCell.remove(ci.getCompkey());
    }


    public Collection<TXInfo> findUnexpandedOutputTX(TXInfo tx) {
        Set<TXInfo> outputs = new HashSet<>();
        findUnexpandedOutputTX(tx, outputs);
        return outputs;
    }
    
    void findUnexpandedOutputTX(TXInfo tx, Collection<TXInfo> collector) {
        // not the most efficient, some nodes could be considered multiple times for diamond merges,
        // but since this is a DAG there are no loops.
        tx.getOutputs().forEach(coin -> {
            TXInfo target = coin.getTargetTX();
            if (target != null && keyToVertexCell.containsKey(target.getHash())) {
                findUnexpandedOutputTX(target, collector);
            } else {
                collector.add(tx);
            }
        });
    }
    
    public Collection<TXInfo> findUnexpandedInputTX(TXInfo tx) {
        Set<TXInfo> inputs = new HashSet<>();
        findUnexpandedInputTX(tx, inputs);
        return inputs;
    }
    
    void findUnexpandedInputTX(TXInfo tx, Collection<TXInfo> collector) {
        // not the most efficient, some nodes could be considered multiple times for diamond TXes,
        // but since this is a DAG there are no loops.
        tx.getInputs().forEach(coin -> {
            TXInfo target = coin.getSourceTX();
            if (target != null && keyToVertexCell.containsKey(target.getHash())) {
                findUnexpandedInputTX(target, collector);
            } else {
                collector.add(tx);
            }
        });
    }
    
    public Node getNode(JsonBase jb) {
        if (jb instanceof TXInfo) {
            return keyToVertexNode.get(((TXInfo) jb).getHash());
        } else if (jb instanceof CoinInfo) {
            return keyToVertexNode.get(((CoinInfo) jb).getCompkey());
        } else if (jb instanceof CBTransaction) {
            return keyToVertexNode.get(((CBTransaction) jb).getHash());
        } else if (jb instanceof BlockInfo) {
            // focus on the coinbase TX
            return keyToVertexNode.get(((BlockInfo) jb).getTXs().get(0).getHash());
        } else if (jb instanceof AddressInfo) {
            // return the first one, truely arbitrary
            List<TXInfo> txs = ((AddressInfo) jb).getTXs();
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
