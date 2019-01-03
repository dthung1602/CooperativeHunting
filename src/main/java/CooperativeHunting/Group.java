package CooperativeHunting;

import java.awt.*;
import java.util.ArrayList;

// TODO ADD COMMENTS
class Group extends Entity {
    private static int groupRadius = 100;
    private static Color groupColor = Color.RED;

    private ArrayList<Predator> members;
    private Predator leader;

    // FIXME isDead is assigned but never accessed
    private boolean isDead = false;

    Group(Predator predator) {
        members = new ArrayList<Predator>();
        members.add(predator);
        leader = null;
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

    @Override
    void update(Map map) {
        // update leader first, if exist
        if (leader != null)
            leader.updateAsLeader(map);

        // update members later
        // members follow leader or move on their own if there's no leader
        for (Predator member : members) {
            member.update(map);
            System.out.println(member);
        }
    }

    @Override
    void paint(Graphics graphics) {
        // paint predators
        //leader.paint(graphics);
        for (Predator predator : members)
            predator.paint(graphics);

        // paint group radius
        if (members.size() > 1) {
            graphics.setColor(groupColor);
            graphics.drawOval(this.x - groupRadius, this.y - groupRadius, groupRadius * 2, groupRadius * 2);
        }

    }

    // FIXME what's this for? it's never called
    ArrayList<Group> rearrange(Map map) {
        ArrayList<Group> newGroups;
        circleCenter();
        grouping(map.groups);
        System.out.println("!!");
        circleCenter();
        newGroups = deleteMember(map.groups);
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

    // FIXME parameter ArrayList<Group> groups not used
    private ArrayList<Group> deleteMember(ArrayList<Group> groups) {
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
     * @param groups: FIXME add explanation
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
                        }
                    }
                }
                group.members.addAll(addGroup);
            }
        }
    }

    // FIXME if is not needed -> delete
    static void mergeOrSplitGroups(ArrayList<Group> groups) {
        // merge groups

        // remove dead and out of group members
    }
}
