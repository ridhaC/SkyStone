package org.firstinspires.ftc.teamcode;

import java.util.ArrayList;
import java.util.List;

public class AutonomousQueues {
    public static final int BLUE_STONES_INT = 0, RED_STONES_INT = 1, BLUE_FOUNDATION_INT = 2, RED_FOUNDATION_INT = 3;
    public static OperationQueue START_BLUE_STONES, START_RED_STONES, START_BLUE_FOUNDATION, START_RED_FOUNDATION;
    /**
     * This should be called at the beginning of the AutonomousOpMode
     */
    public static void initiate(RobotHardware robot, float delayTime) {
        List<Operation> allStartingOps = new ArrayList<Operation>();
        allStartingOps.add(new Operation("prep servos",delayTime) {
            public boolean defineOperation() {
                this.getRobot().initialClamps();
                return true;
            }
        });

        START_BLUE_STONES = new OperationQueue(robot);
        START_RED_STONES = new OperationQueue(robot);
        START_BLUE_FOUNDATION = new OperationQueue(robot);
        START_RED_FOUNDATION = new OperationQueue(robot);

        List<Operation> deployClamp = new ArrayList<Operation>();
        /*deployClamp.add(new Operation("open front clamp",0.5f) {
            public boolean defineOperation() {
                this.getRobot().openFrontClamp();
                return true;
            }
        });*/
        deployClamp.add(new Operation("lift rack",1.0f) {
            public boolean defineOperation() {
                this.getRobot().driveRack(0.6);
                this.getRobot().driveSpool(0.6);
                return true;
            }
        });
        deployClamp.add(new Operation("toggle clamp",0.25f) {
            public boolean defineOperation() {
                this.getRobot().driveRack(0);
                this.getRobot().driveSpool(0.35);
                this.getRobot().toggleClamp();
                return true;
            }
        });
        deployClamp.add(new Operation("lower rack",0.5f) {
            public boolean defineOperation() {
                this.getRobot().driveRack(-0.6);
                this.getRobot().driveSpool(0.0);
                return true;
            }
        });

        List<Operation> ops = new ArrayList<Operation>();
        /*
          Implementation for start position at the blue stones
         */
        ops.add(new Operation("get close to stones",1.0f) {
            public boolean defineOperation() {
                this.getRobot().driveForward(0.3);
                return true;
            }
        });
        ops.add(new Operation("line up with stones") {
            public boolean defineOperation() {
                this.getRobot().driveForward(0.15);
                return this.getRobot().getDistance()>7.0;
            }
        });
        /*ops.add(new Operation("search for stones") {
            public boolean defineOperation() {
                this.getRobot().driveRight(0.3);
                return !this.getRobot().nextToSkystone();
            }
        });*/
        ops.add(new Operation("search for stones") {
            double desiredHeadingRadians;
            double HEADING_CORRECTION_COEFFICIENT;
            public boolean defineOperation() {
                if(isFirstIteration())  {
                    HEADING_CORRECTION_COEFFICIENT = 0.25f;
                    desiredHeadingRadians = this.getRobot().getAngleRadians();
                }
                double currentHeadingRadians = this.getRobot().getAngleRadians();
                double headingError = currentHeadingRadians - desiredHeadingRadians;
                if (headingError > Math.PI)
                    headingError -= (float)Math.PI;
                else if (headingError < -Math.PI)
                    headingError += (float)Math.PI;
                double headingCorrectionPower = -HEADING_CORRECTION_COEFFICIENT * headingError;
                this.getRobot().strafe(0, 0.3, headingCorrectionPower, 1.0);
                return !this.getRobot().nextToSkystone();
            }
        });

        ops.add(new Operation("realign with stones", 0.2f)    {
            public boolean defineOperation()    {
                this.getRobot().driveLeft(0.2);
                return true;
            }
        });
        ops.add(new Operation("close clamp",0.5f){
            public boolean defineOperation() {
                if (this.isFirstIteration())
                    this.getRobot().closeClamp();
                return true;
            }
        });
        ops.add(new Operation("move back", 1.25f) {
            public boolean defineOperation() {
                this.getRobot().driveBackward(0.25);
                return true;
            }
        });
        ops.add(new Operation("get close to stripe", 0.5f)  {
            public boolean defineOperation()    {
                this.getRobot().driveLeft(0.7);
                return true;
            }
        });
        ops.add(new Operation("go to stripe") {
            public boolean defineOperation() {
                this.getRobot().driveLeft(0.5);
                return !this.getRobot().overBlueStripe();
            }
        });
        ops.add(new Operation("clear stripe", 0.6f) {
            public boolean defineOperation() {
                this.getRobot().driveLeft(0.7);
                return true;
            }
        });
        ops.add(new Operation("drop stone", 0.7f) {
            public boolean defineOperation() {
                if (this.isFirstIteration())
                    this.getRobot().openClamps();
                return true;
            }
        });
        ops.add(new Operation("return to parking position", 1.5f) {
             public boolean defineOperation() {
                this.getRobot().driveRight(0.4);
                return true;
            }
        });
        START_BLUE_STONES.add(allStartingOps);
        START_BLUE_STONES.add(deployClamp);
        START_BLUE_STONES.add(ops);
        ops.clear();
        /*
          Implementation for start position at the red stones
         */
        START_RED_STONES = new OperationQueue(robot);
        ops.add(new Operation("Move to stones",1.2f) {
            public boolean defineOperation() {
                this.getRobot().driveForward(0.35);
                return true;
            }
        });
        ops.add(new Operation("search for stones", 7.0f) {
            public boolean defineOperation() {
                this.getRobot().driveRight(0.9);
                return !this.getRobot().nextToSkystone();
            }
        });
        ops.add(new Operation("extrude slide", 0.5f) {
            public boolean defineOperation() {
                this.getRobot().driveSpool(0.7);
                return true;
            }
        });
        ops.add(new Operation("grab stone", 0.5f) {
            public boolean defineOperation() {
                this.getRobot().toggleClamp();
                return true;
            }
        });
        ops.add(new Operation("retract slide", 0.5f) {
            public boolean defineOperation() {
                this.getRobot().driveSpool(-0.7);
                return true;
            }
        });
        ops.add(new Operation("move back", 1.0f) {
            public boolean defineOperation() {
                this.getRobot().driveBackward(0.7);
                return true;
            }
        });
        ops.add(new Operation("deliver stone",2.0f) {
            public boolean defineOperation() {
                getRobot().driveRight(0.4);
                return true;
            }
        });
        ops.add(new Operation("drop stone", 0.5f) {
            public boolean defineOperation() {
                this.getRobot().toggleClamp();
                return true;
            }
        });
        ops.add(new Operation("return to parking position", 0.5f) {
            public boolean defineOperation() {
                this.getRobot().driveLeft(1.0);
                return true;
            }
        });
        START_RED_STONES.add(ops);
        ops.clear();

        /*
          Implementation for start position at the blue foundation
         */
        //strafe right to under bridge
        ops.add(new Operation("giving space",0.1f) {
            public boolean defineOperation() {
                getRobot().driveBackward(0.4);
                return true;
            }
        });
        ops.add(new Operation("moving to center",2.0f){
            public boolean defineOperation() {
                getRobot().driveRight(0.3);
                return true;
            }
        });
        ops.add(new Operation("moving forward to site",1.2f) {
            public boolean defineOperation() {
                getRobot().driveBackward(0.4);
                return true;
            }
        });
        ops.add(new Operation("waiting",2.0f) {
            public boolean defineOperation() {
                return true;
            }
        });
        ops.add(new Operation("lowering hook",2.0f) {
            public boolean defineOperation() {
                getRobot().lowerHook();
                return true;
            }
        });
        ops.add(new Operation("driving back",8.0f) {
            public boolean defineOperation() {
                getRobot().driveForward(0.20);
                return true;
            }
        });
        ops.add(new Operation("raising hook",2.0f) {
            public boolean defineOperation() {
                getRobot().raiseHook();
                return true;
            }
        });
        ops.add(new Operation("parking",7.0f){
            public boolean defineOperation() {
                getRobot().driveLeft(0.4);
                return true;
            }
        });
        START_BLUE_FOUNDATION.add(ops);
        ops.clear();

        /*
         Implementation for start position at the red foundation
         */
        ops.add(new Operation("giving space",0.1f) {
            public boolean defineOperation() {
                getRobot().driveBackward(0.4);
                return true;
            }
        });
        ops.add(new Operation("moving to center",2.0f){
            public boolean defineOperation() {
                getRobot().driveLeft(0.3);
                return true;
            }
        });
        ops.add(new Operation("moving forward to site",1.2f) {
            public boolean defineOperation() {
                getRobot().driveBackward(0.4);
                return true;
            }
        });
        ops.add(new Operation("waiting",2.0f) {
            public boolean defineOperation() {
                return true;
            }
        });
        ops.add(new Operation("lowering hook",2.0f) {
            public boolean defineOperation() {
                getRobot().lowerHook();
                return true;
            }
        });
        ops.add(new Operation("driving back",8.0f) {
            public boolean defineOperation() {
                getRobot().driveForward(0.20);
                return true;
            }
        });
        ops.add(new Operation("raising hook",2.0f) {
            public boolean defineOperation() {
                getRobot().raiseHook();
                return true;
            }
        });
        ops.add(new Operation("parking",7.0f){
            public boolean defineOperation() {
                getRobot().driveRight(0.4);
                return true;
            }
        });
        START_RED_FOUNDATION.add(ops);
        ops.clear();
    }
}
