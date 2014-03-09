package com.shemnon.btc.ftm;

import com.shemnon.btc.JsonBase;
import com.shemnon.btc.coinbase.CBAddress;
import com.shemnon.btc.coinbase.CBTransaction;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.function.Consumer;

/**
 * 
 * Created by shemnon on 7 Mar 2014.
 */
public class CoinbaseDataCell extends ListCell<JsonBase> {

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
        date = new Label();
        hash = new Label();
        dataCell = new VBox(type,date,hash);
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
        if (item == null) {
            type.setText("");
            date.setText("");
            hash.setText("");
        } else if (item instanceof CBAddress) {
            CBAddress address = (CBAddress)item;
            type.setText("Wallet: " + address.getLabel());
            date.setText("Created " + dateFormat.format(address.getCreatedAt()));
            hash.setText(JsonBase.shortHash(address.getAddress()));
        } else if (item instanceof CBTransaction) {
            CBTransaction trans = (CBTransaction) item;
            if (trans.isSpend()) {
                type.setText("Spend " + trans.getAmount());
            } else {
                type.setText("Receive " + trans.getAmount());
            }
            date.setText(dateFormat.format(trans.getCreatedAt()));
            hash.setText(JsonBase.shortHash(trans.getHash()));
        } else {
            type.setText("-");
            date.setText("-");
            hash.setText("-");
        }
    }    
}
