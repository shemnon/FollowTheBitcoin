package com.shemnon.btc.view;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxGraphView;
import com.shemnon.btc.JsonBase;
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
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

/**
 * 
 * Created by shemnon on 3 Mar 2014.
 */
public class GraphViewMXGraph {
    
    NumberFormat btcFormat = new DecimalFormat("\u0e3f###,###.### ### ###");
    
    BiConsumer<MouseEvent, TXInfo> clickHandler;

    mxGraph mxGraph;
    mxIGraphModel mxGraphModel;
    private mxGraphView mxGraphView;
    mxGraphLayout mxGraphLayout;

    Map<String, Object> keyToVertexCell = new ConcurrentHashMap<>();
    Map<String, Object> keyToEdgeCell = new ConcurrentHashMap<>();
    Map<TXInfo, Node> txToNode = new ConcurrentHashMap<>();
    Map<CoinInfo, Node> coinToNode = new ConcurrentHashMap<>();

    Pane graphPane;

    public GraphViewMXGraph(BiConsumer<MouseEvent, TXInfo> clickHandler) {
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
                o = mxGraph.insertVertex(mxGraph.getDefaultParent(), Integer.toHexString(tx.getTxIndex()), tx, 0, 0, n.prefWidth(0), n.prefHeight(0));
                keyToVertexCell.put(key, o);
            }
            return o;
        } catch (Exception e) {
            System.out.println(tx.dumpJson());
            throw e;
        }
    }

    public Object addCoin(CoinInfo ci) {
        String key = ci.getCompkey();
        Object o = keyToEdgeCell.get(key);
        if ((o == null || !mxGraphModel.isEdge(o)) && ci.getTargetTX() != null && ci.getSourceTX() != null) {
            Object from = addTransaction(ci.getSourceTX());
            Object to = addTransaction(ci.getTargetTX());
            o = mxGraph.insertEdge(mxGraph.getDefaultParent(), key, ci, from, to);
            keyToEdgeCell.put(key, o);
        }
        return o;
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
                            lastpoints[0] = point.getX()-dx;
                            lastpoints[1] = point.getY()-dy;
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

    Node createTXNode(TXInfo tx) {
        if (txToNode.containsKey(tx)) {
            return txToNode.get(tx);
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
            
            txToNode.put(tx, box);
            
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
        txToNode = new ConcurrentHashMap<>();
        coinToNode = new ConcurrentHashMap<>();
        
        graphPane.getChildren().clear();
    }
}
