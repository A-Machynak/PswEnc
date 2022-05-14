package com.am.pswenc;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

class Entry extends VBox {
    private TextField Hostname = new TextField();
    private TextField Password = new TextField();
    private PasswordField PasswordHidden = new PasswordField();
    private SwitchButton sb = new SwitchButton();
    private Button button = new Button("Set");
    private Text text = new Text();
    private Line lineX1 = new Line(800.0, 5.0, 805.0, 0.0);
    private Line lineX2 = new Line(805.0, 5.0, 800.0, 0.0);
    private Circle delCircle = new Circle();
    private StackPane deleteButton = new StackPane(delCircle, lineX1, lineX2);
    private HBox content = new HBox(Hostname, new StackPane(PasswordHidden, Password), sb, deleteButton);

    Entry(String Password, String Hostname) {
        super();
        setPassword(Password);
        setHostname(Hostname);
        getChildren().addAll(content);
        setFillWidth(true);
        setAlignment(Pos.TOP_CENTER);
        FXinit();
    }
    private void FXinit() {
        HBox.setMargin(Hostname, new Insets(2, 10, 2, 10));
        HBox.setMargin(Password, new Insets(2, 10, 2, 10));
        HBox.setMargin(PasswordHidden, new Insets(2, 10, 2, 10));
        HBox.setMargin(button, new Insets(2, 10, 2, 10));
        HBox.setMargin(sb, new Insets(2, 10, 2, 10));
        content.setPrefHeight(25);
        VBox.setVgrow(this, Priority.NEVER);
        super.setMinHeight(50);
        Hostname.setStyle("-fx-border-color: white;-fx-control-inner-background: black; -fx-focus-color: red;");
        Password.setStyle("-fx-control-inner-background: black; -fx-focus-color: red; -fx-border-color: white; -fx-border-radius: 2;");
        text.setFont(new Font("Calibri", 16.0));
        text.setFill(Color.WHITE);
        Password.setEditable(false);
        Hostname.setEditable(false);
        Password.setVisible(false);
        PasswordHidden.setEditable(false);
        PasswordHidden.setVisible(true);
        PasswordHidden.setStyle("-fx-background-color: black; -fx-border-width: 1; -fx-border-color: white; -fx-border-radius: 2;");
        Password.setMinHeight(27);
        PasswordHidden.setText("##################");
        lineX1.setStyle("-fx-stroke-width: 3; -fx-stroke: red;");
        lineX2.setStyle("-fx-stroke-width: 3; -fx-stroke: red;");
        delCircle.setStyle("-fx-stroke:red;");
        delCircle.setRadius(10.0);
        HBox.setMargin(deleteButton, new Insets(0, 0, 0, 20));

        sb.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (sb.waitTilAnimStopped()) {
                if (sb.isShown()) {
                    Password.setVisible(false);
                    PasswordHidden.setVisible(true);
                } else {
                    Password.setVisible(true);
                    PasswordHidden.setVisible(false);
                }
            }
        });
        deleteButton.setOnMouseExited((event) -> {
            Scene scene = ((Node) event.getSource()).getScene();
            scene.setCursor(Cursor.DEFAULT);
        });
        deleteButton.setOnMouseEntered((event) -> {
            Scene scene = ((Node) event.getSource()).getScene();
            scene.setCursor(Cursor.HAND);
        });
        deleteButton.setOnMouseClicked((event) -> {
            try {
                findInTree(getHostname(), (Main.mapper).readTree(Main.data));
                Main.dataTopVB.getChildren().remove(this);
            } catch (Throwable e) {
                System.out.println("Unexpected error at Entry.java/class.\n");
                e.printStackTrace();
            }
        });
    }

    private void findInTree(String host, JsonNode root) throws IOException {
        JsonNode dataNode = root.path("data");
        ArrayNode rootNode = (Main.mapper).createArrayNode();
        if (dataNode.isArray()) {
            for (JsonNode node : dataNode) {
                if (!(node.path("Hostname").asText().equals(host))) {
                    rootNode.add(node);
                }
            }
        }
        ObjectNode wrap = (Main.mapper).createObjectNode();
        wrap.put("Iteration", Main.iterace);
        wrap.putPOJO("data", rootNode);
        (Main.mapper).writeValue(Main.data, wrap);
    }
    private String getHostname() {
        return Hostname.getText();
    }
    private void setHostname(String text) {
        Hostname.setText(text);
    }
    private void setPassword(String text) {
        Password.setText(text);
    }
}
