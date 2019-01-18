package CooperativeHunting;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Iterator;

class Predator extends Animal {
    private static int visionRadius;
    private static int speed;
    private static int defaultHealth;
    private static int defaultAttack;
    private static Color defaultColor;

    int health;
    Group group;

    // hold the distance and direction to the prey for the predator
    int globalDistanceX;
    int globalDistanceY;
    int globalDistance = map.getMapWidth();

    /**
     * Predator constructor
     *
     * @param position: initial position of the predator
     */
    Predator(Position position) {
        super(position);
        size = 1;
        attack = defaultAttack;
        color = defaultColor;
        health = defaultHealth;
        group = null;
    }

    @Override
    int getVisionRadius() {
        return visionRadius;
    }

    @Override
    int getSpeed() {
        return speed;
    }

    /**
     * Setter for Predator static fields
     *
     * @param speed:         predators' speed point (tiles/iteration)
     * @param defaultHealth: predators' initial health point
     * @param defaultAttack: predators' attack point
     * @param visionRadius:  predators' vision radius
     * @param defaultColor:  predators' color for visualization
     */
    static void set(int speed, int defaultHealth, int defaultAttack, int visionRadius, Color defaultColor) {
        Predator.speed = speed;
        Predator.defaultHealth = defaultHealth;
        Predator.defaultAttack = defaultAttack;
        Predator.visionRadius = visionRadius;
        Predator.defaultColor = defaultColor;
    }

    /**
     * Update predator's position, health, etc after each iteration
     */
    @Override
    void update() {
        updateScout();
        updateMove();
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

    /**
     * The predator looks for the closest prey inside the predator's vision
     * Check for the health of the predator whether the predator is dead or not
     */
    void updateScout() {
        scout();
        //group.selectLeader(this, globalDistance, globalDistanceX, globalDistanceY);
        checkDead();
    }

    /**
     * Update the new position of the predator
     */
    void updateMove() {
        if (this.group != null) {
            Predator leader = group.getLeader();
            if (leader != null && this != leader) {
                moveToLeader(leader);
            } else {
                move();
            }
        } else {
            move();
        }
    }

    /**
     * Check if the predator is dead or not
     */
    private void checkDead() {
        health--;
        dead = (health < 1);
    }

    /**
     * The predator looks around for prey
     */
    private void scout() {
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

    /**
     * Calculate the next position for the predator
     */
    private void move() {
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

    void grouping(ArrayList<Predator> predators, ArrayList<Group> groups) {
        int dX = 600, dY = 600; // TODO map.width
        for (Predator predator : predators) {
            if (this.group == null && predator != this) {
                dX = this.x - predator.x;
                dY = this.y - predator.y;
                if (dX * dX + dY * dY < visionRadius * visionRadius) {
                    if (predator.group == null) {
                        Group neu = new Group(this, predator);
                        this.group = neu;
                        predator.group = neu;
                        groups.add(neu);
                    } else {
                        predator.group.addMember(this);
                        this.group = predator.group;
                    }
                }

            }
        }
    }
}
