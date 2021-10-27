package ex0;

import ex0.algo.ElevatorAlgo;

import java.util.*;

class LiftCaller{
    Elevator e;
    LinkedList<LiftLevel> callsUp;
    LinkedList<LiftLevel> callsDown;
    int eState;

    public LiftCaller(Elevator e){
        this.e = e;
        eState = Elevator.LEVEL;
        callsUp = new LinkedList<>();
        callsDown = new LinkedList<>();
    }
    public int relativeState(int src, int dest){
        if (dest == src) return Elevator.LEVEL;
        return dest > src ? Elevator.UP : Elevator.DOWN;
    }

    public int relativeState(int level){
        return relativeState(e.getPos(), level);
    }

    public void add2Calls(LiftLevel level, int eState){
        if (eState == Elevator.UP) callsUp.add(level);
        else callsDown.add(level);
    }
    public void addCall(LiftCall call){
        int cState;
        if (eState == Elevator.LEVEL){
            eState = relativeState(call.src);
        }
        cState = relativeState(call.src, call.dest); // Call State
        add2Calls(new LiftLevel(call.src, eState), cState);
        add2Calls(new LiftLevel(call.dest, Elevator.LEVEL), cState);
    }

    public int getState(){
        return e.getState();
    }
    public int getPos(){
        return e.getPos();
    }
    public void sortByExtreme(){

    }
}
class LiftLevel{
    int level;
    int levelState;
    public LiftLevel(int level, int levelState){
        this.level = level;
        this.levelState = levelState;
    }
}

class LiftCall{
    int src, dest;
    public LiftCall(int src, int dest){
        this.src = src;
        this.dest = dest;
    }
    public int getState()
    {
        if (src == dest) return Elevator.LEVEL;
        return dest > src ? Elevator.UP : Elevator.DOWN;
    }
}

class Lift implements ElevatorAlgo {
    private final Building building;
    LiftCaller[] calls;

    public Lift(Building building){
        this.building = building;
        calls = new LiftCaller[building.numberOfElevetors()];
        for (int i = 0; i < calls.length; i++) {
            calls[i] = new LiftCaller(building.getElevetor(i));
        }
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
        return 0;
    }

    /**
     * This method is the low level command for each elevator in terms of the elevator API: GoTo, Stop,
     * The simulator calls the method every time stamp (dt), note: in most cases NO action is needed.
     *
     * @param elev the current Elevator index on which the operation is performs.
     */
    @Override
    public void cmdElevator(int elev) {
    }
}



class LiftCallsV1{
    Elevator e;
    TreeSet<CallBundler> listCalls;
    int time2Idle, minCall, maxCall;
    int direction;
    Comparator<CallBundler> upComp = (o1, o2) -> {
        if (o1.getSrc() == o2.getSrc()) return 0;
        else return o1.getSrc() > o2.getSrc() ? 1 : -1;
    };
    Comparator<CallBundler> downComp = (o1, o2) -> {
        if (o1.getSrc() == o2.getSrc()) return 0;
        else return o1.getSrc() > o2.getSrc() ? -1 : 1;
    };

    public LiftCallsV1(Elevator e){
        this.e = e;
        direction = Elevator.LEVEL;
        listCalls = new TreeSet<>(upComp);
        time2Idle = 0;
        minCall = e.getMinFloor();
        maxCall = e.getMaxFloor();
    }
    public boolean hasCalls(){
        return !listCalls.isEmpty();
    }
    public boolean inRange(int level){ // SRC calls range
        return level <= maxCall && level >= minCall;
    }
    public int getPos(){
        return e.getPos();
    }
    public double getSpeed(){
        return e.getSpeed();
    }
    public int getState(){
        return e.getState();
    }
    private int relativeDirection(int floor){
        if (this.e.getPos() == floor) return Elevator.LEVEL;
        else if (this.e.getPos() - floor > 0) return Elevator.UP;
        else return Elevator.DOWN;
    }

    public void addCall(CallBundler b){
        minCall = Math.min(minCall, b.getSrc());
        maxCall = Math.max(maxCall, b.getSrc());
        if (!hasCalls()) {
            direction = relativeDirection(b.getSrc());
        }
        if (listCalls.size() == 1){
            direction = relativeDirection(b.getSrc());
            TreeSet<CallBundler> temp = new TreeSet<>(direction == Elevator.UP ? upComp: downComp);
            temp.addAll(listCalls);
            listCalls = temp;
        }
        time2Idle += b.time;
        listCalls.add(b);

    }
    public void PopCall(){
        if (! listCalls.isEmpty()){
            e.goTo(listCalls.first().getSrc());
            removeCall(listCalls.first());
        }
    }
    public void removeCall(CallBundler b){
        listCalls.remove(b);
        time2Idle -= b.time;
        if (listCalls.isEmpty()){
            minCall = e.getMinFloor();
            maxCall = e.getMaxFloor();
        }
        else{
            minCall = Integer.MAX_VALUE;
            maxCall = Integer.MIN_VALUE;
            for (CallBundler c: listCalls) {
                if (c.getDest() < minCall)
                    minCall = c.getSrc();
                if (c.getDest() > maxCall)
                    maxCall = c.getSrc();
            }
        }
    }
}

class CallBundler{
    public CallForElevator c;
    public int locateTo;
    public double time;
    public CallBundler(CallForElevator c, int locateTo, double time){
        this.c = c;
        this.locateTo = locateTo;
        this.time = time;
    }
    public int getSrc(){
        return this.c.getSrc();
    }
    public int getDest(){
        return this.c.getDest();
    }
}

class LiftV2 implements ElevatorAlgo {
    private final Building building;
    LiftCallsV1[] calls;
    public LiftV2(Building building){
        this.building = building;
        calls = new LiftCallsV1[building.numberOfElevetors()];
        for (int i = 0; i < building.numberOfElevetors(); i++) {
            calls[i] = new LiftCallsV1(building.getElevetor(i));
        }
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
        int maxElv = calls.length;
        if (maxElv == 0) return 0;
        int source = c.getSrc();
        int dest = c.getDest();
        double travelTime; // The time it takes for the elevator to arrive to the destination from its current point.
        double elevatorTime = Integer.MAX_VALUE;
        int temp;
        int ste = 0; // Shortest time elevator
        for(int i=0; i < maxElv; i ++){
            LiftCallsV1 e = calls[i];
            if (e.getState() == Elevator.ERROR) continue;
            if (! e.hasCalls() || e.inRange(source)){
                calls[ste].addCall(new CallBundler(c, ste, elevatorTime));
                return ste;
            }
            travelTime = e.time2Idle + e.getSpeed() * Math.abs(source - dest);


            if (e.getState() == Elevator.LEVEL)
                temp = Math.abs(source - e.getPos());
            else if (e.getState() == Elevator.DOWN)
                temp = Math.abs(source - e.minCall);
            else
                temp = Math.abs(source - e.maxCall);
            travelTime += Math.abs(e.listCalls.last().c.getDest()-source) * e.getSpeed();
            if (travelTime + e.time2Idle > elevatorTime) continue;
            ste = i;
            elevatorTime = travelTime;
        }
        calls[ste].addCall(new CallBundler(c, ste, elevatorTime));
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
        LiftCallsV1 c = calls[elev];
        if (c.hasCalls()){
            c.PopCall();
        }
    }
}

class LiftV1 implements ElevatorAlgo {
    private final Building building;
    ArrayList<CallBundler> listCalls;
    int[] time2Idle;
    public LiftV1(Building building){
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
                   e.goTo(b.c.getDest());
                   listCalls.remove(b);
                   time2Idle[elev] -= b.time;
                   //e.stop(b.c.getDest());
            }
        }
    }
}
