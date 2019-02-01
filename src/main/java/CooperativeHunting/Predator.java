package CooperativeHunting;

import javafx.scene.paint.Color;

class Predator extends Animal {
    static final int REPRODUCE_SEASON_LENGTH = 4; // unit: iterations  -> predator only reproduce in 4 iterations
    static final int YEAR_LENGTH = 16;            // unit: iterations                     for every 16 iterations

    private static final float KILL_RADIUS_RATIO = 0.5f;  // ratio of predator's kill radius and its vision radius
    private static final float BORN_RADIUS_RATIO = 1f;  // ratio of predator's new offspring distance and vision radius

    private static float visionRadius;
    private static float killRadius;
    private static int speed;
    private static float defaultHealth;
    private static float defaultAttack;
    private static float stayInGroupTendency;
    private static float reproducingThreshold;
    private static Color defaultColor;
    static HuntingMethod huntingMethod = HuntingMethod.DEFAULT;

    private float health;
    Group group;

    private Prey anyPrey;
    private Prey closestPrey;
    private Prey tastiestPrey;
    private Prey closestTastiestPrey;
    float closestPreyDistance;
    float tastiestPreyNutrition;
    float bestCombination;

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

    /**
     * Setter for Predator static fields
     *
     * @param speed:         predators' speed point (tiles/iteration)
     * @param defaultHealth: predators' initial health point
     * @param defaultAttack: predators' attack point
     * @param visionRadius:  predators' vision radius (tiles)
     * @param huntingMethod: predators' hunting strategy
     * @param defaultColor:  predators' color for visualization
     */
    static void set(int speed, float defaultHealth, float defaultAttack, float visionRadius,
                    float stayInGroupTendency, HuntingMethod huntingMethod, Color defaultColor) {
        Predator.speed = speed;
        Predator.defaultHealth = defaultHealth;
        Predator.defaultAttack = defaultAttack;
        Predator.visionRadius = visionRadius;
        Predator.killRadius = visionRadius * KILL_RADIUS_RATIO;
        Predator.stayInGroupTendency = stayInGroupTendency;
        Predator.huntingMethod = huntingMethod;
        Predator.defaultColor = defaultColor;
        Predator.reproducingThreshold = defaultHealth + Prey.defaultNutrition * 10;
    }

    /*************************************    UPDATING METHODS    *****************************************************/

    /**
     * The predator looks for the closest prey inside the predator's vision
     * Check for the health of the predator whether the predator is dead or not
     */
    void updateScout() {
        // update health first
        health--;
        if (health <= 0) {
            die();
            return;
        }

        // find preys
        closestPreyDistance = Float.POSITIVE_INFINITY;
        tastiestPreyNutrition = Float.NEGATIVE_INFINITY;
        bestCombination = Float.NEGATIVE_INFINITY;
        closestPrey = null;
        tastiestPrey = null;
        closestTastiestPrey = null;
        anyPrey = null;

        float groupAttack = getAttack();

        for (Prey prey : map.getPreys()) {
            float distance = distanceTo(prey);
            float nutrition = prey.getNutrition();
            float combination = nutrition - distance;

            if (distance <= visionRadius // prey in vision range
                    && prey.getAttack() <= groupAttack) { // prey must be weaker than group
                anyPrey = prey;
                if (distance < closestPreyDistance) { // prey is the closest
                    closestPreyDistance = distance;
                    closestPrey = prey;
                }
                if (nutrition > tastiestPreyNutrition) { // prey is the most nutritious
                    tastiestPreyNutrition = nutrition;
                    tastiestPrey = prey;
                }
                if (combination > bestCombination) { // prey is the closest and the most nutritious
                    closestTastiestPrey = prey;
                    bestCombination = combination;
                }
            }
        }
    }

    /**
     * Update the new position of the predator
     */
    void updateMove() {
        // this predator belongs to a group
        if (group != null) {
            // this group has a leader
            if (group.leader != null) {
                if (this == group.leader)
                    // this predator is the leader -> move to prey
                    moveInDirection(getTargetPrey());
                else
                    // not leader -> follow leader
                    moveInDirection(group.leader);

            } else { // no leader
                Prey target = getTargetPrey();

                if (target != null) {
                    // found a Prey -> follow it
                    moveInDirection(target);
                } else {
                    // no prey -> decide whether to stay in group
                    if (random.nextFloat() < stayInGroupTendency)
                        moveInDirection(group);
                    else
                        moveRandomly();
                }
            }

        } else {
            // predator has no group
            // -> decide its own move
            Prey target = getTargetPrey();
            if (target != null)
                moveInDirection(target);
            else
                moveRandomly();
        }

        stayInMap();
    }

    /*************************************    ABILITY METHODS    ******************************************************/

    /**
     * The predator reproduce if its gains enough health
     *
     * @return a new Predator near the parent or null
     */
    Predator reproduce() {
        if (health >= reproducingThreshold) {
            health = defaultHealth;
            Predator newBorn = new Predator(new Position(x, y));
            int r = (int) (visionRadius * BORN_RADIUS_RATIO);
            newBorn.x += random.nextInt(-r, r);
            newBorn.y += random.nextInt(-r, r);
            newBorn.stayInMap();
            return newBorn;
        }
        return null;
    }

    /**
     * @return A target prey in predator vision according to its hunting method. Null is return if no preys is found.
     */
    private Prey getTargetPrey() {
        if (huntingMethod == HuntingMethod.DISTANCE && closestPrey != null)
            return closestPrey;
        if (huntingMethod == HuntingMethod.NUTRITION && tastiestPrey != null)
            return tastiestPrey;
        if (huntingMethod == HuntingMethod.DISTANCE_AND_NUTRITION && closestTastiestPrey != null)
            return closestTastiestPrey;
        if (huntingMethod == HuntingMethod.ANY)
            return anyPrey;
        return null;
    }

    boolean canKill(Prey prey) {
        if (distanceTo(prey) > killRadius || getAttack() < prey.getAttack())
            return false;
        if (group == null || group.leader == null)
            return getTargetPrey() == prey;
        return group.leader.getTargetPrey() == prey;
    }

    void kill(Prey prey) {
        prey.dead = true;
        float nutrition = prey.getNutrition();
        map.addFoodGain(nutrition);

        if (group == null)
            health += nutrition;
        else {
            float healthGain = nutrition / group.members.size();
            for (Predator predator : group.members)
                predator.health += healthGain;
        }
    }

    /**
     * Mark predator as dead and remove it from its group
     */
    void die() {
        dead = true;
        health = 0;
        if (group != null)
            group.members.remove(this);
    }

    /*************************************    HUNTING STRATEGY METHODS ENUM    ****************************************/

    /**
     * Enumeration for predator's hunting method
     */
    enum HuntingMethod {
        DISTANCE, NUTRITION, DISTANCE_AND_NUTRITION, ANY;

        static final HuntingMethod DEFAULT = DISTANCE;

        static final String[] methodNames = new String[]{
                "DISTANCE", "NUTRITION", "DISTANCE_AND_NUTRITION", "ANY"
        };

        static HuntingMethod fromString(String string) {
            if (string == null || string.equals(""))
                throw new IllegalArgumentException("Can't convert null string to HuntingMethod enum");

            switch (string.toUpperCase().replaceAll(" ", "_")) {
                case "DISTANCE":
                    return DISTANCE;
                case "NUTRITION":
                    return NUTRITION;
                case "ANY":
                    return ANY;
                default:
                    return DISTANCE_AND_NUTRITION;
            }
        }

        @Override
        public String toString() {
            switch (this) {
                case DISTANCE:
                    return "DISTANCE";
                case NUTRITION:
                    return "NUTRITION";
                case ANY:
                    return "ANY";
                default:
                    return "DISTANCE_AND_NUTRITION";
            }
        }
    }

    static void setHuntingMethod(HuntingMethod huntingMethod) {
        Predator.huntingMethod = huntingMethod;
    }

    /*************************************    GET AND ADDITIONAL METHODS    *******************************************/

    @Override
    float getVisionRadius() {
        return visionRadius;
    }

    @Override
    int getSpeed() {
        return speed;
    }

    float getAttack() {
        return (group == null) ? attack : group.members.size() * attack;
    }

    @Override
    void postDeserialize() {
        color = defaultColor;
    }
}
