package CooperativeHunting;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

class Prey extends Animal {
    private static final float NUTRITION_VARIANCE = 0.2f;

    private static float visionRadius;
    private static int speed;
    static float defaultNutrition;
    private static float defaultAttack;

    private static int minSize;
    private static int maxSize;

    private static Color smallPreyColor;
    private static Color mediumPreyColor;
    private static Color largePreyColor;

    private float nutrition;

    /**
     * Prey constructor
     *
     * @param position: initial position of the prey
     */
    Prey(Position position) {
        super(position);
        size = randomSize();
        nutrition = getRandomNutrition();
        attack = (int) (size * defaultAttack);
        adjustColorAccordingToSize();
    }

    Prey(Position position, int size) {
        super(position);
        this.size = size;
        nutrition = getRandomNutrition();
        attack = (int) (size * defaultAttack);
        adjustColorAccordingToSize();
    }

    @Override
    float getVisionRadius() {
        return visionRadius;
    }

    @Override
    int getSpeed() {
        return speed;
    }

    float getNutrition() {
        return nutrition;
    }

    int getAttack() {
        return size * attack;
    }

    /**
     * Setter for Prey static fields
     *
     * @param speed:            preys' speed (tiles/iteration)
     * @param defaultNutrition: preys' defaultNutrition value
     * @param defaultAttack:    preys' attack
     * @param visionRadius:     preys' vision radius (tiles)
     * @param smallPreyColor:   small preys' color for visualization
     * @param mediumPreyColor:  medium preys' color for visualization
     * @param largePreyColor:   large preys' color for visualization
     */
    static void set(int speed, float defaultNutrition, int defaultAttack, float visionRadius, int minSize, int maxSize,
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

    @Override
    void postDeserialize() {
        adjustColorAccordingToSize();
    }

    /**
     * Update prey's position, health, etc after each iteration
     * Handle interactions between predators and preys
     */
    void update() {
        // get predators in vision
        List<Predator> predators = getPredatorsInVision();

        // if no predator is found, move randomly
        if (predators.isEmpty()) {
            moveRandomly();
            stayInMap();
            return;
        }

        // predator found -> counter attack
        Predator closestPredator = predators.get(0);
        if (attack > closestPredator.getAttack())
            closestPredator.die();

        // move away from predators
        avoidPredators(predators);
        stayInMap();
    }

    /**
     * Prey try to avoids preys
     *
     * @param predators: predators in the prey's vision
     */
    private void avoidPredators(List<Predator> predators) {
        // for each predator, calculate the vector predator -> this
        // get the value 1 / vector
        // -> the closer the predator, the larger the value 1 / vector, the more influential it is
        // calculate the sum of the value 1 / vector
        float sumX = 0;
        float sumY = 0;
        for (Predator predator : predators) {
            float dx = x - predator.x;
            float dy = y - predator.y;
            float d = (float) Math.sqrt(dx * dx + dy * dy);
            sumX += 1f / dx / d;
            sumY += 1f / dy / d;
        }

        // move according to the sum vector
        moveInDirection(sumX, sumY);
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
        float minDistance = Float.POSITIVE_INFINITY;
        int minIndex = -1;
        int index = 0;

        // loop through every predators
        for (Predator predator : map.getPredators()) {
            float distance = distanceTo(predator);

            if (distance <= visionRadius) {
                // add predator to list
                predatorsInRange.add(predator);

                // updateMove min distance
                if (distance < minDistance) {
                    minDistance = distance;
                    minIndex = index;
                }
                index++;
            }
        }

        // swap closest predator to the 0-index position
        if (minIndex > -1) {
            Predator closestPredator = predatorsInRange.get(minIndex);
            Predator tmp = predatorsInRange.get(0);
            predatorsInRange.set(0, closestPredator);
            predatorsInRange.set(minIndex, tmp);
        }

        return predatorsInRange;
    }

    /**
     * Create and return a random size between minSize and maxSize
     * The probability that the return value is minSize + x is 1 / x^3
     *
     * @return an int in range [minSize, maxSize]
     */
    private static int randomSize() {
        int s = maxSize - minSize + 1;
        s = random.nextInt(s * s * (s + 1) * (s + 1) / 4) + 1;
        int i = 0;
        while (s > i * i * (i + 1) * (i + 1) / 4)
            i++;
        return maxSize - i + 1;
    }

    /**
     * Random nutrition value base on size, and default nutrition and NUTRITION_VARIANCE
     *
     * @return size * defaultNutrition +- NUTRITION_VARIANCE%
     */
    private float getRandomNutrition() {
        return size * defaultNutrition * (1 - NUTRITION_VARIANCE + random.nextFloat() * NUTRITION_VARIANCE * 2);
    }

    /**
     * Change color opacity according to size
     * minSize     1/3        2/3      maxSize
     * |---------|----------|----------|
     * small       mid       large
     */
    private void adjustColorAccordingToSize() {
        float midThreshold = minSize + (maxSize - minSize) / 3.0f;
        float largeThreshold = minSize + (maxSize - minSize) / 3.0f * 2;
        if (size > largeThreshold)
            color = largePreyColor;
        else if (size > midThreshold)
            color = mediumPreyColor;
        else
            color = smallPreyColor;
    }
}
