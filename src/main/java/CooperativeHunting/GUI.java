package CooperativeHunting;

import CooperativeHunting.Predator.HuntingMethod;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.stage.Stage;

import static CooperativeHunting.FileLoader.readResourceFile;

/**
 * Store the application event handlers
 */
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
    @FXML
    private MenuItem showGraph;

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
    @FXML
    private TextField preyCount;

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
    private Button next;

    @FXML
    private ComboBox<String> iterationSpeed;
    @FXML
    ComboBox<String> huntingMethod;

    // GUI component groups
    TextField[] inputTextFields;
    CheckBox[] checkBoxes;
    ColorPicker[] colorPickers;
    private TextField[] outputTextFields;
    private MenuItem[] saveMenuItems;
    private MenuItem[] menuItems;
    private Control[] widgets;

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
        next.setDisable(true);

        inputTextFields = new TextField[]{
                width, height,
                predatorNumber, predatorAttack, health, predatorSpeed, predatorVisionRadius, groupRadius, stayInGroupTendency,
                preyNumber, preyAttack, nutrition, preySpeed, preyVisionRadius, preyMinSize, preyMaxSize, newPreyPerIteration,
        };
        outputTextFields = new TextField[]{
                predatorCount, preyCount, averageFood
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
        saveMenuItems = new MenuItem[]{
                loadSettings, loadMap, loadSimulation,
        };
        menuItems = new MenuItem[]{
                loadSettings, loadMap, loadSimulation,
                saveSettings, saveMap, saveSimulation,
                demo1, demo2, demo3,
        };

        // Init combo boxes
        iterationSpeed.setItems(FXCollections.observableArrayList(
                "0.25", "0.5", "1", "2", "3", "4", "5", "10", "20", "30", "40", "50"));
        huntingMethod.setItems(FXCollections.observableArrayList(
                HuntingMethod.methodNames
        ));
        iterationSpeed.setValue("1");
        huntingMethod.setValue(HuntingMethod.DEFAULT.toString());

        // disable editing output
        for (TextField textField : outputTextFields)
            textField.setEditable(false);

        // clear text in input text fields
        clearInputTextFields();

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
     * @param data: array of map output values
     */
    void displayOutput(float[] data) {
        averageFood.setText(String.valueOf(data[0]));
        predatorCount.setText(String.valueOf(data[1]));
        preyCount.setText(String.valueOf(data[2]));
    }

    /*************************************    GETTERS AND SETTERS    **************************************************/

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

    /*************************************    BUTTONS ACTIONS    ******************************************************/

    /**
     * Disable fields, apply and clear buttons and do the runningToggle function in the main
     */
    @FXML
    void play() {
        Platform.runLater(() -> {
            boolean running = application.runningToggle();

            play.setText(running ? "Pause" : "Play");

            apply.setDisable(true);
            clear.setDisable(true);
            play.setDisable(false);
            stop.setDisable(false);
            next.setDisable(running);

            setSettingsDisable(true);
            setMenuItemsDisable(running);
        });
    }

    /**
     * Enable fields, disable display buttons and do the stopSimulation function in the main
     */
    @FXML
    void stop() {
        Platform.runLater(() -> {
            play.setText("Play");

            apply.setDisable(false);
            clear.setDisable(false);
            play.setDisable(true);
            stop.setDisable(true);
            next.setDisable(true);

            setSettingsDisable(false);
            application.stopSimulation();
        });
    }

    /**
     * Save user inputs to 3 initializing functions in Map class
     */
    @FXML
    void apply() {
        try {
            map.clear();

            // pass values to initializing functions in Map class
            map.set(
                    Integer.parseInt(width.getText()),
                    Integer.parseInt(height.getText()),
                    showGrid.isSelected(),
                    predatorShowVision.isSelected(),
                    preyShowVision.isSelected(),
                    showGroup.isSelected()
            );
            map.initializePreys(
                    Integer.parseInt(preyNumber.getText()),
                    Integer.parseInt(preySpeed.getText()),
                    Float.parseFloat(nutrition.getText()),
                    Float.parseFloat(preyAttack.getText()),
                    Integer.parseInt(preyMinSize.getText()),
                    Integer.parseInt(preyMaxSize.getText()),
                    Float.parseFloat(preyVisionRadius.getText()),
                    Float.parseFloat(newPreyPerIteration.getText()),
                    preyColor.getValue()
            );
            map.initializePredators(
                    Integer.parseInt(predatorNumber.getText()),
                    Integer.parseInt(predatorSpeed.getText()),
                    Float.parseFloat(health.getText()),
                    Float.parseFloat(predatorAttack.getText()),
                    Float.parseFloat(groupRadius.getText()),
                    Float.parseFloat(predatorVisionRadius.getText()),
                    Float.parseFloat(stayInGroupTendency.getText()),
                    HuntingMethod.fromString(huntingMethod.getValue()),
                    predatorColor.getValue(),
                    groupColor.getValue()
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

        Platform.runLater(() -> {
            apply.setDisable(false);
            clear.setDisable(false);
            play.setDisable(false);
            stop.setDisable(true);
            next.setDisable(false);

            setSaveMenuItemsDisable(false);
            clearOutputTextFields();
        });
    }

    /**
     * Reset all input fields
     */
    @FXML
    void clear() {
        Platform.runLater(() -> {
            clearInputTextFields();
            clearOutputTextFields();
            for (CheckBox checkBox : checkBoxes)
                checkBox.setSelected(false);
            for (ColorPicker colorPicker : colorPickers)
                colorPicker.setValue(javafx.scene.paint.Color.WHITE);
        });
    }

    /**
     * Do 1 iteration
     */
    @FXML
    void next() {
        Platform.runLater(() -> {
            map.update();
            map.paint();

            apply.setDisable(true);
            clear.setDisable(true);
            play.setDisable(false);
            stop.setDisable(false);
            setSettingsDisable(true);
            setMenuItemsDisable(false);
        });
    }

    /**
     * The iteration speed will be changed base on the value of slider
     */
    @FXML
    void updateSimulationSpeed() {
        application.setSimulationSpeed(Float.parseFloat(iterationSpeed.getValue()));
    }

    /**
     * The iteration speed will be changed base on the value of slider
     */
    @FXML
    void changeHuntingMethod() {
        Predator.setHuntingMethod(HuntingMethod.fromString(huntingMethod.getValue()));
    }

    /*************************************    MENU ACTIONS (Open Statistic Graph)   ***********************************/

    /**
     * Show the graph window
     */
    @FXML
    void showGraph() {
        ChartDrawer.display(map);
    }

    /*************************************    MENU ACTIONS    *********************************************************/

    @FXML
    void showDemo1(ActionEvent event) {
        FileLoader.loadDemo(this, 1);
    }

    @FXML
    void showDemo2(ActionEvent event) {
        FileLoader.loadDemo(this, 2);
    }

    @FXML
    void showDemo3(ActionEvent event) {
        FileLoader.loadDemo(this, 3);
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

    /*************************************    UTILITIES    ************************************************************/

    void clearOutputTextFields() {
        for (TextField textField : outputTextFields)
            textField.setText("");
    }

    private void clearInputTextFields() {
        for (TextField field : inputTextFields)
            field.setText("");
    }

    private void setSettingsDisable(boolean value) {
        for (Control widget : widgets)
            widget.setDisable(value);
        for (MenuItem item : menuItems)
            item.setDisable(value);
    }

    private void setSaveMenuItemsDisable(boolean value) {
        for (MenuItem menuItem : saveMenuItems)
            menuItem.setDisable(value);
    }

    private void setMenuItemsDisable(boolean value) {
        for (MenuItem menuItem : menuItems)
            menuItem.setDisable(value);
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
        next.setDisable(false);
    }
}
