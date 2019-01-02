package CooperativeHunting;

import java.awt.*;


class Predator extends Animal {
    private static int visionRadius;
    private static int speed;
    private static int health;
    private static int attack;
    static Color color;

    Group group;

    /**
     * @param position: initial position of the predator
     * @param group:    the group the predator belongs to
     */
    Predator(Position position, Group group) {
        super(position);
        this.group = group;
    }

    /**
     * Set numerous settings for predators
     *
     * @param speed:  predators' speed
     * @param health: predators' nutrition value
     * @param attack: predators' attack
     * @param color:  predators' color for visualization
     */
    static void set(int speed, int health, int attack, Color color) {
        Predator.speed = speed;
        Predator.health = health;
        Predator.attack = attack;
        Predator.color = color;
    }

    /**
     * Predator moves as a group member
     *
     * @param map: Map object
     */
    void update(Map map) {

    }

    /**
     * Predator moves as a group leader
     *
     * @param map: Map object
     */
    void updateAsLeader(Map map) {

    }

    /**
     * Paint predator to the map
     *
     * @param graphics: Graphic object
     */
    void paint(Graphics graphics) {
    }
}
