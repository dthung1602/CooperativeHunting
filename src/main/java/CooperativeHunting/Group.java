package CooperativeHunting;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

class Group extends Entity {
    private static float groupRadius;
    private static float groupTileDiameterInPixel;
    private static Color color;

    List<Predator> members;
    Predator leader;

    /**
     * Group constructor
     *
     * @param predator1: the initial member of the group
     * @param predator2: the initial member of the group
     */
    private Group(Predator predator1, Predator predator2) {
        members = new LinkedList<>();
        members.add(predator1);
        members.add(predator2);
        predator1.group = predator2.group = this;
    }

    @Override
    void postDeserialize() {

    }

    static Color getColor() {
        return color;
    }

    /**
     * Setter for Predator static fields
     *
     * @param groupRadius: group radius (tiles)
     * @param color:       group circle color
     */
    static void set(float groupRadius, Color color) {
        Group.groupRadius = groupRadius;
        Group.groupTileDiameterInPixel = 2 * groupRadius * map.getTileSize();
        Group.color = color;
    }

    /**
     * Form new groups for lone predators or let them join existing groups
     *
     * @param groups:    list of current groups in map
     * @param predators: list of current predators in map
     */
    static void formNewGroups(List<Group> groups, List<Predator> predators) {

        for (int i = 0; i < predators.size() - 1; i++) { // for every pair of predator
            Predator predator = predators.get(i);

            if (predator.group == null) { // only touch predators that do not belong to any group
                for (int j = i + 1; j < predators.size(); j++) { // for every pair of predator
                    Predator otherPredator = predators.get(j);

                    if (predator.distanceTo(otherPredator) < groupRadius) { // if close enough to form new group
                        if (otherPredator.group == null) { // other predator also has no group
                            Group newGroup = new Group(predator, otherPredator); // -> new group contains both of them
                            groups.add(newGroup);
                        } else { // other predator has already in a group
                            otherPredator.group.members.add(predator); // predator join that group
                            predator.group = otherPredator.group;
                        }
                        break;
                    }
                }
            }
        }
    }

    /**
     * Recalculate the group circle center, check for leaving members and updateMove leader
     */
    void updateMembers() {
        // TODO this is just tmp fix
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
        // recalculate group attack
        attack = 0;

        // remove members out of group radius
        Iterator<Predator> iterator = members.iterator();
        while (iterator.hasNext()) {
            Predator member = iterator.next();
            if (distanceTo(member) > groupRadius) {
                iterator.remove();
                member.group = null;
            } else {
                attack += member.attack;
            }
        }
    }

    /**
     * Select leader for the group
     * The leader is the predator that saw a prey is closest to its prey
     * It no member saw a prey, the group has no leader
     */
    void updateLeader() {
        // reset leader
        leader = null;
        float minPreyDistance = Float.POSITIVE_INFINITY;

        // select leader with the closest prey
        for (Predator member : members) {
            if (member.closestPreyDistance < minPreyDistance) {
                minPreyDistance = member.closestPreyDistance;
                leader = member;
            }
        }
    }

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
}
