package com.shemnon.btc.ftm;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.util.Map;
import java.util.TreeMap;

/**
 * 
 * Created by shemnon on 30 Mar 2014.
 */
public class TipPanel {
    
    HostServices hostServices;

    private final javafx.util.Duration slideTime = Duration.millis(400);
    
    @FXML SplitMenuButton tipButton;
    @FXML Label tipLabel;
    @FXML HBox tipPanel;
    
    
    private final Timeline tipbarTimeline;
     

    public TipPanel(HostServices hostServices) {
        this.hostServices = hostServices;
        
        tipbarTimeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(tipPanel.translateYProperty(), 0, Interpolator.EASE_BOTH)),
                new KeyFrame(slideTime,
                        new KeyValue(tipPanel.translateYProperty(), -40, Interpolator.EASE_BOTH))
        );
        tipbarTimeline.setAutoReverse(false);

        new Timeline(new KeyFrame(Duration.minutes(1), e -> showTipBar())).play();

    }

    private void showTipBar() {
//        String tip = coinBaseAuth.loadToken("tip");
//        if (tip != null && !tip.isEmpty()) {
//            tipPanel.getChildren().remove(tipButton);
//            tipLabel.setText("CryptoCrumb thanks you for your tip of " + tip);
//            new Timeline(new KeyFrame(Duration.seconds(60), e -> hideTipBar())).play();
//        }
        tipbarTimeline.setRate(1);
        Duration time = tipbarTimeline.getCurrentTime();
        tipbarTimeline.stop();
        tipbarTimeline.playFrom(time);

    }

    public void hideTipBar(ActionEvent e) {
        hideTipBar();
    }

    private void hideTipBar() {
        tipbarTimeline.setRate(-1);
        Duration time = tipbarTimeline.getCurrentTime();
        tipbarTimeline.stop();
        tipbarTimeline.playFrom(time);
    }


    static final Map<String, String> tipMap = new TreeMap<>();
    static {
        tipMap.put("One Fiat US Dollar - $1.00",        "https://coinbase.com/checkouts/b25f15cccb7f69329dd640b27d092e5c");
        tipMap.put("Coffee - $1.38",                    "https://coinbase.com/checkouts/ee47e101a1d76f4180e40e6ca06266f3");
        tipMap.put("Tea - $1.62",                       "https://coinbase.com/checkouts/3c812a86bd866ad8c5353e0e402d9e2c");
        tipMap.put("Beer - $3.64",                      "https://coinbase.com/checkouts/75daf25fa8e45e17f347cb16579703a1");
        tipMap.put("Gold (reddit gold) - $3.99",        "https://coinbase.com/checkouts/f5aa3242de75abb42705e206320d9f98");
        tipMap.put("Dessert-like Coffee - $4.69",       "https://coinbase.com/checkouts/413dfa5da984e9ef73b4be5c4e444122");
        tipMap.put("A Pony (Twilight Sparkle) - $5.99", "https://coinbase.com/checkouts/bf657c6ac19609c18e5d1cbf0ad5abe1");
        tipMap.put("Merkle's Root Beer - $7.99",        "https://coinbase.com/checkouts/eab36e4a02658c5e4b23f9b10673105e");
    }

    public void tip(ActionEvent actionEvent) {
//        String tip = coinBaseAuth.loadToken("tip");

        final String s;
        Object source = actionEvent.getSource();
        if (source instanceof SplitMenuButton) {
            s = ((SplitMenuButton)source).getText();
        } else if (source instanceof MenuItem) {
            s = ((MenuItem)source).getText();
        } else {
            hideTipBar();
            return;
        }

        String url = tipMap.get(s);
        if (url == null) return;

        hostServices.showDocument(url);

//        WebEngine webEngine = webViewLogin.getEngine();
//
//        @SuppressWarnings("unchecked")
//        ChangeListener<? super Worker.State>[] tipListener = new ChangeListener[1];
//
//        tipListener[0] = (ov, oldValue, newValue) -> {
//            if (newValue == Worker.State.SUCCEEDED) {
//                String location = webEngine.getLocation();
//                if (location.startsWith("http://cryptocrumb.com/success")) {
//                    String[] stuff = s.split(" - ");
//                    coinBaseAuth.saveToken(stuff[0], "tip");
//                    webEngine.getLoadWorker().stateProperty().removeListener(tipListener[0]);
//                }
//            }
//        };
//
//        webEngine.getLoadWorker().stateProperty().addListener(tipListener[0]);
//        webEngine.load(url);
//        paneLogin.setVisible(true);

    }
    
}
