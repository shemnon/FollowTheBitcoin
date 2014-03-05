package com.shemnon.btc.ftm;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

public class FTMGenerated {

    @FXML
    ResourceBundle resources;

    @FXML
    URL location;

    @FXML
    ChoiceBox<String> choiceType;

    @FXML
    TextField textSearch;

    @FXML
    Pane mapPane;

    @FXML
    void newHash(ActionEvent event) {

    }

    @FXML
    void onReset(ActionEvent event) {

    }

    @FXML
    void onCenter(ActionEvent event) {

    }

    @FXML
    void initialize() {
        assert choiceType != null : "fx:id=\"choiceType\" was not injected: check your FXML file 'FTM.fxml'.";
        assert textSearch != null : "fx:id=\"textSearch\" was not injected: check your FXML file 'FTM.fxml'.";
        assert mapPane != null : "fx:id=\"mapPane\" was not injected: check your FXML file 'FTM.fxml'.";

    }
}
