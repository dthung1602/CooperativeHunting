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
    @FXML
    TextField predatorvisionRadius;
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
    TextField preyvisionRadius;
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
    @FXML
    Button save;

    // list of all text fields in GUI
    private TextField[] textFields;
    // list of all widgets in GUI
    private Control[] widgets;

    private Button[] button;

    void setApplication(Main application) {
        this.application = application;
    }

    void setMap(Map map) {
        this.map = map;
    }

    private void setFieldsDisable() {
        for (Control widget : widgets)
            widget.setDisable(editDisable);
    }

    /**
     * Disable clear and save buttons
     */
    private void setButtonDisable(){
        for(Button button: button)
            button.setDisable(editDisable);
    }

    @FXML
    void play() {
        stop.setDisable(false);
        editDisable = true;
        setFieldsDisable();
        setButtonDisable();
        application.runningToggle();
    }

    @FXML
    void stop() {
        editDisable = false;
        setFieldsDisable(); //enable fields
        setButtonDisable(); //enable buttons
        play.setDisable(true);
        application.stopSimulation();
    }

    /**
     * The iteration speed will be changed base on the value of slider
     */
    @FXML
    public void sliders() {
        slider.showTickLabelsProperty();
        application.setSimulationSpeed(slider.getValue());
    }

    //Save button
    @FXML
    private void save() {
        // TODO common code to method toAwtColor
        // Only display Play/Pause button after pressing Save
        // Stop button is hided unless pressing Play button
        editDisable=true;
        play.setDisable(false);
        stop.setDisable(false);
        setButtonDisable();
        javafx.scene.paint.Color predatorColorJavafx = predatorColor.getValue();
        Color predatorColorAwt = new java.awt.Color((float) predatorColorJavafx.getRed(),
                (float) predatorColorJavafx.getGreen(),
                (float) predatorColorJavafx.getBlue(),
                (float) predatorColorJavafx.getOpacity());

        javafx.scene.paint.Color preyColorJavafx = preyColor.getValue();
        Color preyColorAwt = new java.awt.Color((float) preyColorJavafx.getRed(),
                (float) preyColorJavafx.getGreen(),
                (float) preyColorJavafx.getBlue(),
                (float) preyColorJavafx.getOpacity());

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
                predatorColorAwt
        );
        map.initializePreys(
                Integer.parseInt(preyNumber.getText()),
                Integer.parseInt(preySpeed.getText()),
                Float.parseFloat(nutrition.getText()),
                Integer.parseInt(preyAttack.getText()),
                preyColorAwt
        );


    }

    // Clear button
    @FXML
    public void clear() {
        if (!editDisable)
            setText("0");
    }

    // Initialize
    @FXML
    public void initialize() {
        play.setDisable(true);
        stop.setDisable(true);
        textFields = new TextField[]{
                height, width,
                predatorNumber, health, predatorAttack, predatorSpeed, groupRadius,
                preyNumber, nutrition, preyAttack, preySpeed, size
        };
        button= new Button[]{clear, save

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

