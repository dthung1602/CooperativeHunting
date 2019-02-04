package CooperativeHunting;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.nio.file.Paths;

public class Main extends Application {

    private boolean running = false;
    private double simulationSpeed = 1;

    private Map map;
    private GUI gui;
    private Thread mainThread;

    public void start(Stage stage) throws Exception {
        // prepare stage
        stage.setTitle("Cooperative Hunting");
        stage.setOnCloseRequest(t -> System.exit(0));

        // load the GUI
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("/GUI.fxml"));
        VBox layout = loader.load();

        // get the controller and pass Map Object to it
        gui = loader.getController();
        map = new Map(gui.getMapCanvas());
        gui.set(this, map, stage);

        // display the GUI
        stage.getIcons().add(new Image(new FileInputStream(
                Paths.get(Main.class.getResource("/icon.png").toURI()).toFile())));
        stage.setScene(new Scene(layout));
        stage.show();


        // start simulator thread
        mainThread = new Thread(new Simulator());
        mainThread.start();
    }

    public static void main(String... args) {
        launch(args);
    }

    void setMap(Map map) {
        this.map = map;
    }

    /**
     * Change the running state of the simulation
     *
     * @return whether the simulation is running
     */
    boolean runningToggle() {
        running = !running;
        if (running)
            mainThread.interrupt();
        return running;
    }

    void stopSimulation() {
        running = false;
    }

    /**
     * Change simulation speed
     *
     * @param speed: unit: iteration/second
     */
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
                    map.paint();
                    gui.displayOutput(map.getOutput());

                    // sleep
                    Thread.sleep((long) (1000.0 / simulationSpeed));

                } catch (InterruptedException ignore) {
                }
            }
        }
    }
}




