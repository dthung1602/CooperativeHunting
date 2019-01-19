package CooperativeHunting;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

class Map {
    private ArrayList<Prey> preys;
    private ArrayList<Group> groups;
    private ArrayList<Predator> predators;
    private int newPreyPerIteration;

    private GUI controller;
    private Canvas canvas;
    private GraphicsContext graphics;

    // map size
    private int mapWidth;
    private int mapHeight;
    private int tileSize;

    // display options
    private boolean showPreyVision;
    private boolean showPredatorVision;
    private boolean showGroup;
    private boolean showGrid;

    // output
    float avgFoodGained;

    private static Random random = new Random();

    Map() {
        predators = new ArrayList<Predator>();
        preys = new ArrayList<Prey>();
        groups = new ArrayList<Group>();
    }

    ArrayList<Prey> getPreys() {
        return preys;
    }

    ArrayList<Group> getGroups() {
        return groups;
    }

    ArrayList<Predator> getPredators() {
        return predators;
    }

    int getMapWidth() {
        return mapWidth;
    }

    int getMapHeight() {
        return mapHeight;
    }

    int getTileSize() {
        return tileSize;
    }

    void set(int width, int height, boolean showGrid,
             boolean showPredatorVision, boolean showPreyVision, boolean showGroup) {
        mapWidth = width;
        mapHeight = height;
        tileSize = Math.min(
                (int) (canvas.getHeight() / mapHeight),
                (int) (canvas.getWidth() / mapWidth)
        );
        this.showPredatorVision = showPredatorVision;
        this.showPreyVision = showPreyVision;
        this.showGroup = showGroup;
        this.showGrid = showGrid;
    }

    void setController(GUI controller) {
        this.controller = controller;
        this.canvas = controller.getMapCanvas();
        this.graphics = canvas.getGraphicsContext2D();
    }

    /**
     * Paint preys and predators to canvas
     */
    void paint() {
        // clear screen
        graphics.setFill(Color.WHITE);
        graphics.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

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

    /**
     * Preys and predators move and interact
     */
    void update() {
        // initialize output for this iteration
        avgFoodGained = 0;

        // remove dead animals
        Predator.removeDeadPredators(predators);
        Prey.removeDeadPreys(preys);

        // new preys pop up
        if (newPreyPerIteration > 0)
            createNewPreys();

        // split and merge predators groups
        //Order
        //Create group
        //Scout
        //Reset leader
        //Select leader
        //Move
        //Calculate circle
        //Delete member
        for (Predator predator : predators) {
            predator.grouping(predators, groups);
            predator.updateScout();
        }

        for (Group group : groups) {
            group.updateLeader();
        }

        for (Predator predator : predators) {
            predator.updateMove();
        }

        ArrayList<Group> delGroups = new ArrayList<Group>();
        for (Group group : groups) {
            group.updateCircleAndDeleteMember();
            if (group.members.size() <= 1) {
                delGroups.add(group);
            }
        }
        groups.removeAll(delGroups);

        for (Prey prey : preys)
            prey.update();

        // display output
        controller.displayOutput(avgFoodGained, predators.size());
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
    void initializePreys(int number, int speed, float nutrition, int attack, int minSize, int maxSize,
                         int visionRadius, int newPreyPerIteration, Color primaryColor) throws IllegalArgumentException {

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

        // set values for prey class
        this.newPreyPerIteration = newPreyPerIteration;
        Prey.set(speed, nutrition, attack, visionRadius, minSize, maxSize,
                smallPreyColor, mediumPreyColor, largePreyColor);

        // create preys randomly
        ArrayList<Position> positions = getRandomPositions(number);
        preys.clear();
        for (Position position : positions)
            preys.add(new Prey(position));
    }

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
    void initializePredators(int number, int speed, int health, int attack, int groupRadius, int visionRadius,
                             Color predatorColor, Color groupColor) throws IllegalArgumentException {

        // Validate arguments
        if (number > mapWidth * mapHeight)
            throw new IllegalArgumentException("Too many predators");
        if (number < 0 || speed < 0 || health < 0 || attack < 0 || visionRadius < 0 || groupRadius < 0)
            throw new IllegalArgumentException("Predator's number, speed, health, attack, vision radius" +
                    " and group radius cannot be negative");

        // set values for predator and group class
        Predator.set(speed, health, attack, visionRadius, predatorColor);
        Group.set(groupRadius, groupColor);

        // create predators randomly
        ArrayList<Position> positions = getRandomPositions(number);
        predators.clear();
        for (Position position : positions)
            predators.add(new Predator(position));
    }

    /**
     * Create new preys to replace dead ones
     */
    private void createNewPreys() {
        ArrayList<Position> positions = getRandomPositions(newPreyPerIteration);
        for (Position position : positions)
            preys.add(new Prey(position));
    }

    /**
     * Generate a list of unused positions in the map
     *
     * @param number: number of positions to generate
     * @return a list of unused positions
     */
    private ArrayList<Position> getRandomPositions(int number) {
        HashSet<Position> usedPositions = new HashSet<Position>();
        ArrayList<Position> newPositions = new ArrayList<Position>(number);

        for (int i = 0; i < number; i++) {
            Position position;
            int x, y;
            do {
                x = random.nextInt(mapWidth);
                y = random.nextInt(mapHeight);
                position = new Position(x, y);
            } while (usedPositions.contains(position));
            newPositions.add(position);
            usedPositions.add(position);
        }

        return newPositions;
    }

    private Color copyColor(Color color, double newOpacity) {
        return new Color(
                color.getRed(),
                color.getGreen(),
                color.getBlue(),
                newOpacity
        );
    }
}
