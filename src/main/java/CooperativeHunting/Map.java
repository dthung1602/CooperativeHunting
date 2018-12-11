package CooperativeHunting;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

class Map extends JPanel {
    ArrayList<Prey> preys;
    ArrayList<Group> groups;

    Map() {
        preys = new ArrayList<Prey>();
        groups = new ArrayList<Group>();
    }

    void update() {
        Prey.removeDeadPreys(preys);
        Group.mergeOrSplitGroups(groups);

        for (Group group : groups)
            group.update(this);

        for (Prey prey : preys)
            prey.update(this);
    }

    public void paintComponent(Graphics g) {
        clearScreen(g);

        for (Group group : groups)
            group.paint(g);

        for (Prey prey : preys)
            prey.paint(g);
    }


    private void clearScreen(Graphics g) {
        g.clearRect(0, 0, this.getWidth(), this.getHeight());
    }
}