package com.shemnon.btc.graph;



import com.shemnon.btc.blockchaininfo.CoinInfo;
import com.shemnon.btc.blockchaininfo.TXInfo;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.graph.util.EdgeType;

import java.awt.*;
import java.util.Collection;

/**
 * Nodes - Tansactions
 * Edges - coins
 * Created by shemnon on 2 Mar 2014.
 */
public class TransactionCoinGraphJung {

    private final Layout<TXInfo, CoinInfo> layout;
    private DirectedGraph<TXInfo, CoinInfo> graph; 
    Forest<TXInfo, CoinInfo> forest;
    
    public TransactionCoinGraphJung() {
        graph = new DirectedSparseMultigraph<>();
        forest = new DelegateForest<>(graph);
        
//        graphModel = graph.getModel();
//        setGraphView(graph.getView());
//        
//        graphLayout = new mxHierarchicalLayout(graph);
//        graphLayout.setUseBoundingBox(true);
        
        layout = new TreeLayout<>(forest, 200, 200);
        
        //graphLayout = new mxHierarchicalLayout(graph, SwingConstants.WEST);
        //((mxHierarchicalLayout)graphLayout).set
    }
    
    
    public TXInfo addTransaction(TXInfo tx) {
        if (!graph.containsVertex(tx)) {
            System.out.println("Adding tx " + tx.getTxIndex());
            graph.addVertex(tx);
            System.out.printf("graph has %d verteces%n", graph.getVertexCount());
        }
        return tx;
    }
    
    public CoinInfo addCoin(CoinInfo ci) {
        if (!graph.containsEdge(ci)) {
            TXInfo from = addTransaction(ci.getSourceTX());
            TXInfo to = addTransaction(ci.getTargetTX());
            graph.addEdge(ci, from, to, EdgeType.DIRECTED);
        }
        return ci; 
    }
    
    public void layout() {
        layout.setGraph(forest);
    }
    
    public Dimension getGraphBounds() {
        return layout.getSize();
    }
    
//    public boolean isTxFullyExpanded(TXInfo tx) {
//        tx.getCoins().stream().map(vertexCache)
//        graphModel.isVertex()
//    }
    
    public Collection<TXInfo> getTransactionVertexes() {
        return graph.getVertices();
    }

    public Collection<CoinInfo> getCoinEdges() {
        return graph.getEdges();
    }

    public Layout<TXInfo, CoinInfo> getLayout() {
        return layout;
    }

    public DirectedGraph<TXInfo, CoinInfo> getGraph() {
        return graph;
    }

}
