package CooperativeHunting;

import java.awt.*;
import java.util.ArrayList;

class Group extends Entity {
    private ArrayList<Predator> members;
    private Predator leader;

    static float groupRadius;

    Group() {
        members = new ArrayList<Predator>();
        leader = null;
    }

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

    static void mergeOrSplitGroups(ArrayList<Group> groups) {
        // merge groups

        // remove dead and out of group members
    }

    @Override
    void paint(Graphics graphics) {
        // paint predators
        leader.paint(graphics);
        for (Predator predator : members)
            predator.paint(graphics);

        // paint group radius
    }
}
