/*
 * Trail of Bitcoin
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

package com.shemnon.btc.ftm;

import com.shemnon.btc.coinbase.CBAddress;
import com.shemnon.btc.coinbase.CBTransaction;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;

/**
 * 
 * Created by shemnon on 7 Mar 2014.
 */
public class CoinbaseDataCell extends TreeCell<JsonBase> {

    JsonBase userData; //cringe
    
    VBox dataCell;
    Label type;
    Label date;
    Label hash;
    
    Consumer<JsonBase> nodeDoubleClicked;

    protected static final DateFormat dateFormat =  new SimpleDateFormat("yy-MM-dd HH:mm");

    public CoinbaseDataCell(Consumer<JsonBase> nodeDoubleClicked) {
        this.nodeDoubleClicked = nodeDoubleClicked;

        type = new Label();
        type.setWrapText(true);
        date = new Label();
        date.setWrapText(true);
        hash = new Label();
        hash.setWrapText(true);
        dataCell = new VBox();
        setGraphic(dataCell);
        
        setOnMouseClicked(this::mouseClicked);
    }
    
    public void mouseClicked(MouseEvent event) {
        if (event.getClickCount() == 2) {
            nodeDoubleClicked.accept(userData);
        }
    }
    

    @Override
    protected void updateItem(JsonBase item, boolean empty) {
        super.updateItem(item, empty);
        userData = item;
        if (item == null || empty) {
            setGraphic(null);
            setText(null);
        } else if (item instanceof CBAddress) {
            CBAddress address = (CBAddress)item;
            if (address.getLabel().isEmpty()) {
                type.setText("Unnamed Wallet");
            } else {
                type.setText(address.getLabel());
            }
            Date createdAt = address.getCreatedAt();
            if (createdAt == null) {
                date.setText("");
            } else {
                date.setText("Created " + dateFormat.format(address.getCreatedAt()));
            }
            hash.setText(JsonBase.shortHash(address.getAddress()));
            dataCell.getChildren().setAll(type, hash, date);
            setGraphic(dataCell);
        } else if (item instanceof CBTransaction) {
            CBTransaction trans = (CBTransaction) item;
            String note = trans.getNotes();
            if (note != null && !note.isEmpty()) {
                type.setText(trans.getNotes());
            } else {
                if (trans.isSpend()) {
                    type.setText("Spend " + trans.getAmount().substring(1));
                } else {
                    type.setText("Receive " + trans.getAmount());
                }
            }
            date.setText(dateFormat.format(trans.getCreatedAt()));
            hash.setText(JsonBase.shortHash(trans.getHash()));
            dataCell.getChildren().setAll(type, hash, date);
            setGraphic(dataCell);
        } else if (item instanceof JsonBaseLabel) {
            type.setText(((JsonBaseLabel)item).getLabel());
            dataCell.getChildren().setAll(type);
            setGraphic(dataCell);
        } else {
            type.setText(item.getClass().getName());
            date.setText(item.dumpJson());
            hash.setText("-");
            dataCell.getChildren().setAll(type, hash);
            setGraphic(dataCell);
        }
        
    }    
}
