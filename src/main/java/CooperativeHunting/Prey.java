package CooperativeHunting;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class Prey extends Animal {
    private static int visionRadius;
    private static int speed;
    private static float defaultNutrition;
    private static float defaultAttack;

    private static int minSize;
    private static int maxSize;

    private static float attackRatio;

    private static Color smallPreyColor;
    private static Color mediumPreyColor;
    private static Color largePreyColor;

    /**
     * Prey constructor
     *
     * @param position: initial position of the prey
     */
    Prey(Position position) {
        super(position);
        size = random.nextInt(minSize, maxSize);
        attack = (int) (size * defaultAttack);

        // minSize     1/3        2/3      maxSize
        //    |---------|----------|----------|
        //       small       mid       large
        double midThreshold = minSize + (maxSize - minSize) / 3.0;
        double largeThreshold = minSize + (maxSize - minSize) / 3.0 * 2;
        if (size > largeThreshold)
            color = largePreyColor;
        else if (size > midThreshold)
            color = mediumPreyColor;
        else
            color = smallPreyColor;
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
     * Setter for Prey static fields
     *
     * @param speed:            preys' speed
     * @param defaultNutrition: preys' defaultNutrition value
     * @param defaultAttack:    preys' attack
     * @param visionRadius:     preys' vision radius
     * @param smallPreyColor:   small preys' color for visualization
     * @param mediumPreyColor:  medium preys' color for visualization
     * @param largePreyColor:   large preys' color for visualization
     */
    static void set(int speed, float defaultNutrition, int defaultAttack, int visionRadius, int minSize, int maxSize,
                    Color smallPreyColor, Color mediumPreyColor, Color largePreyColor) {
        Prey.speed = speed;
        Prey.defaultNutrition = defaultNutrition;
        Prey.defaultAttack = defaultAttack;
        Prey.visionRadius = visionRadius;
        Prey.minSize = minSize;
        Prey.maxSize = maxSize;
        Prey.smallPreyColor = smallPreyColor;
        Prey.mediumPreyColor = mediumPreyColor;
        Prey.largePreyColor = largePreyColor;
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
        float predatorAttack = predators.get(0).attack * attackRatio;

        // predators attacks first and succeed
        if (predators.size() * predatorAttack >= attack) {
            // prey is dead
            this.dead = true;

            // increase food gained
            map.avgFoodGained += defaultNutrition;

            // increase health of predators
            int healthGained = (int) defaultNutrition / predators.size();
            for (Predator predator : predators)
                predator.health += healthGained;
            return;
        }

        // prey attacks closest predator back
        if (attack > predatorAttack)
            predators.get(0).dead = true;
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
        for (Predator predator : map.getPredators()) {
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
