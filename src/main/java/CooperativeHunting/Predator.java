package CooperativeHunting;

import javafx.scene.paint.Color;

class Predator extends Animal {
    private static float visionRadius;
    private static int speed;
    private static int defaultHealth;
    private static int defaultAttack;
    private static float stayInGroupTendency;
    private static Color defaultColor;
    private static int reproducingRate;
    static HuntingMethod huntingMethod = HuntingMethod.DEFAULT;

    private int health;
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
    static void set(int speed, int defaultHealth, int defaultAttack, float visionRadius,
                    float stayInGroupTendency, HuntingMethod huntingMethod, Color defaultColor) {
        Predator.speed = speed;
        Predator.defaultHealth = defaultHealth;
        Predator.defaultAttack = defaultAttack;
        Predator.visionRadius = visionRadius;
        Predator.stayInGroupTendency = stayInGroupTendency;
        Predator.huntingMethod = huntingMethod;
        Predator.defaultColor = defaultColor;
        Predator.reproducingRate = defaultHealth + (int)Prey.defaultNutrition;
    }

/************************************************UPDATING METHODS************************************************************************************/

    /**
     * The predator looks for the closest prey inside the predator's vision
     * Check for the health of the predator whether the predator is dead or not
     */
    void updateScout() {
        // update health first
        health--;
        if (health < 1) {
            die();
            return;
        }

        // find closest prey
        //closestPreyDistanceX = Integer.MAX_VALUE;
        //closestPreyDistanceY = Integer.MAX_VALUE;
        closestPreyDistance = Float.POSITIVE_INFINITY;
        tastiestPreyNutrition = 0;
        bestCombination = 0;
        float groupAttack = getAttack();

        for (Prey prey : map.getPreys()) {
            float distance = distanceTo(prey);
            float nutrition = prey.getNutrition();
            float combination = nutrition - distance;

            if (distance <= visionRadius // prey in vision range
                    && prey.getAttack() < groupAttack) { // prey must be weaker than group
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

/************************************************ABILITY METHODS************************************************************************************/

    /**
     * Generate new predators
     *
     * @return signal to generate
     */
    boolean reproduce(){
        if(this.health >= reproducingRate){
            health = defaultHealth;
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * @return A target prey in predator vision according to its hunting method. Null is return if no preys is found.
     */
    private Prey getTargetPrey() {
        if (huntingMethod == HuntingMethod.DISTANCE && closestPreyDistance < Float.POSITIVE_INFINITY)
            return closestPrey;
        if (huntingMethod == HuntingMethod.NUTRITION && tastiestPreyNutrition > 0)
            return tastiestPrey;
        if (huntingMethod == HuntingMethod.DISTANCE_AND_NUTRITION && bestCombination > 0)
            return closestTastiestPrey;
        if (huntingMethod == HuntingMethod.ANY)
            return anyPrey;
        return null;
    }

    /**
     * Attacks Preys in range
     */
    boolean attack() {
        int atk = getAttack(); // total attack of the group

        for (Prey prey : map.getPreys()) {
            // if prey not dead, in range and weaker
            if (!prey.dead && distanceTo(prey) <= visionRadius && prey.getAttack() <= atk) {
                // get killed
                prey.dead = true;

                // increase health for predators in the same group
                float nutrition = prey.getNutrition();
                map.addFoodGain(nutrition);
                if (group == null) {
                    health += (int) nutrition;
                } else {
                    int healthGain = (int) (nutrition / group.members.size());
                    for (Predator predator : group.members)
                        predator.health += healthGain;
                }
                return reproduce();
            }
        }
        return false;
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

/************************************************HUNTING STRATEGY METHODS**************************************************************************/

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

/************************************************GET AND ADDITIONAL METHODS*********************************************************************/

    @Override
    float getVisionRadius() {
        return visionRadius;
    }

    @Override
    int getSpeed() {
        return speed;
    }

    int getAttack() {
        return (group == null) ? attack : group.attack;
    }

    @Override
    void postDeserialize() {
        color = defaultColor;
    }

}
