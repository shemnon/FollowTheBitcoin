package com.shemnon.btc.coinbase;

import com.sun.javafx.application.PlatformImpl;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

/**
 * 
 * Created by shemnon on 5 Mar 2014.
 */
public class OAuthLoginTest {
    
    @Test
    public void testSimpleLogin() throws InterruptedException {
        CountDownLatch waitForIt = new CountDownLatch(1);
        

        PlatformImpl.startup(() -> {
            WebView loginView = new WebView();
            loginView.setPrefWidth(600);
            loginView.setPrefHeight(600);
            
            Scene theScene = new Scene(loginView);
            Stage theStage = new Stage();
            theStage.setScene(theScene);
            CoinBaseOAuth cbl = new CoinBaseOAuth(loginView);
            theStage.show();
            theStage.setOnHiding(e -> waitForIt.countDown());
    
            
            cbl.accessTokenProperty().addListener((change, oldV, newV) -> waitForIt.countDown());
            cbl.requestLogin();
        });
            
        waitForIt.await();        
    }
}
