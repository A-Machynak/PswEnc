package com.am.pswenc;

import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.Objects;

class SwitchButton extends StackPane {
    private final ImageView slide = new ImageView(new Image((Objects.requireNonNull(Main.class.getClassLoader().getResourceAsStream("slide1.png"))), 100, 25, false, false));
    private Rectangle obd = new Rectangle(49, 23, Color.BLACK);
    private TranslateTransition tt = new TranslateTransition(Duration.millis(400), obd);
    private boolean show = false;
    SwitchButton() {
        super();
        super.getChildren().addAll(slide, obd);
        obd.setArcHeight(100);
        obd.setArcWidth(18);
        obd.setStroke(Color.RED);
        setAlignment(obd, Pos.CENTER_LEFT);
        setAlignment(slide, Pos.CENTER_LEFT);
        setOnMouseEntered((event) -> {
            Scene scene = ((Node)event.getSource()).getScene();
            scene.setCursor(Cursor.HAND);
        });
        setOnMouseExited((event) -> {
            Scene scene = ((Node)event.getSource()).getScene();
            scene.setCursor(Cursor.DEFAULT);
        });
        setOnMouseClicked((event) -> {
            if(waitTilAnimStopped()) {
                System.out.println(waitTilAnimStopped());
                if (show) {
                    tt.setByX(-51f);
                } else {
                    tt.setByX(51f);
                }
                show = !show;
                tt.setCycleCount(1);
                tt.setAutoReverse(true);
                tt.play();
            }
        });
    }
    boolean isShown() {
        return show;
    }
    boolean waitTilAnimStopped() {
        return tt.getStatus().equals(Animation.Status.STOPPED);
    }
}
