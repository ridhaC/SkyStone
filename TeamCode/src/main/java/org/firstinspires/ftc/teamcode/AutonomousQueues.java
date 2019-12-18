package org.firstinspires.ftc.teamcode;

import android.graphics.Path;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.ArrayList;
import java.util.List;

public class AutonomousQueues {
    public static final int BLUE_STONES_INT = 0, RED_STONES_INT = 1, BLUE_FOUNDATION_INT = 2, RED_FOUNDATION_INT = 3, FORWARD_LEFT_INT = 4, FORWARD_RIGHT_INT = 5, LEFT_INT = 6, RIGHT_INT = 7;
    public static OperationQueue START_BLUE_STONES, START_RED_STONES, START_BLUE_FOUNDATION, START_RED_FOUNDATION, START_RED_PARK;
    public static OperationQueue FORWARD_LEFT, FORWARD_RIGHT, LEFT, RIGHT;
    public static OperationQueue TEST_QUEUE;
    public static int firstStoneLocation = 2;
    /**
     * This should be called at the beginning of the AutonomousOpMode
     */
    public static void initiate(RobotHardware robot, float delayTime) {
        List<Operation> allStartingOps = new ArrayList<Operation>();
        allStartingOps.add(new Operation("prep servos",delayTime) {
            public boolean defineOperation() {
                if (this.isFirstIteration()) {
                    this.getRobot().initialClamps();
                }
                return true;
            }
        });

        START_BLUE_STONES = new OperationQueue(robot);
        START_RED_STONES = new OperationQueue(robot);
        START_BLUE_FOUNDATION = new OperationQueue(robot);
        START_RED_FOUNDATION = new OperationQueue(robot);
        START_RED_PARK = new OperationQueue(robot);
        FORWARD_LEFT = new OperationQueue(robot);
        FORWARD_RIGHT = new OperationQueue(robot);
        LEFT  = new OperationQueue(robot);
        RIGHT = new OperationQueue(robot);
        TEST_QUEUE = new OperationQueue(robot);
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
                this.getRobot().driveForward(0.35);
                return true;
            }
        });

        List<Operation> ops = new ArrayList<Operation>();
        /*
          Implementation for start position at the blue stones
         */
        /*ops.add(new Operation("get close to stones",1.0f) {
            public boolean defineOperation() {
                this.getRobot().driveForward(0.3);
                return true;
            }
        });*/
        ops.add(new Operation("line up with stones") {
            public boolean defineOperation() {
                this.getRobot().driveForward(0.15);
                return this.getRobot().getDistance()>3.7;
            }
        });
        ops.add(new Operation("check if stone is one of the first two", 1.0f) {
            @Override
            public boolean defineOperation() {
                /*if(this.getRobot().nextToSkystone(RobotHardware.Side.RIGHT)) {
                    AutonomousQueues.firstStoneLocation = 1;
                }
                else if(this.getRobot().nextToSkystone(RobotHardware.Side.LEFT)) {
                    AutonomousQueues.firstStoneLocation = 0;
                }
                else
                    AutonomousQueues.firstStoneLocation = 2;*/
                AutonomousQueues.firstStoneLocation = 2;
                return true;
            }
        });
        if(firstStoneLocation == 0) {
            ops.add(new Operation("align with stone", 1.0f) {
                public boolean defineOperation() {
                    this.getRobot().driveLeft(0.3);
                    return true;
                }
            });
        }
        else if(firstStoneLocation == 1)    {
            ops.add(new Operation("align with stone", 1.0f) {
                public boolean defineOperation() {
                    this.getRobot().driveRight(0.3);
                    return true;
                }
            });
        }
        else    {
            ops.add(new Operation("align with stone", 2.0f) {
                public boolean defineOperation() {
                    this.getRobot().driveRight(0.3);
                    return true;
                }
            });
        }
        ops.add(new Operation("close clamp",0.5f){
            public boolean defineOperation() {
                if (this.isFirstIteration())
                    this.getRobot().closeClamp();
                return true;
            }
        });
        ops.add(new Operation("move back", 0.35f) {
            public boolean defineOperation() {
                this.getRobot().driveBackward(0.25);
                return true;
            }
        });
        ops.add(new Operation("rotate",1.7f) {
            public boolean defineOperation() {
                this.getRobot().turnLeft(0.5);
                return this.getRobot().getAngle()<50;
            }
        });
        ops.add(new Operation("rotate slower",1.7f) {
            public boolean defineOperation() {
                this.getRobot().turnLeft(0.25
                );
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
        ops.add(new Operation("move into area", 0.6f) {
           public boolean defineOperation() {
               this.getRobot().driveForward(0.4);
               return true;
           }
        });
        ops.add(new Operation("drop stone", 0.5f) {
            public boolean defineOperation() {
                if (this.isFirstIteration())
                    this.getRobot().openClamp();
                return true;
            }
        });
        ops.add(new Operation("return to stones position", 1.75f) {
             public boolean defineOperation() {
                 this.getRobot().driveBackward(0.5);
                return true;
            }
        });
        if(firstStoneLocation == 1) {
            ops.add(new Operation("return to stones position", 0.7f) {
                public boolean defineOperation() {
                    this.getRobot().driveBackward(0.4);
                    return true;
                }
            });
        }
        else    {
            ops.add(new Operation("return to stones position", 0.45f) {
                public boolean defineOperation() {
                    this.getRobot().driveBackward(0.4);
                    return true;
                }
            });
        }
        ops.add(new Operation("rotate",1.7f) {
            public boolean defineOperation() {
                this.getRobot().turnRight(0.5);
                return this.getRobot().getAngle()>30;
            }
        });
        ops.add(new Operation("rotate slower",1.7f) {
            public boolean defineOperation() {
                this.getRobot().turnRight(0.25);
                return this.getRobot().getAngle()>0;
            }
        });
        ops.add(new Operation("line up with stones") {
            public boolean defineOperation() {
                this.getRobot().driveForward(0.15);
                return this.getRobot().getDistance()>3.7;
            }
        });
        ops.add(new Operation("close clamp",0.5f){
            public boolean defineOperation() {
                if (this.isFirstIteration())
                    this.getRobot().closeClamp();
                return true;
            }
        });
        ops.add(new Operation("move back", 0.45f) {
            public boolean defineOperation() {
                this.getRobot().driveBackward(0.25);
                return true;
            }
        });
        ops.add(new Operation("rotate",1.7f) {
            public boolean defineOperation() {
                this.getRobot().turnLeft(0.5);
                return this.getRobot().getAngle()<50;
            }
        });
        ops.add(new Operation("rotate slower",1.7f) {
            public boolean defineOperation() {
                this.getRobot().turnLeft(0.25
                );
                return this.getRobot().getAngle()<85;
            }
        });
        ops.add(new Operation("get close to stripes",1.0f) {
            public boolean defineOperation() {
                this.getRobot().driveForward(1.0);
                return true;
            }
        });
        ops.add(new Operation("go to stripe", 3.0f)  {
            public boolean defineOperation() {
                this.getRobot().driveForward(0.2);
                return !this.getRobot().overBlueStripe();
            }
        });
        ops.add(new Operation("move into area", 0.5f) {
            public boolean defineOperation() {
                this.getRobot().driveForward(0.4);
                return true;
            }
        });
        ops.add(new Operation("drop stone", 0.5f) {
            public boolean defineOperation() {
                if (this.isFirstIteration())
                    this.getRobot().openClamp();
                return true;
            }
        });
        ops.add(new Operation("return to parking position", 1.5f) {
            @Override
            public boolean defineOperation() {
                this.getRobot().driveBackward(0.2);
                return !this.getRobot().overBlueStripe();
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
                this.getRobot().driveForward(0.15 );
                return this.getRobot().frontRightDistanceSensor.getDistance(DistanceUnit.CM)>3.95;
            }
        });
        ops.add(new Operation("move right",1.0f) {
            public boolean defineOperation() {
                this.getRobot().driveRight(0.3);
                return true;
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
        ops.add(new Operation("move back", 0.45f) {
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
        ops.add(new Operation("get close to stripes",2.0f) {
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
        ops.add(new Operation("move a lil",1.3f) {
            public boolean defineOperation() {
                this.getRobot().driveRight(0.4);
                return true;
            }
        });
        ops.add(new Operation("moving forward to site",1.45f) {
            public boolean defineOperation() {
                getRobot().driveBackward(0.4);
                return true;
            }
        });
        ops.add(new Operation("waiting",0.5f) {
            public boolean defineOperation() {
                return true;
            }
        });
        ops.add(new Operation("lowering hook",1.0f) {
            public boolean defineOperation() {
                getRobot().lowerHook();
                return true;
            }
        });
        ops.add(new Operation("turn",10.0f) {
           public boolean defineOperation() {
               this.getRobot().turnLeft(0.4);
               return this.getRobot().getAngle() < 20;
           }
        });
        ops.add(new Operation("go back",3.0f) {
           public boolean defineOperation() {
               this.getRobot().driveForward(0.3);
               return true;
           }
        });
        ops.add(new Operation("turn",10.0f) {
            public boolean defineOperation() {
                this.getRobot().turnLeft(0.4);
                return this.getRobot().getAngle() < 85;
            }
        });
        ops.add(new Operation("push into thing",3.0f) {
            public boolean defineOperation() {
                this.getRobot().driveBackward(0.3);
                return true;
            }
        });
        ops.add(new Operation("raising hook",1.0f) {
            public boolean defineOperation() {
                getRobot().raiseHook();
                return true;
            }
        });
        ops.add(new Operation("move to wall",1.9f) {
           public boolean defineOperation() {
               getRobot().driveRight(0.35);
               return true;
           }
        });
        ops.addAll(deployClamp);
        ops.add(new Operation("parking",1.5f){
            public boolean defineOperation() {
                getRobot().driveForward(0.3);
                return true;
            }
        });
        ops.add(new Operation("stop at blue line",5f) {
            public boolean defineOperation() {
                getRobot().driveForward(0.2);
                return !this.getRobot().overBlueStripe();
            }
        });
        ops.add(new Operation("center",0.5f) {
            public boolean defineOperation() {
                getRobot().driveForward(0.2);
                return true;
            }
        });
        START_BLUE_FOUNDATION.add(allStartingOps);
        START_BLUE_FOUNDATION.add(ops);
        ops.clear();

        /*
         Implementation for start position at the red foundation
         */
        ops.add(new Operation("move a lil",1.0f) {
            public boolean defineOperation() {
                this.getRobot().driveLeft(0.4);
                return true;
            }
        });
        ops.add(new Operation("moving forward to site",1.25f) {
            public boolean defineOperation() {
                getRobot().driveBackward(0.4);
                return true;
            }
        });
        ops.add(new Operation("waiting",0.5f) {
            public boolean defineOperation() {
                return true;
            }
        });
        ops.add(new Operation("lowering hook",1.0f) {
            public boolean defineOperation() {
                getRobot().lowerHook();
                return true;
            }
        });
        ops.add(new Operation("turn",10.0f) {
            public boolean defineOperation() {
                this.getRobot().turnRight(0.4);
                return this.getRobot().getAngle() > -20;
            }
        });
        ops.add(new Operation("go back",3.0f) {
            public boolean defineOperation() {
                this.getRobot().driveForward(0.3);
                return true;
            }
        });
        ops.add(new Operation("turn",10.0f) {
            public boolean defineOperation() {
                this.getRobot().turnRight(0.4);
                return this.getRobot().getAngle() > -85;
            }
        });
        ops.add(new Operation("push into thing",3.0f) {
            public boolean defineOperation() {
                this.getRobot().driveBackward(0.3);
                return true;
            }
        });
        ops.add(new Operation("raising hook",1.0f) {
            public boolean defineOperation() {
                getRobot().raiseHook();
                return true;
            }
        });
        ops.add(new Operation("move to wall",1.9f) {
            public boolean defineOperation() {
                getRobot().driveLeft(0.35);
                return true;
            }
        });
        ops.addAll(deployClamp);
        ops.add(new Operation("parking",1.5f){
            public boolean defineOperation() {
                getRobot().driveForward(0.3);
                return true;
            }
        });
        ops.add(new Operation("stop at red line",5f) {
            public boolean defineOperation() {
                getRobot().driveForward(0.2);
                return !this.getRobot().overRedStripe();
            }
        });
        ops.add(new Operation("center",0.5f) {
            public boolean defineOperation() {
                getRobot().driveForward(0.2);
                return true;
            }
        });
        START_RED_FOUNDATION.add(allStartingOps);
        START_RED_FOUNDATION.add(ops);
        ops.clear();

        /*
        just parking operations
         */
        ops.add(new Operation("moving forward", 0.5f) {
            @Override
            public boolean defineOperation() {
                this.getRobot().driveForward(0.4);
                return true;
            }
        });
        ops.add(new Operation("moving right", 0.5f) {
            @Override
            public boolean defineOperation() {
                this.getRobot().driveRight(0.4);
                return !this.getRobot().overBlueStripe()||!this.getRobot().overRedStripe();
            }
        });
        FORWARD_RIGHT.add(allStartingOps);
        FORWARD_RIGHT.add(ops);

        ops.add(new Operation("moving forward", 0.5f) {
            @Override
            public boolean defineOperation() {
                this.getRobot().driveForward(0.4);
                return true;
            }
        });
        ops.add(new Operation("moving left", 0.5f) {
            @Override
            public boolean defineOperation() {
                this.getRobot().driveLeft(0.4);
                return !this.getRobot().overBlueStripe()||!this.getRobot().overRedStripe();
            }
        });
        FORWARD_LEFT.add(allStartingOps);
        FORWARD_LEFT.add(ops);

        ops.add(new Operation("spacing", 0.15f) {
            @Override
            public boolean defineOperation() {
                this.getRobot().driveForward(0.4);
                return true;
            }
        });
        ops.add(new Operation("moving right", 0.5f) {
            @Override
            public boolean defineOperation() {
                this.getRobot().driveRight(0.4);
                return !this.getRobot().overBlueStripe()||!this.getRobot().overRedStripe();
            }
        });
        RIGHT.add(allStartingOps);
        RIGHT.add(ops);

        ops.add(new Operation("spacing", 0.15f) {
            @Override
            public boolean defineOperation() {
                this.getRobot().driveForward(0.4);
                return true;
            }
        });
        ops.add(new Operation("moving left", 0.5f) {
            @Override
            public boolean defineOperation() {
                this.getRobot().driveLeft(0.4);
                return !this.getRobot().overBlueStripe()||!this.getRobot().overRedStripe();
            }
        });
        LEFT.add(allStartingOps);
        LEFT.add(ops);

        ops.clear();

        ops.add(new Operation("wait",16.0f) {
            public boolean defineOperation() {
                return true;
            }
        });

        ops.add(new Operation("move forward",5.0f) {
            public boolean defineOperation() {
                this.getRobot().driveForward(0.2);
                return !this.getRobot().overRedStripe() || !this.getRobot().overBlueStripe();
            }
        });
        START_RED_PARK.add(allStartingOps);
        START_RED_PARK.add(deployClamp);
        START_RED_PARK.add(ops);

        //ops.add(new TurnOperation("gyro turn", 90, 1.0));
        TEST_QUEUE.add(ops);

        ops.clear();
    }
}
