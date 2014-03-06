package com.shemnon.btc.coinbase;

import com.sun.javafx.application.PlatformImpl;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

import java.security.SecureRandom;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 
 * Created by shemnon on 5 Mar 2014.
 */
public class OAuthLoginTest {

    private String cachedValue;

    @Test
    public void testStoreKey() throws InterruptedException {
        CoinBaseOAuth cbl = new CoinBaseOAuth(null);

        String secret = Long.toHexString(new SecureRandom().nextLong());

        cbl.saveToken(secret, "test");

        String secret2 = cbl.loadToken("test");

        Assert.assertEquals(secret, secret2);
    }


    @Test
    public void snoopKeys() {
        CoinBaseOAuth cbl = new CoinBaseOAuth(null);
        System.out.println(cbl.loadToken("access_token"));
        System.out.println(cbl.loadToken("refresh_token"));
        System.out.println(cbl.loadToken("expire"));
    }

    @Test
    public void testSimpleLogin() throws InterruptedException {

        CountDownLatch waitForIt = new CountDownLatch(1);

        PlatformImpl.startup(() -> {
            WebView loginView = new WebView();
            loginView.setPrefWidth(600);
            loginView.setPrefHeight(600);

            CoinBaseOAuth cbl = new CoinBaseOAuth(loginView);

            Scene theScene = new Scene(loginView);
            Stage theStage = new Stage();
            theStage.setScene(theScene);
            theStage.setOnHiding(e -> waitForIt.countDown());


            cbl.accessTokenProperty().addListener((change, oldV, newV) -> {
                System.out.println(newV);
                CoinBaseAPI cbapi = new CoinBaseAPI(cbl);
                List<CBAddress> addresses = cbapi.getAddresses();
                System.out.println(addresses);
                addresses.forEach(a -> System.out.println(a.getJsonSeed()));
                waitForIt.countDown();
            });
            if (!cbl.checkTokens(true)) {
                theStage.show();
            }
        });

        waitForIt.await();
    }

    @Test
    public void getAddresses() {
        CoinBaseOAuth cbl = new CoinBaseOAuth(null);
        
        Assume.assumeTrue(cbl.checkTokens(false));

        CoinBaseAPI cbapi = new CoinBaseAPI(cbl);
        List<CBAddress> addresses = cbapi.getAddresses();
        System.out.println(addresses);
        addresses.forEach(a -> System.out.println(a.getJsonSeed())); 
    }
}
