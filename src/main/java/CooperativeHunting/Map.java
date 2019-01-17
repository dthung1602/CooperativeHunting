package CooperativeHunting;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;

class Map {
    private ArrayList<Prey> preys;
    private ArrayList<Group> groups;
    private ArrayList<Predator> predators;

    private GUI controller;
    private GraphicsContext graphics;

    private int preysNum;

    // map size
    private int mapWidth;
    private int mapHeight;
    int tiles;

    // output
    float avgFoodGained;
    int predatorCount;

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

    void setMapSize(int width, int height) {
        mapWidth = width;
        mapHeight = height;
        tiles = Math.min(600 / mapHeight, 600 / mapWidth);
    }

    void setController(GUI controller) {
        this.controller = controller;
        this.graphics = controller.mapCanvas.getGraphicsContext2D();
    }

    /**
     * Paint preys and predators to canvas
     */
    void paint() {
        // clear screen
        graphics.setFill(Color.WHITE);
        graphics.fillRect(0, 0, mapWidth * tiles, mapHeight * tiles);

        // paint predators
        graphics.setFill(Predator.getColor());
        graphics.setStroke(Predator.getColor());
        for (Predator predator : predators)
            predator.paint(graphics);

        // paint groups
        graphics.setStroke(Group.getColor());
        for (Group group : groups)
            group.paint(graphics);

        // paint preys
        graphics.setFill(Prey.getColor());
        for (Prey prey : preys)
            prey.paint(graphics);
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
        controller.displayOutput(avgFoodGained, predatorCount);
    }

    /**
     * Randomly create preys in the map
     *
     * @param number:       number of preys to create
     * @param speed:        preys' speed (tiles/iteration)
     * @param nutrition:    preys' nutrition
     * @param attack:       preys' attack
     * @param visionRadius: preys' vision radius
     * @param color:        preys' color for visualization
     */
    void initializePreys(int number, int speed, float nutrition, int attack, int visionRadius, Color color) {
        preysNum = number;

        // set values for prey class
        Prey.set(speed, nutrition, attack, visionRadius, color);

        // get occupied positions on map
        HashSet<Position> usedPositions = new HashSet<Position>();
        for (Predator predator : predators)
            usedPositions.add(predator.getPosition());

        // TODO check prey > map capacity
        // create preys randomly
        ArrayList<Position> positions = getRandomPositions(number, usedPositions);
        preys.clear();
        for (Position position : positions)
            preys.add(new Prey(position));
    }

    /**
     * Randomly create predators in the map
     *
     * @param number:       number of predators to create
     * @param speed:        predators' speed (tiles/iteration)
     * @param health:       predators' health
     * @param attack:       predators' attack
     * @param groupRadius:  predators' group radius
     * @param visionRadius: predators' vision radius
     * @param color:        predators' color for visualization
     */
    void initializePredators(int number, int speed, int health, int attack, int groupRadius, int visionRadius, Color color) {
        predatorCount = number;

        // set values for predator class
        Predator.set(speed, health, attack, visionRadius, color);
        Group.setGroupRadius(groupRadius);

        // get occupied positions on map
        HashSet<Position> usedPositions = new HashSet<Position>();
        for (Prey prey : preys)
            usedPositions.add(prey.getPosition());

        // TODO check prey > map capacity
        // create predators randomly
        ArrayList<Position> positions = getRandomPositions(number, usedPositions);
        predators.clear();
        for (Position position : positions)
            predators.add(new Predator(position));
    }

    /**
     * Create new preys to replace dead ones
     */
    private void createNewPreys() {
        int numPreyToAdd = preysNum - preys.size();
        ArrayList<Position> positions = getRandomPositions(numPreyToAdd, new ArrayList<Position>());
        for (int i = 0; i < numPreyToAdd; i++)
            preys.add(new Prey(positions.get(i)));
    }

    /**
     * Generate a list of unused positions in the map
     *
     * @param number:        number of positions to generate
     * @param usedPositions: list of used positions
     * @return a list of unused positions
     */
    private ArrayList<Position> getRandomPositions(int number, Collection<Position> usedPositions) {
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
}
