package CooperativeHunting;

import java.util.Random;

abstract class Animal extends Entity {
    static Random random = new Random();

    boolean dead;
    int size;

    Animal(Position position) {
        super(position);
        this.dead = false;
    }

    void stayInMap(Map map) {
        // do not move out of map
        x = Math.max(x, 0);
        x = Math.min(x, map.getMapWidth() - 1);
        y = Math.max(y, 0);
        y = Math.min(y, map.getMapHeight() - 1);
    }
}
