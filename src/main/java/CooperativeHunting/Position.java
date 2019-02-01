package CooperativeHunting;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

class Position {
    private static final Random random = new MyRandom();
    int x;
    int y;

    Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int hashCode() {
        return x * 600 + y;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Position) {
            Position p = (Position) o;
            return x == p.x && y == p.y;
        }
        return false;
    }

    /**
     * Generate a list of unused positions in the map
     *
     * @param number:    number of positions to generate
     * @param mapWidth:  map width
     * @param mapHeight: map height
     * @return a list of unused positions
     */
    static ArrayList<Position> getRandomPositions(int number, int mapWidth, int mapHeight) {
        HashSet<Position> usedPositions = new HashSet<>();
        ArrayList<Position> newPositions = new ArrayList<>(number);

        for (int i = 0; i < number; i++) {
            Position position;
            int x, y;
            do {
                x = random.nextInt(mapWidth);
                y = random.nextInt(mapHeight);
                position = new Position(x, y);
            } while (usedPositions.contains(position));
            newPositions.add(position);
            usedPositions.add(position);
        }

        return newPositions;
    }
}
