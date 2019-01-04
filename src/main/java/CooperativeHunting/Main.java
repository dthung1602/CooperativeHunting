package CooperativeHunting;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

    private boolean running = false;
    private double simulationSpeed = 1;

    private Map map;
    private Thread mainThread;

    public void start(Stage stage) throws Exception {
        // prepare stage
        stage.setTitle("Cooperative Hunting");
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                System.exit(0);
            }
        });

        // load the GUI
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("/GUI.fxml"));
        VBox layout = loader.load();

        // get the controller and pass Map Object to it
        GUI controller = loader.getController();
        Entity.map = map = new Map();
        controller.setMap(map);
        controller.setApplication(this);

        // display the GUI
        stage.setScene(new Scene(layout));
        stage.show();

        // start simulator thread
        mainThread = new Thread(new Simulator());
        mainThread.start();
    }

    public static void main(String... args) {
        launch(args);
    }

    void runningToggle() {
        running = !running;
        if (running)
            mainThread.interrupt();
    }

    void stopSimulation() {
        running = false;
    }

    void setSimulationSpeed(double speed) {
        this.simulationSpeed = speed;
    }

    /**
     * Simulation thread runnable
     */
    class Simulator implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    // only update map when running
                    if (!running)
                        synchronized (this) {
                            this.wait();
                        }
                    // update and repaint
                    map.update();
                    map.repaint();

                    // sleep
                    Thread.sleep((long) (1000.0 / simulationSpeed));

                } catch (InterruptedException ignore) {
                }
            }
        }
    }
}




