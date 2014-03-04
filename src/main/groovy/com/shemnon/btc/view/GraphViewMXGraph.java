package com.shemnon.btc.view;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraphView;
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
public class GraphViewMXGraph {

    mxGraphView view;

    Pane graphPane;

    public void rebuildGraph() {
        mxRectangle bounds = view.getGraphBounds();
        double dx = bounds.getX();
        double dy = bounds.getY();
        List<Node> newKids = new ArrayList<>();

        view.getStates().entrySet().forEach(
                entry -> {
                    mxCell cell = (mxCell) entry.getKey();
                    if (cell.isEdge()) {
                        Double[] lastpoints = new Double[]{null, null};
                        entry.getValue().getAbsolutePoints().forEach(point -> {

                            if (lastpoints[0] != null) {
                                Line l = new Line(lastpoints[0], lastpoints[1], point.getX() - dx, point.getY() - dy);
                                l.setFill(Color.BLACK);
                                newKids.add(l);
                            }
                            lastpoints[0] = point.getX();
                            lastpoints[1] = point.getY();
                        });
                        //TODO add arrow

                    } else if (cell.isVertex()) {
                        mxGeometry geom = cell.getGeometry();
                        Rectangle rectangle = new Rectangle(geom.getX() - dx, geom.getY() - dy,
                                geom.getWidth(), geom.getHeight());
                        rectangle.setFill(Color.LIGHTBLUE);
                        newKids.add(rectangle);
                    }
                }
        );
        graphPane.getChildren().setAll(newKids);
    }

    public mxGraphView getView() {
        return view;
    }

    public void setView(mxGraphView view) {
        this.view = view;
    }

    public Pane getGraphPane() {
        return graphPane;
    }

    public void setGraphPane(Pane graphPane) {
        this.graphPane = graphPane;
    }
}
