package com.am.pswenc;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

class MidVB extends VBox {
    MidVB(Node... Children) {
        getChildren().addAll(Children);
        super.setPrefSize(100.0,500.0);
        super.setStyle("-fx-background-color:black;");
        super.setAlignment(Pos.CENTER);
    }
}
