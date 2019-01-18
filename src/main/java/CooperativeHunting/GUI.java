package CooperativeHunting;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;

public class GUI {
    private boolean editDisable = false;
    private Main application;
    private Map map;

    // Grid (Map)
    @FXML
    private Canvas mapCanvas;
    @FXML
    private TextField width;
    @FXML
    private TextField height;
    @FXML
    private CheckBox showGrid;

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
    private ColorPicker predatorColor;
    @FXML
    private ColorPicker groupColor;
    @FXML
    private CheckBox predatorShowVision;
    @FXML
    private CheckBox showGroup;

    // Prey
    @FXML
    private TextField preyNumber;
    @FXML
    private TextField preySpeed;
    @FXML
    private TextField nutrition;
    @FXML
    private TextField preyAttack;
    @FXML
    private TextField preyMinSize;
    @FXML
    private TextField preyMaxSize;
    @FXML
    private TextField preyVisionRadius;
    @FXML
    private ColorPicker preyColor;
    @FXML
    private CheckBox preyShowVision;
    @FXML
    private CheckBox autoGeneratePreys;

    // Output
    @FXML
    private TextField averageFood;
    @FXML
    private TextField predatorCount;

    // Button
    @FXML
    private Button clear;
    @FXML
    private Slider slider;
    @FXML
    private Button play;
    @FXML
    private Button stop;
    @FXML
    private Button save;

    // GUI component groups
    private TextField[] inputTextFields;
    private Control[] widgets;

    Canvas getMapCanvas() {
        return mapCanvas;
    }

    void setApplication(Main application) {
        this.application = application;
    }

    void setMap(Map map) {
        this.map = map;
    }

    /**
     * Disable fields, save and clear buttons and do the runningToggle function in the main
     */
    @FXML
    void play() {
        stop.setDisable(false);
        editDisable = true;

        setFieldsDisable();
        setButtonsEditable();

        application.runningToggle();
    }

    /**
     * Enable fields, disable play buttons and do the stopSimulation function in the main
     */
    @FXML
    void stop() {
        editDisable = false;
        setFieldsDisable(); //enable fields
        setButtonsEditable(); //enable buttons
        play.setDisable(true);
        application.stopSimulation();
        //reset the value of Output
        averageFood.setText("");
        predatorCount.setText("");

    }

    /**
     * The iteration speed will be changed base on the value of slider
     */
    @FXML
    public void sliders() {
        slider.showTickLabelsProperty();
        application.setSimulationSpeed(slider.getValue());
    }

    /**
     * Save user inputs to 3 initializing functions in Map class
     */
    @FXML
    private void save() {
        try {
            // Only display Play/Pause buttons after pressing Save
            // Stop buttons is hided unless pressing Play buttons
            editDisable = true;
            setButtonsEditable();
            play.setDisable(false);
            stop.setDisable(false);

            // pass values to initializing functions in Map class
            map.set(
                    Integer.parseInt(width.getText()),
                    Integer.parseInt(height.getText()),
                    autoGeneratePreys.isSelected(),
                    predatorShowVision.isSelected(),
                    preyShowVision.isSelected(),
                    showGroup.isSelected(),
                    showGrid.isSelected()
            );
            map.initializePredators(
                    Integer.parseInt(predatorNumber.getText()),
                    Integer.parseInt(predatorSpeed.getText()),
                    Integer.parseInt(health.getText()),
                    Integer.parseInt(predatorAttack.getText()),
                    Integer.parseInt(groupRadius.getText()),
                    Integer.parseInt(predatorVisionRadius.getText()),
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
                    preyColor.getValue()
            );

            // paint the init position in the map
            map.paint();

        } catch (NumberFormatException e) { // Invalid input -> warning
            alert(
                    "Invalid format",
                    "Some fields has invalid number format:\n" + e.getMessage()
            );
        } catch (IllegalArgumentException e) {
            alert("Invalid argument", e.getMessage());
        }
    }

    /**
     * Set text of all fields to value 0
     */
    @FXML
    public void clear() {
        if (!editDisable)
            setText("0");
        preyColor.setValue(javafx.scene.paint.Color.WHITE);
        predatorColor.setValue(javafx.scene.paint.Color.WHITE);
    }

    /**
     * Initialize the controller class. This method is automatically called after the fxml file has been loaded.
     * Initialize empty string in all fields
     * Create the map in the big AnchorPane
     * Initialize conditions that only accept numbers in the fields
     */
    @FXML
    public void initialize() {
        play.setDisable(true);
        stop.setDisable(true);
        averageFood.setEditable(false);
        predatorCount.setEditable(false);

        inputTextFields = new TextField[]{
                height, width,
                predatorNumber, health, predatorAttack, predatorSpeed, groupRadius, predatorVisionRadius,
                preyNumber, nutrition, preyAttack, preySpeed, preyMinSize, preyMaxSize, preyVisionRadius
        };
        widgets = new Control[]{
                height, width, showGrid,
                predatorNumber, health, predatorAttack, predatorSpeed, groupRadius, predatorVisionRadius,
                predatorColor, groupColor, predatorShowVision, showGroup,
                preyNumber, nutrition, preyAttack, preySpeed, preyMinSize, preyMaxSize, preyVisionRadius,
                preyColor, predatorShowVision, autoGeneratePreys
        };

        // clear text in input text fields
        setText("");

        // Accept only 0-9 and .
        for (final TextField field : inputTextFields)
            field.textProperty().addListener(
                    new ChangeListener<String>() {
                        @Override
                        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                            if (newValue.matches(".*[^\\d.]+.*")) {
                                field.setText(newValue.replaceAll("[^\\d.]", ""));
                            }
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
     *
     * @param text: text to set
     */
    private void setText(String text) {
        for (TextField field : inputTextFields)
            field.setText(text);
    }

    /**
     * Do not let user type input to all fields
     */
    private void setFieldsDisable() {
        for (Control widget : widgets)
            widget.setDisable(editDisable);
    }

    /**
     * Disable clear and save buttons
     */
    private void setButtonsEditable() {
        clear.setDisable(editDisable);
        save.setDisable(editDisable);
    }

    private void alert(String title, String text) {
        editDisable = false;
        setButtonsEditable();
        play.setDisable(true);
        stop.setDisable(true);

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Invalid Input");
        alert.setContentText("Please fill all the fields");
        alert.showAndWait();
    }
}

