package se.su.inlupp;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class Gui extends Application {
    private Graph<Place> graph = new ListGraph<>();
    private Stage stage;
    private BorderPane root;
    private FileChooser fileChooser = new FileChooser();
    private Map<String, String> maps = new HashMap<>();
    private final ImageView imageView = new ImageView();
    private Pane center;
    private Place from;
    private Place to;
    private boolean changed;


    public void start(Stage primaryStage) {
        stage = primaryStage;
        primaryStage.setTitle("PathFinder");
        Image icon = new Image("icon.png");
        primaryStage.getIcons().add(icon);
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #2E2E2E;");

        VBox vbox = new VBox();
        root.setTop(vbox);

         center = new Pane();
         root.setCenter(center);

        MenuBar menuBar = createMenuBar();
        vbox.getChildren().add(menuBar);

        FlowPane flowPane = createButtons();
        vbox.getChildren().add(flowPane);
        flowPane.setAlignment(Pos.CENTER_LEFT);
        flowPane.setPadding(new Insets(5));

        root.setCenter(imageView);
        Scene scene = new Scene(root, 1000, 800);


        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private FlowPane createButtons() {
        FlowPane topButtons = new FlowPane();
        topButtons.setAlignment(Pos.BOTTOM_CENTER);
        topButtons.setHgap(5);

        Button findButton = styleButtons("Find Path");
        Button showButton = styleButtons("Show Connection");
        Button newPlaceButton = styleButtons("New Place");
        Button newConnectionButton = styleButtons("New Connection");
        Button changeConnectionButton = styleButtons("Change Connection");

        topButtons.getChildren().add(findButton);
        topButtons.getChildren().add(showButton);
        topButtons.getChildren().add(newPlaceButton);
        topButtons.getChildren().add(newConnectionButton);
        topButtons.getChildren().add(changeConnectionButton);

        return topButtons;
    }

    //Ändrar färg på ikoner och när man hovrar
    private Button styleButtons(String text) {
        Button button = new Button(text);
        // Ursprunglig stil
        String baseStyle = "-fx-background-color: #555879;" +
                "-fx-text-fill: white;" +
                "-fx-font-family: 'Segoe UI';" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 6;" +
                "-fx-padding: 8 16;";

        // Hover-stil
        String hoverStyle = "-fx-background-color: #6F73A8;" +  // lite ljusare
                "-fx-text-fill: white;" +
                "-fx-font-family: 'Segoe UI';" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 6;" +
                "-fx-padding: 8 16;";

        // Återställ originalstil
        button.setStyle(baseStyle);

        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(baseStyle));

        return button;
    }

    private MenuBar createMenuBar() {
        Menu menu = new Menu("File");

        MenuItem newMap = new MenuItem("New Map");
        newMap.setOnAction(e -> handleNewMap());

        MenuItem open = new MenuItem("Open");
        open.setOnAction(e -> handleOpen());

        MenuItem save = new MenuItem("Save");
        MenuItem saveImage = new MenuItem("Save Image");
        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(e -> stage.close());

        menu.getItems().add(newMap);
        menu.getItems().add(open);
        menu.getItems().add(save);
        menu.getItems().add(saveImage);
        menu.getItems().add(exit);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(menu);

        //Lägger till färg på menuBar
        menuBar.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-family: 'Segoe UI';" +
                        "-fx-font-weight: bold;"
        );

        return menuBar;
    }

    private void handleOpen() {

        if(changed){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Warning");
            alert.setHeaderText("Unsaved changes, do you wish to continue?");
            Optional<ButtonType> result = alert.showAndWait();

                if(result.isPresent() && !result.get().equals(ButtonType.OK)){
                    return;

                }
        }

        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Graph Files", "*.graph")
        );
        File startFolder = new File("C:\\Users\\marck\\Desktop\\DSV matrial\\Prog2\\Inlämningsuppgiften\\gui\\src\\main\\resources");
        if (startFolder.exists()) {
            fileChooser.setInitialDirectory(startFolder);
        }
        fileChooser.setTitle("Open Graf file");

        File file = fileChooser.showOpenDialog(stage);
        String fileName = file.getName();
        if (file != null) {
            openGraph(fileName);
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("File " + fileName + " not found");
            alert.showAndWait();
        }
    }

    private void openGraph(String fileName){
        try{
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            graph = new ListGraph<>();
            center = new Pane();
            center.setId("Output area");
            center.getChildren().clear();

            from = null;
            to = null;

            Image image = new Image(bufferedReader.readLine());
            imageView.setImage(image);
            center.getChildren().add(imageView);

            root.setCenter(center);
            stage.setWidth(image.getWidth());
            stage.setHeight(image.getHeight());
            stage.centerOnScreen();

            String line = bufferedReader.readLine();
            String[] split = line.split(";");

            HashMap<String, Place> places = new HashMap<>();

            for(){

            }

        }catch(FileNotFoundException e){

        }catch(IOException e){

        }

    }

    private void handleNewMap() {
        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));

        //Detta anväder vi för att öppna resource mapen (Funger dock bara på denna dator)
        File startFolder = new File("C:\\Users\\marck\\Desktop\\DSV matrial\\Prog2\\Inlämningsuppgiften\\gui\\src\\main\\resources");
        if (startFolder.exists()) {
            fileChooser.setInitialDirectory(startFolder);
        }

        fileChooser.setTitle("Select new map");
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            loadImageFile(file);
        }

    }
        //SNYGGA TILL!!!
    private void loadImageFile(File file) {
        try {
            Image image = new Image(file.toURI().toString());
            imageView.setImage(image);

            center.getChildren().retainAll();

            graph = new ListGraph<>();
            from = null;
            to = null;

            stage.setHeight(image.getHeight()+100);
            stage.setWidth(image.getWidth()+100);
            stage.centerOnScreen();

            changed = true;


        } catch (Exception e) {
            System.err.println("Error when trying to load image: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
