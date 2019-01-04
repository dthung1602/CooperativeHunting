package CooperativeHunting;

import java.awt.*;
import java.util.ArrayList;

class Group extends Entity {
    private static int groupRadius = 100;
    private static Color groupColor = Color.RED;

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
        members.add(predator1);
        members.add(predator2);
    }

    ArrayList<Predator> getMembers() {
        return members;
    }

    Predator getLeader() {
        return leader;
    }

    static void setGroupRadius(int groupRadius) {
        Group.groupRadius = groupRadius;
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
    void updateLeader(){
        this.resetLeader();
        this.selectLeader();
    }

    /**
     * Calculate the group's circle center and delete member
     */
    void updateCicleAndDeleteMember(){
        this.circleCenter();
        this.delMember();
    }

    /**
     * Add new member to the group
     *
     * @param predator: the new member of the group
     */
    public void addMember(Predator predator){
        members.add(predator);
    }

    /**
     * Delete the ID of the members who left
     */
    public void delMember(){
        int dX=0, dY=0;
        ArrayList<Predator> notMembers = new ArrayList<Predator>();
        for(Predator member : members){
            dX = Math.abs(this.x - member.x);
            dY = Math.abs(this.y - member.y);
            //System.out.println(dX*dX+dY*dY);
            if(dX*dX+dY*dY > groupRadius *groupRadius){
                notMembers.add(member);
                member.group = null;
            }
        }
        members.removeAll(notMembers);
        System.out.println("S: "+notMembers.size());
    }

    /**
     * Calculate the group circle center
     */
    public void circleCenter(){
        this.x = 0;
        this.y = 0;
        //System.out.println("New");
        for (Predator member : members) {
            //System.out.println("M: "+member.X+" "+member.Y+" "+members.size());
            this.x += member.x;
            this.y += member.y;
        }
        x = x/members.size();
        y = y/members.size();
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
        for(Predator member : members) {
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
     *
     * @param graphics: Graphic object
     */
    @Override
    void paint(Graphics graphics) {
        // paint predators
        leader.paint(graphics);
        for (Predator predator : members)
            predator.paint(graphics);

        // paint group radius
        if (members.size() > 1) {
            graphics.setColor(groupColor);
            graphics.drawOval(this.x - groupRadius, this.y - groupRadius, groupRadius * 2, groupRadius * 2);
        }

    }
}
