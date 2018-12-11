package CooperativeHunting;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

class Prey extends Animal {
    void update(Map map) {

    }

    void paint(Graphics graphics) {

    }

    static void removeDeadPreys(ArrayList<Prey> preys) {
        for (Iterator<Prey> iterator = preys.iterator(); iterator.hasNext(); ) {
            Prey prey = iterator.next();
            if (prey.dead)
                iterator.remove();
        }
    }
}
