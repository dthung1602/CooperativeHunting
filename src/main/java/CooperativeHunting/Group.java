package CooperativeHunting;

import java.awt.*;
import java.util.ArrayList;

class Group extends Entity {
    private ArrayList<Predator> members;
    private Predator leader;
    private static int groupRadius = 100;
    private boolean isDead = false;

    Group(Predator predator) {
        members = new ArrayList<Predator>();
        members.add(predator);
        leader = null;
    }

    static void setGroupRadius(int groupRadius) {
        Group.groupRadius = groupRadius;
    }

    @Override
    void update(Map map) {
        // update leader first, if exist
        if (leader != null)
            leader.updateAsLeader(map);

        // update members later
        // members follow leader or move on their own if there's no leader
        System.out.println(this + " " + this.members.size());
        for (Predator member : members) {
            member.update(map);
            System.out.println(member);
        }
    }

    ArrayList<Group> rearrange(Map map) {
        ArrayList<Group> newGroups;
        circleCenter();
        grouping(map.groups);
        System.out.println("!!");
        circleCenter();
        newGroups = delMember(map.groups);
        return newGroups;
    }

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

    private ArrayList<Group> delMember(ArrayList<Group> groups) {
        ArrayList<Predator> notMembers = new ArrayList<Predator>();
        int distance;
        ArrayList<Group> newGroups = new ArrayList<Group>();
        for (Predator member : members) {
            distance = (int) this.distanceTo(member);
            System.out.println(distance + "delete");
            if (distance > this.groupRadius) {
                notMembers.add(member);
                newGroups.add(new Group(member));
            }
        }
        members.removeAll(notMembers);
        return newGroups;
    }

    private void grouping(ArrayList<Group> groups) { //SELECT ALONE PREDATOR TO COMPARE WITH GROUPS
        int distance;
        //ArrayList<Group> delGroups = new ArrayList<>();
        ArrayList<Predator> addGroup = new ArrayList<Predator>();
        if (this.members.size() < 2) {
            for (Group group : groups) {
                if (this != group) {
                    for (Predator member : group.members) {
                        distance = (int) distanceTo(member);
                        if (distance < member.visionRadius) {
                            for (Predator predator : this.members) {
                                addGroup.add(predator);
                            }
                            this.isDead = true;
                        }
                    }
                }
                group.members.addAll(addGroup);
            }
        }
    }

    ArrayList<Predator> getMembers() {
        return members;
    }

    Predator getLeader() {
        return leader;
    }

    static void mergeOrSplitGroups(ArrayList<Group> groups) {
        // merge groups

        // remove dead and out of group members
    }

    @Override
    void paint(Graphics graphics) {
        // paint predators
        //leader.paint(graphics);
        for (Predator predator : members)
            predator.paint(graphics);

        // paint group radius
        if (members.size() > 1) {
            graphics.setColor(Color.RED);
            graphics.drawOval(this.x - groupRadius, this.y - groupRadius, groupRadius * 2, groupRadius * 2);
        }

    }

}
