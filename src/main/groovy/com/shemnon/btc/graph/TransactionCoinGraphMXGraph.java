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
public class TransactionCoinGraphMXGraph {
    
    mxGraph graph;
    mxIGraphModel graphModel;
    private mxGraphView graphView;
    mxGraphLayout graphLayout;
    
    Map<String, Object> vertexCache = new ConcurrentHashMap<>();
    Map<String, Object> edgeCache = new ConcurrentHashMap<>();
    
    public TransactionCoinGraphMXGraph() {
        graph = new mxGraph();
        graphModel = graph.getModel();
        setGraphView(graph.getView());
        
        //graphLayout = new mxCompactTreeLayout(graph);
        //graphLayout.setUseBoundingBox(true);
        
        graphLayout = new mxHierarchicalLayout(graph, SwingConstants.WEST);
        ((mxHierarchicalLayout)graphLayout).setInterHierarchySpacing(10);
        ((mxHierarchicalLayout)graphLayout).setIntraCellSpacing(10);
        ((mxHierarchicalLayout)graphLayout).setResizeParent(true);
    }
    
    
    public Object addTransaction(TXInfo tx) {
        String key = tx.getHash();
        Object o = vertexCache.get(key);
        if (o == null || !graphModel.isVertex(o)) {
            o = graph.insertVertex(graph.getDefaultParent(), Integer.toHexString(tx.getTxIndex()), tx, 0, 0, 100, 100);
            vertexCache.put(key, o);
        }
        return o;
    }
    
    public Object addCoin(CoinInfo ci) {
        String key = ci.getCompkey();
        Object o = edgeCache.get(key);
        if ((o == null || !graphModel.isEdge(o)) && ci.getTargetTX() != null && ci.getSourceTX() != null) {
            Object from = addTransaction(ci.getSourceTX());
            Object to = addTransaction(ci.getTargetTX());
            o = graph.insertEdge(graph.getDefaultParent(), key, ci, from, to);
            edgeCache.put(key, o);
        }
        return o; 
    }
    
    public void layout() {
        graphLayout.execute(graph.getDefaultParent());
    }
    
    public mxRectangle getGraphBounds() {
        return getGraphView().getGraphBounds();
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
                .map(o -> getGraphView().getState((mxCell) o))
                .collect(Collectors.toList());
    }

    public mxCell getTransactionVertex(TXInfo tx) {
        return (mxCell)vertexCache.get(tx.getHash());
    }

    public mxCell getCoinEdge(CoinInfo coin) {
        return (mxCell)edgeCache.get(coin.getCompkey());
    }

    public mxGraphView getGraphView() {
        return graphView;
    }

    public void setGraphView(mxGraphView graphView) {
        this.graphView = graphView;
    }
}
