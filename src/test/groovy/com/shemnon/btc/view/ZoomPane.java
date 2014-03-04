package com.shemnon.btc.view;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Affine;

/**
 * 
 * Created by shemnon on 3 Mar 2014.
 */
public class ZoomPane extends Pane {

    double scaleFactor = Math.sqrt(2.0d);
    
    Affine transform;
    Node zoomNode;
    
    double lastX = Double.NaN;
    double lastY = Double.NaN;
    

    public ZoomPane(Node... nodes) {
        super(new Group(nodes));
        
        transform = new Affine();
        
        getChildren().get(0).getTransforms().setAll(transform);
        
        setOnZoom(this::zooming);
        setOnScroll(this::scrolling);
        setOnMouseDragged(this::dragging);
        setOnMouseClicked(this::mouseDown);
        setOnMouseReleased(this::mouseUp);
        
        //TOTO clip?
    }
    
    public void zooming(ZoomEvent ze) { 
        zoom(ze.getZoomFactor(), ze.getX(), ze.getY());
    }

    private void zoom(double zoomFactor, double x, double y) {
        transform.appendScale(zoomFactor, zoomFactor, x, y);

    }

    public void scrolling(ScrollEvent se) {
        // get a click count...
        double mickys = se.getDeltaY() / se.getMultiplierY();
        if (mickys == 0) return;
        double zoom = scaleFactor * mickys;
        if (zoom < 0) {
            zoom = -1/zoom;
        }
        zoom(zoom, se.getX(), se.getY());
    }

    public void mouseDown(MouseEvent me) {
        lastX = me.getX();
        lastY = me.getY();
    }

    public void mouseUp(MouseEvent me) {
        lastX = Double.NaN;
        lastY = Double.NaN;
    }

    public void dragging(MouseEvent me) {        
        if (!Double.isNaN(lastX) && !Double.isNaN(lastY)) {
            double dx = me.getX() - lastX; 
            double dy = me.getY() - lastY; 
            transform.appendTranslation(dx, dy);
        }
        lastX = me.getX();
        lastY = me.getY();
    }
    
}
