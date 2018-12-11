package CooperativeHunting;

public class Main {
    public static void main(String args[]) {
        Map map = new Map();
        GUI gui = new GUI(map);

        while (true) {
            // only update map when running
            if (!gui.running) {
                try {
                    gui.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // update and repaint
            map.update();
            map.repaint();

            // sleep
            try {
                Thread.sleep((long) (1000.0 / gui.simulationSpeed));
            } catch (InterruptedException ignore) {
            }
        }
    }
}
