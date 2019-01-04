package CooperativeHunting;

import java.awt.*;
import java.util.Random;

// TODO ADD COMMENTS
class Predator extends Animal {
    static int visionRadius;

    private static int speed;
    private static int health = 100;
    private static int attack;
    private static Color color = Color.RED;

    /**
     * Predator constructor
     *
     * @param position: initial position of the predator
     */
    Predator(Position position) {
        super(position);
    }

    // FIXME camelCase, write full name, add visionRadius
    static void set(int sp, int HP, int ATK, Color clr) {
        speed = sp;
        health = HP;
        attack = ATK;
        color = clr;
    }

    // TODO overridden methods should be marked with @Override decorator
    void update(Map map) {
        scout(map);
        checkDead();
    }

    // TODO overridden methods should be marked with @Override decorator
    void paint(Graphics graphics) {
        graphics.setColor(color);
        graphics.fillRect(x - size / 2, y - size / 2, size, size);
        graphics.setColor(Color.PINK);
        graphics.drawOval(x - visionRadius, y - visionRadius, visionRadius * 2, visionRadius * 2);
    }

    // FIXME not used -> delete
    void updateAsLeader(Map map) {

    }

    private void checkDead() {
        health--;
        this.dead = (health < 1);
    }

    private void scout(Map map) {
        int gdX = 0;
        int gdY = 0;
        int gDistance = 600;

        for (Prey prey : map.getPreys()) {
            int templateDistance = (int) distanceTo(prey);
            if (templateDistance <= visionRadius && templateDistance < gDistance) {
                gDistance = templateDistance;
                gdX = prey.x - this.x;
                gdY = prey.y - this.y;
            }
        }

        float ratio;
        Random random = new Random();
        if (gdX != 0) {
            try {
                System.out.println("T"); // TODO remove debug prints
                ratio = Math.abs((float) gdX / (float) gdY);
                this.y += Math.round(speed / (ratio + 1)) * (gdY / Math.abs(gdY));
                this.x += Math.round(speed / (1 / ratio + 1)) * (gdX / Math.abs(gdX));
            } catch (Exception e) { // FIXME Exception too general -> catch division by zero
                if (gdX > 0) {
                    this.x += speed;
                } else {
                    this.x += -speed;
                }
            }
        } else {
            System.out.println("R"); // TODO remove debug prints
            this.x += random.nextInt(speed) + -speed / 4;
            this.y += random.nextInt(speed) + -speed / 4;
        }

        // TODO change 3, 597 to map.height and map.width
        if (this.x < 3) {
            this.x = 3;
        }
        if (this.x > 597) {
            this.x = 597;
        }
        if (this.y < 3) {
            this.y = 3;
        }
        if (this.y > 597) {
            this.y = 597;
        }
    }
}
