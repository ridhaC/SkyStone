package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.ArrayList;
import java.util.List;

@Autonomous(name="Main Autonomous",group="Linear Opmode")
public class AutonomousOpMode extends LinearOpMode {
    private RobotHardware robot = new RobotHardware();
    private ElapsedTime elapsedTime = new ElapsedTime();

    public AutonomousOpMode() {

    }

    private OperationQueue opQueue;
    private int opInt;
    float delayTime = 1.0f;

    ControllerCommand delayIncrement = new ControllerCommand(ControllerCommand.actionable.onPress) {
        @Override
        public void defineOperation() {delayTime+=0.5f;}
    };
    ControllerCommand delayDecrement = new ControllerCommand(ControllerCommand.actionable.onPress) {
        @Override
        public void defineOperation() {delayTime-=0.5f;}
    };
    ControllerCommand blueFoundation = new ControllerCommand(ControllerCommand.actionable.onPress) {
        @Override
        public void defineOperation() {opInt = AutonomousQueues.BLUE_FOUNDATION_INT;}
    };
    ControllerCommand blueStones = new ControllerCommand(ControllerCommand.actionable.onPress) {
        @Override
        public void defineOperation() {opInt = AutonomousQueues.BLUE_STONES_INT;}
    };
    ControllerCommand redFoundation = new ControllerCommand(ControllerCommand.actionable.onPress) {
        @Override
        public void defineOperation() {opInt = AutonomousQueues.RED_FOUNDATION_INT;}
    };
    ControllerCommand redStones = new ControllerCommand(ControllerCommand.actionable.onPress) {
        @Override
        public void defineOperation() {opInt = AutonomousQueues.RED_STONES_INT;}
    };
    public void runOpMode() {
        robot = new RobotHardware();
        robot.init(this.hardwareMap);

        opInt = AutonomousQueues.BLUE_FOUNDATION_INT;
        
        telemetry.addData("Status","Initialized");
        telemetry.update();

        String pos = "BLUE FOUNDATION";

        while (!gamepad1.back) {
            blueFoundation.operate(gamepad1.y);
            blueStones.operate(gamepad1.b);
            redFoundation.operate(gamepad1.a);
            redStones.operate(gamepad1.x);
            delayDecrement.operate(gamepad1.dpad_down);
            delayIncrement.operate(gamepad1.dpad_up);

            switch (opInt) {
                case AutonomousQueues.BLUE_FOUNDATION_INT:
                    pos = "BLUE FOUNDATION";
                    break;
                case AutonomousQueues.RED_FOUNDATION_INT:
                    pos = "RED FOUNDATION";
                    break;
                case AutonomousQueues.BLUE_STONES_INT:
                    pos = "BLUE STONES";
                    break;
                case AutonomousQueues.RED_STONES_INT:
                    pos = "RED STONES";
                    break;
            }

            telemetry.addData("Position",pos);

            telemetry.addData("Delay", delayTime);

            telemetry.addData("PRESS 'BACK' TO READY UP","");

            telemetry.update();
        }

        telemetry.addData("READY FOR START","");
        telemetry.addData("Position",pos);

        telemetry.update();

        AutonomousQueues.initiate(robot, delayTime);

        switch(opInt)   {
            case AutonomousQueues.BLUE_FOUNDATION_INT:
                opQueue = AutonomousQueues.START_BLUE_FOUNDATION;
                robot.setSide(RobotHardware.Side.BLUE);
                break;
            case AutonomousQueues.BLUE_STONES_INT:
                opQueue = AutonomousQueues.START_BLUE_STONES;
                robot.setSide(RobotHardware.Side.BLUE);
                break;
            case AutonomousQueues.RED_FOUNDATION_INT:
                opQueue = AutonomousQueues.START_RED_FOUNDATION;
                robot.setSide(RobotHardware.Side.RED);
                break;
            case AutonomousQueues.RED_STONES_INT:
                opQueue = AutonomousQueues.START_RED_STONES;
                robot.setSide(RobotHardware.Side.RED);
                break;
        }

        waitForStart();
        while (opModeIsActive()) {
            boolean t = opQueue.operate();
            String msg = "opqueue completed";
            if (t)
                msg = opQueue.getCurrentOperation().getName();
            telemetry.addData("Current Objective",msg);
            telemetry.addData("ss chance",robot.chanceNextToSkystone());
            telemetry.addData("skystone",robot.nextToSkystone());
            telemetry.addData("Angle ", robot.getAngle());
            telemetry.update();
        }
    }
}