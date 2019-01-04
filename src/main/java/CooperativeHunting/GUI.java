package CooperativeHunting;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import javax.swing.*;
import java.awt.*;


public class GUI {
    private boolean editDisable = false;
    private Main application;
    private Map map;

    @FXML
    AnchorPane mapContainer;

    //Grid (Map)
    @FXML
    TextField width;
    @FXML
    TextField height;

    //Predator
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

    //Prey
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
    ColorPicker preyColor;

    //Button
    @FXML
    Button clear;
    @FXML
    Slider slider;
    @FXML
    Button play;
    @FXML
    Button stop;

    // list of all text fields in GUI
    private TextField[] textFields;
    // list of all widgets in GUI
    private Control[] widgets;

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
     * Disable fields, save and clear buttons and do the runningToggle function in the main
     */
    @FXML
    void play() {
        editDisable = true;
        setFieldsDisable(); // disable fields
        application.runningToggle();

    }

    /**
     * Enable fields, disable play button and do the stopSimulation function in the main
     */
    @FXML
    void stop() {
        editDisable = false;
        setFieldsDisable();
        application.stopSimulation();

    }

    //Slider
    @FXML
    public void sliders() {
        slider.showTickLabelsProperty();
        application.setSimulationSpeed(slider.getValue());
    }

    /**
     * Change color type from javafx.scene.paint.Color to java.awt.Color
     *
     * @param color: javafx.scene.paint.Color type to be changed
     * @return java.awt.Color type
     */
    private java.awt.Color changeColorType(javafx.scene.paint.Color color) {
        return new java.awt.Color(
                (float) color.getRed(),
                (float) color.getGreen(),
                (float) color.getBlue(),
                (float) color.getOpacity()
        );
    }

    /**
     * Save user inputs to 3 initializing functions in Map class
     */
    @FXML
    private void save() {
        // change color type to java.awt.Color
        Color predatorColorAwt = changeColorType(predatorColor.getValue());
        Color preyColorAwt = changeColorType(preyColor.getValue());

        // pass value to 3 initializing functions in Map class
        try {
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
                    5, //TODO add visionRadius
                    predatorColorAwt
            );
            map.initializePreys(
                    Integer.parseInt(preyNumber.getText()),
                    Integer.parseInt(preySpeed.getText()),
                    Float.parseFloat(nutrition.getText()),
                    Integer.parseInt(preyAttack.getText()),
                    5, //TODO add visionRadius
                    preyColorAwt
            );
        } catch (NumberFormatException e){
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
    }

    /**
     * Initialize the controller class. This method is automatically called after the fxml file has been loaded.
     * Initialize empty string in all fields
     * Create the map in the big AnchorPane
     * Initialize conditions that only accept numbers in the fields
     */
    @FXML
    public void initialize() {
        textFields = new TextField[]{
                height, width,
                predatorNumber, health, predatorAttack, predatorSpeed, groupRadius,
                preyNumber, nutrition, preyAttack, preySpeed, size
        };

        widgets = new Control[]{
                height, width,
                predatorNumber, health, predatorAttack, predatorSpeed, groupRadius, predatorColor,
                preyNumber, nutrition, preyAttack, preySpeed, size, preyColor
        };

        //initialize some variable
        setText("");

        //create map
        final SwingNode swingNode = new SwingNode();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                swingNode.setContent(map);
            }
        });
        mapContainer.getChildren().add(swingNode);

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
     * Set all text fields in GUI to value text
     *
     * @param text: text to set
     */
    private void setText(String text) {
        for (TextField field : textFields)
            field.setText(text);
    }
}

