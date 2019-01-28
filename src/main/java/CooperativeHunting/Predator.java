package CooperativeHunting;

import javafx.scene.paint.Color;

class Predator extends Animal {
    private static float visionRadius;
    private static int speed;
    private static int defaultHealth;
    private static int defaultAttack;
    private static float stayInGroupTendency;
    private static Color defaultColor;

    private int health;
    Group group;

    // hold the distance and direction to the prey for the predator
    private int closestPreyDistanceX;
    private int closestPreyDistanceY;
    float closestPreyDistance;

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

    /**
     * Setter for Predator static fields
     *
     * @param speed:         predators' speed point (tiles/iteration)
     * @param defaultHealth: predators' initial health point
     * @param defaultAttack: predators' attack point
     * @param visionRadius:  predators' vision radius (tiles)
     * @param defaultColor:  predators' color for visualization
     */
    static void set(int speed, int defaultHealth, int defaultAttack, float visionRadius, float stayInGroupTendency, Color defaultColor) {
        Predator.speed = speed;
        Predator.defaultHealth = defaultHealth;
        Predator.defaultAttack = defaultAttack;
        Predator.visionRadius = visionRadius;
        Predator.stayInGroupTendency = stayInGroupTendency;
        Predator.defaultColor = defaultColor;
    }

    @Override
    void postDeserialize() {
        color = defaultColor;
    }

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
        closestPreyDistanceX = Integer.MAX_VALUE;
        closestPreyDistanceY = Integer.MAX_VALUE;
        closestPreyDistance = Float.POSITIVE_INFINITY;
        float groupAttack = getAttack();

        for (Prey prey : map.getPreys()) {
            float distance = distanceTo(prey);
            if (distance <= visionRadius // prey in vision range
                    && distance < closestPreyDistance // prey is the closest
                    && prey.getAttack() <= groupAttack) { // prey must be weaker than group
                closestPreyDistance = distance;
                closestPreyDistanceX = prey.x - this.x;
                closestPreyDistanceY = prey.y - this.y;
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
                    moveInDirection(closestPreyDistanceX, closestPreyDistanceY);
                else
                    // not leader -> follow leader
                    moveInDirection(group.leader.x - x, group.leader.y - y);

            } else { // no leader
                if (closestPreyDistance < Float.POSITIVE_INFINITY)
                    // a prey is found -> move to that prey
                    moveInDirection(closestPreyDistanceX, closestPreyDistanceY);
                else {
                    // no prey -> decide whether to stay in group
                    boolean stay = random.nextFloat() < stayInGroupTendency;
                    if (stay)
                        moveInDirection(group.x - x, group.y - y);
                    else
                        moveRandomly();
                }
            }

        } else {
            // predator has no group
            // -> decide its own move

            if (closestPreyDistance < Float.POSITIVE_INFINITY)
                // a prey is found -> move to that prey
                moveInDirection(closestPreyDistanceX, closestPreyDistanceY);
            else
                // no prey -> move randomly
                moveRandomly();
        }

        stayInMap();
    }

    /**
     * Attacks Preys in range
     */
    void attack() {
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

                break; // TODO attack multiple preys?
            }
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
}
