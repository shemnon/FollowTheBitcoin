package com.shemnon.btc.coinbase;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 *
 * Created by shemnon on 5 Mar 2014.
 */
public class CoinBaseOAuth {

    static final String COINBASE_CLIENT_ID = System.getProperty("coinbase.client.id");

    static final String CALLBACK_URL = "urn:ietf:wg:oauth:2.0:oob";
    static final String COINBASE_OAUTH_LOGIN = "https://coinbase.com/oauth/authorize?" +
            "response_type=code" +
            "&client_id=" + COINBASE_CLIENT_ID +
            "&redirect_uri=" + CALLBACK_URL +
            "&scope=addresses+transactions+";

    static final String COINBASE_AUTHORIZED_URL_ROOT = "https://coinbase.com/oauth/authorize/";
    WebView browser;

    StringProperty accessToken = new SimpleStringProperty();

    public CoinBaseOAuth(WebView browser) {
        this.browser = browser;
    }

    public void requestLogin() {
        WebEngine webEngine = browser.getEngine();
        System.out.println(COINBASE_CLIENT_ID);

        webEngine.setOnStatusChanged(event -> {
            if (event.getSource() instanceof WebEngine) {
                WebEngine we = (WebEngine) event.getSource();
                String location = we.getLocation();
                System.out.println(" -> '" + location + "'");
                if (location.startsWith(COINBASE_AUTHORIZED_URL_ROOT)) {
                    String token = location.substring(COINBASE_AUTHORIZED_URL_ROOT.length());
                    System.out.println("The access token: " + token);
                    accessToken.setValue(token);
                    webEngine.setOnStatusChanged(null);
                } else {
                    System.out.println(event);
                }
            }
        });
        webEngine.load(COINBASE_OAUTH_LOGIN);
    }


    public String getAccessToken() {
        return accessToken.get();
    }

    public void setAccessToken(String accessToken) {
        this.accessToken.set(accessToken);
    }

    public StringProperty accessTokenProperty() {
        return accessToken;
    }

}
