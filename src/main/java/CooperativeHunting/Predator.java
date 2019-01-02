package CooperativeHunting;

import java.awt.*;


import java.util.Random;

public class Predator extends Animal {
    protected static int visionRadius;

    private static int speed;
    private static int health = 100;
    private static int attack;
    private static Color color = Color.RED;


    /**
     * @param position: initial position of the predator
     */
    Predator(Position position) {
        super(position);
    }

    protected static void set(int sp, int HP, int ATK, Color clr){
        speed = sp;
        health = HP;
        attack = ATK;
        color = clr;
    }

    void update(Map map) {
        scout(map);
        checkDead();
    }

    void updateAsLeader(Map map) {

    }

    private void checkDead() {
        health = health - 1;
        if (health < 1) {
            this.dead = true;
        }
    }

    private void scout(Map map) {
        int gdX = 0, gdY = 0, gDistance = 600;
        int templateDistance;
        for (Prey prey : map.preys) {
            templateDistance = (int) distanceTo(prey);
            if (templateDistance <= this.visionRadius && templateDistance < gDistance) {
                gDistance = templateDistance;
                gdX = prey.x - this.x;
                gdY = prey.y - this.y;
            }
        }
        float ratio;
        Random random = new Random();
        if (gdX != 0) {
            try {
                System.out.println("T");
                ratio = Math.abs((float) gdX / (float) gdY);
                this.y += Math.round(speed / (ratio + 1)) * (gdY / Math.abs(gdY));
                this.x += Math.round(speed / (1 / ratio + 1)) * (gdX / Math.abs(gdX));
            } catch (Exception e) {
                if (gdX > 0) {
                    this.x += speed;
                } else {
                    this.x += -speed;
                }
            }
        } else {
            System.out.println("R");
            this.x += random.nextInt(speed) + -speed / 4;
            this.y += random.nextInt(speed) + -speed / 4;
        }
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

    void paint(Graphics graphics) {
        graphics.setColor(color);
        graphics.fillRect(x - size / 2, y - size / 2, size, size);
        graphics.setColor(Color.PINK);
        graphics.drawOval(x - visionRadius, y - visionRadius,  visionRadius * 2,  visionRadius * 2);
    }

}
