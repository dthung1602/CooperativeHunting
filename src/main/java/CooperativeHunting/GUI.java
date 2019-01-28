package CooperativeHunting;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.stage.Stage;

import static CooperativeHunting.FileLoader.readResourceFile;

public class GUI {
    private Main application;
    Stage stage;
    Map map;

    // Grid (Map)
    @FXML
    private Canvas mapCanvas;
    @FXML
    private TextField width;
    @FXML
    private TextField height;

    // Menu items
    @FXML
    private MenuItem loadSettings;
    @FXML
    private MenuItem loadMap;
    @FXML
    private MenuItem loadSimulation;
    @FXML
    private MenuItem saveSettings;
    @FXML
    private MenuItem saveMap;
    @FXML
    private MenuItem saveSimulation;
    @FXML
    private MenuItem demo1;
    @FXML
    private MenuItem demo2;
    @FXML
    private MenuItem demo3;

    // Display options
    @FXML
    private CheckBox showGrid;
    @FXML
    private CheckBox predatorShowVision;
    @FXML
    private CheckBox showGroup;
    @FXML
    private CheckBox preyShowVision;

    // Predator
    @FXML
    private TextField predatorNumber;
    @FXML
    private TextField predatorAttack;
    @FXML
    private TextField health;
    @FXML
    private TextField predatorSpeed;
    @FXML
    private TextField predatorVisionRadius;
    @FXML
    private TextField groupRadius;
    @FXML
    private TextField stayInGroupTendency;
    @FXML
    private ColorPicker predatorColor;
    @FXML
    private ColorPicker groupColor;

    // Prey
    @FXML
    private TextField preyNumber;
    @FXML
    private TextField preyAttack;
    @FXML
    private TextField nutrition;
    @FXML
    private TextField preySpeed;
    @FXML
    private TextField preyVisionRadius;
    @FXML
    private TextField preyMinSize;
    @FXML
    private TextField preyMaxSize;
    @FXML
    private TextField newPreyPerIteration;
    @FXML
    private ColorPicker preyColor;

    // Output
    @FXML
    private TextField averageFood;
    @FXML
    private TextField predatorCount;

    // Button
    @FXML
    private Button apply;
    @FXML
    private Button clear;
    @FXML
    private Button play;
    @FXML
    private Button stop;

    @FXML
    private Slider slider;

    // GUI component groups
    TextField[] inputTextFields;
    CheckBox[] checkBoxes;
    ColorPicker[] colorPickers;
    private MenuItem[] menuItems;
    private Control[] widgets;

    Canvas getMapCanvas() {
        return mapCanvas;
    }

    void set(Main application, Map map, Stage stage) {
        this.application = application;
        this.stage = stage;
        setMap(map);
    }

    void setMap(Map map) {
        this.map = map;
        Entity.map = map;
        application.setMap(map);
    }

    /**
     * Disable fields, apply and clear buttons and do the runningToggle function in the main
     */
    @FXML
    void play() {
        boolean running = application.runningToggle();

        play.setText(running ? "Pause" : "Play");

        apply.setDisable(true);
        clear.setDisable(true);
        play.setDisable(false);
        stop.setDisable(false);

        setSettingsDisable(true);
        saveSettings.setDisable(running);
        saveMap.setDisable(running);
        saveSimulation.setDisable(running);
    }

    /**
     * Enable fields, disable play buttons and do the stopSimulation function in the main
     */
    @FXML
    void stop() {
        play.setText("Play");

        apply.setDisable(false);
        clear.setDisable(false);
        play.setDisable(true);
        stop.setDisable(true);

        setSettingsDisable(false);
        application.stopSimulation();

        // reset the value of output
        averageFood.setText("");
        predatorCount.setText("");
    }

    /**
     * Save user inputs to 3 initializing functions in Map class
     */
    @FXML
    void apply() {
        try {
            // pass values to initializing functions in Map class
            map.set(
                    Integer.parseInt(width.getText()),
                    Integer.parseInt(height.getText()),
                    showGrid.isSelected(),
                    predatorShowVision.isSelected(),
                    preyShowVision.isSelected(),
                    showGroup.isSelected()
            );
            map.initializePredators(
                    Integer.parseInt(predatorNumber.getText()),
                    Integer.parseInt(predatorSpeed.getText()),
                    Integer.parseInt(health.getText()),
                    Integer.parseInt(predatorAttack.getText()),
                    Float.parseFloat(groupRadius.getText()),
                    Float.parseFloat(predatorVisionRadius.getText()),
                    Float.parseFloat(stayInGroupTendency.getText()),
                    predatorColor.getValue(),
                    groupColor.getValue()
            );
            map.initializePreys(
                    Integer.parseInt(preyNumber.getText()),
                    Integer.parseInt(preySpeed.getText()),
                    Float.parseFloat(nutrition.getText()),
                    Integer.parseInt(preyAttack.getText()),
                    Integer.parseInt(preyMinSize.getText()),
                    Integer.parseInt(preyMaxSize.getText()),
                    Integer.parseInt(preyVisionRadius.getText()),
                    Float.parseFloat(newPreyPerIteration.getText()),
                    preyColor.getValue()
            );

            // paint the init position in the map
            map.paint();

        } catch (NumberFormatException e) { // Invalid input -> warning
            alert(
                    "Invalid format",
                    "Some fields has invalid number format:\n" + e.getMessage()
            );
            return;
        } catch (IllegalArgumentException e) {
            alert("Invalid argument", e.getMessage());
            return;
        }

        apply.setDisable(false);
        clear.setDisable(false);
        play.setDisable(false);
        stop.setDisable(true);
    }

    /**
     * Reset all input fields
     */
    @FXML
    void clear() {
        clearTextFields();
        for (CheckBox checkBox : checkBoxes)
            checkBox.setSelected(false);
        for (ColorPicker colorPicker : colorPickers)
            colorPicker.setValue(javafx.scene.paint.Color.WHITE);
    }

    /**
     * The iteration speed will be changed base on the value of slider
     */
    @FXML
    void updateSimulationSpeed() {
        slider.showTickLabelsProperty();
        application.setSimulationSpeed(slider.getValue());
    }

    /**
     * Show demo 1
     */
    @FXML
    void showDemo1(ActionEvent event) {
        showDemo(1);
    }

    /**
     * Show demo 2
     */
    @FXML
    void showDemo2(ActionEvent event) {
        showDemo(2);
    }

    /**
     * Show demo 3
     */
    @FXML
    void showDemo3(ActionEvent event) {
        showDemo(3);
    }

    @FXML
    void showContributing(ActionEvent event) {
        String content = readResourceFile("CONTRIBUTING.txt");

        if (content == null) {
            alert("Warning", "Error opening file CONTRIBUTING.txt");
            return;
        }

        Dialog<Boolean> dialog = new Dialog<>();

        ButtonType button = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(button);

        dialog.getDialogPane().setMinSize(350, 200);
        dialog.setTitle("Contributing");
        dialog.setContentText(content);

        dialog.showAndWait();
    }

    /**
     * Display a window contains the project specification
     */
    @FXML
    void showSpecification(ActionEvent event) {
        String content = readResourceFile("SPECIFICATIONS.txt");

        if (content == null) {
            alert("Warning", "Error opening file SPECIFICATIONS.txt");
            return;
        }

        Dialog<Boolean> dialog = new Dialog<>();

        ButtonType button = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(button);

        dialog.getDialogPane().setMinSize(700, 500);
        dialog.setTitle("Project specification");

        TextArea textArea = new TextArea(content);
        textArea.setEditable(true);
        dialog.getDialogPane().setContent(textArea);

        dialog.showAndWait();
    }

    @FXML
    void loadSettingsFromFile(ActionEvent event) {
        FileLoader.loadFromFile(
                "Load settings",
                "loadSettingsFromString",
                this);
    }

    @FXML
    void loadMapFromFile(ActionEvent event) {
        FileLoader.loadFromFile(
                "Load map",
                "loadMapFromString",
                this);
    }

    @FXML
    void loadSimulationFromFile(ActionEvent event) {
        FileLoader.loadFromFile("Load simulation",
                "loadSimulationFromString",
                this);
    }

    @FXML
    void saveSettingsToFile(ActionEvent event) {
        FileLoader.saveToFile(
                "Save settings",
                "saveSettingsToString",
                this);
    }

    @FXML
    void saveMapToFile(ActionEvent event) {
        FileLoader.saveToFile(
                "Save map",
                "saveMapToString",
                this);
    }

    @FXML
    void saveSimulationToFile(ActionEvent event) {
        FileLoader.saveToFile(
                "Save simulation",
                "saveSimulationToString",
                this);
    }

    /**
     * Initialize the controller class. This method is automatically called after the fxml file has been loaded.
     * Initialize empty string in all fields
     * Create the map in the big AnchorPane
     * Initialize conditions that only accept numbers in the fields
     */
    @FXML
    void initialize() {
        play.setDisable(true);
        stop.setDisable(true);
        averageFood.setEditable(false);
        predatorCount.setEditable(false);

        inputTextFields = new TextField[]{
                width, height,
                predatorNumber, predatorAttack, health, predatorSpeed, predatorVisionRadius, groupRadius, stayInGroupTendency,
                preyNumber, preyAttack, nutrition, preySpeed, preyVisionRadius, preyMinSize, preyMaxSize, newPreyPerIteration,
        };
        checkBoxes = new CheckBox[]{
                showGrid, predatorShowVision, showGroup, preyShowVision
        };
        colorPickers = new ColorPicker[]{
                predatorColor, groupColor, preyColor
        };
        widgets = new Control[]{
                width, height,
                predatorNumber, predatorAttack, health, predatorSpeed, predatorVisionRadius, groupRadius, stayInGroupTendency,
                preyNumber, preyAttack, nutrition, preySpeed, preyVisionRadius, preyMinSize, preyMaxSize, newPreyPerIteration,
                showGrid, predatorShowVision, showGroup, preyShowVision,
                predatorColor, groupColor, preyColor
        };
        menuItems = new MenuItem[]{
                loadSettings, loadMap, loadSimulation,
                saveSettings, saveMap, saveSimulation,
                demo1, demo2, demo3
        };

        // clear text in input text fields
        clearTextFields();

        // Accept only 0-9 and .
        for (final TextField field : inputTextFields)
            field.textProperty().addListener(
                    (observable, oldValue, newValue) -> {
                        if (newValue.matches(".*[^\\d.]+.*")) {
                            field.setText(newValue.replaceAll("[^\\d.]", ""));
                        }
                    }
            );
    }

    /**
     * Display simulation outputs to GUI
     *
     * @param averageFoodIteration: average food gained per iteration
     * @param predatorNumber:       number of remaining predators
     */
    void displayOutput(float averageFoodIteration, int predatorNumber) {
        averageFood.setText(String.valueOf(averageFoodIteration));
        predatorCount.setText(String.valueOf(predatorNumber));
    }

    /**
     * Set all input text fields in GUI to value text
     */
    private void clearTextFields() {
        for (TextField field : inputTextFields)
            field.setText("");
    }

    /**
     * Do not let user type input to all fields
     */
    private void setSettingsDisable(boolean value) {
        for (Control widget : widgets)
            widget.setDisable(value);
        for (MenuItem item : menuItems)
            item.setDisable(value);
    }

    /**
     * Stop simulation and display error
     *
     * @param title: Dialog box's title
     * @param text:  Dialog box's content
     */
    static void alert(String title, String text) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(text);
        alert.showAndWait();
    }

    void enablePlayButton() {
        play.setDisable(false);
    }

    private void showDemo(int i) {
        FileLoader.loadSettingsFromString(FileLoader.readResourceFile("SettingsDemo" + i + ".txt"), this);
        FileLoader.loadMapFromString(FileLoader.readResourceFile("MapDemo" + i + ".txt"), this);
    }
}
