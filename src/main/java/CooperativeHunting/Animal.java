package CooperativeHunting;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Random;

/**
 * Abstract base class for preys and predators
 */
abstract class Animal extends Entity {
    static MyRandom random = new MyRandom();

    boolean dead;
    int size;
    int attack;
    Color color;

    /**
     * Animal constructor
     *
     * @param position: initial position of the entity
     */
    Animal(Position position) {
        super(position);
        this.dead = false;
    }

    /**
     * @return The animal's vision radius
     */
    abstract int getVisionRadius();

    /**
     * @return The animal's speed
     */
    abstract int getSpeed();

    /**
     * Paint the animal to the map
     *
     * @param graphics:   canvas graphic context
     * @param showVision: whether to paint the vision circle
     */
    void paint(GraphicsContext graphics, boolean showVision) {
        // paint a square for the animal
        graphics.setFill(color);
        graphics.fillRect(
                map.tiles * x,
                map.tiles * y,
                map.tiles * size,
                map.tiles * size
        );

        // paint vision circle
        if (showVision) {
            int visionRadius = getVisionRadius();
            int visionDiameter = visionRadius * 2;

            graphics.setStroke(color);
            graphics.strokeOval(
                    map.tiles * (x - visionRadius + size / 2.0),
                    map.tiles * (y - visionRadius + size / 2.0),
                    map.tiles * visionDiameter,
                    map.tiles * visionDiameter
            );
        }
    }

    /**
     * If the animal is out of map, move it to the map edge
     *
     * @param map: Map object to check the boundary
     */
    void stayInMap(Map map) {
        x = Math.max(x, 0);
        x = Math.min(x, map.getMapWidth() - 1);
        y = Math.max(y, 0);
        y = Math.min(y, map.getMapHeight() - 1);
    }

    /**
     * Animal moves randomly
     */
    void moveRandomly() {
        int step = random.nextInt(getSpeed() + 1);
        switch (random.nextInt(4)) {
            case 0:
                x += step;
                break;
            case 1:
                x -= step;
                break;
            case 2:
                y += step;
                break;
            default:
                y -= step;
        }
    }
}

/**
 * Extension to java default random class
 */
class MyRandom extends Random {
    /**
     * @param min: lower bound, inclusive
     * @param max: upper bound, inclusive
     * @return an random integer in [min, max]
     */
    int nextInt(int min, int max) {
        return min + this.nextInt(max + 1 - min);
    }
}
