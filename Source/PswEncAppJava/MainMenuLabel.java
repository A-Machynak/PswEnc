package com.am.pswenc;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;


class MainMenuLabel extends Label {
    MainMenuLabel(String text) {
        super(text);
        //getStyleClass().setAll("main-menu-label");
        init();
    }
    private void init() {
        super.setTextFill(Color.WHITE);
        super.setFont(Font.font("Calibri", FontWeight.BOLD,32.0));
        super.setPrefSize(250.0,66);
        super.setAlignment(Pos.CENTER);
    }
}
