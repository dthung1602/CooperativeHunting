package CooperativeHunting;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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

    //Prey
    @FXML
    TextField preyNumber;
    @FXML
    TextField preySpeed;
    @FXML
    TextField nutrition;
    @FXML
    TextField preyAttack;

    //Button
    @FXML
    Button clear;
    @FXML
    Slider slider;
    @FXML
    Button play;
    @FXML
    Button stop;

    void setApplication(Main application) {
        this.application = application;
    }

    void setMap(Map map) {
        this.map = map;
    }

    private void setFieldsDisable() {
        //Map fields disable
        height.setDisable(editDisable);
        width.setDisable(editDisable);

        //Predator fields disable
        predatorNumber.setDisable(editDisable);
        predatorAttack.setDisable(editDisable);
        predatorSpeed.setDisable(editDisable);
        groupRadius.setDisable(editDisable);
        health.setDisable(editDisable);

        //Prey fields disable
        preyNumber.setDisable(editDisable);
        nutrition.setDisable(editDisable);
        preyAttack.setDisable(editDisable);
        preySpeed.setDisable(editDisable);

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
        map.initializeSize(
                Integer.parseInt(width.getText()),
                Integer.parseInt(height.getText())
        );
        map.initializePredators(
                Integer.parseInt(predatorNumber.getText()),
                Integer.parseInt(predatorSpeed.getText()),
                Float.parseFloat(health.getText()),
                Integer.parseInt(predatorAttack.getText()),
                Integer.parseInt(groupRadius.getText())
        );
        map.initializePreys(
                Integer.parseInt(preyNumber.getText()),
                Integer.parseInt(preySpeed.getText()),
                Float.parseFloat(nutrition.getText()),
                Integer.parseInt(preyAttack.getText())
        );
    }

    //Clear button
    @FXML
    public void clear() {
        if (!editDisable) {
            height.setText("0");
            width.setText("0");

            predatorNumber.setText("0");
            predatorAttack.setText("0");
            predatorSpeed.setText("0");
            groupRadius.setText("0");
            health.setText("0");

            preyNumber.setText("0");
            nutrition.setText("0");
            preyAttack.setText("0");
            preySpeed.setText("0");
        }
    }

    //Initialize
    @FXML
    public void initialize() {
        //initialize some variable
        height.setText("");
        width.setText("");

        predatorNumber.setText("");
        predatorAttack.setText("");
        predatorSpeed.setText("");
        groupRadius.setText("");
        health.setText("");
        preySpeed.setText("");

        preyNumber.setText("");
        nutrition.setText("");
        preyAttack.setText("");

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
        width.textProperty().addListener(new ChangeListener<String>() {
                                             @Override
                                             public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                                                 if (newValue.matches(".*[^\\d]+.*")) {
                                                     width.setText(newValue.replaceAll("[^\\d]", ""));
                                                 }
                                             }
                                         }
        );

        height.textProperty().addListener(new ChangeListener<String>() {
                                              @Override
                                              public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                                                  if (newValue.matches(".*[^\\d]+.*")) {
                                                      height.setText(newValue.replaceAll("[^\\d]", ""));
                                                  }
                                              }
                                          }
        );

        //Predator fields

        health.textProperty().addListener(new ChangeListener<String>() {
                                              @Override
                                              public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                                                  if (newValue.matches(".*[^\\d]+.*")) {
                                                      health.setText(newValue.replaceAll("[^\\d]", ""));
                                                  }
                                              }
                                          }
        );

        predatorAttack.textProperty().addListener(new ChangeListener<String>() {
                                                      @Override
                                                      public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                                                          if (newValue.matches(".*[^\\d]+.*")) {
                                                              predatorAttack.setText(newValue.replaceAll("[^\\d]", ""));
                                                          }
                                                      }
                                                  }
        );

        predatorNumber.textProperty().addListener(new ChangeListener<String>() {
                                                      @Override
                                                      public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                                                          if (newValue.matches(".*[^\\d]+.*")) {
                                                              predatorNumber.setText(newValue.replaceAll("[^\\d]", ""));
                                                          }
                                                      }
                                                  }
        );

        predatorSpeed.textProperty().addListener(new ChangeListener<String>() {
                                                     @Override
                                                     public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                                                         if (newValue.matches(".*[^\\d]+.*")) {
                                                             predatorSpeed.setText(newValue.replaceAll("[^\\d]", ""));
                                                         }
                                                     }
                                                 }
        );

        groupRadius.textProperty().addListener(new ChangeListener<String>() {
                                                   @Override
                                                   public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                                                       if (newValue.matches(".*[^\\d]+.*")) {
                                                           groupRadius.setText(newValue.replaceAll("[^\\d]", ""));
                                                       }
                                                   }
                                               }
        );

        //Prey fields

        preyNumber.textProperty().addListener(new ChangeListener<String>() {
                                                  @Override
                                                  public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                                                      if (newValue.matches(".*[^\\d]+.*")) {
                                                          preyNumber.setText(newValue.replaceAll("[^\\d]", ""));
                                                      }
                                                  }
                                              }
        );

        preyAttack.textProperty().addListener(new ChangeListener<String>() {
                                                  @Override
                                                  public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                                                      if (newValue.matches(".*[^\\d]+.*")) {
                                                          preyAttack.setText(newValue.replaceAll("[^\\d]", ""));
                                                      }
                                                  }
                                              }
        );

        preySpeed.textProperty().addListener(new ChangeListener<String>() {
                                                 @Override
                                                 public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                                                     if (newValue.matches(".*[^\\d]+.*")) {
                                                         preySpeed.setText(newValue.replaceAll("[^\\d]", ""));
                                                     }
                                                 }
                                             }
        );

        nutrition.textProperty().addListener(new ChangeListener<String>() {
                                                 @Override
                                                 public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                                                     if (newValue.matches(".*[^\\d]+.*")) {
                                                         System.out.println("y");
                                                         nutrition.setText(newValue.replaceAll("[^\\d]", ""));
                                                     }
                                                     if (nutrition.getText().equals("")) nutrition.setText("");

                                                 }
                                             }
        );


        //text.textProperty().addListener(((observable, oldValue, newValue) -> {
        //    if (newValue.matches(".*[^\\d]+.*")){
        //        text.setText(newValue.replaceAll("[^\\d]", ""));
        //    }
        //    if (text.getText().equals("")) text.setText("0");
        //    label.setText(text.getText());
        //}));
    }
}

