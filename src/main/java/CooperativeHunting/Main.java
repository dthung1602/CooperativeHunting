package CooperativeHunting;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    private boolean running = false;
    private float simulationSpeed;
    private Stage primaryStage;
    private VBox Layout;


    public void start(Stage stage) throws Exception {
        //Use to load GUI and pass Map object to the GUI

        Map map = new Map();
        this.primaryStage = stage;
        this.primaryStage.setTitle("Cooperative Hunting");

        //load the GUI
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("/GUI.fxml"));
        Layout = loader.load();

        //get the controller and pass Map Object to it
        GUI controller = loader.getController();
        controller.setMap(map);
        controller.setApplication(this);

        //display the GUI
        Scene scene = new Scene(Layout);
        primaryStage.setScene(scene);
        primaryStage.show();

        while (true) {
            // only update map when running
            if (!running) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // update and repaint
            map.update();
            map.repaint();

            // sleep
            try {
                Thread.sleep((long) (1000.0 / simulationSpeed));
            } catch (InterruptedException ignore) {
            }
        }
    }

    public static void main(String... args) {
        launch(args);
    }

    void runningToggle() {

    }

    void stopSimulation() {

    }

    void setSimulationSpeed(double speed) {

    }
}
