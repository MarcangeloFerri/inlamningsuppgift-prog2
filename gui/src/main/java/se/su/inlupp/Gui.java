package se.su.inlupp;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;

import java.io.*;
import java.util.HashMap;
import java.util.Map;


public class Gui extends Application {
    private Stage stage;
    private FileChooser fileChooser = new FileChooser();
    private Map<String, String> maps = new HashMap<>();
    private final ImageView imageView = new ImageView();

    public void start(Stage primaryStage) {
        stage = primaryStage;
        Graph<String> graph = new ListGraph<String>();
        primaryStage.setTitle("PathFinder");
        Image icon = new Image("icon.png");
        primaryStage.getIcons().add(icon);
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #2E2E2E;");
        VBox vbox = new VBox();
        root.setTop(vbox);

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

        Button findButton =  styleButtons("Find Path");
        Button showButton =  styleButtons("Show Connection");
        Button newPlaceButton =  styleButtons("New Place");
        Button newConnectionButton =  styleButtons("New Connection");
        Button changeConnectionButton =  styleButtons("Change Connection");

        topButtons.getChildren().add(findButton);
        topButtons.getChildren().add(showButton);
        topButtons.getChildren().add(newPlaceButton);
        topButtons.getChildren().add(newConnectionButton);
        topButtons.getChildren().add(changeConnectionButton);

        return topButtons;
    }
    private Button styleButtons(String text){
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

        return menuBar;
    }

  private void handleOpen() {
    fileChooser.getExtensionFilters().clear();
    fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Graph Files","Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
    );
    fileChooser.setTitle("Välj graf-fil");

    File file = fileChooser.showOpenDialog(stage);
    if (file != null) {
      loadGraphFile(file);
    }
  }

  private void loadGraphFile(File file) {
    System.out.println("Graf-fil vald: " + file.getName());
    // Här implementerar du laddning av graf-fil senare
    // För nu bara testa att fildialogen fungerar
  }

  private void handleNewMap() {
        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));

      //Detta anväder vi för att öppna resource mapen
        File startFolder = new File((System.getProperty("user.home")), "Pictures");
      if (startFolder.exists()) {
          fileChooser.setInitialDirectory(startFolder);
      }

        fileChooser.setTitle("Select map");
        File file = fileChooser.showOpenDialog(stage);
        if(file != null){
          loadImageFile(file);
        }

    }

  private void loadImageFile(File file) {
    try {
      System.out.println("Försöker ladda bild: " + file.getAbsolutePath());

      Image image = new Image(file.toURI().toString());
      imageView.setImage(image);
      imageView.setPreserveRatio(true);
      imageView.setFitWidth(700);

    } catch (Exception e) {
      System.err.println("Error when trying to load image: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
        launch(args);
    }
}
