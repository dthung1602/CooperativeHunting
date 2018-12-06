package CooperativeHunting;

public class Main {

    private static Map map;
    private static IOVariables ioVariables;

    public static void main(String args[]) {
        createGUI();
        loop();
    }

    private static void createGUI() {

    }

    private static void loop() {
        while (true) {
            // only update map when running
            if (!ioVariables.running) {
                try {
                    ioVariables.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // update
            // ...

            // paint
            // ...

            // sleep
            try {
                Thread.sleep((long) (1000.0 / ioVariables.simulationSpeed));
            } catch (InterruptedException ignore) {
            }
        }
    }
}
