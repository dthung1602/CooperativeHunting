package CooperativeHunting;


import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


class Prey extends Animal {
    // TODO set vision radius?
    private static int visionRadius = 5;
    private static int speed;
    private static float nutrition;
    private static int attack;
    static Color color;

    /**
     * @param position: initial position of the prey
     */
    Prey(Position position) {
        super(position);
    }

    /**
     * Set numerous settings for preys
     *
     * @param speed:     preys' speed
     * @param nutrition: preys' nutrition value
     * @param attack:    preys' attack
     * @param color:     preys' color for visualization
     */
    static void set(int speed, float nutrition, int attack, Color color) {
        Prey.speed = speed;
        Prey.nutrition = nutrition;
        Prey.attack = attack;
        Prey.color = color;
    }

    /**
     * Prey moves and interacts with predators
     *
     * @param map: Map object
     */
    @Override
    void update(Map map) {
        // get the sum of vectors predator -> prey
        int sumX = 0;
        int sumY = 0;
        for (Predator predator : getPredatorsInVision(map)) {
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
                x += speed;
        } else {
            if (sumX > 0)
                y -= speed;
            else
                y += speed;
        }

        // do not move out of map
        x = Math.min(x, 0);
        x = Math.max(x, map.getMapWidth() - 1);
        y = Math.min(y, 0);
        y = Math.max(y, map.getMapHeight() - 1);
    }

    /**
     * Paint prey to the map
     *
     * @param graphics: Graphic object
     */
    @Override
    void paint(Graphics graphics) {
        graphics.drawRect(x, y, 600 / Map.mapWidth, 600 / Map.mapHeight);
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
     * Get a list of predators in prey's vision
     *
     * @param map: Map object
     * @return list of predators in prey's vision
     */
    private List<Predator> getPredatorsInVision(Map map) {
        ArrayList<Predator> predatorsInRange = new ArrayList<Predator>();

        // loop through every group
        for (Group group : map.getGroups()) {
            // loop through group's members
            for (Predator predator : group.getMembers())
                if (inVision(predator))
                    predatorsInRange.add(predator);
            // check group's leader
            Predator leader = group.getLeader();
            if (inVision(leader))
                predatorsInRange.add(leader);
        }

        return predatorsInRange;
    }

    /**
     * Check whether an entity in prey's vision
     *
     * @param entity: Entity to check
     * @return true / false
     */
    private boolean inVision(Entity entity) {
        return distanceTo(entity) <= visionRadius;
    }
}
