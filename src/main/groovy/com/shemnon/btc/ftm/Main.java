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
        ExceptionHandler.registerExceptionHandler();
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Trail of \u0e3fitcoins");
        
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

class ExceptionHandler implements Thread.UncaughtExceptionHandler {
    public void uncaughtException(Thread t, Throwable e) {
        handle(e);
    }

    public void handle(Throwable throwable) {
        try {
            System.out.println("Uncaught!");
            throwable.printStackTrace(System.err);
        } catch (Throwable t) {
            // don't let the exception get thrown out, will cause infinite looping!
        }
    }

    public static void registerExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
        System.setProperty("sun.awt.exception.handler", ExceptionHandler.class.getName());
    }
}
