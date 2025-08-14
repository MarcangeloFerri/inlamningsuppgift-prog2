// PROG2 VT2025, Inlämningsuppgift, del 2
// Grupp 045
// MarcAngelo Ferri mafe1831
// Simon Sundvisson sisu5284

package se.su.inlupp;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.shape.Line;
import java.time.format.DateTimeFormatter;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
//HEj

public class Gui extends Application {
    private Graph<Place> graph = new ListGraph<>();
    private Stage stage;
    private BorderPane root;
    private FileChooser fileChooser = new FileChooser();
    private final ImageView imageView = new ImageView();
    private Pane center;
    private Place from;
    private Place to;
    private boolean hasUnsavedChanges;
    private static final int MARGIN_W = 100;
    private static final int MARGIN_H = 140;
    private File currentImageFile;
    private List<Line> highlightedEdges = new ArrayList<>();
    private Button findButton;
    private Button showButton;
    private Button newPlaceButton;
    private Button newConnectionButton;
    private Button changeConnectionButton;

// Lägga: Knappar skall aktiveras igen (gäoras ”enabled”).

    public void start(Stage primaryStage) {
        stage = primaryStage;
        primaryStage.setTitle("PathFinder");
        Image icon = new Image("icon.png");
        primaryStage.getIcons().add(icon);
        root = new BorderPane();
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


        Scene scene = new Scene(root, 1000, 800);


        primaryStage.setScene(scene);
        primaryStage.show();
        setButtonsEnabled(false);

        stage.setOnCloseRequest(event -> {
            if (hasUnsavedChanges) {
                event.consume(); // Stoppa stängning
                handleExit();
            }
        });
    }

    //Skapar knappar
    private FlowPane createButtons() {
        FlowPane topButtons = new FlowPane();
        topButtons.setAlignment(Pos.BOTTOM_CENTER);
        topButtons.setHgap(5);

        findButton = styleButtons("Find Path");
        findButton.setOnMouseClicked(e -> {
            findButton.setDisable(true);
            handleFindPath();
            findButton.setDisable(false);
        });
        showButton = styleButtons("Show Connection");
        showButton.setOnMouseClicked(e -> {
            showButton.setDisable(true);
            handleShowCoon();
            showButton.setDisable(false);
        });


        newPlaceButton = styleButtons("New Place");
        newPlaceButton.setOnMouseClicked(e -> {
            newPlaceButton.setDisable(true);
            handleNewPlace();

        });


        newConnectionButton = styleButtons("New Connection");
        newConnectionButton.setOnMouseClicked(e -> {
            newConnectionButton.setDisable(true);
            handleNewCoon();
            newConnectionButton.setDisable(false);

        });

        changeConnectionButton = styleButtons("Change Connection");
        changeConnectionButton.setOnMouseClicked(e -> {
            changeConnectionButton.setDisable(true);
            handleChangeCoon();
            changeConnectionButton.setDisable(false);

        });

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

    //Skapar det menybalken för användare gränssnittet
    private MenuBar createMenuBar() {
        Menu menu = new Menu("File");

        MenuItem newMap = new MenuItem("New Map");
        newMap.setOnAction(e -> handleNewMap());

        MenuItem open = new MenuItem("Open");
        open.setOnAction(e -> handleOpen());

        MenuItem save = new MenuItem("Save");
        save.setOnAction(e -> handleSave());
        //NÅT SÅNT???
        // save.setDisable(true);

        MenuItem saveImage = new MenuItem("Save Image");
        saveImage.setOnAction(e -> handleSaveImage());

        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(e -> handleExit());

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

    //öppnar nya kartor
    void handleNewMap() {
        if (!warnUnsaved()) {
            return;
        }

        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));

        //Detta anväder vi för att öppna resource mapen
        File startFolder = new File("gui/src/main/resources");
        if (startFolder.exists()) {
            fileChooser.setInitialDirectory(startFolder);
        }

        fileChooser.setTitle("Select new map");
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            loadImageFile(file);
        }

    }

    //Öppnar befintliga graph filer
    private void handleOpen() {
        if (!warnUnsaved()) {
            return;
        }

        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Graph Files", "*.graph")
        );
        File startFolder = new File("gui/src/main/resources");
        if (startFolder.exists()) {
            fileChooser.setInitialDirectory(startFolder);
        }
        fileChooser.setTitle("Open Graf file");

        File file = fileChooser.showOpenDialog(stage);


        if (file != null) {
            openGraph(file.getAbsolutePath());
        }
    }

    //Sparar en bild och des noder och vägar som en graph fil
    private void handleSave() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Graph");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Graph Files", "*.graph"));
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {

                //Hämtar bildens sökväg och skriver den i filen
                if (currentImageFile != null) {
                    writer.println(currentImageFile.getAbsolutePath());
                } else {
                    // Fallback om currentImageFile är null (för säkerhets skull)
                    writer.println(imageView.getImage().getUrl());
                }

                // Hämtar Noder
                StringBuilder nodeLine = new StringBuilder();
                for (Place place : graph.getNodes()) {
                    nodeLine.append(place.getName()).append(";")
                            .append((int) place.getX()).append(";")
                            .append((int) place.getY()).append(";");
                }
                writer.println(nodeLine);

                //Hämtar förbindelserna
                for (Place from : graph.getNodes()) {
                    for (Edge<Place> edge : graph.getEdgesFrom(from)) {
                        Place to = edge.getDestination();
                        writer.println(from.getName() + ";" +
                                to.getName() + ";" +
                                edge.getName() + ";" +
                                edge.getWeight());
                    }
                }
                hasUnsavedChanges = false;

            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("IO-Error: " + e.getMessage());
            }
        }

    }

    //Tar en bild av kartan och sparar den högst upp i fil hierarkin
    private void handleSaveImage() {
        WritableImage snapShot = center.snapshot(null, null);
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(snapShot, null);

        // Lägger till datum och tid för varje bild för ökad tydlighet
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmm");
        String timestamp = now.format(formatter);

        File outputImage = new File("capture_" + timestamp + ".png");

        try {
            ImageIO.write(bufferedImage, "png", outputImage);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Image Saved");
            alert.setHeaderText("Your image was successfully saved as:\n" + outputImage.getName());
            alert.show();


        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("IO-Error " + e.getMessage());
            alert.showAndWait();
        }
    }

    //Låter användaren stänga programmet
    private void handleExit() {
        if (warnUnsaved()) {
            Platform.exit();
        }
    }

    //MENUBAR METODER:

    //Hittar den snabbaste vägen mellan två noder och visar total tid samt alla stopp för att nå slut noden
    private void handleFindPath() {
        if (from == null || to == null) {
            errorMes("You must select two places to find a path!");
            return;
        }

        Place startLocation = from;
        Place endLocation = to;

        try {
            List<Edge<Place>> path = graph.getPath(startLocation, endLocation);

            if (path == null || path.isEmpty()) {
                Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
                infoAlert.setTitle("No Path Found");
                infoAlert.setHeaderText(null);
                infoAlert.setContentText(String.format("No path exists between %s and %s",
                        startLocation.getName(), endLocation.getName()));
                infoAlert.showAndWait();
                return;
            }


            int totalWeight = path.stream().mapToInt(Edge::getWeight).sum();


            StringBuilder pathDescription = new StringBuilder();
            pathDescription.append(String.format("Path from %s to %s:\n\n",
                    startLocation.getName(), endLocation.getName()));

            Place currentLocation = startLocation;
            for (Edge<Place> edge : path) {
                pathDescription.append(String.format("%s -> %s via %s (time: %d:h)\n",
                        currentLocation.getName(),
                        edge.getDestination().getName(),
                        edge.getName(),
                        edge.getWeight()));
                currentLocation = edge.getDestination();
            }

            pathDescription.append(String.format("\nTotal time: %d", totalWeight) + ":h");


            Alert pathAlert = new Alert(Alert.AlertType.INFORMATION);
            pathAlert.setTitle("Shortest Path");
            pathAlert.setHeaderText("Path Found");
            pathAlert.setContentText(pathDescription.toString());
            pathAlert.getDialogPane().setPrefWidth(400);
            pathAlert.setResizable(true);

            //För varje conn i pathen kallar den metoden highlightConn()
            Place current = startLocation;
            for (Edge<Place> edge : path) {
                Place next = edge.getDestination();
                highlightConn(current, next);
                current = next;
            }
            // om man klickar försvinner highlighten
            center.setOnMouseClicked(e -> {
                clearHighlight();
            });

            pathAlert.showAndWait();

        } catch (Exception e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Path Finding Error");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("Error finding path: " + e.getMessage());
            errorAlert.showAndWait();
        }

    }

    //Visar en anslutning mellan två noder bredvid varandra och dess namn samt tid
    private void handleShowCoon() {
        if (from == null || to == null) {
            errorMes("You must select two places");
            return;

        }
        if (!graph.pathExists(from, to)) {
            errorMes("Path dose not exist");
            return;

        }
        try {
            var edge = graph.getEdgeBetween(from, to);
            Alert alertMes = new Alert(Alert.AlertType.INFORMATION);
            alertMes.setTitle("Connection");
            alertMes.setHeaderText("Connection from " + from.getName() + " to " + to.getName());
            alertMes.setContentText("Name: " + edge.getName() + "\nTime: " + edge.getWeight() + ":h");

            highlightConn(from, to);
            // om man klickar försvinner highlighten
            center.setOnMouseClicked(e -> {
                clearHighlight();
            });

            alertMes.showAndWait();

        } catch (Exception e) {
            errorMes("Error: " + e.getMessage());
        }

    }

    //Lägger till en nod i graph och en punkt på kartan genom att hämta
    //Musklicks kordinater och be användaren om namn för platsen
    private void handleNewPlace() {
        center.setCursor(Cursor.CROSSHAIR);

        center.setOnMouseClicked(event -> {

            double xPos = event.getX();
            double yPos = event.getY();

            var inputDialog = new TextInputDialog();
            inputDialog.setTitle("New Place");
            inputDialog.setHeaderText("Name Input");
            inputDialog.setContentText("Enter name:");
            inputDialog.setGraphic(null);

            var nameResult = inputDialog.showAndWait();


            if (nameResult.isPresent() && nameResult.get().trim().isEmpty()) {
                Alert warningAlert = new Alert(Alert.AlertType.WARNING);
                warningAlert.setTitle("Invalid Input");
                warningAlert.setHeaderText(null);
                warningAlert.setContentText("Input cannot be empty!");
                warningAlert.showAndWait();

                center.setCursor(Cursor.DEFAULT);
                center.setOnMouseClicked(null);
                newPlaceButton.setDisable(false);

                return;
            }

            String placeName = nameResult.get();

            var newLocation = new Place(nameResult.get(), xPos, yPos);

            graph.add(newLocation);
            drawPlace(newLocation);

            //Lägger till Namn för platser på kartan
            Label placeLabel = new Label(placeName);
            placeLabel.setTextFill(Color.BLACK);
            placeLabel.setStyle("-fx-font-weight: bold;");
            placeLabel.setLayoutX(xPos - 8);
            placeLabel.setLayoutY(yPos + 8);

            center.getChildren().add(placeLabel);

            hasUnsavedChanges = true;

            center.setCursor(Cursor.DEFAULT);
            center.setOnMouseClicked(null);
            newPlaceButton.setDisable(false);


            Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
            infoAlert.setTitle("New place added");
            infoAlert.setHeaderText(null);
            infoAlert.setContentText("%s at [%s; %s]".formatted(nameResult.get(), xPos, yPos));
            infoAlert.showAndWait();
        });
    }

    //Ritar upp nodens plats på kartan
    private void drawPlace(Place place) {
        place.setOnMouseClicked(new PlaceClickHandler());
        center.getChildren().add(place);

    }

    //Ritar upp anslutning mellan två noder på kartan
    private void drawCoon(Place from, Place to) {
        Line edge = new Line();
        edge.setStartX(from.getCenterX());
        edge.setStartY(from.getCenterY());
        edge.setEndX(to.getCenterX());
        edge.setEndY(to.getCenterY());
        edge.setStrokeWidth(2.5);
        edge.setStroke(Color.BLACK);
        edge.setDisable(true);
        center.getChildren().add(edge);
    }

    //Lägger till en överstrykning över vald rut
    private void highlightConn(Place from, Place to) {
        Line edge = new Line();
        edge.setStartX(from.getCenterX());
        edge.setStartY(from.getCenterY());
        edge.setEndX(to.getCenterX());
        edge.setEndY(to.getCenterY());
        edge.setStrokeWidth(5);
        edge.setStroke(Color.GREEN);
        edge.setDisable(true);

        center.getChildren().add(edge);
        highlightedEdges.add(edge);
    }

    //Tar bort överstrykning
    private void clearHighlight() {
        for (Line line : highlightedEdges) {
            center.getChildren().remove(line);
        }
        highlightedEdges.clear();
    }

    //Til låter användaren att skapa nya vägar
    private void handleNewCoon() {

        if (from == null || to == null) {
            errorMes("Two places must be selected!");
            return;
        }

        if (graph.pathExists(from, to)) {
            errorMes("Path already exists!");
            return;
        }

        //Skapar en dialogruta med möjlighet för egen anpassning
        Dialog<ButtonType> dialogPopup = new Dialog<>();
        dialogPopup.setTitle("Add new connection");
        dialogPopup.setHeaderText("Connection between  " + from + " and " + to);

        //Lägger till knappar till dialog rutan
        dialogPopup.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        //Skapar en gridpane för inehållet
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20, 150, 10, 10));

        //Skapar textfält för input fån användaren
        TextField nameField = new TextField();
        nameField.setPromptText("Enter connection name");
        TextField timeField = new TextField();
        timeField.setPromptText("Enter connection time");

        //Lägger till labels och textfält
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Time:"), 0, 1);
        grid.add(timeField, 1, 1);

        // Sätter in grid:en i dialogen
        dialogPopup.getDialogPane().setContent(grid);


        //Visar dialog för att hantera resultatet
        Optional<ButtonType> result = dialogPopup.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String coonName = nameField.getText().trim();
            String textTime = timeField.getText().trim();

            //Kontrolerar att inputen är korekt
            if (coonName.isEmpty()) {
                errorMes("New connection needs a name");
                return;
            }

            if (textTime.isEmpty()) {
                errorMes("New connection needs a time");
                return;
            }


            int coonTime;
            try {
                coonTime = Integer.parseInt(textTime);
            } catch (NumberFormatException e) {
                errorMes("Time must be a valid integer.");
                return;
            }


            try {
                graph.connect(from, to, coonName, coonTime);
                drawCoon(from, to);
                hasUnsavedChanges = true;

                center.setCursor(Cursor.DEFAULT);
                center.setOnMouseClicked(null);

            } catch (Exception e) {
                errorMes("Error when drawing connections: " + e.getMessage());
            }

        }
    }

    //Ger användaren möjlighet att ändra tiden för en kant
    private void handleChangeCoon() {

        if (from == null || to == null) {
            errorMes("You must select two places to change a connection");
            return;
        }

        // Hämta den befintliga kanten
        Edge<Place> edge = graph.getEdgeBetween(from, to);
        if (edge == null) {
            errorMes("No connection exists between selected places.");
            return;
        }
        try {
            TextInputDialog Ctime = new TextInputDialog(String.valueOf(edge.getWeight()));
            Ctime.setTitle("Change time for connection " + from + " to " + to);
            Ctime.setHeaderText("For the connection named: " + edge.getName());
            Ctime.setContentText("Enter new time for of connection:");
            Optional<String> timeRes = Ctime.showAndWait();

            if (timeRes.isEmpty()) {
                return;
            }

            int coonTime = Integer.parseInt(timeRes.get().trim());
            graph.setConnectionWeight(from, to, coonTime);
            hasUnsavedChanges = true;

        } catch (NumberFormatException e) {
            errorMes("Time must be a valid integer.");

        } catch (Exception e) {
            errorMes(" Error when trying to change connection: " + e.getMessage());
        }
    }

    //Återskapar graph från sparad fil
    private void openGraph(String fileName) {
        try {

            File file = new File(fileName);  //Öppnar en fil men namnet som finns i Variabeln fileName

            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String imagePath = bufferedReader.readLine();              //Läser första raden i filen för att hitta bildsökväg

            //Kontrolerar om bilden finns om JA skickar till loadImagFile om NEJ kastar Fel
            File imageFile = new File(imagePath);
            if (!imageFile.exists()) {
                throw new FileNotFoundException("Image file not found: " + imagePath);
            }
            loadImageFile(imageFile);


            // Lägg till null-kontroll för platserna
            String line = bufferedReader.readLine();
            if (line != null && !line.trim().isEmpty()) {
                String[] split = line.split(";");
                HashMap<String, Place> places = new HashMap<>();

                // Går igenom och återskapar alla platser på kartan
                for (int i = 0; i < split.length; i += 3) {
                    String name = split[i];
                    double x = Double.parseDouble(split[i + 1]);
                    double y = Double.parseDouble(split[i + 2]);
                    Place place = new Place(name, x, y);
                    graph.add(place);
                    places.put(name, place);
                    place.setOnMouseClicked(new PlaceClickHandler());

                    //Lägger till Namn för platser på kartan
                    Label placeLabel = new Label(name);
                    placeLabel.setTextFill(Color.BLACK);
                    placeLabel.setStyle("-fx-font-weight: bold;");
                    placeLabel.setLayoutX(x - 8);
                    placeLabel.setLayoutY(y + 8);
                    center.getChildren().add(placeLabel);

                    center.getChildren().add(place);
                }

                //Läser in vägar mellan platser
                while ((line = bufferedReader.readLine()) != null) {
                    split = line.split(";");

                    if (split.length >= 4) {
                        String fromName = split[0];
                        String toName = split[1];
                        String edgeName = split[2];
                        int time = Integer.parseInt(split[3]);


                        Place fromPlace = places.get(fromName);
                        Place toPlace = places.get(toName);

                        if (fromPlace != null && toPlace != null) {
                            //Ser till att det inte skapas dubbla kanter mellan två platser.
                            if (graph.getEdgeBetween(fromPlace, toPlace) == null) {

                                //Ritar förbindelsen
                                graph.connect(fromPlace, toPlace, edgeName, time);
                                drawCoon(fromPlace, toPlace);
                            }
                        }
                    }
                }
            }

            bufferedReader.close();
            fileReader.close();
            hasUnsavedChanges = false;

        } catch (FileNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("File not found: " + fileName);
            alert.showAndWait();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("IO-Error: " + e.getMessage());
            alert.showAndWait();

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid file format - could not parse numbers");
            alert.showAndWait();
        }
    }

    //Markerar platser på kartan
    private class PlaceClickHandler implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent event) {
            Place clicked = (Place) event.getSource();

            //Om platsen är markerad och man klickar på samma plats igen återställs den
            if (clicked.isSelected()) {
                clicked.setSelected(false);
                if (clicked == from) from = null;
                if (clicked == to) to = null;
                return;
            }

            // Om from och to redan är markerade återställ båda när man klickar på en tredje plats
            if (from != null && to != null) {
                from.setSelected(false);
                to.setSelected(false);
                from = null;
                to = null;
            }

            //Väljer platsen (antingen from eller to)
            if (from == null) {
                from = clicked;
                clicked.setSelected(true);
            } else if (to == null && clicked != from) {
                to = clicked;
                clicked.setSelected(true);
            }
        }
    }

    private void loadImageFile(File file) {
        try {
            currentImageFile = file; // spara var bilden som nuvarande källa för att komma ihåg

            Image image = new Image(file.toURI().toString());
            imageView.setImage(image);
            imageView.setPreserveRatio(true); //Behåller storlek vid skalning

            // Centrera bilden i center Pane manuellt genom att lyssna efter ändringar och centrera bilden efter dem
            center.widthProperty().addListener((obs, oldVal, newVal) -> {
                imageView.setLayoutX((newVal.doubleValue() - image.getWidth()) / 2);
            });
            center.heightProperty().addListener((obs, oldVal, newVal) -> {
                imageView.setLayoutY((newVal.doubleValue() - image.getHeight()) / 2);
            });

            //Ser till att center matchar bildens storlek + marginaler
            center.setPrefSize(image.getWidth() + MARGIN_W, image.getHeight() + MARGIN_H);
            center.getChildren().clear();           //Tar bort gammalt
            center.getChildren().add(imageView);   //Lägger till bild

            //Nollställer graf och noder genom att skapa en ny instans av ListGraph
            graph = new ListGraph<>();
            from = null;
            to = null;

            //Anpassar fönster storlek efter bilden + marginal
            stage.setHeight(image.getHeight() + MARGIN_H);
            stage.setWidth(image.getWidth() + MARGIN_W);
            stage.centerOnScreen();

            root.setCenter(center);
            hasUnsavedChanges = true;
            setButtonsEnabled(true);


        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error loading image: " + e.getMessage());
            alert.showAndWait();
        }
    }

    //Ser till att alla knappar är avstängda fram tills att kartan lästs in
    private void setButtonsEnabled(boolean enabled) {
        findButton.setDisable(!enabled);
        showButton.setDisable(!enabled);
        newPlaceButton.setDisable(!enabled);
        newConnectionButton.setDisable(!enabled);
        changeConnectionButton.setDisable(!enabled);
    }

    //Varnar användaren om det finns osparade ändringar
    private boolean warnUnsaved() {
        if (hasUnsavedChanges) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Warning");
            alert.setHeaderText("Unsaved changes, do you wish to continue?");
            Optional<ButtonType> result = alert.showAndWait();

            return result.isPresent() && result.get().equals(ButtonType.OK);

        }
        return true;
    }

    //Hjälp metod som visar fel meddelanden
    private void errorMes(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
