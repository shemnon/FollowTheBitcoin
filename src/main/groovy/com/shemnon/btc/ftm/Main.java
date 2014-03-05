package com.shemnon.btc.ftm;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * 
 * Created by shemnon on 4 Mar 2014.
 */
public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Follow the \u0e3fitcoins");
        
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/com/shemnon/btc/ftm/FTM.fxml"));
        loader.load();
        AnchorPane root = loader.getRoot();
        
        Scene scene = new Scene(root);
        scene.getStylesheets().setAll(
                 "/com/shemnon/btc/view/btc.css"
        );
        
        primaryStage.setScene(scene);
        
        primaryStage.show();
        
    }
}
