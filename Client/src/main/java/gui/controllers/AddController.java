package gui.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import newclient.ClientHandler;
import newclient.Creation;
import other.Color;
import other.Location;
import other.Person;
import other.ServerResponse;

public class AddController extends Controller {

    private ClientHandler clientHandler;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField nameFiled;

    @FXML
    private Button readyButton;

    @FXML
    private TextField weightField;

    @FXML
    private TextField heightField;

    @FXML
    private TextField yLocationField;

    @FXML
    private TextField coordYField;

    @FXML
    private TextField nameLocationField;

    @FXML
    private TextField zLocationY;

    @FXML
    private TextField passportField;

    @FXML
    private TextField coordXField;

    @FXML
    private TextField xLocationField;

    @FXML
    private ComboBox<String> hairField;

    @FXML
    private ComboBox<String> locationField;

    @FXML
    private Label userInfoLable;

    @FXML
    private Button toCommandsButton;

    @FXML
    private Button toMapButton;

    Map<Integer, Location> readyLocations = new HashMap<>();

    private ObservableList<String> locations = FXCollections.observableArrayList();

    @FXML
    void initialize() throws IOException {
        clientHandler = ClientHandler.getInstance(args);
        userInfoLable.setText("Пользователь: " + clientHandler.getLogin());

        ObservableList<String> colors = FXCollections.observableArrayList("YELLOW", "WHITE", "BROWN", "ORANGE");
        hairField.setItems(colors);

        createLocationsList();
        locationField.setItems(locations);

        toMapButton.setOnAction(event -> {
            switchToWindow("/map.fxml", toMapButton);
        });

        toCommandsButton.setOnAction(event -> {
            switchToWindow("/commands.fxml", toCommandsButton);
        });

        readyButton.setOnAction(event -> {
            String name = nameFiled.getText().trim();
            String height = heightField.getText().trim();
            String weight = weightField.getText().trim();
            String passport = passportField.getText().trim();
            String hair = hairField.getValue();
            String x = coordXField.getText().trim();
            String y = coordYField.getText().trim();
            String location = locationField.getValue();
            String nameLoc = nameLocationField.getText().trim();
            String xLoc = xLocationField.getText().trim();
            String yLoc = yLocationField.getText().trim();
            String zLoc = zLocationY.getText().trim();
            Creation creation = new Creation(name, height ,weight, passport,hair, x, y, location, nameLoc, xLoc, yLoc, zLoc);
            ServerResponse response = creation.createNewPerson(readyLocations);
            if (response.getError()!=null) showAlert(Alert.AlertType.ERROR, "Create new person St.1", response.getError(), "");
            else {
                clientHandler.setPerson(response.getPersonList().get(0));
                clientHandler.sendCommand("add");
                ServerResponse answer = null;
                while (answer == null) {
                    try {
                        answer = clientHandler.getAnswer();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (answer.getError()==null){
                    showAlert(Alert.AlertType.INFORMATION, "Create new person", answer.getMessage(), "");
                } else showAlert(Alert.AlertType.ERROR, "Create new person St.2", "Ошибка", "");
            }
        });

    }

    private void createLocationsList() throws IOException {
        clientHandler.sendCommand("show");
        clientHandler.getPeopleAnswer();
        List<Person> readyPeople = clientHandler.getPeople();
        boolean alreadyLocation = false;
        for (Person p : readyPeople) {
            Location currentLocation = p.getLocation();
            for (Location l : readyLocations.values()) {
                if (currentLocation.equals(l)) {
                    alreadyLocation = true;
                    break;
                }
            }
            if (!alreadyLocation) {
                readyLocations.put(readyLocations.size() + 1, currentLocation);
                alreadyLocation = false;
            }
        }
        for (Location l : readyLocations.values()) {
            locations.add(l.getName());
        }
        locations.add("Новая локация");
    }
}
