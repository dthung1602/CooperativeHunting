package CooperativeHunting;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;

class Group extends Entity {
    private static int groupRadius;
    private static int groupTileDiameter;
    private static Color color;

    ArrayList<Predator> members;
    private Predator leader = null;
    boolean isDead = false;

    private int localPreyDistance = map.getMapWidth(); // TODO map.width
    private int localGroupPreyX = 0;
    private int localGroupPreyY = 0;

    /**
     * Group constructor
     *
     * @param predator1: the initial member of the group
     * @param predator2: the initial member of the group
     */
    Group(Predator predator1, Predator predator2) {
        members = new ArrayList<Predator>();
        members.add(predator1);
        members.add(predator2);
    }

    static Color getColor() {
        return color;
    }

    ArrayList<Predator> getMembers() {
        return members;
    }

    Predator getLeader() {
        return leader;
    }

    static void set(int groupRadius, Color color) {
        Group.groupRadius = groupRadius;
        Group.groupTileDiameter = 2 * groupRadius * map.getTileSize();
        Group.color = color;
    }

    /**
     * Recalculate the group circle center and check for leaving members
     */
    @Override
    void update() {
        resetLeader();
        selectLeader();
        this.circleCenter();
        this.delMember();
    }

    /**
     * Select leader
     */
    void updateLeader() {
        this.resetLeader();
        this.selectLeader();
    }

    /**
     * Calculate the group's circle center and delete member
     */
    void updateCircleAndDeleteMember() {
        this.circleCenter();
        this.delMember();
    }

    /**
     * Add new member to the group
     *
     * @param predator: the new member of the group
     */
    void addMember(Predator predator) {
        members.add(predator);
    }

    /**
     * Delete the ID of the members who left
     */
    private void delMember() {
        int dX = 0, dY = 0;
        ArrayList<Predator> notMembers = new ArrayList<Predator>();
        for (Predator member : members) {
            dX = Math.abs(this.x - member.x);
            dY = Math.abs(this.y - member.y);
            //System.out.println(dX*dX+dY*dY);
            if (dX * dX + dY * dY > groupRadius * groupRadius) {
                notMembers.add(member);
                member.group = null;
            }
        }
        members.removeAll(notMembers);
    }

    /**
     * Calculate the group circle center
     */
    private void circleCenter() {
        this.x = 0;
        this.y = 0;
        //System.out.println("New");
        for (Predator member : members) {
            //System.out.println("M: "+member.X+" "+member.Y+" "+members.size());
            this.x += member.x;
            this.y += member.y;
        }
        x = x / members.size();
        y = y / members.size();
        //System.out.println(X+" "+Y+" "+members.size());
    }

    /**
     * Reset leader to null before new iteration
     */
    private void resetLeader() {
        leader = null;
        localGroupPreyX = 0;
        localGroupPreyY = 0;
        localPreyDistance = 600; // TODO map.width
    }

    /**
     * Select the leader based on the closest prey
     */
    private void selectLeader() {
        for (Predator member : members) {
            if (member.globalDistance < localPreyDistance && member.globalDistanceX != -1) {
                localPreyDistance = member.globalDistance;
                leader = member;
                localGroupPreyX = member.globalDistanceX;
                localGroupPreyY = member.globalDistanceY;
            }
        }
    }

    /**
     * Paint group and its predators to the map
     */
    @Override
    void paint(GraphicsContext graphics, boolean showGroup) {
        int tileSize = map.getTileSize();
        graphics.setStroke(color);
        graphics.strokeOval(
                x * tileSize,
                y * tileSize,
                groupTileDiameter,
                groupTileDiameter
        );
    }
}
