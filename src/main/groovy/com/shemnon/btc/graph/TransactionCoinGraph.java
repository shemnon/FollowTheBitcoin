package com.shemnon.btc.graph;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxGraphView;
import com.shemnon.btc.blockchaininfo.CoinInfo;
import com.shemnon.btc.blockchaininfo.TXInfo;

import javax.swing.*;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Nodes - Tansactions
 * Edges - coins
 * Created by shemnon on 2 Mar 2014.
 */
public class TransactionCoinGraph {
    
    mxGraph graph;
    mxIGraphModel graphModel;
    mxGraphView graphView;
    mxGraphLayout graphLayout;
    
    Map<String, Object> vertexCache = new ConcurrentHashMap<>();
    Map<String, Object> edgeCache = new ConcurrentHashMap<>();
    
    public TransactionCoinGraph() {
        graph = new mxGraph();
        graphModel = graph.getModel();
        graphView = graph.getView();
        
        graphLayout = new mxHierarchicalLayout(graph, SwingConstants.WEST);
    }
    
    
    public Object addTransaction(TXInfo tx) {
        String key = tx.getHash();
        Object o = vertexCache.get(key);
        if (o == null || !graphModel.isVertex(o)) {
            o = graph.insertVertex(graph.getDefaultParent(), tx.getHash(), tx, 0, 0, 100, 100);
            vertexCache.put(key, o);
            layout();
        }
        return o;
    }
    
    public Object addCoin(CoinInfo ci) {
        String key = ci.getCompkey();
        Object o = edgeCache.get(key);
        if (o == null || !graphModel.isEdge(o)) {
            Object from = addTransaction(ci.getSourceTX());
            Object to = addTransaction(ci.getTargetTX());
            o = graph.insertEdge(graph.getDefaultParent(), key, ci, from, to);
            edgeCache.put(key, o);
            layout();
        }
        return o; 
    }
    
    public void layout() {
        graphLayout.execute(graph.getDefaultParent());
    }
    
    public mxRectangle getGraphBounds() {
        return graphView.getGraphBounds();
    }
    
//    public boolean isTxFullyExpanded(TXInfo tx) {
//        tx.getCoins().stream().map(vertexCache)
//        graphModel.isVertex()
//    }
    
    public Collection<mxCell> getTransactionVertexes() {
        return vertexCache.values().stream().map(o -> (mxCell)o).collect(Collectors.toList());
    }

    public Collection<mxCellState> getCoinEdges() {
        return edgeCache.values().stream()
                .map(o -> graphView.getState((mxCell)o))
                .collect(Collectors.toList());
    }

    public mxCell getTransactionVertex(TXInfo tx) {
        return (mxCell)vertexCache.get(tx.getHash());
    }

    public mxCell getCoinEdge(CoinInfo coin) {
        return (mxCell)edgeCache.get(coin.getCompkey());
    }
}
