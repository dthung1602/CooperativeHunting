package CooperativeHunting;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    private boolean running = false;
    private float simulationSpeed;

    public void start(Stage stage) throws Exception {
        Map map = new Map();

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

    void runingToggle() {

    }

    void stopSimulation() {

    }

    void setSimulationSpeed(float speed) {

    }
}
