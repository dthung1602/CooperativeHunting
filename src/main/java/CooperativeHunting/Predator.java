package CooperativeHunting;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Iterator;

class Predator extends Animal {
    private static int visionRadius;
    private static int visionDiameter;
    private static int speed;
    private static int initHealth;
    private static int attack;
    private static Color color = Color.RED;

    // predator's group
    int health;
    Group group;

    // hold the distance and direction to the prey for the predator
    int globalDistanceX;
    int globalDistanceY;
    int globalDistance = map.getMapWidth(); // TODO change to map.width

    static Color getColor() {
        return color;
    }

    /**
     * Predator constructor
     *
     * @param position: initial position of the predator
     */
    Predator(Position position) {
        super(position);
        health = initHealth;
        group = null;
    }

    static int getAttack() {
        return attack;
    }

    /**
     * @param speed:        predators' speed point (tiles/iteration)
     * @param initHealth:   predators' initial health point
     * @param attack:       predators' attack point
     * @param visionRadius: predators' vision radius
     * @param color:        predators' color for visualization
     */
    static void set(int speed, int initHealth, int attack, int visionRadius, Color color) {
        Predator.speed = speed;
        Predator.initHealth = initHealth;
        Predator.attack = attack;
        Predator.visionRadius = visionRadius;
        Predator.visionDiameter = visionRadius * 2;
        Predator.color = color;
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
        if (health < 1) {
            dead = true;
            map.predatorCount--;
        }
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
        int dX = 600, dY = 600;
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

    /**
     * Paint predator to the map
     */
    @Override
    void paint(GraphicsContext graphics) {
        graphics.fillRect(map.tiles * x, map.tiles * y, map.tiles, map.tiles);
        graphics.strokeOval(
                map.tiles * (x - visionRadius + map.tiles / 2.0),
                map.tiles * (y - visionRadius + map.tiles / 2.0),
                map.tiles * visionDiameter,
                map.tiles * visionDiameter
        );
    }
}
