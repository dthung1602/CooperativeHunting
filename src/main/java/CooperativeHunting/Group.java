package CooperativeHunting;

import java.awt.*;
import java.util.ArrayList;

class Group extends Entity {
    private static int groupRadius = 100;
    private static Color groupColor = Color.RED;

    ArrayList<Predator> members;
    private Predator leader;
    boolean isDead = false;

    private int localPreyDistance = 600; // TODO map.width
    private int localGroupPreyX = 0; // FIXME not used
    private int localGroupPreyY = 0; // FIXME not used

    /**
     * Group constructor
     *
     * @param predator: the initial member of the group
     */
    Group(Predator predator) {
        members = new ArrayList<Predator>();
        members.add(predator);
        leader = null;
        predator.group = this;
    }

    ArrayList<Predator> getMembers() {
        return members;
    }

    Predator getLeader() {
        return leader;
    }

    static void setGroupRadius(int groupRadius) {
        Group.groupRadius = groupRadius;
    }

    /**
     * TODO add comment
     *
     * @param map: Map object
     */
    @Override
    void update(Map map) {
        resetLeader();
        for (Predator member : members)
            member.update(map);
    }

    /**
     * Recalculate the group members after all the predators have moved in a iteration
     *
     * @param map: Map object
     * @return TODO return what?
     */
    ArrayList<Group> rearrange(Map map) {
        circleCenter();
        grouping(map.groups);
        circleCenter();
        return deleteMember();
    }

    /**
     * Paint group and its predators to the map
     *
     * @param graphics: Graphic object
     */
    @Override
    void paint(Graphics graphics) {
        // paint predators
        leader.paint(graphics);
        for (Predator predator : members)
            predator.paint(graphics);

        // paint group radius
        if (members.size() > 1) {
            graphics.setColor(groupColor);
            graphics.drawOval(this.x - groupRadius, this.y - groupRadius, groupRadius * 2, groupRadius * 2);
        }

    }

    // TODO add comments for all below methods
    // TODO comment in code for all below methods
    private void circleCenter() {
        x = 0;
        y = 0;
        for (Predator member : members) {
            x += member.x;
            y += member.y;
        }
        x = x / members.size();
        y = y / members.size();
    }

    private ArrayList<Group> deleteMember() {
        ArrayList<Predator> notMembers = new ArrayList<Predator>();
        ArrayList<Group> newGroups = new ArrayList<Group>();

        for (Predator member : members) {
            int distance = (int) this.distanceTo(member);
            if (distance > groupRadius) {
                notMembers.add(member);
                newGroups.add(new Group(member));
            }
        }

        members.removeAll(notMembers);
        return newGroups;
    }

    /**
     * Select alone predator to compare with group
     *
     * @param groups: the list of the predators' groups which are existing on the map
     */
    private void grouping(ArrayList<Group> groups) {
        ArrayList<Predator> addGroup = new ArrayList<Predator>();

        if (this.members.size() < 2) {
            for (Group group : groups) {
                if (this != group) {
                    for (Predator member : group.members) {
                        int distance = (int) distanceTo(member);
                        if (distance < Predator.visionRadius) {
                            addGroup.addAll(this.members);
                            this.isDead = true;
                            for (Predator thisMember : this.members) {
                                thisMember.group = group;
                            }
                        }
                    }
                }
                group.members.addAll(addGroup);
            }
        }
    }

    private void resetLeader() {
        leader = null;
        localGroupPreyX = 0;
        localGroupPreyY = 0;
        localPreyDistance = 600; // TODO map.width
    }

    void selectLeader(Predator nextLeader, int preyDistance, int groupPreyX, int groupPreyY) {
        if (preyDistance < localPreyDistance && groupPreyX != -1) {
            localPreyDistance = preyDistance;
            leader = nextLeader;
            localGroupPreyX = groupPreyX;
            localGroupPreyY = groupPreyY;
        }
    }
}
