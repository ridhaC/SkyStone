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
        public void defineOperation() {opQueue = AutonomousQueues.START_BLUE_FOUNDATION;}
    };
    ControllerCommand blueStones = new ControllerCommand(ControllerCommand.actionable.onPress) {
        @Override
        public void defineOperation() {opQueue = AutonomousQueues.START_BLUE_STONES;}
    };
    ControllerCommand redFoundation = new ControllerCommand(ControllerCommand.actionable.onPress) {
        @Override
        public void defineOperation() {opQueue = AutonomousQueues.START_RED_FOUNDATION;}
    };
    ControllerCommand redStones = new ControllerCommand(ControllerCommand.actionable.onPress) {
        @Override
        public void defineOperation() {opQueue = AutonomousQueues.START_RED_STONES;}
    };
    public void runOpMode() {
        robot = new RobotHardware();
        robot.init(this.hardwareMap);

        AutonomousQueues.initiate(robot, delayTime);
        opQueue = AutonomousQueues.START_BLUE_FOUNDATION;
        
        telemetry.addData("Status","Initialized");
        telemetry.update();

        while (!gamepad1.start) {
            blueFoundation.defineOperation();
            blueStones.defineOperation();
            redFoundation.defineOperation();
            redStones.defineOperation();
            delayDecrement.defineOperation();
            delayIncrement.defineOperation();

            if (opQueue == AutonomousQueues.START_BLUE_FOUNDATION)
                telemetry.addData("Position","BLUE FOUNDATION");
            else if (opQueue == AutonomousQueues.START_RED_FOUNDATION)
                telemetry.addData("Position","RED FOUNDATION");
            else if  (opQueue == AutonomousQueues.START_BLUE_STONES)
                telemetry.addData("Position","BLUE STONES");
            else
                telemetry.addData("Position","RED STONES");
            telemetry.addData("Delay", delayTime);
            telemetry.update();
        }
        opQueue = AutonomousQueues.START_BLUE_FOUNDATION;
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




