package ex0.algo;

import ex0.Building;
import ex0.CallForElevator;
import ex0.Elevator;

import java.util.Comparator;
import java.util.Timer;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

class Call {
    boolean isSet; // picked up? dropped?
    int level;

    public Call(int level, boolean isSet) {
        this.level = level;
        this.isSet = isSet;
    }
}

class Lift {
    Elevator e;
    boolean isUp;
    TreeSet<Call> iWay;
    TreeSet<Call> aWay;
    double staStoSeq;
    Comparator<Call> aWayComp = (o1, o2) -> {
        if (o1.level == o2.level) return 0;
        return o1.level > o2.level ? 1 : -1; // If we are going down we sort from the highest to the lowest.
    };
    Comparator<Call> iWayComp = (o1, o2) -> {
        if (o1.level == o2.level) return 0;
        return o1.level < o2.level ? 1 : -1; // If we are going down we sort from the highest to the lowest.
    };

    public Lift(Elevator e, int startPos){
        this.e = e;
        staStoSeq = e.getStopTime() + e.getTimeForOpen() + e.getTimeForOpen() + e.getStartTime();
        isUp = true;
        if (startPos > e.getMinFloor() && startPos < e.getMaxFloor())
            e.goTo(startPos); // The middle floor is the best floor for extreme cases distance.
        iWay = new TreeSet<>(iWayComp);
        aWay = new TreeSet<>(aWayComp);
    }

    public Lift(Elevator e) {
        this.e = e;
        staStoSeq = e.getStopTime() + e.getTimeForOpen() + e.getTimeForOpen() + e.getStartTime();
        isUp = true;
        //e.goTo((int) Math.floor((e.getMaxFloor() + e.getMinFloor()) / 2.0)); // The middle floor is the best floor for extreme cases distance.
        iWay = new TreeSet<>(iWayComp);
        aWay = new TreeSet<>(aWayComp);
    }

    public int getState() {
        return e.getState();
    }

    public int getPos() {
        return this.e.getPos();
    }

    public int numOfCalls() {
        return iWay.size() + aWay.size();
    }

    public double estTime(int src, int dest) {
        if (getPos() == src && relativeState(src, dest) == isUp) return 0;
        int stops = 1;
        double est = 0;
        int l;
        if (iWay.isEmpty())
            l = src;
        else
            l = iWay.last().level;

        // NOTE THAT BY COMPARING RELATIVE DIRECTION TO ISUP WE CHECK IF THE ELEVATOR IS GOING TO THE SAME WAY AS THE CALL, NOT NECESSARILY UP
        // Calculating time to source from the current position (with the stops in the way), then calculating the time to the dest. with the stops on it's way.
        if (getPos() < src && isUp) { // We are in the right direction and can pick them up.
            for (Call c : iWay)
                if (c.level < src) stops++;
                else break;
            est += Math.abs(getPos() - src) / e.getSpeed() + stops * staStoSeq; // time to src
        } else if (getPos() > src && !isUp) {
            for (Call c : iWay)
                if (c.level > src) stops++;
                else break;
            est += Math.abs(getPos() - src) / e.getSpeed() + stops * staStoSeq;
        } else if (getPos() < src && !isUp) {
            stops += iWay.size();
            //stops += aWay.size();
            for (Call c : aWay)
                if (c.level < src) stops++;
                else break;
            //return (Math.abs(getPos() - iWay.last().level) + Math.abs(level - iWay.last().level)) * e.getSpeed() - stops * staStoSeq;
            est += (Math.abs(getPos() - l) + Math.abs(src - l)) / e.getSpeed() + stops * staStoSeq;
        } else if (getPos() > src && isUp) {
            stops += iWay.size();
            //stops += aWay.size();
            for (Call c : aWay)
                if (c.level > src) stops++;
                else break;
            //return (Math.abs(getPos() - iWay.last().level) + Math.abs(level - iWay.last().level)) * e.getSpeed() - stops * staStoSeq;
            est += (Math.abs(getPos() - l) + Math.abs(src - l)) / e.getSpeed() + stops * staStoSeq;

        }
        if (l < dest) {
            if (relativeState(l, dest) == isUp) //the last in that direction is at the same way as the last call. We have calculated the route so we just need to add the stop start time of this.
            {
                est += staStoSeq;
            }
        }
        else if (l > dest) {

        }
        return est;
    }


    public void addCall(int src, int dest) {
        if (iWay.size() == 0) {
            if (relativeState(src, dest)) {
                swapComp();
                isUp = relativeState(getPos(), src);
                iWay = new TreeSet<>(iWayComp);
                aWay = new TreeSet<>(aWayComp);
            }
        }
        if (relativeState(getPos(), src) == isUp) { // Here the elevator source is it's current position and the destination is the pickup level.
            iWay.add(new Call(src, false));
            if (relativeState(src, dest) == isUp)
                iWay.add(new Call(dest, false));
            else
                aWay.add(new Call(dest, false));
        } else {
            aWay.add(new Call(src, false));
            if (relativeState(src, dest) == isUp)
                iWay.add(new Call(dest, false));
            else
                aWay.add(new Call(dest, false));
        }
        if (iWay.size() != 0 && aWay.size() != 0 && iWay.last() == aWay.first()){
            aWay.remove(aWay.first());
        }
    }

    public boolean relativeState(int src, int dest) {
        if (src == dest) return isUp; // It doesn't mather for the elevator, set it for the elevator "state"
        return dest > src;
    }

    public void swapComp() {
        Comparator<Call> temp = iWayComp;
        iWayComp = aWayComp;
        aWayComp = temp;
    }

    public void cmdLift() {
        if (iWay.size() == 1) {
            e.goTo(iWay.first().level);
            swapComp();
            isUp = !isUp;
            iWay = aWay;
            aWay = new TreeSet<>(aWay);
        }
        if (iWay.size() == 0) return;
        e.goTo(iWay.first().level);
        iWay.remove(iWay.first());
    }

}

public class LiftAlgo implements ElevatorAlgo {
    Building b;
    TreeSet<Lift> lifts;

    public LiftAlgo(Building b) {
        this.b = b;
        lifts = new TreeSet<>(((o1, o2) -> {
            //if (o1.numOfCalls() == o2.numOfCalls()) return 0;
            if (o1.numOfCalls() == -1) return 0;
            //else
            return o1.numOfCalls() <= o2.numOfCalls() ? 1 : -1;
        }));
        for (int i = 0; i < b.numberOfElevetors(); i++) {
            lifts.add(new Lift(b.getElevetor(i)));
        }
    }

    /**
     * @return the Building on which the (online) elevator algorithm works on.
     */
    @Override
    public Building getBuilding() {
        return b;
    }

    /**
     * @return he algorithm name - can be any String.
     */
    @Override
    public String algoName() {
        return "Lift Algorithm";
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
        Lift selectedLift = lifts.first();
        double liftTime = 20000, temp;
        for (Lift l : lifts) {

            temp = l.estTime(c.getSrc(), c.getDest());
            //System.out.println("EST TIME WITH THIS ELEVATOR: " + temp + " WHILE SELECTED IS: " + liftTime + " THIS ELEVATOR IS BETTER? " + (temp < liftTime));
            //if (temp > 70) continue;
            if (temp < liftTime) {
                selectedLift = l;
                liftTime = temp;
            }
        }
        System.out.println("SELECTED EST TIME: " + liftTime + " ELEVATOR: " + selectedLift.e.getID() + " / " + (b.numberOfElevetors()-1));
        selectedLift.addCall(c.getSrc(), c.getDest());
        return selectedLift.e.getID();
    }

    /**
     * This method is the low level command for each elevator in terms of the elevator API: GoTo, Stop,
     * The simulator calls the method every time stamp (dt), note: in most cases NO action is needed.
     *
     * @param elev the current Elevator index on which the operation is performs.
     */
    @Override
    public void cmdElevator(int elev) {
        for (Lift l : lifts) {
            if (l.e.getID() == elev) {
                l.cmdLift();
                return;
            }
        }
    }
}
