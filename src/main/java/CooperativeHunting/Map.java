package CooperativeHunting;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static CooperativeHunting.Position.getRandomPositions;

class Map implements Serializable {
    private List<Predator> predators;
    private List<Group> groups;
    private List<Prey> preys;

    private int newPreyPerIterationInt;
    private float newPreyPerIterationFloat;

    Integer numberOfIteration;

    transient private Canvas canvas;

    // map size
    private int mapWidth;
    private int mapHeight;
    private float tileSize;

    // display options
    private boolean showPreyVision;
    private boolean showPredatorVision;
    private boolean showGroup;
    private boolean showGrid;

    // graph output
    LinkedList<Double> predatorPopulationPerIteration;
    LinkedList<Double> preyPopulationPerIteration;
    LinkedList<Double> avgFoodGainedPerIteration;
    transient Lock outputChartDataLock;

    // text output
    private float foodGainedThisIteration;

    private static Random random = new Random();

    /**
     * Map constructor
     *
     * @param canvas: GUI's Canvas object
     */
    Map(Canvas canvas) {
        this.canvas = canvas;
        predatorPopulationPerIteration = new LinkedList<>();
        preyPopulationPerIteration = new LinkedList<>();
        avgFoodGainedPerIteration = new LinkedList<>();
        outputChartDataLock = new ReentrantLock();
        predators = new ArrayList<>();
        preys = new LinkedList<>();
        groups = new LinkedList<>();
        numberOfIteration = 0;
    }

    /*************************************    INITIALIZE METHODS    ***************************************************/

    /**
     * Randomly create predators in the map
     *
     * @param number:        number of predators to create
     * @param speed:         predators' speed (tiles/iteration)
     * @param health:        predators' health
     * @param attack:        predators' attack
     * @param groupRadius:   predators' group radius
     * @param visionRadius:  predators' vision radius
     * @param predatorColor: predators' color for visualization
     * @param groupColor:    groups' color for visualization
     */
    void initializePredators(int number, int speed, float health, float attack, float groupRadius, float visionRadius,
                             float stayInGroupTendency, Predator.HuntingMethod huntingMethod,
                             Color predatorColor, Color groupColor)
            throws IllegalArgumentException {

        // Validate arguments
        if (number > mapWidth * mapHeight)
            throw new IllegalArgumentException("Too many predators");
        if (number < 0 || speed < 0 || health < 0 || attack < 0 || visionRadius < 0 || groupRadius < 0)
            throw new IllegalArgumentException("Predator's number, speed, health, attack, vision radius" +
                    " and group radius cannot be negative");
        if (Math.abs(stayInGroupTendency - 0.5) > 0.5)
            throw new IllegalArgumentException("Stay in group tendency must be a float in range [0, 1]");

        // set values for predator and group class
        Predator.set(speed, health, attack, visionRadius, stayInGroupTendency, huntingMethod, predatorColor);
        Group.set(groupRadius, groupColor);

        // create predators randomly
        ArrayList<Position> positions = getRandomPositions(number, mapWidth, mapHeight);
        predators.clear();
        groups.clear();
        for (Position position : positions)
            predators.add(new Predator(position));
    }

    /**
     * Randomly create preys in the map
     *
     * @param number:       number of preys to create
     * @param speed:        preys' speed (tileSize/iteration)
     * @param nutrition:    preys' nutrition
     * @param attack:       preys' attack
     * @param minSize:      preys' min size, cannot be less than 1
     * @param maxSize:      preys' max size, cannot be less than 1
     * @param visionRadius: preys' vision radius
     * @param primaryColor: preys' primary color for visualization
     */
    void initializePreys(int number, int speed, float nutrition, float attack, int minSize, int maxSize,
                         float visionRadius, float newPreyPerIteration, Color primaryColor)
            throws IllegalArgumentException {

        // validate arguments
        if (minSize > maxSize || minSize < 1)
            throw new IllegalArgumentException("Condition 1 <= prey min size <= prey max size must be satisfied");
        if (number > mapWidth * mapHeight || newPreyPerIteration > mapHeight * mapHeight)
            throw new IllegalArgumentException("Too many preys / new prey per iteration");
        if (number < 0 || speed < 0 || nutrition < 0 || attack < 0 || visionRadius < 0 || newPreyPerIteration < 0)
            throw new IllegalArgumentException("Prey's number, speed, nutrition, attack and new prey/iter cannot be negative");

        // calculate colors for preys from primary color
        Color smallPreyColor = copyColor(primaryColor, 0.8);
        Color mediumPreyColor = copyColor(primaryColor, 0.9);
        Color largePreyColor = copyColor(primaryColor, 1);

        // set value for creating new Preys
        newPreyPerIterationInt = (int) newPreyPerIteration;
        newPreyPerIterationFloat = newPreyPerIteration - (int) newPreyPerIteration;

        // set values for prey class
        Prey.set(speed, nutrition, attack, visionRadius, minSize, maxSize,
                smallPreyColor, mediumPreyColor, largePreyColor);

        // create preys randomly
        ArrayList<Position> positions = getRandomPositions(number, mapWidth, mapHeight);
        preys.clear();
        for (Position position : positions)
            preys.add(new Prey(position));
    }

    /*************************************    UPDATING METHODS    *****************************************************/

    /**
     * Preys and predators move and interact
     */
    synchronized void update() {
        numberOfIteration++;
        foodGainedThisIteration = 0;

        createNewPreys();
        Group.createNewGroup(groups, predators);

        for (Predator predator : predators)
            predator.updateScout();

        for (Group group : groups)
            group.updateLeader();

        for (Predator predator : predators)
            predator.updateMove();

        for (Group group : groups)
            group.updateMembers();

        for (Prey prey : preys)
            prey.update();

        createNewPredators();
        removeDeadAnimals(preys);
        removeDeadAnimals(predators);
        removeEmptyGroups();
    }

    float[] getOutput() {
        float avg = foodGainedThisIteration / predators.size();

        try {
            outputChartDataLock.lock();
            predatorPopulationPerIteration.add((double) predators.size());
            preyPopulationPerIteration.add((double) preys.size());
            avgFoodGainedPerIteration.add((double) avg);
            if (numberOfIteration % 100 == 0) {
                float x = 0;
                for (double d : preyPopulationPerIteration) x += d;
                System.out.print(x / preyPopulationPerIteration.size() + " ");
                x = 0;
                for (double d : predatorPopulationPerIteration) x += d;
                System.out.print(x / preyPopulationPerIteration.size() + " ");
                x = 0;
                for (double d : avgFoodGainedPerIteration) x += d;
                System.out.println(x / preyPopulationPerIteration.size());
            }
        } finally {
            outputChartDataLock.unlock();
        }

        return new float[]{avg, predators.size(), preys.size()};
    }

    /*************************************    ADDITIONAL INITIALIZATION AND UPDATING METHODS    ***********************/

    /**
     * Predators reproduce
     */
    private void createNewPredators() {
        // check if this reproducing season
        if (numberOfIteration % Predator.YEAR_LENGTH > Predator.REPRODUCE_SEASON_LENGTH)
            return;

        // add new born predators to list
        LinkedList<Predator> newPredators = new LinkedList<>();
        for (Predator predator : predators) {
            Predator newBorn = predator.reproduce();
            if (newBorn != null)
                newPredators.add(newBorn);
        }
        predators.addAll(newPredators);
    }

    /**
     * Create new preys to replace dead ones
     */
    private void createNewPreys() {
        // check if this reproducing season
        if (numberOfIteration % Prey.YEAR_LENGTH > Prey.REPRODUCE_SEASON_LENGTH || preys.size() == 0)
            return;
        int one = (random.nextFloat() < newPreyPerIterationFloat) ? 1 : 0;
        ArrayList<Position> positions = getRandomPositions(newPreyPerIterationInt + one, mapWidth, mapHeight);
        for (Position position : positions)
            preys.add(new Prey(position));
    }

    /**
     * Remove dead animal from the list
     *
     * @param animals: list to remove
     */
    private void removeDeadAnimals(List<? extends Animal> animals) {
        for (Iterator<? extends Animal> iterator = animals.iterator(); iterator.hasNext(); )
            if (iterator.next().dead)
                iterator.remove();
    }

    /**
     * Remove empty groups and groups with only 1 member
     */
    private void removeEmptyGroups() {
        for (Iterator<Group> iterator = groups.iterator(); iterator.hasNext(); ) {
            Group group = iterator.next();
            switch (group.members.size()) {
                case 1:
                    group.members.get(0).group = null;
                case 0:
                    iterator.remove();
            }
        }
    }

    /*************************************    ADDITIONAL PAINTING METHODS    ******************************************/

    /**
     * Clone a color and change the clone's opacity
     *
     * @param color:      original color
     * @param newOpacity: clone color opacity
     */
    private Color copyColor(Color color, double newOpacity) {
        return new Color(
                color.getRed(),
                color.getGreen(),
                color.getBlue(),
                newOpacity
        );
    }

    private void clearScreen() {
        GraphicsContext graphics = canvas.getGraphicsContext2D();
        graphics.setFill(Color.WHITE);
        graphics.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    void clear() {
        try {
            outputChartDataLock.lock();
            predators.clear();
            groups.clear();
            preys.clear();
            predatorPopulationPerIteration.clear();
            preyPopulationPerIteration.clear();
            avgFoodGainedPerIteration.clear();
            clearScreen();
            numberOfIteration = 0;
        } finally {
            outputChartDataLock.unlock();
        }
    }

    /*************************************    PAINTING METHODS    *****************************************************/

    /**
     * Paint preys and predators to canvas
     */
    void paint() {
        GraphicsContext graphics = canvas.getGraphicsContext2D();

        // clear screen
        clearScreen();

        // paint predators
        for (Predator predator : predators)
            predator.paint(graphics, showPredatorVision);

        // paint groups
        if (showGroup) {
            graphics.setStroke(Group.getColor());
            for (Group group : groups)
                group.paint(graphics, true);
        }

        // paint preys
        for (Prey prey : preys)
            prey.paint(graphics, showPreyVision);

        // paint grid
        if (showGrid) {
            graphics.setStroke(Color.BLACK);
            for (int i = 0; i <= mapWidth; i++) {
                graphics.strokeLine(
                        i * tileSize, 0,
                        i * tileSize, mapHeight * tileSize
                );
            }
            for (int i = 0; i <= mapHeight; i++) {
                graphics.strokeLine(
                        0, i * tileSize,
                        mapWidth * tileSize, i * tileSize
                );
            }
        }
    }

    /*************************************    SETTERS AND GETTERS     *************************************************/

    List<Prey> getPreys() {
        return preys;
    }

    List<Predator> getPredators() {
        return predators;
    }

    int getMapWidth() {
        return mapWidth;
    }

    int getMapHeight() {
        return mapHeight;
    }

    float getTileSize() {
        return tileSize;
    }

    void set(int width, int height, boolean showGrid,
             boolean showPredatorVision, boolean showPreyVision, boolean showGroup) {
        mapWidth = width;
        mapHeight = height;
        tileSize = (float) Math.min(
                canvas.getHeight() / mapHeight,
                canvas.getWidth() / mapWidth
        );
        this.showPredatorVision = showPredatorVision;
        this.showPreyVision = showPreyVision;
        this.showGroup = showGroup;
        this.showGrid = showGrid;
    }

    void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    /*************************************    UTILITIES    ************************************************************/

    void addFoodGain(float foodGain) {
        foodGainedThisIteration += foodGain;
    }
}
