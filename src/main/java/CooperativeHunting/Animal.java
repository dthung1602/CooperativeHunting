package CooperativeHunting;

abstract class Animal extends Entity {
    boolean dead;

    Animal(Position position) {
        super(position);
        this.dead = false;
    }

}
