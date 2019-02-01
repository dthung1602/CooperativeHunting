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
    float attack;
    transient Color color;

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
    abstract float getVisionRadius();

    /**
     * @return The animal's vision radius
     */
    abstract float getAttack();

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
        float tileSize = map.getTileSize();

        // paint a square for the animal
        graphics.setFill(color);
        graphics.fillRect(
                tileSize * x,
                tileSize * y,
                tileSize * size,
                tileSize * size
        );

        // paint vision circle
        if (showVision) {
            float visionRadius = getVisionRadius();
            float visionDiameterInPixel = visionRadius * 2 * tileSize;

            graphics.setStroke(color);
            graphics.strokeOval(
                    tileSize * (x - visionRadius + size / 2.0),
                    tileSize * (y - visionRadius + size / 2.0),
                    visionDiameterInPixel,
                    visionDiameterInPixel
            );
        }
    }

    /**
     * If the animal is out of map, move it to the map edge
     */
    void stayInMap() {
        x = Math.max(x, 0);
        x = Math.min(x, map.getMapWidth() - 1);
        y = Math.max(y, 0);
        y = Math.min(y, map.getMapHeight() - 1);
    }

    /**
     * Move the animal in the given direction
     * The animal move X tiles horizontally and Y tiles vertically with
     * |X| + |Y| = speed
     * X / Y  = directionX / directionY
     * X, directionX have the same sign
     * Y, directionY have the same sign
     *
     * @param directionX: the horizontal direction
     * @param directionY: the vertical direction
     */
    void moveInDirection(float directionX, float directionY) {
        int speed = this.getSpeed();

        if (directionY == 0 || Float.isInfinite(directionY)) { // move along x-axis only
            if (directionX > 0)
                this.x += Math.min(speed, directionX);
            else
                this.x += Math.max(-speed, directionX);
        } else if (directionX == 0 || Float.isInfinite(directionX)) { // move along y-axis only
            if (directionY > 0)
                this.y += Math.min(speed, directionY);
            else
                this.y += Math.max(-speed, directionY);
        } else {
            float sum = Math.abs(directionX) + Math.abs(directionY);
            float len = Math.min(sum, speed);
            this.x += Math.round(len * directionX / sum);
            this.y += Math.round(len * directionY / sum);
        }
    }

    /**
     * Move the animal to the given entity position
     *
     * @param entity: target to move to
     */
    void moveInDirection(Entity entity) {
        float directionX = entity.x - x;
        float directionY = entity.y - y;
        moveInDirection(directionX, directionY);
    }

    /**
     * Animal moves randomly
     * The animal move X tiles horizontally and Y tiles vertically with
     * |X| + |Y| <= speed
     */
    void moveRandomly() {
        int speed = getSpeed();
        int dx = random.nextInt(-speed, speed);
        int signY = (random.nextInt(2) == 0) ? 1 : -1;
        this.x += dx;
        this.y += random.nextInt(speed - Math.abs(dx) + 1) * signY;
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
