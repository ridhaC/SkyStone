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

        while (!gamepad1.start) {
            blueFoundation.operate(gamepad1.y);
            blueStones.operate(gamepad1.b);
            redFoundation.operate(gamepad1.a);
            redStones.operate(gamepad1.x);
            delayDecrement.operate(gamepad1.dpad_down);
            delayIncrement.operate(gamepad1.dpad_up);

            if (opInt == AutonomousQueues.BLUE_FOUNDATION_INT)
                telemetry.addData("Position","BLUE FOUNDATION");
            else if (opInt == AutonomousQueues.RED_FOUNDATION_INT)
                telemetry.addData("Position","RED FOUNDATION");
            else if  (opInt == AutonomousQueues.BLUE_STONES_INT)
                telemetry.addData("Position","BLUE STONES");
            else
                telemetry.addData("Position","RED STONES");
            telemetry.addData("Delay", delayTime);
            telemetry.update();
        }

        AutonomousQueues.initiate(robot, delayTime);

        switch(opInt)   {
            case AutonomousQueues.BLUE_FOUNDATION_INT:
                opQueue = AutonomousQueues.START_BLUE_FOUNDATION;
                break;
            case AutonomousQueues.BLUE_STONES_INT:
                opQueue = AutonomousQueues.START_BLUE_STONES;
                break;

            case AutonomousQueues.RED_FOUNDATION_INT:
                opQueue = AutonomousQueues.START_RED_FOUNDATION;
                break;

            case AutonomousQueues.RED_STONES_INT:
                opQueue = AutonomousQueues.START_RED_STONES;
                break;
        }

        waitForStart();
        while (opModeIsActive()) {
            boolean t = opQueue.operate();
            String msg = "opqueue completed";
            if (t)
                msg = opQueue.getCurrentOperation().getName();
            telemetry.addData("Current Objective",msg);
            telemetry.addData("Angle ", robot.getAngle());
            telemetry.update();
        }
    }
}




