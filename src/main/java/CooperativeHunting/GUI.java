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
    Canvas mapCanvas;
    @FXML
    TextField width;
    @FXML
    TextField height;

    // Predator
    @FXML
    TextField predatorNumber;
    @FXML
    TextField predatorAttack;
    @FXML
    TextField health;
    @FXML
    TextField predatorSpeed;
    @FXML
    TextField groupRadius;
    @FXML
    ColorPicker predatorColor;
    @FXML
    TextField predatorVisionRadius;

    // Prey
    @FXML
    TextField preyNumber;
    @FXML
    TextField preySpeed;
    @FXML
    TextField nutrition;
    @FXML
    TextField preyAttack;
    @FXML
    TextField size;
    @FXML
    TextField preyVisionRadius;
    @FXML
    ColorPicker preyColor;

    // Output
    @FXML
    TextField averageFood;
    @FXML
    TextField predatorCount;

    // Button
    @FXML
    Button clear;
    @FXML
    Slider slider;
    @FXML
    Button play;
    @FXML
    Button stop;
    @FXML
    Button save;

    // GUI component groups
    private TextField[] textFields;
    private Control[] widgets;
    private Button[] buttons;

    void setApplication(Main application) {
        this.application = application;
    }

    void setMap(Map map) {
        this.map = map;
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
    private void setButtonsDisable() {
        for (Button button : buttons)
            button.setDisable(editDisable);
    }

    /**
     * Disable fields, save and clear buttons and do the runningToggle function in the main
     */
    @FXML
    void play() {
        stop.setDisable(false);
        editDisable = true;

        setFieldsDisable();
        setButtonsDisable();

        application.runningToggle();
    }

    /**
     * Enable fields, disable play buttons and do the stopSimulation function in the main
     */
    @FXML
    void stop() {
        editDisable = false;
        setFieldsDisable(); //enable fields
        setButtonsDisable(); //enable buttons
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
        //Cannot change the value of Output manually
        averageFood.setEditable(false);
        predatorCount.setEditable(false);

        // pass value to 3 initializing functions in Map class
        try {
            // Only display Play/Pause buttons after pressing Save
            // Stop buttons is hided unless pressing Play buttons
            editDisable = true;
            setButtonsDisable();
            play.setDisable(false);
            stop.setDisable(false);
            map.setMapSize(
                    Integer.parseInt(width.getText()),
                    Integer.parseInt(height.getText())
            );
            map.initializePredators(
                    Integer.parseInt(predatorNumber.getText()),
                    Integer.parseInt(predatorSpeed.getText()),
                    Integer.parseInt(health.getText()),
                    Integer.parseInt(predatorAttack.getText()),
                    Integer.parseInt(groupRadius.getText()),
                    Integer.parseInt(predatorVisionRadius.getText()),
                    predatorColor.getValue()
            );
            map.initializePreys(
                    Integer.parseInt(preyNumber.getText()),
                    Integer.parseInt(preySpeed.getText()),
                    Float.parseFloat(nutrition.getText()),
                    Integer.parseInt(preyAttack.getText()),
                    Integer.parseInt(preyVisionRadius.getText()),
                    preyColor.getValue()
            );

        } catch (NumberFormatException e) {
            editDisable = false;
            setButtonsDisable();
            play.setDisable(true);
            stop.setDisable(true);

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid Input");
            alert.setContentText("Please fill all the fields");
            alert.showAndWait();
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

        textFields = new TextField[]{
                height, width,
                predatorNumber, health, predatorAttack, predatorSpeed, groupRadius, predatorVisionRadius,
                preyNumber, nutrition, preyAttack, preySpeed, size, preyVisionRadius
        };
        buttons = new Button[]{clear, save};
        widgets = new Control[]{
                height, width,
                predatorNumber, health, predatorAttack, predatorSpeed, groupRadius, predatorVisionRadius, predatorColor,
                preyNumber, nutrition, preyAttack, preySpeed, size, preyVisionRadius, preyColor
        };

        //initialize some variable
        setText("");

        //Accept only valid input
        //Grid (Map) fields
        for (final TextField field : textFields)
            field.textProperty().addListener(
                    new ChangeListener<String>() {
                        @Override
                        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                            if (newValue.matches(".*[^\\d]+.*")) {
                                field.setText(newValue.replaceAll("[^\\d]", ""));
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
     * Set all text fields in GUI to value text
     *
     * @param text: text to set
     */
    private void setText(String text) {
        for (TextField field : textFields)
            field.setText(text);
    }

}

