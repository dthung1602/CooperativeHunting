package CooperativeHunting;

import java.awt.*;

abstract class Entity {
    int x;
    int y;

    Entity(){}

    Entity(Position position) {
        x = position.x;
        y = position.y;
    }

    abstract void update(Map map);

    abstract void paint(Graphics graphics);

    double distanceTo(Entity entity) {
        float dx = x - entity.x;
        float dy = y - entity.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    Position getPosition() {
        return new Position(x, y);
    }
}
