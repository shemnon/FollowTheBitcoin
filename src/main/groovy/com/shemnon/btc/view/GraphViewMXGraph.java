package com.shemnon.btc.view;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxGraphView;
import com.shemnon.btc.blockchaininfo.AddressInfo;
import com.shemnon.btc.blockchaininfo.BlockInfo;
import com.shemnon.btc.coinbase.CBTransaction;
import com.shemnon.btc.ftm.JsonBase;
import com.shemnon.btc.blockchaininfo.CoinInfo;
import com.shemnon.btc.blockchaininfo.TXInfo;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

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
    Map<String, Node> keyToVertexNode = new ConcurrentHashMap<>();
    //Map<String, Node> keyToEdgeNode = new ConcurrentHashMap<>();

    Pane graphPane;

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
                Node n = getFXNodeForCell(tx);
                o = mxGraph.insertVertex(mxGraph.getDefaultParent(), Integer.toString(tx.getTxIndex()), tx, 0, 0, n.prefWidth(0), n.prefHeight(0));
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
                Node n = getFXNodeForCell(coin);
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

    public void layout() {
        mxGraphLayout.execute(mxGraph.getDefaultParent());
    }

    public mxRectangle getGraphBounds() {
        return mxGraphView.getGraphBounds();
    }

    public void rebuildGraph() {
        mxRectangle bounds = mxGraphView.getGraphBounds();
        double dx = bounds.getX();
        double dy = bounds.getY();
        LinkedList<Node> newKids = new LinkedList<>();

        mxGraphView.getStates().entrySet().forEach(
                entry -> {
                    mxCell cell = (mxCell) entry.getKey();
                    if (cell.isEdge()) {
                        Double[] lastpoints = new Double[]{null, null};
                        entry.getValue().getAbsolutePoints().forEach(point -> {

                            if (lastpoints[0] != null) {
                                Line l = new Line(lastpoints[0], lastpoints[1], point.getX() - dx, point.getY() - dy);
                                l.setFill(Color.BLACK);
                                newKids.addFirst(l);
                            }
                            lastpoints[0] = point.getX() - dx;
                            lastpoints[1] = point.getY() - dy;
                        });
                        //TODO add arrow

                    } else if (cell.isVertex()) {
                        mxGeometry geom = cell.getGeometry();
                        Node node = getFXNodeForCell(cell.getValue());

                        node.setLayoutX(geom.getX() - dx);
                        node.setLayoutY(geom.getY() - dy);
                        newKids.addLast(node);
                    }
                }
        );
        graphPane.getChildren().setAll(newKids);
    }

    private Node getFXNodeForCell(Object value) {
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
        rebuildGraph();
    }
    
    Node createUnspentOutput(CoinInfo coin) {
        if (keyToVertexCell.containsKey(coin.getCompkey())) {
            return keyToVertexNode.get(coin.getCompkey());
        } else {
            Text btc = new Text(btcFormat.format(coin.getValue()));
            Text address = new Text(JsonBase.shortHash(coin.getAddr()));
            
            VBox box = new VBox(btc, address);
            box.getStyleClass().add("coin");

            box.setOnMouseClicked(event -> clickHandler.accept(event, coin));
           
            keyToVertexNode.put(coin.getCompkey(), box);
            updateExpanded(coin.getSourceTX());
            
            return box;
        }
    }

    Node createTXNode(TXInfo tx) {
        if (keyToVertexNode.containsKey(tx.getHash())) {
            return keyToVertexNode.get(tx.getHash());
        } else {
            Text hash = new Text(JsonBase.shortHash(tx.getHash()));
            Text btc = new Text(btcFormat.format(tx.getInputValue()));
            Text date = new Text(tx.getTimeString());
            Text index = new Text("Block# " + tx.getBlockHeight());
            
            Text coins = new Text(tx.getInputs().size() + " in " + tx.getOutputs().size() + " out " + tx.getUnspentCoins().size() + " unspent");

            VBox box = new VBox(hash, btc, date, index, coins);
            box.getStyleClass().setAll("tx");
            
            box.setOnMouseClicked(event -> clickHandler.accept(event, tx));
            
            // This eliminates jiggly text at non 1.0 scale
//            box.setCache(true);
//            box.setCacheHint(CacheHint.QUALITY);
            
            keyToVertexNode.put(tx.getHash(), box);
            updateExpanded(tx);
            
            return box;
        }
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

}
