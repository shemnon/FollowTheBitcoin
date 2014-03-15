/*
 * Follow the  Bitcoin
 * Copyright (C) 2014  Danno Ferrin
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package com.shemnon.btc.view;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Bounds;
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

    double scrollScaleFactor = 1.1;
    
    double minZoom = 0.01; // 1:100
    double maxZoom = 10.0; // 10:1
    
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
    
    final Group zoomGroup;

    public ZoomPane(Node... nodes) {
        super();
        zoomGroup = new Group(nodes);
        getChildren().addAll(zoomGroup);
        
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
        
        double cs = scale.get();
        double netZoom = zoomFactor * cs;
        if (netZoom < minZoom) {
            zoomFactor = minZoom / cs;
        } else if (netZoom > maxZoom) {
            zoomFactor = maxZoom / cs;
        }
        
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
        double clickCount;
        if (se.getTouchCount() == 0) {
            finish();
            clickCount = Math.signum(se.getDeltaY())*4;
        } else {
            if (!set) {
                start(se.getX(), se.getY());
            }
            if (se.isInertia()) return; // inertia tends to cause crazy zooms
    
            clickCount = se.getTotalDeltaY() / se.getMultiplierY();
        }
        zoom(Math.pow(scrollScaleFactor, clickCount), 
             se.getX(), se.getY());
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
    
    public void center() {
        Bounds gb = zoomGroup.getLayoutBounds();
        Bounds tb = getLayoutBounds();
        double scale = this.scale.get();
        
        tx.set((tb.getWidth() - gb.getWidth()*scale) / 2);
        ty.set((tb.getHeight() - gb.getHeight()*scale) / 2 );
        writeToTransform();
    }
    
    public void fit() {
        Bounds gb = zoomGroup.getLayoutBounds();
        Bounds tb = getLayoutBounds();
        double scalex = tb.getWidth() / (gb.getWidth() + 10.0/scale.get());
        double scaley = tb.getHeight() / (gb.getHeight() + 70.0/scale.get());
        
        double scale = Math.min(1.0, Math.min(scalex, scaley));

        tx.set((tb.getWidth() - gb.getWidth() * scale) / 2);
        ty.set((tb.getHeight() - gb.getHeight()*scale) / 2 + 25);
        this.scale.set(scale);
        writeToTransform();
        
    }
    
    public void zoomOneToOne() {
        Bounds tb = getLayoutBounds();
        double scale = 1 / this.scale.get();
        
        zoom(scale, tb.getWidth() / 2, tb.getHeight() / 2);
    }
    
    public void centerOnNode(Node n) {
        if (n == null) return;
        
        Bounds b = n.getLayoutBounds();
        while (n != null && n != zoomGroup) {
            b = n.localToParent(b);
            n = n.getParent();
        }
        if (n == zoomGroup) {
            Bounds tb = getLayoutBounds();
            tx.set((tb.getWidth() - (b.getMinX() + b.getMaxX())*scale.get()) / 2);
            ty.set((tb.getHeight() - (b.getMinY() + b.getMaxY())*scale.get()) / 2);
            writeToTransform();
        }
    }
    
}
