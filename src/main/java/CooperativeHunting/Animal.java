package CooperativeHunting;

abstract class Animal extends Entity {
    boolean dead;
    int size;

    Animal(Position position) {
        super(position);
        this.dead = false;
    }
}
