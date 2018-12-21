package CooperativeHunting;

import javax.swing.*;
import java.awt.*;
import java.util.*;

class Map extends JPanel {
    // preys and predator groups
    private ArrayList<Prey> preys;
    private ArrayList<Group> groups;

    // Map size
    private int mapWidth;
    private int mapHeight;

    private static Random random = new Random();

    Map() {
        preys = new ArrayList<Prey>();
        groups = new ArrayList<Group>();
    }

    ArrayList<Prey> getPreys() {
        return preys;
    }

    ArrayList<Group> getGroups() {
        return groups;
    }

    int getMapWidth() {
        return mapWidth;
    }

    int getMapHeight() {
        return mapHeight;
    }

    void setMapSize(int width, int height) {
        this.mapWidth = width;
        this.mapHeight = height;
    }

    /**
     * Preys and predators move and interact
     */
    void update() {
        Prey.removeDeadPreys(preys);
        Group.mergeOrSplitGroups(groups);

        for (Group group : groups)
            group.update(this);

        for (Prey prey : preys)
            prey.update(this);
    }

    /**
     * Paint preys and predators to panel
     *
     * @param g: Graphic object
     */
    public void paintComponent(Graphics g) {
        clearScreen(g);

        for (Group group : groups)
            group.paint(g);

        for (Prey prey : preys)
            prey.paint(g);
    }

    /**
     * Randomly create preys in the map
     *
     * @param number:    number of preys to create
     * @param speed:     preys' speed (tiles/iteration)
     * @param nutrition: preys' nutrition
     * @param attack:    preys' attack
     * @param color:     preys' color for visualization
     */
    void inititalizePreys(int number, int speed, float nutrition, int attack, Color color) {
        // set values for prey class
        Prey.set(speed, nutrition, attack, color);

        // get occupied positions on map
        HashSet<Position> usedPositions = new HashSet<Position>();
        for (Group group : groups) {
            usedPositions.add(group.getLeader().getPosition());
            for (Predator member : group.getMembers())
                usedPositions.add(member.getPosition());
        }

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
     * @param number:      number of predators to create
     * @param speed:       predators' speed (tiles/iteration)
     * @param health:      predators' health
     * @param attack:      predators' attack
     * @param groupRadius: predators' group radius
     * @param color:       predators' color for visualization
     */
    void inititalizePredators(int number, int speed, int health, int attack, int groupRadius, Color color) {
        // set values for predator class
        Predator.set(speed, health, attack, color);
        Group.setGroupRadius(groupRadius);

        // get occupied positions on map
        HashSet<Position> usedPositions = new HashSet<Position>();
        for (Prey prey : preys)
            usedPositions.add(prey.getPosition());

        // TODO check prey > map capacity
        // create predators randomly
        ArrayList<Position> positions = getRandomPositions(number, usedPositions);
        groups.clear();
        for (Position position : positions)
            groups.add(new Group(position));
    }

    /**
     * Clear map's visualizing panel
     *
     * @param g: graphic object
     */
    private void clearScreen(Graphics g) {
        g.clearRect(0, 0, this.mapWidth, this.mapHeight);
    }

    /**
     * Create random integer in range [min, max)
     *
     * @param min: lower bound - inclusive
     * @param max: upper bound - exclusive
     * @return random int in range [min, max)
     */
    private static int getRandomInt(int min, int max) {
        if (min > max)
            throw new IllegalArgumentException("Max must not be less than min");

        return random.nextInt(max - min) + min;
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
                x = getRandomInt(0, mapWidth);
                y = getRandomInt(0, mapWidth);
                position = new Position(x, y);
            } while (usedPositions.contains(position));
            newPositions.add(position);
        }
        return newPositions;
    }
}
