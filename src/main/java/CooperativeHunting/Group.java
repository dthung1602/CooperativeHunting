package CooperativeHunting;

import java.awt.*;
import java.util.ArrayList;

class Group extends Entity {
    private ArrayList<Predator> members;
    private Predator leader;

    private static int groupRadius;

    /**
     * Create a group with a single predator
     *
     * @param position: predator's initial position
     */
    Group(Position position) {
        super(position);
        members = new ArrayList<Predator>();
        leader = new Predator(position, this);
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
     * Predator group moves and interacts with preys
     *
     * @param map: Map object
     */
    @Override
    void update(Map map) {
        // update leader first, if exist
        if (leader != null)
            leader.updateAsLeader(map);

        // update members later
        // members follow leader or move on their own if there's no leader
        for (Predator member : members)
            member.update(map);

    }

    /**
     * Paint members and group to the map
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
    }

    /**
     * Merge close by predators's groups, split predators out of group radius to their own group
     *
     * @param groups: list of group to manipulate
     */
    static void mergeOrSplitGroups(ArrayList<Group> groups) {
        // merge groups

        // remove dead and out of group members
    }
}
