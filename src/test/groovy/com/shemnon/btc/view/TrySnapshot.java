package com.shemnon.btc.view;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sun.net.www.content.image.png;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class TrySnapshot extends Application {

    public static void main(String... args) {
        Application.launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(TrySnapshot.class.getResource("/com/shemnon/btc/view/logo.fxml"));
        loader.load();
        Pane root = loader.getRoot();


        root.setOnMouseClicked(event -> {
            
            for (String s : Arrays.asList("shortcut", "volume")) {
                for (int i : Arrays.asList(16, 32, 128, 256, 512)) {
                    // here we make image from vbox and add it to scene, can be repeated :)
                    SnapshotParameters snp = new SnapshotParameters();
                    snp.setFill(Color.TRANSPARENT);
                    double scale = i / root.getWidth();
                    snp.setTransform(Transform.scale(scale, scale, root.getWidth() / 2, root.getHeight() / 2));
                    WritableImage snapshot = root.snapshot(snp, null);

                    File file = new File("/tmp/" + s + "-" + i + ".png");

                    try {
                        ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", file);
                    } catch (Exception e) {
                    }

                    scale = scale * 2;
                    snp.setTransform(Transform.scale(scale, scale, root.getWidth() / 2, root.getHeight() / 2));
                    snapshot = root.snapshot(snp, null);

                    file = new File("/tmp/" + s + "-" + i + "@2x.png");

                    try {
                        ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", file);
                    } catch (Exception e) {
                    }
                }
            }
        });

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().setAll(
                "/com/shemnon/btc/view/btc.css",
                "com/shemnon/btc/ftm/ftm.css"
        );

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        
        primaryStage.setScene(scene);
        

        primaryStage.setOnHidden(exit -> System.exit(0));
        primaryStage.show();
    }
}
