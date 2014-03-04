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

    double scrollScaleFactor = 1.2;
    
    Affine transform;
    Affine workTransform;
    Node zoomNode;
    
    
    boolean set = false;
    double lastScale = 1.0;
    double lastTX = 0;
    double lastTY = 0;
    double lastX = 0;
    double lastY = 0;
    

    public ZoomPane(Node... nodes) {
        super(new Group(nodes));
        
        transform = new Affine();
        workTransform = new Affine();
        
        getChildren().get(0).getTransforms().setAll(transform);
        
        setOnZoomStarted(e -> start(e.getX(), e.getY()));
        setOnScrollStarted(e -> start(e.getX(), e.getY()));
        setOnMousePressed(e -> start(e.getX(), e.getY()));
        setOnZoomFinished(e -> finish());
        setOnMouseReleased(e -> finish());
        setOnScrollFinished(e -> finish());
        
        setOnZoom(this::zooming);
        setOnScroll(this::scrolling);
        setOnMouseDragged(this::dragging);
    }
    
    public void zooming(ZoomEvent ze) { 
        zoom(ze.getTotalZoomFactor(), ze.getX(), ze.getY());
    }

    private void zoom(double zoomFactor, double x, double y) {
        workTransform.setMxx(lastScale);
        workTransform.setMyy(lastScale);
        workTransform.setTx(lastTX);
        workTransform.setTy(lastTY);
        
        workTransform.appendScale(zoomFactor, zoomFactor, x, y);
        
        transform.setToTransform(
                workTransform.getMxx(), workTransform.getMxy(), workTransform.getTx(),
                workTransform.getMyx(), workTransform.getMyy(), workTransform.getTy());
    }

    public void scrolling(ScrollEvent se) {
        // get a click count...
        double currentZoom = transform.getMxx();
        if (se.getDeltaY() > 0) {
            zoom(currentZoom * scrollScaleFactor, se.getX(), se.getY());
        } else if (se.getDeltaY() < 0) {
            zoom(currentZoom / scrollScaleFactor, se.getX(), se.getY());
        }
    }

    public void start(double x, double y) {
        lastScale = transform.getMxx();
        lastTX = transform.getTx();
        lastTY = transform.getTy();
        lastX = x;
        lastY = y;
        set = true;
    }

    public void finish() {
        set = false;
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
