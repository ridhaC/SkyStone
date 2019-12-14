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
                if (this.isFirstIteration()) {
                    this.getRobot().initialClamps();
                    this.getRobot().frontRightColorSensor.enableLed(false);
                }
                return true;
            }
        });

        START_BLUE_STONES = new OperationQueue(robot);
        START_RED_STONES = new OperationQueue(robot);
        START_BLUE_FOUNDATION = new OperationQueue(robot);
        START_RED_FOUNDATION = new OperationQueue(robot);

        List<Operation> deployClamp = new ArrayList<Operation>();
        deployClamp.add(new Operation("lift rack a little",0.4f){
            public boolean defineOperation() {
                this.getRobot().driveRack(0.6);
                return true;
            }
        });
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
                this.getRobot().openClamp();
                return true;
            }
        });
        deployClamp.add(new Operation("lower rack",0.7f) {
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
                this.getRobot().frontRightColorSensor.enableLed(true);
                return this.getRobot().getDistance()>3.8;
            }
        });
        ops.add(new Operation("search for stones",2.9f) {
            public boolean defineOperation() {
                this.getRobot().driveRight(0.3);
                return !this.getRobot().nextToSkystone();
            }
        });
        ops.add(new Operation("realign with stones", 1.5f)    {
            public boolean defineOperation()    {
                this.getRobot().driveRight(0.3);
                this.getRobot().frontRightColorSensor.enableLed(false);
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
        ops.add(new Operation("rotate",1.7f) {
            public boolean defineOperation() {
                this.getRobot().turnLeft(0.5);
                return this.getRobot().getAngle()<60;
            }
        });
        ops.add(new Operation("rotate slower",1.7f) {
            public boolean defineOperation() {
                this.getRobot().turnLeft(0.3);
                return this.getRobot().getAngle()<85;
            }
        });
        ops.add(new Operation("get close to stripes",0.5f) {
            public boolean defineOperation() {
                this.getRobot().driveForward(0.4);
                return true;
            }
        });
        ops.add(new Operation("go to stripe", 3.0f)  {
            public boolean defineOperation() {
                this.getRobot().driveForward(0.2);
                return !this.getRobot().overBlueStripe();
            }
        });
        ops.add(new Operation("move into area", 0.8f) {
           public boolean defineOperation() {
               this.getRobot().driveForward(0.4);
               return true;
           }
        });
        ops.add(new Operation("drop stone", 1.0f) {
            public boolean defineOperation() {
                if (this.isFirstIteration())
                    this.getRobot().openClamp();
                return true;
            }
        });
        ops.add(new Operation("return to parking position", 0.8f) {
             public boolean defineOperation() {
                this.getRobot().driveBackward(0.4);
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
        ops.add(new Operation("get close to stones",1.0f) {
            public boolean defineOperation() {
                this.getRobot().driveForward(0.3);
                return true;
            }
        });
        ops.add(new Operation("line up with stones") {
            public boolean defineOperation() {
                this.getRobot().driveForward(0.15);
                this.getRobot().frontRightColorSensor.enableLed(true);
                return this.getRobot().getDistance()>3.8;
            }
        });
        ops.add(new Operation("search for stones", 3.4f) {
            public boolean defineOperation() {
                this.getRobot().driveLeft(0.3);
                return !this.getRobot().nextToSkystone();
            }
        });
        ops.add(new Operation("realign with stones", 1.0f)    {
            public boolean defineOperation()    {
                this.getRobot().driveLeft(0.3);
                this.getRobot().frontRightColorSensor.enableLed(false);
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
        ops.add(new Operation("move back", 0.4f) {
            public boolean defineOperation() {
                this.getRobot().driveBackward(0.25);
                return true;
            }
        });
        ops.add(new Operation("rotate",1.7f) {
            public boolean defineOperation() {
                this.getRobot().turnRight(0.5);
                return this.getRobot().getAngle()>-60;
            }
        });
        ops.add(new Operation("rotate slower",1.7f) {
            public boolean defineOperation() {
                this.getRobot().turnRight(0.3);
                return this.getRobot().getAngle()>-85;
            }
        });
        ops.add(new Operation("get close to stripes",0.5f) {
            public boolean defineOperation() {
                this.getRobot().driveForward(0.4);
                return true;
            }
        });
        ops.add(new Operation("go to stripe", 3.0f)  {
            public boolean defineOperation() {
                this.getRobot().driveForward(0.2);
                return !this.getRobot().overRedStripe();
            }
        });
        ops.add(new Operation("move into area", 0.9f) {
            public boolean defineOperation() {
                this.getRobot().driveForward(0.4);
                return true;
            }
        });
        ops.add(new Operation("drop stone", 1.0f) {
            public boolean defineOperation() {
                if (this.isFirstIteration())
                    this.getRobot().openClamp();
                return true;
            }
        });
        ops.add(new Operation("return to parking position", 0.7f) {
            public boolean defineOperation() {
                this.getRobot().driveBackward(0.4);
                return true;
            }
        });
        START_RED_STONES.add(allStartingOps);
        START_RED_STONES.add(deployClamp);
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
