package com.shemnon.btc.coinbase;

import groovy.json.JsonSlurper;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.prefs.Preferences;

/**
 *
 * Created by shemnon on 5 Mar 2014.
 */
public class CoinBaseOAuth {

    static final byte[] STORE_PASSWORD = "correct battery ".getBytes();
    static final String FTM_KEY = "FTM_OAuth";
    
    static final String COINBASE_CLIENT_ID = System.getProperty("coinbase.client.id");
    static final String COINBASE_CLIENT_SECRET = System.getProperty("coinbase.client.secret");

    static final String CALLBACK_URL = "urn:ietf:wg:oauth:2.0:oob";
    static final String COINBASE_OAUTH_LOGIN = "https://coinbase.com/oauth/authorize?" +
            "response_type=code" +
            "&client_id=" + COINBASE_CLIENT_ID +
            "&redirect_uri=" + CALLBACK_URL +
            "&scope=addresses+transactions+user";

    static final String COINBASE_AUTHORIZED_URL_ROOT = "https://coinbase.com/oauth/authorize/";
    WebView browser;

    StringProperty accessToken = new SimpleStringProperty();
    BooleanProperty visualAuthInProgress = new SimpleBooleanProperty(false);
    
    ExecutorService apiQueries = Executors.newSingleThreadExecutor();

    public CoinBaseOAuth(WebView browser) {
        this.browser = browser;
    }

    public String loadToken(String perm) {
        Preferences node = Preferences.userNodeForPackage(CoinBaseOAuth.class);
        String val = node.get(FTM_KEY+perm, null);
        if (val == null) return null;

        return decrypt(val);
    }

    public void saveToken(String value, String perm) {
        Preferences node = Preferences.userNodeForPackage(CoinBaseOAuth.class);
        if (value == null) {
            node.remove(FTM_KEY+perm);
        } else {
            String val = encrypt(value);
            node.put(FTM_KEY+perm, val);
        }
    }

    private String decrypt(String val) {
        try {
            SecretKeySpec skeySpec = new SecretKeySpec(STORE_PASSWORD, "AES");

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] encrypted = Base64.getDecoder().decode(val.getBytes());
            byte[] original = cipher.doFinal(encrypted);

            return new String(original, Charset.forName("US-ASCII"));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        return null; // fall through
    }

    private String encrypt(String key) {
        String val;
        try {
            SecretKeySpec skeySpec = new SecretKeySpec(STORE_PASSWORD, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] encrypted = cipher.doFinal(key.getBytes());
            val = new String(Base64.getEncoder().encode(encrypted));

        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            val = null;
        }
        return val;
    }

    public void clearTokens() {
        saveToken(null, "refresh_token");
        saveToken(null, "access_token");
        saveToken(null, "expire");
        Platform.runLater(() -> accessToken.setValue(null));
    }
    
    /**
     * 
     *
     * @param attemptRefresh If tokens are out of date, attempt a refresh
     * @param attemptLogin If the tokens are invalid and refresh fails, attempt login
     * @return true if tokens are immediatly valid.  
     */
    public boolean checkTokens(boolean attemptRefresh, boolean attemptLogin) {
        boolean success = false;
        do {
            try {
                long time;
                String xp = loadToken("expire");
                if (xp == null) break;
                time = Long.parseLong(xp);

                if (System.currentTimeMillis() < time) {
                    Platform.runLater(() -> accessToken.setValue(loadToken("access_token")));
                    success = true;
                } else {
                    String refreshToken = loadToken("refresh_token");
                    if (refreshToken != null) {
                        success = attemptRefresh && refreshTokens(refreshToken);
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
                break;
            }
        } while (false);
        
        if (!success && attemptLogin) {
            requestLogin();
        }
         
        return success;
    }
    
    public boolean refreshTokens(String refreshToken) {
        if (refreshToken == null) {
            return false;
        }
        String postContent = "";
        try {
            URL tokenURL = new URL("https://coinbase.com/oauth/token");

            HttpURLConnection uc = (HttpURLConnection) tokenURL.openConnection();
            uc.setRequestMethod("POST");
            uc.setDoOutput(true);
            postContent = "grant_type=refresh_token" +
                    "&refresh_token=" + refreshToken +
                    "&redirect_uri=" + CALLBACK_URL +
                    "&client_id=" + COINBASE_CLIENT_ID +
                    "&client_secret=" + COINBASE_CLIENT_SECRET;
            uc.getOutputStream().write(postContent.getBytes());
            uc.connect();

            JsonSlurper slurper = new JsonSlurper();
            Map m = (Map) slurper.parse(new InputStreamReader(uc.getInputStream())); // TODO cleanup

            long expire = System.currentTimeMillis() + ((((Number)m.get("expires_in")).longValue()*1000));

            saveToken((String) m.get("access_token"), "access_token");
            saveToken((String) m.get("refresh_token"), "refresh_token");
            saveToken(Long.toString(expire), "expire");

            Platform.runLater(() -> accessToken.setValue((String) m.get("access_token")));
            return true;
        } catch (IOException e) {
            System.out.println(postContent);
            e.printStackTrace();
        }

        return false;
    }
    
    public void requestLogin() {
        visualAuthInProgress.set(true);
        WebEngine webEngine = browser.getEngine();

        webEngine.getLoadWorker().stateProperty().addListener((ov, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                String location = webEngine.getLocation();
                if (location.startsWith(COINBASE_AUTHORIZED_URL_ROOT)) {
                    String code = location.substring(COINBASE_AUTHORIZED_URL_ROOT.length());
                    
                    apiQueries.submit(() -> {
                        requestTokens(code);
                        Platform.runLater(() -> visualAuthInProgress.set(false));
                    });

                    webEngine.setOnStatusChanged(null);
                }
            }
        });
        webEngine.load(COINBASE_OAUTH_LOGIN);
    }

    private void requestTokens(String code) {
        try {
            URL tokenURL = new URL("https://coinbase.com/oauth/token");

            HttpURLConnection uc = (HttpURLConnection) tokenURL.openConnection();
            uc.setRequestMethod("POST");
            uc.setDoOutput(true);

            String postContent = "grant_type=authorization_code" +
                    "&code=" + code +
                    "&redirect_uri=" + CALLBACK_URL +
                    "&client_id=" + COINBASE_CLIENT_ID +
                    "&client_secret=" + COINBASE_CLIENT_SECRET;
            uc.getOutputStream().write(postContent.getBytes());
            uc.connect();

            JsonSlurper slurper = new JsonSlurper();
            Map m = (Map) slurper.parse(new InputStreamReader(uc.getInputStream())); // TODO cleanup

            long expire = System.currentTimeMillis() + ((((Number)m.get("expires_in")).longValue()*1000));

            saveToken((String) m.get("access_token"), "access_token");
            saveToken((String) m.get("refresh_token"), "refresh_token");
            saveToken(Long.toString(expire), "expire");

            Platform.runLater(() -> accessToken.setValue((String) m.get("access_token")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String getAccessToken() {
        return accessToken.get();
    }

    public void setAccessToken(String newAccessToken) {
        Platform.runLater(() -> accessToken.setValue(newAccessToken));
    }

    public StringProperty accessTokenProperty() {
        return accessToken;
    }

    public boolean getVisualAuthInProgress() {
        return visualAuthInProgress.get();
    }

    public BooleanProperty visualAuthInProgressProperty() {
        return visualAuthInProgress;
    }

    public void setVisualAuthInProgress(boolean visualAuthInProgress) {
        this.visualAuthInProgress.set(visualAuthInProgress);
    }
}
