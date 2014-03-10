package com.shemnon.btc.view;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
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
    
    
    DoubleProperty scale = new SimpleDoubleProperty(1.0);
    DoubleProperty tx = new SimpleDoubleProperty(0.0);
    DoubleProperty ty = new SimpleDoubleProperty(0.0);
    
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

    void start(double x, double y) {
        lastScale = scale.get();
        lastTX = tx.get();
        lastTY = ty.get();
        lastX = x;
        lastY = y;
        set = true;
    }

    void finish() {
        set = false;
    }

    protected void writeToTransform() {
        transform.setToTransform(
                scale.get(), 0, tx.get(),
                0, scale.get(), ty.get());
    }
    
    public void zooming(ZoomEvent ze) { 
        zoom(ze.getTotalZoomFactor(), ze.getX(), ze.getY());
    }

    private void zoom(double zoomFactor, double x, double y) {
        if (!set) start(x, y);
        workTransform.setMxx(lastScale);
        workTransform.setMyy(lastScale);
        workTransform.setTx(lastTX);
        workTransform.setTy(lastTY);
        
        workTransform.prependScale(zoomFactor, zoomFactor, x, y);
        
        scale.set(workTransform.getMxx());
        tx.set(workTransform.getTx());
        ty.set(workTransform.getTy());
        
        writeToTransform();
    }

    public void scrolling(ScrollEvent se) {
        if (!set) start(se.getX(), se.getY());
        // get a click count...
        double currentZoom = transform.getMxx();
        if (se.getDeltaY() > 0) {
            zoom(currentZoom * scrollScaleFactor, se.getX(), se.getY());
        } else if (se.getDeltaY() < 0) {
            zoom(currentZoom / scrollScaleFactor, se.getX(), se.getY());
        }
    }


    public void dragging(MouseEvent me) {
        if (!set) start(me.getX(), me.getY());
        if (!Double.isNaN(lastX) && !Double.isNaN(lastY)) {
            double dx = me.getX() - lastX; 
            double dy = me.getY() - lastY; 
            tx.set(tx.get() + dx);
            ty.set(ty.get() + dy);
        }
        lastX = me.getX();
        lastY = me.getY();
        writeToTransform();
    }
    
}
