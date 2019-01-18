package CooperativeHunting;

import javafx.scene.canvas.GraphicsContext;

/**
 * Abstract base class for group, preys and predators
 */
abstract class Entity {
    static Map map;
    int x;
    int y;

    /**
     * Default constructor
     */
    Entity() {
    }

    /**
     * Entity constructor
     *
     * @param position: initial position of the entity
     */
    Entity(Position position) {
        x = position.x;
        y = position.y;
    }

    /**
     * Entity move an interact with other entities in map
     */
    abstract void update();

    /**
     * Paint entity to the canvas
     *
     * @param graphics:   canvas's graphic context
     * @param showCircle: whether to paint the circle around the entity
     */
    abstract void paint(GraphicsContext graphics, boolean showCircle);

    /**
     * @param entity: any entity object
     * @return distance from this entity to the given entity
     */
    double distanceTo(Entity entity) {
        float dx = x - entity.x;
        float dy = y - entity.y;
        return Math.sqrt(dx * dx + dy * dy);
    }
}
