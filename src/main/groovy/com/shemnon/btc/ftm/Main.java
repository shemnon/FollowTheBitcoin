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
    
    public static void main(String... args) {
        Application.launch(Main.class, args);
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Where's My \u0e3fitcoin?");
        
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/com/shemnon/btc/ftm/FTM.fxml"));
        loader.load();
        AnchorPane root = loader.getRoot();
        
        Scene scene = new Scene(root);
        scene.getStylesheets().setAll(
                 "/com/shemnon/btc/view/btc.css" ,
                 "com/shemnon/btc/ftm/ftm.css"
        );
        
        primaryStage.setScene(scene);
        
        primaryStage.setOnHidden(exit -> System.exit(0));
        
        primaryStage.show();
        
    }
}
