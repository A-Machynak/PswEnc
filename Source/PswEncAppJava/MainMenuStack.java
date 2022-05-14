package com.am.pswenc;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;


class MainMenuStack extends StackPane {
    MainMenuStack(Node... children) {
        super();
        getChildren().addAll(children);
        HoverHandle handle = new HoverHandle();
        ExitHoverHandle EHandle = new ExitHoverHandle();
        super.setOnMouseEntered(handle);
        super.setOnMouseExited(EHandle);
    }
    private static class HoverHandle implements EventHandler<MouseEvent> {
        Node node;
        @Override
        public void handle(MouseEvent event) {
            Scene scene = ((Node)event.getSource()).getScene();
            scene.setCursor(Cursor.HAND);
            node = ((Node)event.getSource());
            animation.play();
        }
        final Animation animation = new Transition() {
            {
                setCycleDuration(Duration.millis(100));
                setInterpolator(Interpolator.EASE_BOTH);
            }
            @Override
            protected void interpolate(double v) {
                Color vColor = Color.rgb((int)(24 * v), (int)(24 * v), (int)(24 * v), 1);
                ((StackPane)node).setBackground(new Background(new BackgroundFill(vColor, new CornerRadii(0), new Insets(0))));
            }
        };
    }
    private static class ExitHoverHandle implements EventHandler<MouseEvent> {
        Node node;
        @Override
        public void handle(MouseEvent event) {
            Scene scene = ((Node)event.getSource()).getScene();
            node = ((Node)event.getSource());
            scene.setCursor(Cursor.DEFAULT);
            animation.play();
        }
        final Animation animation = new Transition() {
            {
                setCycleDuration(Duration.millis(100));
                setInterpolator(Interpolator.EASE_BOTH);
            }
            @Override
            protected void interpolate(double v) {
                Color vColor = Color.rgb(24 - (int)(24 * v), 24 - (int)(24 * v), 24 - (int)(24 * v), 1);
                ((StackPane)node).setBackground(new Background(new BackgroundFill(vColor, new CornerRadii(0), new Insets(0))));
            }
        };
    }
}
