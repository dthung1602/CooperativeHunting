package CooperativeHunting;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

class Predator extends Animal {
    static int visionRadius;
    private static int speed;
    private static int health = 100;
    private static int attack;
    private static Color color = Color.RED;

    // predator's group
    Group group;

    // hold the distance and direction to the prey for the predator
    private int globalDistanceX;
    private int globalDistanceY;
    private int globalDistance = 600; // TODO change to map.width

    /**
     * Predator constructor
     *
     * @param position: initial position of the predator
     */
    Predator(Position position) {
        super(position);
    }

    static int getAttack() {
        return attack;
    }

    /**
     * @param speed:  predator's speed point (tiles/iteration)
     * @param health: predator's health point
     * @param attack  : predator's attack point
     * @param color   : predator's color for visualization
     */
    static void set(int speed, int health, int attack, Color color) {
        Predator.speed = speed;
        Predator.health = health;
        Predator.attack = attack;
        Predator.color = color;
    }

    /**
     * Update predator's position, health, etc after each iteration
     *
     * @param map: Map object
     */
    @Override
    void update(Map map) {
        updateScout(map);
        updateMove(map);
    }

    /**
     * Paint predator to the map
     *
     * @param graphics: Graphic object
     */
    @Override
    // TODO rewrite
    void paint(Graphics graphics) {
        graphics.setColor(color);
        graphics.fillRect(x - size / 2, y - size / 2, size, size);
        graphics.setColor(Color.PINK);
        graphics.drawOval(x - visionRadius, y - visionRadius, visionRadius * 2, visionRadius * 2);
    }

    /**
     * Remove dead predators out of the given list
     *
     * @param predators: list to remove dead predators
     */
    static void removeDeadPredators(ArrayList<Predator> predators) {
        for (Iterator<Predator> iterator = predators.iterator(); iterator.hasNext(); ) {
            Predator predator = iterator.next();
            if (predator.dead)
                iterator.remove();
        }
    }

    // TODO add comments for all below methods
    // TODO comment in code for all below methods
    private void updateScout(Map map) {
        scout(map);
        group.selectLeader(this, globalDistance, globalDistanceX, globalDistanceY);
        checkDead();
    }

    private void updateMove(Map map) {
        Predator leader = group.getLeader();
        if (leader != null && this != leader) {
            moveToLeader(leader);
        } else {
            move(map);
        }
    }

    private void checkDead() {
        health--;
        this.dead = (health < 1);
    }

    private void scout(Map map) {
        globalDistanceX = -1;
        globalDistanceY = -1;
        globalDistance = map.getMapWidth();

        for (Prey prey : map.getPreys()) {
            int templateDistance = (int) distanceTo(prey);
            if (templateDistance <= visionRadius && templateDistance < globalDistance) {
                globalDistance = templateDistance;
                globalDistanceX = prey.x - this.x;
                globalDistanceY = prey.y - this.y;
            }
        }
    }

    private void move(Map map) {
        if (globalDistanceX != -1) {
            // TODO duplicated code to line 138. consider move common code to another method
            try {
                float ratio = Math.abs((float) globalDistanceX / globalDistanceY);
                this.y += Math.round(speed / (ratio + 1)) * (globalDistanceY / Math.abs(globalDistanceY));
                this.x += Math.round(speed / (1 / ratio + 1)) * (globalDistanceX / Math.abs(globalDistanceX));
            } catch (ArithmeticException e) { // division by zero
                if (globalDistanceX > 0)
                    this.x += speed;
                else
                    this.x -= speed;
            }
        } else {
            this.x += random.nextInt(speed) + -speed / 4;
            this.y += random.nextInt(speed) + -speed / 4;
        }

        stayInMap(map);
    }

    private void moveToLeader(Predator leader) {
        try {
            float ratio = Math.abs((float) leader.x / leader.y);
            this.y += Math.round(speed / (ratio + 1)) * (leader.y / Math.abs(leader.y));
            this.x += Math.round(speed / (1 / ratio + 1)) * (leader.x / Math.abs(leader.x));
        } catch (ArithmeticException e) { // division by zero
            if (leader.x > 0)
                this.x += speed;
            else
                this.x -= speed;
        }
    }
}
