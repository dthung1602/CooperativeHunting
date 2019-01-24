package CooperativeHunting;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

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
    private CheckBox[] checkBoxes;
    private ColorPicker[] colorPickers;
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
        // change Play <--> Pause
        play.setText((play.getText().equals("Play")) ? "Pause" : "Play");

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

        play.setText("Play");
        play.setDisable(true);

        application.stopSimulation();

        // reset the value of output
        averageFood.setText("");
        predatorCount.setText("");

    }

    /**
     * The iteration speed will be changed base on the value of slider
     */
    @FXML
    void sliders() {
        slider.showTickLabelsProperty();
        application.setSimulationSpeed(slider.getValue());
    }

    /**
     * Save user inputs to 3 initializing functions in Map class
     */
    @FXML
    void save() {
        try {
            // Only display Play/Pause buttons after pressing Save
            // Stop buttons is hided unless pressing Play buttons
            editDisable = true;
            setFieldsDisable();
            setButtonsEditable();
            play.setDisable(false);
            stop.setDisable(false);

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
        } catch (IllegalArgumentException e) {
            alert("Invalid argument", e.getMessage());
        }
    }

    /**
     * Set text of all fields to value 0
     */
    @FXML
    void clear() {
        if (!editDisable)
            setText("0");
        preyColor.setValue(javafx.scene.paint.Color.WHITE);
        predatorColor.setValue(javafx.scene.paint.Color.WHITE);
    }

    /**
     * Display a window contains the project specification
     */
    @FXML
    void setDefaultValues(ActionEvent event) {
        String rawValues = readWholeFile("DEFAULT_VALUES.txt");

        // error occurred
        if (rawValues.indexOf("Error reading file") == 0) {
            alert("Warning", rawValues);
            return;
        }

        try {
            String[] values = rawValues.split("\n");
            int i = 0;

            for (int j = 0; j < inputTextFields.length; j++, i++)
                inputTextFields[j].setText(values[i]);
            for (int j = 0; j < checkBoxes.length; j++, i++)
                checkBoxes[j].setSelected(Boolean.parseBoolean(values[i]));
            for (int j = 0; j < colorPickers.length; j++, i++) {
                String[] colorValues = values[i].split(" ");
                Color color = Color.rgb(
                        Integer.parseInt(colorValues[0]),
                        Integer.parseInt(colorValues[1]),
                        Integer.parseInt(colorValues[2]),
                        Float.parseFloat(colorValues[3])
                );
                colorPickers[j].setValue(color);
            }

        } catch (NumberFormatException e) {
            alert("Warning", "Corrupted default value file");
        } catch (IllegalArgumentException e) {
            alert("Warning", "Corrupted color value in default value file");
        }
    }

    @FXML
    void showContributing(ActionEvent event) {
        Dialog<Boolean> dialog = new Dialog<Boolean>();

        ButtonType button = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(button);

        dialog.getDialogPane().setMinSize(350, 200);
        dialog.setTitle("Contributing");
        dialog.setContentText(readWholeFile("CONTRIBUTING.txt"));

        dialog.showAndWait();
    }

    /**
     * Display a window contains the project specification
     */
    @FXML
    void showSpecification(ActionEvent event) {
        Dialog<Boolean> dialog = new Dialog<Boolean>();

        ButtonType button = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(button);

        dialog.getDialogPane().setMinSize(700, 500);
        dialog.setTitle("Project specification");

        TextArea textArea = new TextArea(readWholeFile("SPECIFICATIONS.txt"));
        textArea.setEditable(true);
        dialog.getDialogPane().setContent(textArea);

        dialog.showAndWait();
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
                predatorNumber, predatorAttack, health, predatorSpeed, predatorVisionRadius, groupRadius,
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
                predatorNumber, predatorAttack, health, predatorSpeed, predatorVisionRadius, groupRadius,
                preyNumber, preyAttack, nutrition, preySpeed, preyVisionRadius, preyMinSize, preyMaxSize, newPreyPerIteration,
                showGrid, predatorShowVision, showGroup, preyShowVision,
                predatorColor, groupColor, preyColor
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

    /**
     * Stop simulation and display error
     *
     * @param title: Dialog box's title
     * @param text:  Dialog box's content
     */
    private void alert(String title, String text) {
        editDisable = false;
        setButtonsEditable();
        play.setDisable(true);
        stop.setDisable(true);

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(text);
        alert.showAndWait();
    }

    /**
     * @param fileName: file to read
     * @return file content string
     */
    private String readWholeFile(String fileName) {
        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream stream = classloader.getResourceAsStream(fileName);
            if (stream == null)
                throw new FileNotFoundException();
            Scanner s = new Scanner(stream).useDelimiter("\\A");
            String content = s.hasNext() ? s.next() : "";
            stream.close();
            return content;
        } catch (IOException e) {
            return "Error reading file '" + fileName + "'";
        }
    }
}

