package CooperativeHunting;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class Prey extends Animal {
    private static int visionRadius = 5;
    private static int speed;
    private static float nutrition;
    private static int attack;
    private static float attackRatio;
    private static Color color;

    /**
     * Prey constructor
     *
     * @param position: initial position of the prey
     */
    Prey(Position position) {
        super(position);
    }

    static Color getColor() {
        return color;
    }

    /**
     * Set numerous settings for preys
     *
     * @param speed:        preys' speed
     * @param nutrition:    preys' nutrition value
     * @param attack:       preys' attack
     * @param visionRadius: preys' vision radius
     * @param color:        preys' color for visualization
     */
    static void set(int speed, float nutrition, int attack, int visionRadius, Color color) {
        Prey.speed = speed;
        Prey.nutrition = nutrition;
        Prey.attack = attack;
        Prey.visionRadius = visionRadius;
        Prey.color = color;
    }

    /**
     * Update prey's position, health, etc after each iteration
     * Handle interactions between predators and preys
     */
    @Override
    void update() {
        // get predators in vision
        List<Predator> predators = getPredatorsInVision();

        // if no predator found, move randomly
        if (predators.isEmpty()) {
            moveRandomly();
            stayInMap(map);
            return;
        }

        // predator found
        resolveAttack(predators);
        if (!dead) {
            avoidPredators(predators);
            stayInMap(map);
        }
    }

    /**
     * Paint prey to the map
     */
    @Override
    void paint(GraphicsContext graphics) {
        graphics.fillRect(map.tiles * x, map.tiles * y, map.tiles, map.tiles);
    }

    /**
     * Remove dead preys in the given prey list
     *
     * @param preys: list of prey
     */
    static void removeDeadPreys(ArrayList<Prey> preys) {
        for (Iterator<Prey> iterator = preys.iterator(); iterator.hasNext(); ) {
            Prey prey = iterator.next();
            if (prey.dead)
                iterator.remove();
        }
    }

    /**
     * Prey moves randomly
     */
    private void moveRandomly() {
        int step = random.nextInt(speed + 1);
        switch (random.nextInt() % 4) {
            case 0:
                x += step;
                break;
            case 1:
                x -= step;
                break;
            case 3:
                y += step;
                break;
            default:
                y -= step;
        }
    }

    /**
     * Prey try to avoids preys
     *
     * @param predators: predators in the prey's vision
     */
    private void avoidPredators(List<Predator> predators) {
        // get the sum of vectors predator -> prey
        int sumX = 0;
        int sumY = 0;
        for (Predator predator : predators) {
            sumX += x - predator.x;
            sumY += y - predator.y;
        }

        // move according to vector's angle to avoid predators
        double angle = Math.toDegrees(Math.atan2(sumY, sumX));
        if (angle > 45) {
            if (sumX > 0)
                y += speed;
            else
                y -= speed;
        } else if (angle > -45) {
            if (sumX > 0)
                x += speed;
            else
                x -= speed;
        } else {
            if (sumX > 0)
                y -= speed;
            else
                y += speed;
        }
    }

    /**
     * Predators and preys attack each other
     *
     * @param predators : predator in vision
     */
    private void resolveAttack(List<Predator> predators) {
        // predators attacks first and succeed
        if (predators.size() * Predator.getAttack() * attackRatio >= attack) {
            // prey is dead
            this.dead = true;

            // increase food gained
            map.avgFoodGained += nutrition;

            // increase health of predators
            int healthGained = (int) nutrition / predators.size();
            for (Predator predator : predators)
                predator.health += healthGained;
            return;
        }

        // prey attacks closest predator back
        if (attack > attackRatio * Predator.getAttack()) {
            predators.get(0).dead = true;
            map.predatorCount--;
        }
    }

    /**
     * Get a list of predators in prey's vision.
     * The closest predator is at the first position.
     *
     * @return list of predators in prey's vision
     */
    private List<Predator> getPredatorsInVision() {
        ArrayList<Predator> predatorsInRange = new ArrayList<Predator>();

        // keep track of closest predator
        double minDistance = Double.MAX_VALUE;
        int minIndex = -1;
        int index = 0;

        // loop through every predators
        for (Group group : map.getGroups()) {
            for (Predator predator : group.getMembers()) {
                double distance = distanceTo(predator);

                if (distance <= visionRadius) {
                    // add predator to list
                    predatorsInRange.add(predator);

                    // update min distance
                    if (distance < minDistance) {
                        minDistance = distance;
                        minIndex = index;
                    }
                    index++;
                }
            }
        }

        // swap closest predator to the 1st position
        if (minIndex > -1) {
            Predator closestPredator = predatorsInRange.get(minIndex);
            Predator tmp = predatorsInRange.get(0);
            predatorsInRange.set(0, closestPredator);
            predatorsInRange.set(minIndex, tmp);
        }

        return predatorsInRange;
    }
}
