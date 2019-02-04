package CooperativeHunting;

import CooperativeHunting.Predator.HuntingMethod;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * The class represent predator's hunting group
 */
class Group extends Entity {
    private static float groupRadius;
    private static float groupTileDiameterInPixel;
    private static Color color;

    List<Predator> members;
    Predator leader;

    /**
     * Group constructor
     *
     * @param members the initial members of the group
     */
    private Group(List<Predator> members) {
        this.members = members;
        for (Predator member : members)
            member.group = this;
        calculateCenter();
    }

    /*************************************    CREATE GROUP METHODS    *************************************************/

    /**
     * Form new groups for lone predators or let them join existing groups
     *
     * @param groups    list of current groups in map
     * @param predators list of current predators in map
     */
    static void createNewGroup(List<Group> groups, List<Predator> predators) {

        for (int i = 0; i < predators.size() - 1; i++) { // for every predator
            Predator predator = predators.get(i);

            if (predator.group == null) { // only touch predators that do not belong to any group
                List<Predator> members = new LinkedList<>();
                Predator predatorInAnotherGroup = null;

                for (int j = i + 1; j < predators.size(); j++) { // for every pair of predator
                    Predator otherPredator = predators.get(j);

                    if (predator.distanceTo(otherPredator) < groupRadius) {// if close enough to form new group
                        if (otherPredator.group == null) // other predator has no group -> create a new one
                            members.add(otherPredator);
                        else // other predator has got a group -> join that group
                            predatorInAnotherGroup = otherPredator;
                    }
                }

                if (!members.isEmpty()) { // try to form new group first
                    members.add(predator);
                    Group newGroup = new Group(members);
                    groups.add(newGroup);
                } else if (predatorInAnotherGroup != null) { // cant't form new group -> try to join another group
                    predatorInAnotherGroup.group.members.add(predator);
                    predator.group = predatorInAnotherGroup.group;
                }
            }
        }
    }

    /*************************************    ORGANIZING METHODS    ***************************************************/

    /**
     * Recalculate the group circle center, check for leaving members and updateMove leader
     */
    void updateMembers() {
        if (members.size() == 0) return;
        calculateCenter();
        deleteMembers();
    }

    /**
     * Calculate the group circle center
     */
    private void calculateCenter() {
        x = 0;
        y = 0;
        for (Predator member : members) {
            x += member.x;
            y += member.y;
        }
        x = Math.round((float) x / members.size());
        y = Math.round((float) y / members.size());
    }

    /**
     * Remove members that moved out of the group radius
     */
    private void deleteMembers() {
        // remove members out of group radius
        Iterator<Predator> iterator = members.iterator();
        while (iterator.hasNext()) {
            Predator member = iterator.next();
            if (distanceTo(member) > groupRadius) {
                iterator.remove();
                member.group = null;
            }
        }
    }

    /*************************************    LEADER METHODS    *******************************************************/

    /**
     * Select leader for the group
     * The leader is the predator that saw a prey is closest to its prey
     * It no member saw a prey, the group has no leader
     */
    void updateLeader() {
        // reset leader
        leader = null;
        float minPreyDistance = Float.POSITIVE_INFINITY;
        float maxNutrition = 0;
        float bestCombination = 0;

        // select leader according to hunting method
        for (Predator member : members) {
            if (Predator.huntingMethod == HuntingMethod.DISTANCE && member.closestPreyDistance < minPreyDistance) {
                minPreyDistance = member.closestPreyDistance;
                leader = member;
            } else if (Predator.huntingMethod == HuntingMethod.NUTRITION && member.tastiestPreyNutrition > maxNutrition) {
                maxNutrition = member.tastiestPreyNutrition;
                leader = member;
            } else if (Predator.huntingMethod == HuntingMethod.DISTANCE_AND_NUTRITION && member.bestCombination > bestCombination) {
                bestCombination = member.bestCombination;
                leader = member;
            } else if (Predator.huntingMethod == HuntingMethod.ANY) {
                leader = member;
                break;
            }
        }
    }


    /*************************************    ADDITIONAL PAINTING METHODS    ******************************************/

    /**
     * Setter for Predator static fields
     *
     * @param groupRadius group radius (tiles)
     * @param color       group circle color
     */
    static void set(float groupRadius, Color color) {
        Group.groupRadius = groupRadius;
        Group.groupTileDiameterInPixel = 2 * groupRadius * map.getTileSize();
        Group.color = color;
    }

    static Color getColor() {
        return color;
    }

    /*************************************    PAINTING METHODS    *****************************************************/

    /**
     * Paint group and its predators to the map
     */
    @Override
    void paint(GraphicsContext graphics, boolean showGroup) {
        if (members.size() < 2) return;
        float tileSize = map.getTileSize();
        graphics.setStroke(color);
        graphics.strokeOval(
                (x - groupRadius + 0.5) * tileSize,
                (y - groupRadius + 0.5) * tileSize,
                groupTileDiameterInPixel,
                groupTileDiameterInPixel
        );
    }

    @Override
    void postDeserialize() {

    }
}
