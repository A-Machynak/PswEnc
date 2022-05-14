package com.am.pswenc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import javax.crypto.BadPaddingException;
import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

/*
--- Hierarchie ---
 |RootBPane|
 |-topHB
 |--topRHB
 |---StackMin
 |----LineMin
 |---StackX
 |----LineX1
 |----LineX2
 |--topLHB
 |
 |-leftVB
 |--LTopVB
 |---ivLogo (Image logo)
 |--StackLogin
 |---Login
 |---ivLogin (Image icon_login)
 |--StackData
 |---Data
 |---ivData (Image icon_data)
 |--StackSett
 |---Settings
 |---ivSett (Image icon_settings)
 |--LBotVB
 |---Label (A.M. , 2019)
 |
 |-midVB --> Proměnlivý obsah
 |-- ...
------------------
*/
public class Main extends Application {
    private double x;
    private double y;
    private static final Image logo = new Image(Objects.requireNonNull(Main.class.getClassLoader().getResourceAsStream("icon_128.png")), 128, 128, false, false);
    //private static final Image icon_login = new Image(Objects.requireNonNull(Main.class.getClassLoader().getResourceAsStream("icon_acc.png")), 64, 64, true, false);
    private static final Image icon_data = new Image(Objects.requireNonNull(Main.class.getClassLoader().getResourceAsStream("icon_data.png")), 64, 64, true, true);
    private static final Image icon_settings = new Image(Objects.requireNonNull(Main.class.getClassLoader().getResourceAsStream("icon_settings.png")), 64, 64, true, true);
    private static final Image icon_lock = new Image(Objects.requireNonNull(Main.class.getClassLoader().getResourceAsStream("icon_lock.png")), 51, 64, true, true);
    private static final Image icon_unlock = new Image(Objects.requireNonNull(Main.class.getClassLoader().getResourceAsStream("icon_unlock.png")), 51, 64, true, true);
    private Line lineMin = new Line(800.0, 0.0, 812.0, 0.0);
    private Line lineX1 = new Line(800.0, 10.0, 810.0, 0.0);
    private Line lineX2 = new Line(810.0, 10.0, 800.0, 0.0);

    private StackPane StackMin = new StackPane(lineMin);
    private StackPane StackX = new StackPane(lineX1, lineX2);

    private HBox topRHB = new HBox(StackMin, StackX);
    private HBox topLHB = new HBox();
    private HBox topHB = new HBox(topLHB, topRHB);
    private MainMenuLabel Accounts = new MainMenuLabel("Login");
    //private MainMenuLabel CD = new MainMenuLabel("Connected", false);
    private MainMenuLabel Data = new MainMenuLabel("Data");
    private MainMenuLabel Settings = new MainMenuLabel("Settings");
    private MainMenuLabel Info = new MainMenuLabel("Info");

    private ImageView ivLogo = new ImageView(logo);
    private ImageView ivLogin = new ImageView(icon_lock);
    private ImageView ivData = new ImageView(icon_data);
    private ImageView ivSettings = new ImageView(icon_settings);

    private MainMenuStack StackLogin = new MainMenuStack(Accounts, ivLogin);
    private MainMenuStack StackData = new MainMenuStack(Data, ivData);
    private MainMenuStack StackSett = new MainMenuStack(Settings, ivSettings);
    private MainMenuStack StackInfo = new MainMenuStack(Info);

    private VBox LTopVB = new VBox(ivLogo);
    private VBox LBotVB = new VBox(StackInfo, new Label("AM, 2019"));
    private VBox leftVB = new VBox(LTopVB, StackLogin, StackData, StackSett, LBotVB); //CD
    // Login
    private Text textMK = new Text("Master key");
    private PasswordField PFieldMK = new PasswordField();
    private Button loginButton = new Button("Set");
    private Text loginText = new Text();
    private MidVB loginMidVB = new MidVB(textMK, PFieldMK, loginButton, loginText); // Content
    // Data
    private static Text dataText;
    private Text dataHostText = new Text("Hostname");
    private Text dataPassText = new Text("Password");
    private HBox dataHostname = new HBox(dataHostText);
    private HBox dataPassword = new HBox(dataPassText);
    private HBox dataTextWrap = new HBox(dataHostname, dataPassword);
    static VBox dataTopVB;
    private static VBox dataBotVB;
    private static ScrollPane dataSCP;
    private static MidVB dataMidVB; // Content

    // Settings
    private Text settingsText = new Text("Login before proceeding");
    private TextField settingsTField = new TextField();
    private PasswordField settingsTField2 = new PasswordField();
    private HBox settingsFields = new HBox(settingsTField2, settingsTField);
    private Button settingsButton = new Button("Set");
    private MidVB settingsMidVB = new MidVB(settingsText); // Content

    // Info
    private Text infoText = new Text("PswEnc uses AES/ECB encryption\nto encrypt your passwords.\n\n" +
            "For creating password hash a PBKDF2-HMAC-SHA256\nwith optional iteration count is used.\n" +
            "Iteration count can be changed in the settings tab.\n\n" +
            "Hostname/Webpage is optional and if you're not using\nthe PswEnc browser extension,\nthen anything can be entered in this field.\n");
    private MidVB infoMidVB = new MidVB(infoText); // Content

    private BorderPane RootBPane = new BorderPane(infoMidVB, topHB, null, null, leftVB);

    private Scene MainScene = new Scene(RootBPane, 800, 600);

    private Map<MainMenuStack, MidVB> MainStacks = new HashMap<MainMenuStack, MidVB>();

    // 1 objectmapper - je příliš nákladné vytvářet více
    static ObjectMapper mapper;
    static File data;
    private boolean firstAccess = false;
    private Crypt Access;
    static int iterace;

    @Override
    public void init() {
        StackLogin.setDisable(false);
        StackData.setDisable(false);
        StackSett.setDisable(false);
        StackInfo.setDisable(true);
        FXInit();
        staticInit();
        MidVBSwitching();
        /* Minimalizovat, zavřít */
        StackMin.setOnMouseClicked((event) -> {
            if(event.getButton() == MouseButton.PRIMARY) {
                Stage stage = ((Stage) ((Node) event.getSource()).getScene().getWindow());
                stage.setIconified(true);
            }
        });
        StackX.setOnMouseClicked((event) -> {
            if(event.getButton() == MouseButton.PRIMARY) {
                Stage stage = ((Stage) ((Node) event.getSource()).getScene().getWindow());
                stage.close();
            }
        });

        /* Přesouvání */
        topLHB.setOnMouseDragged((event) -> {
            if(event.isPrimaryButtonDown()) {
                Stage stage = ((Stage) ((Node) event.getSource()).getScene().getWindow());
                stage.setX(event.getScreenX() - x);
                stage.setY(event.getScreenY() - y);
            }
        });
        topLHB.setOnMousePressed((event) -> {
            if(event.isPrimaryButtonDown()) {
                x = event.getSceneX();
                y = event.getSceneY();
            }
        });

        StackMin.setOnMouseEntered(new HoverHandle());
        StackMin.setOnMouseExited(new ExitHoverHandle());
        StackX.setOnMouseEntered(new HoverHandle());
        StackX.setOnMouseExited(new ExitHoverHandle());

        // vyzadat focus pri kliknuti na jakykoliv uzel
        RootBPane.setOnMouseClicked((event) -> {
            Node node = (Node)event.getSource();
            node.requestFocus();
        });
        // vytvorit novy soubor s daty
        try {
            data = new File("../../Data/data.json");
            try {
                if (data.createNewFile()) {
                    iterace = 100000;
                    emptyDataFile(100000);
                }
            } catch (IOException e) {
                //System.out.println("Folder Data doesn't exist.");
                loginText.setText("Application couldn't find \"Data\" folder\n" +
                        "in the root folder.\n" +
                        "Create a new folder or reinstall the app.");
                return;
            }
            // Zkontrolujeme, jestli je soubor s daty prazdny
            // zobrazime text popripade
            if ((mapper.readTree(data).get("data")).toString().equals("[]")) {
                iterace = 100000;
                firstAccess = true;
                loginText.setText("\nIt seems like this is your first time\n" +
                        "setting up a master key.\n\n" +
                        "Please choose a master key,\n" +
                        "which will be used to encrypt your passwords.\n" +
                        "You'll need to enter the same master key\n" +
                        "every time you login.\n\nChoose carefully.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // -- Login Button --
        // pri kliknuti se vypne

        loginButton.setOnMouseEntered((event) -> {
            loginButton.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-text-fill: black; -fx-border-color: white; -fx-border-radius: 5;");
            Scene scene = ((Node) event.getSource()).getScene();
            scene.setCursor(Cursor.HAND);
        });
        loginButton.setOnMouseExited((event) -> {
            loginButton.setStyle("-fx-background-color: black; -fx-background-radius: 5; -fx-text-fill: white; -fx-border-color: white; -fx-border-radius: 5;");
            Scene scene = ((Node) event.getSource()).getScene();
            scene.setCursor(Cursor.DEFAULT);
        });

        settingsButton.setOnMouseEntered((event) -> {
            settingsButton.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-text-fill: black; -fx-border-color: white; -fx-border-radius: 5;");
            Scene scene = ((Node) event.getSource()).getScene();
            scene.setCursor(Cursor.HAND);
        });
        settingsButton.setOnMouseExited((event) -> {
            settingsButton.setStyle("-fx-background-color: black; -fx-background-radius: 5; -fx-text-fill: white; -fx-border-color: white; -fx-border-radius: 5;");
            Scene scene = ((Node) event.getSource()).getScene();
            scene.setCursor(Cursor.DEFAULT);
        });
        settingsButton.setOnAction((event) -> {
            if(!settingsTField.getText().equals("")) {
                int temp = Integer.parseInt(settingsTField.getText());
                if (temp < 1000 || temp > 10000000) {
                    settingsText.setText("Choose a value between 1000 and 10000000");
                } else {
                    settingsText.setText("Wait until all your passwords get processed.");
                    PauseTransition pause = new PauseTransition(Duration.seconds(1));
                    pause.play();
                    pause.setOnFinished((event1) -> {
                        try {
                            newIterationCount(temp);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        });
        // Textfield, do kterého lze přidávat pouze čísla
        // - listener na text, kontola přes regex, pokud není číslo, tak smaže
        settingsTField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                settingsTField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        /* Po kliknuti na login button smaze nektere uzly
        *  z ostatnich "scen" a desifruje vsechny hesla
        *  pokud nastane BadPaddingException, tak byl
        *  zadan spatny master klic
        */
        loginButton.setOnAction((event) -> {
            dataTopVB.getChildren().remove(dataText);
            dataTopVB.getChildren().add(dataTextWrap);
            String pswentry = String.valueOf(PFieldMK.getText());
            NewEntry entryS = new NewEntry(pswentry);
            VBox.setVgrow(entryS, Priority.ALWAYS);
            if(!firstAccess) {
                loginText.setText("Attempting to decrypt files... Please wait");
            } else {
                loginText.setText("Master key set.\nYou'll need to enter the same key\nevery time you login.");
            }
            dataBotVB.getChildren().add(entryS);
            PauseTransition pause = new PauseTransition(Duration.millis(100));
            pause.play();
            // pockat, aby se stihl zobrazit text
            pause.setOnFinished((event1) -> {
                try {
                    if(!firstAccess) {
                        JsonNode root = (mapper.readTree(data));
                        JsonNode dataNode = root.path("data");
                        iterace = root.get("Iteration").asInt();
                        long starttime = System.nanoTime();
                        // counter hesel
                        int count = 0;
                        if (dataNode.isArray()) {
                            boolean noException = true;
                            // Projde data array a dešifruje každé heslo
                            // Špatně zadané heslo vyhodí BadPaddingException
                            for (JsonNode node : dataNode) {
                                String hostname = node.path("Hostname").textValue();
                                String password = node.path("Password").asText();
                                String salt = node.path("Salt").asText();
                                try {
                                    count++;
                                    Access = new Crypt(pswentry, Base64.getDecoder().decode(salt), iterace);
                                    dataTopVB.getChildren().add(new Entry(Access.Decrypt(password), hostname));
                                } catch (BadPaddingException e) {
                                    e.printStackTrace();
                                    dataTopVB.getChildren().removeAll(dataTopVB.getChildren());
                                    dataBotVB.getChildren().remove(entryS);
                                    dataTopVB.getChildren().add(dataText);
                                    loginText.setText("Incorrect master key, try again");
                                    noException = false;
                                    break;
                                }
                            }
                            if(noException) {
                                DecimalFormat df = new DecimalFormat("#.####");
                                df.setRoundingMode(RoundingMode.CEILING);
                                double time = ((System.nanoTime() - starttime) * 0.000000001);
                                loginText.setText("It took around " + df.format(time) + " seconds\n" +
                                        "to decrypt your passwords.\n" +
                                        "(average of " + df.format(time / count) + " seconds for 1 password)" +
                                        "\n\nConsider changing iteration count,\nif the number is too high.");
                                loggedIn();
                            }
                        }
                    } else { // První login
                        loggedIn();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("\nUnexpected error\n");
                }
            });
        });
    }
    // -- HoverHandle a ExitHoverHandle --
    // pro "ikony" minimalizace a zavreni
    // - jiný zpusob zapisu bez pouziti lambda vyrazu
    private static class HoverHandle implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent event) {
            Scene scene = ((Node)event.getSource()).getScene();
            Node node = ((Node)event.getSource());
            scene.setCursor(Cursor.HAND);
            node.setStyle("-fx-background-color:#C0C0C0;");
        }
    }
    private static class ExitHoverHandle implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent event) {
            Scene scene = ((Node)event.getSource()).getScene();
            Node node = ((Node)event.getSource());
            scene.setCursor(Cursor.DEFAULT);
            node.setStyle("-fx-background-color: #fff;");
        }
    }

    /* Přepínání contentu */
    private void MidVBSwitching() {
        MainStacks.put(StackLogin, loginMidVB);
        MainStacks.put(StackData, dataMidVB);
        MainStacks.put(StackSett, settingsMidVB);
        MainStacks.put(StackInfo, infoMidVB);

        StackSwitch(StackLogin);
        StackSwitch(StackData);
        StackSwitch(StackSett);
        StackSwitch(StackInfo);
    }
    private void StackSwitch(MainMenuStack node) {
        node.setOnMouseClicked((event) -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                RootBPane.setCenter(MainStacks.get(node));
                for(MainMenuStack temp : MainStacks.keySet()) {
                    temp.setDisable(false);
                }
                node.setDisable(true);
            }
        });
    }
    /* Inicicalizace statických proměnných */
    private void staticInit() {
        dataText = new Text("Data unavailable.");
        dataText.setFill(Color.WHITE);
        dataText.setFont(Font.font("Arial", FontWeight.BOLD, 26.0));
        VBox.setMargin(dataText, new Insets(20, 0, 20, 175));

        dataTopVB = new VBox(dataText);
        dataTopVB.setAlignment(Pos.CENTER);
        dataBotVB = new VBox();
        dataSCP = new ScrollPane(dataTopVB);
        dataMidVB = new MidVB(dataSCP, dataBotVB);

        dataSCP.setStyle("-fx-background: black; -fx-background-color: transparent;");
        dataSCP.vvalueProperty().bind(dataTopVB.heightProperty());

        dataTopVB.setPrefHeight(500);
        dataMidVB.setAlignment(Pos.TOP_LEFT);

        mapper = new ObjectMapper();
    }
    /*  Inicializace stylů, pozicování, velikosti,
        fontů, barev, margin, padding...
    */
    private void FXInit() {
        lineMin.setStyle("-fx-stroke-width: 3; -fx-stroke: black;");
        lineX1.setStyle("-fx-stroke-width: 3; -fx-stroke: red;");
        lineX2.setStyle("-fx-stroke-width: 3; -fx-stroke: red;");

        topRHB.setAlignment(Pos.CENTER_RIGHT);
        topRHB.setPrefWidth(75.0);

        HBox.setHgrow(topLHB, Priority.ALWAYS);

        topHB.setAlignment(Pos.CENTER_RIGHT);
        topHB.setStyle("-fx-background-color: white");

        StackX.setPrefSize(41.0,30.0);
        StackMin.setPrefSize(41.0,30.0);

        StackPane.setAlignment(ivLogin, Pos.CENTER_LEFT);

        textMK.setFill(Color.WHITE);
        textMK.setFont(Font.font("Arial", FontWeight.BOLD, 36.0));

        HBox.setMargin(dataHostname, new Insets(10, 0, 0, 10));
        HBox.setMargin(dataPassword, new Insets(10, 0, 0, 0));
        dataHostname.setMinWidth(160);
        dataPassword.setMinWidth(160);
        dataHostText.setFill(Color.WHITE);
        dataPassText.setFill(Color.WHITE);
        dataHostText.setFont(Font.font("Arial", FontWeight.BOLD, 16.0));
        dataPassText.setFont(Font.font("Arial", FontWeight.BOLD, 16.0));
        dataHostText.setTextAlignment(TextAlignment.CENTER);
        dataPassText.setTextAlignment(TextAlignment.CENTER);

        infoText.setFill(Color.WHITE);
        infoText.setFont(Font.font("Arial", FontWeight.BOLD, 18.0));
        infoText.setTextAlignment(TextAlignment.CENTER);

        loginText.setFill(Color.WHITE);
        loginText.setFont(Font.font("Arial", FontWeight.BOLD, 18.0));
        loginText.setTextAlignment(TextAlignment.CENTER);

        PFieldMK.setPrefSize(100.0, 20.0);
        PFieldMK.setMaxSize(200.0,20.0);
        PFieldMK.setStyle("-fx-control-inner-background: black; -fx-focus-color: red;");

        settingsText.setFill(Color.WHITE);
        settingsText.setFont(Font.font("Arial", FontWeight.BOLD, 18.0));
        settingsText.setTextAlignment(TextAlignment.CENTER);
        VBox.setMargin(settingsText, new Insets(20, 0, 20, 0));

        settingsTField.setPrefSize(100.0, 20.0);
        settingsTField.setMaxSize(200.0,20.0);
        settingsTField.setStyle("-fx-control-inner-background: black; -fx-focus-color: red;");
        settingsTField.setPrefWidth(100);

        settingsTField2.setPrefSize(100.0, 20.0);
        settingsTField2.setMaxSize(200.0,20.0);
        settingsTField2.setStyle("-fx-control-inner-background: black; -fx-focus-color: red;");
        HBox.setMargin(settingsTField, new Insets(0,0,0,5));
        HBox.setMargin(settingsTField2, new Insets(0,5,0,0));

        settingsFields.setAlignment(Pos.CENTER);

        VBox.setMargin(settingsButton, new Insets(10,0,10,0));
        settingsButton.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-background-radius: 5; -fx-border-color: white; -fx-border-radius: 5;");

        VBox.setMargin(loginButton, new Insets(10,0,10,0));
        loginButton.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-background-radius: 5; -fx-border-color: white; -fx-border-radius: 5;");

        StackPane.setAlignment(ivLogin, Pos.CENTER_LEFT);
        StackPane.setAlignment(ivData, Pos.CENTER_LEFT);
        StackPane.setAlignment(ivSettings, Pos.CENTER_LEFT);
        StackPane.setMargin(ivLogin, new Insets(0, 0, 0, 16));
        StackPane.setMargin(ivData, new Insets(0, 0, 0, 10));
        StackPane.setMargin(ivSettings, new Insets(0, 0, 0, 10));

        VBox.setMargin(StackLogin, new Insets(50,0,5,0));
        VBox.setMargin(StackData, new Insets(20,0,5,0));
        VBox.setMargin(StackSett, new Insets(20, 0,0,0));

        Accounts.setAlignment(Pos.CENTER_RIGHT);
        Accounts.setPadding(new Insets(0,10,0,0));
        Data.setAlignment(Pos.CENTER_RIGHT);
        Data.setPadding(new Insets(0,10,0,0));
        Settings.setAlignment(Pos.CENTER_RIGHT);
        Settings.setPadding(new Insets(0,10,0,0));

        LTopVB.setAlignment(Pos.TOP_CENTER);
        LTopVB.setPrefSize(201.0,0);

        LBotVB.setAlignment(Pos.BOTTOM_CENTER);
        LBotVB.setPrefHeight(600);

        leftVB.setStyle("-fx-background-color: black;");
        leftVB.setPrefSize(250.0,370.0);

        RootBPane.setStyle("-fx-border-width: 1; -fx-border-color: red;");
    }
    /* Funkce pro změnění počtu iterací
     - je nutné vytvořit všechny psw hashe znovu
     */
    private void newIterationCount(int newIter) throws Exception {
        String pswentry = settingsTField2.getText();
        // nacteme stare data
        JsonNode root = (mapper.readTree(data));
        JsonNode dataNode = root.path("data");
        ArrayNode rootNode = mapper.createArrayNode();

        // Vytvoříme temp uzel obsahující původní data,
        // pokud změna iterací neproběhne v pořádku
        Group temp = new Group(dataTopVB.getChildren());
        temp.getChildren().remove(dataTextWrap);
        dataTopVB.getChildren().clear();
        dataTopVB.getChildren().add(dataTextWrap);
        // Doplníme nové data
        for (JsonNode node : dataNode) {
            String hostname = node.path("Hostname").textValue();
            String password = node.path("Password").asText();
            String salt = node.path("Salt").asText();
            String plainPass;
            try {
                Access = new Crypt(pswentry, Base64.getDecoder().decode(salt), iterace);
                plainPass = Access.Decrypt(password);
            } catch (BadPaddingException e) {
                settingsText.setText("Wrong master key");
                // Pro jistotu znovu clearneme
                // existuje malá šance pro každé heslo, že projde
                // - mohlo by se 1 heslo desifrovat a cela zalozka s daty se rozbije
                dataTopVB.getChildren().clear();
                dataTopVB.getChildren().add(dataTextWrap);
                dataTopVB.getChildren().addAll(temp.getChildren());
                return;
            }
            // Rovnou vytvoříme novy salt
            Access = new Crypt(pswentry, newIter);
            String newPass = Access.Encrypt(plainPass);
            byte[] newsalt = Access.getSalt();
            ObjectNode addData = mapper.createObjectNode();
            addData.put("Hostname", hostname);
            addData.put("Password", newPass);
            addData.put("Salt", Base64.getEncoder().encodeToString(newsalt));
            rootNode.add(addData);
            dataTopVB.getChildren().add(new Entry(plainPass, hostname));
        }
        // Smažeme staré data, nastavíme nový počet iterací
        emptyDataFile(newIter);
        ObjectNode wrap = mapper.createObjectNode();
        wrap.put("Iteration", newIter);
        wrap.putPOJO("data", rootNode);
        mapper.writeValue(data, wrap);
        iterace = newIter;
        settingsText.setText("Iteration count changed successfully.");
    }
    private void emptyDataFile(int iter) throws IOException {
        ArrayNode arNode = mapper.createArrayNode();
        ObjectNode obj = mapper.createObjectNode();
        obj.put("Iteration", iter);
        obj.putPOJO("data", arNode);
        mapper.writeValue(data, obj);
    }
    private void loggedIn() {
        PFieldMK.setDisable(true);
        loginButton.setDisable(true);
        dataTopVB.setAlignment(Pos.TOP_CENTER);
        ivLogin.setImage(icon_unlock);
        settingsMidVB.getChildren().addAll(settingsFields, settingsButton);
        settingsText.setText("Iteration count adds computational work to\n" +
                "your password hash (key stretching).\n" +
                "Higher iteration count is better,\n" +
                "but requires more time to process.\n" +
                "Your password hashes will have to be created again,\n" +
                "which will take some time.\n\n" +
                "Enter your master key and new iteration count.");
    }

    @Override
    public void start(Stage primaryStage) {
        //Scene rootScene = root.getScene();
        primaryStage.setTitle("PswEnc");
        primaryStage.setScene(MainScene);
        primaryStage.setResizable(true);
        // Schovat topbar
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
