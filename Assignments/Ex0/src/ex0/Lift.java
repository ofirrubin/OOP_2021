package ex0;

import ex0.algo.ElevatorAlgo;

import java.util.ArrayList;
import java.util.Arrays;

class CallBundler{
    public CallForElevator c;
    public int locateTo;
    public double time;
    public CallBundler(CallForElevator c, int locateTo, double time){
        this.c = c;
        this.locateTo = locateTo;
        this.time = time;
    }
}

public class Lift implements ElevatorAlgo {
    private Building building;
    ArrayList<CallBundler> listCalls;
    int[] time2Idle;
    public Lift(Building building){
        this.building = building;
        listCalls = new ArrayList<>();
        time2Idle = new int[this.building.numberOfElevetors()];
        Arrays.fill(time2Idle, 0);
    }
    /**
     * @return the Building on which the (online) elevator algorithm works on.
     */
    @Override
    public Building getBuilding() {
        return this.building;
    }

    /**
     * @return The algorithm name
     */
    @Override
    public String algoName() {
        return "Lift algorithm";
    }

    /**
     * This method is the main optimal allocation (aka load-balancing) algorithm for allocating the
     * "best" elevator for a call (over all the elevators in the building).
     *
     * @param c the call for elevator (src, dest)
     * @return the index of the elevator to which this call was allocated to.
     */
    @Override
    public int allocateAnElevator(CallForElevator c) {
        int maxElv = building.numberOfElevetors();
        if (maxElv == 0) return 0;
        int source = c.getSrc();
        int dest = c.getDest();
        double travelTime; // Times takes for the elevator to arrive to the destination from its current point.
        double elevatorTime = Integer.MAX_VALUE;
        int temp;
        int ste = 0; // Shortest time elevator
        for(int i=0; i < maxElv; i ++){
            Elevator e = building.getElevetor(i);
            if (e.getState() == Elevator.ERROR) continue;
            travelTime = e.getStopTime() + e.getTimeForOpen() + e.getStartTime() + e.getStopTime() + e.getSpeed() * Math.abs(source - dest);
            if (e.getState() == Elevator.LEVEL)
                temp = Math.abs(source - e.getPos());
            else if (e.getState() == Elevator.DOWN)
                temp = Math.abs(source - e.getMinFloor());
            else
                temp = Math.abs(source - e.getMaxFloor());
            travelTime += temp * e.getSpeed();
            if (travelTime + time2Idle[i] > elevatorTime) continue;
            ste = i;
            elevatorTime = travelTime;
        }
        listCalls.add(new CallBundler(c, ste, elevatorTime));
        time2Idle[ste] += elevatorTime;
        return ste;
    }

    /**
     * This method is the low level command for each elevator in terms of the elevator API: GoTo, Stop,
     * The simulator calls the method every time stamp (dt), note: in most cases NO action is needed.
     *
     * @param elev the current Elevator index on which the operation is performs.
     */
    @Override
    public void cmdElevator(int elev) {
        Elevator e = building.getElevetor(elev);
        if (e.getState() != Elevator.LEVEL) return;
        CallBundler b;
        int i = 0;
        while(i < listCalls.size()){
            b = listCalls.get(i); // Using while in order to remove it later.
            i++;
            if (b.locateTo == elev){
                   e.goTo(b.c.getSrc());
                   listCalls.remove(b);
                   time2Idle[elev] -= b.time;
                   //e.stop(b.c.getDest());
            }
        }
    }
}
