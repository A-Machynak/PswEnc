package com.am.pswenc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.Base64;

public class NewEntry extends VBox {
    private TextField Hostname = new TextField();
    private TextField Password = new TextField();
    private Button button = new Button("Set");
    private Text text = new Text();
    private HBox content = new HBox(Hostname, Password, button);
    private HBox err_msg = new HBox(text);

    private String encrypted;
    private byte[] salt;
    private Crypt crypt;
    private String password;

    NewEntry(String password) {
        super();
        this.password = password;
        getChildren().addAll(content, err_msg);
        setFillWidth(true);
        setAlignment(Pos.TOP_CENTER);
        init();
    }
    private void init() {
        HBox.setMargin(Hostname, new Insets(2, 10, 2, 10));
        HBox.setMargin(Password, new Insets(2, 10, 2, 10));
        HBox.setMargin(button, new Insets(2, 10, 2, 10));
        content.setPrefHeight(25);
        err_msg.setPrefHeight(25);
        super.setPrefHeight(50);
        Hostname.setStyle("-fx-border-color: white;-fx-control-inner-background: black; -fx-focus-color: red;");
        Password.setStyle("-fx-control-inner-background: black; -fx-focus-color: red;");
        text.setFont(new Font("Calibri", 16.0));
        text.setFill(Color.WHITE);
        Password.setMinHeight(27);
        super.setAlignment(Pos.BOTTOM_CENTER);

        button.setOnMouseClicked((event) -> {
            if(!getHostname().isEmpty() && !getPassword().isEmpty()) {
                try {
                    JsonNode rootDat = (Main.mapper).readTree(Main.data);
                    if(!hostExists(getHostname(), rootDat)) {
                        crypt = new Crypt(password, Main.iterace);
                        encrypted = crypt.Encrypt(getPassword());
                        this.salt = crypt.getSalt();
                        ArrayNode root = ((ArrayNode) (rootDat.get("data")));
                        ObjectNode addData = (Main.mapper).createObjectNode();
                        addData.put("Hostname", getHostname());
                        addData.put("Password", encrypted);
                        addData.put("Salt", Base64.getEncoder().encodeToString(salt));
                        root.add(addData);
                        ObjectNode obj = Main.mapper.createObjectNode();
                        obj.put("Iteration", Main.iterace);
                        obj.putPOJO("data", root);
                        (Main.mapper).writeValue(Main.data, obj);
                        Main.dataTopVB.getChildren().add(new Entry(getPassword(), getHostname()));
                        emptyColons();
                    } else {
                        text.setText("Hostname has to be unique. (Add a number at the end for example)");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    text.setText("Password is too short.");
                }
            } else {
                text.setText("Please enter hostname and password.");
            }
        });
    }
    private void emptyColons() {
        Hostname.setText("");
        Password.setText("");
        text.setText("");
    }
    private boolean hostExists(String host, JsonNode root) {
        JsonNode dataNode = root.path("data");
        if (dataNode.isArray()) {
            for (JsonNode node : dataNode) {
                if (node.path("Hostname").asText().equals(host)) {
                    return true;
                }
            }
        }
        return false;
    }
    String getHostname() {
        return Hostname.getText();
    }
    String getPassword() {
        return Password.getText();
    }
    public void setHostname(String text) {
        Hostname.setText(text);
    }
    public void setPassword(String text) {
        Password.setText(text);
    }
}
