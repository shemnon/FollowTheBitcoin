package com.shemnon.btc.view;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Created by shemnon on 3 Mar 2014.
 */
public class GraphViewJung<V, E> {

    Layout<V, E> layout;
    Graph<V, E> graph;

    Pane graphPane;

    public void rebuildGraph() {
//        mxRectangle bounds = view.getGraphBounds();
//        double dx = bounds.getX();
//        double dy = bounds.getY();
        List<Node> newKids = new ArrayList<>();

        graph.getVertices().forEach(v -> {
            java.awt.geom.Point2D point = layout.transform(v);

            Rectangle rectangle = new Rectangle(point.getX() - 50, point.getY() - 50,
                    100, 100);
            rectangle.setFill(Color.LIGHTBLUE);
            newKids.add(rectangle);
            
        });
        
        graph.getEdges().forEach(e -> {
            java.awt.geom.Point2D points = layout.transform(graph.getSource(e));
            java.awt.geom.Point2D pointd = layout.transform(graph.getDest(e));
            Line l = new Line(points.getX(), points.getY(), pointd.getX(), pointd.getY());
            l.setFill(Color.BLACK);
            newKids.add(l);
        });
    
        
        graphPane.getChildren().setAll(newKids);
    }

    public Layout<V, E> getLayout() {
        return layout;
    }

    public void setLayout(Layout<V, E> layout) {
        this.layout = layout;
    }

    public Graph<V, E> getGraph() {
        return graph;
    }

    public void setGraph(Graph<V, E> graph) {
        this.graph = graph;
    }

    public Pane getGraphPane() {
        return graphPane;
    }

    public void setGraphPane(Pane graphPane) {
        this.graphPane = graphPane;
    }
}
