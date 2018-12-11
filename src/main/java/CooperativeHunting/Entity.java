package CooperativeHunting;

import java.awt.*;

abstract class Entity {
    protected int x;
    protected int y;

    abstract void update(Map map);

    abstract void paint(Graphics graphics);

    double distanceTo(Entity entity) {
        float dx = x - entity.x;
        float dy = y - entity.y;
        return Math.sqrt(dx * dx + dy * dy);
    }
}
