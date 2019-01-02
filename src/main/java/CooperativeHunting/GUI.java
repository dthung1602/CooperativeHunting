package CooperativeHunting;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import javax.swing.*;

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

    // list of text fields in GUI
    private TextField[] textFields;

    void setApplication(Main application) {
        this.application = application;
    }

    void setMap(Map map) {
        this.map = map;
    }

    private void setFieldsDisable() {
        for (TextField field : textFields)
            field.setDisable(editDisable);
    }

    @FXML
    void play() {
        editDisable = true;
        setFieldsDisable();
        application.runningToggle();
    }

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

    //Save button
    @FXML
    private void save() {
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
                predatorColor.getValue()
        );
        map.initializePreys(
                Integer.parseInt(preyNumber.getText()),
                Integer.parseInt(preySpeed.getText()),
                Float.parseFloat(nutrition.getText()),
                Integer.parseInt(preyAttack.getText()),
                preyColor.getValue()
        );
    }

    //Clear button
    @FXML
    public void clear() {
        if (!editDisable) {
            setText("0");
        }
    }

    //Initialize
    @FXML
    public void initialize() {
        textFields = new TextField[]{
                height, width,
                predatorNumber, health, predatorAttack, predatorSpeed, groupRadius,
                preyNumber, nutrition, preyAttack, preySpeed,
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

