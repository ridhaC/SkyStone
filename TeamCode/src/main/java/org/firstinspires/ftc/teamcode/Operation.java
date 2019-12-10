package org.firstinspires.ftc.teamcode;

public abstract class Operation {
    private RobotHardware robot;
    private TickTimer tt;
    private int iterations = 0;
    private String opName = "";

    private float runtime = -1.0f; //if this is -1 then the operation is not timer based

    public Operation(String opName) {
        tt = new TickTimer();
        this.opName = opName;
    }

    public Operation(String opName, float runtime) {
        this(opName);
        this.runtime = runtime;
    }

    public String getName() { return opName; }

    public void setRobot(RobotHardware robot) {
        this.robot  = robot;
    }

    public RobotHardware getRobot() {
        return robot;
    }

    public boolean isFirstIteration() {
        return iterations == 0;
    }

    public int iterations() {
        return iterations;
    }

    public void iterate() {
        iterations++;
    }

    public void clearTimer() {
        tt.clear();
    }

    public float elapsedTime() {
        return tt.elapsedTime();
    }

    /**
     * Runs the operations
     * @return True if the operation is running, false if it is done.
     */
    public boolean operate() {
        if (this.isFirstIteration())
            this.clearTimer(); // if this is the first run of the operation method
        boolean run = defineOperation(); //calls the specified operation of the anonymous class
        this.iterate();
        if (!run) //if the defineOperation has said were done, then were done
            return false;
        if (runtime < 0) //no timer is set so return the end condition of the operation
            return run;
        if (elapsedTime() >= runtime) //if its set to run off a timer and it has completed
            return false;
        else  //if its set to run off a timer and the timer is not completed
            return true;
    }

    public abstract boolean defineOperation();
}
