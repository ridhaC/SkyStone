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
                this.getRobot().compactClamp();
                return true;
            }
        });

        START_BLUE_STONES = new OperationQueue(robot);
        START_RED_STONES = new OperationQueue(robot);
        START_BLUE_FOUNDATION = new OperationQueue(robot);
        START_RED_FOUNDATION = new OperationQueue(robot);

        List<Operation> deployClamp = new ArrayList<Operation>();
        deployClamp.add(new Operation("lift rack",0.5f) {
            public boolean defineOperation() {
                this.getRobot().driveRack(0.5);
                return true;
            }
        });
        deployClamp.add(new Operation("extrude slide",2.0f) {
            public boolean defineOperation() {
                this.getRobot().driveSpool(0.7);
                return true;
            }
        });
        deployClamp.add(new Operation("toggle clamp",0.5f) {
            public boolean defineOperation() {
                this.getRobot().toggleClamp();
                return true;
            }
        });
        deployClamp.add(new Operation("lower rack",0.7f) {
            public boolean defineOperation() {
                this.getRobot().driveRack(-0.5);
                return true;
            }
        });

        List<Operation> ops = new ArrayList<Operation>();
        /**
         * Implementation for start position at the blue stones
         */
        ops.add(new Operation("open clamp",0.5f){
            public boolean defineOperation() {
                if (this.isFirstIteration())
                    this.getRobot().openClamp();
                return true;
            }
        });
        ops.add(new Operation("get close to stones",1.0f) {
            public boolean defineOperation() {
                this.getRobot().driveForward(0.3);
                return true;
            }
        });
        ops.add(new Operation("line up with stones") {
            public boolean defineOperation() {
                this.getRobot().driveForward(0.15);
                return this.getRobot().getDistance()>3.8;
            }
        });
        ops.add(new Operation("search for stones") {
            public boolean defineOperation() {
                this.getRobot().driveRight(0.3);
                return !this.getRobot().nextToSkystone();
            }
        });
        ops.add(new Operation("extrude slide", 2.0f) {
            public boolean defineOperation() {
                this.getRobot().driveSpool(1.0);
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
        ops.add(new Operation("move back", 0.5f) {
            public boolean defineOperation() {
                this.getRobot().driveBackward(0.25);
                return true;
            }
        });
        ops.add(new Operation("waiting", 0.5f) {
           public boolean defineOperation() {
               return true;
           }
        });
        ops.add(new Operation("deliver stone",4.0f) {
            public boolean defineOperation() {
                getRobot().driveLeft(0.4);
                return true;
            }
        });
        ops.add(new Operation("drop stone", 1.0f) {
            public boolean defineOperation() {
                if (this.isFirstIteration())
                    this.getRobot().toggleClamp();
                return true;
            }
        });
        ops.add(new Operation("return to parking position", 1.5f) {
             public boolean defineOperation() {
                this.getRobot().driveRight(0.4);
                return true;
            }
        });
        START_BLUE_STONES.add(ops);
        ops.clear();
        /**
         * Implementation for start position at the red stones
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
        ops.add(new Operation("move back", 0.5f) {
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

        /**
         * Implementation for start position at the blue foundation
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

        /**
         * Implementation for start position at the red foundation
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
