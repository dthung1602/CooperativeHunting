package CooperativeHunting;

import javafx.scene.canvas.GraphicsContext;

abstract class Entity {
    static Map map;
    int x;
    int y;

    Entity() {
    }

    Entity(Position position) {
        x = position.x;
        y = position.y;
    }

    abstract void update();

    abstract void paint(GraphicsContext graphics);

    double distanceTo(Entity entity) {
        float dx = x - entity.x;
        float dy = y - entity.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    Position getPosition() {
        return new Position(x, y);
    }
}
