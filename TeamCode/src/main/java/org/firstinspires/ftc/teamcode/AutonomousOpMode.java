package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name="Main Autonomous",group="Linear Opmode")
public class AutonomousOpMode extends LinearOpMode {
    private RobotHardware robot = new RobotHardware();
    private ElapsedTime elapsedTime = new ElapsedTime();



    public AutonomousOpMode() {

    }

    private OperationQueue opQueue;

    public void runOpMode() {
        robot = new RobotHardware();
        robot.init(this.hardwareMap);

        AutonomousQueues.initiate(robot);
        opQueue = AutonomousQueues.START_BLUE_FOUNDATION;
        
        telemetry.addData("Status","Initialized");
        telemetry.update();

        robot.compactClamp();

        while (!gamepad1.start) {
            if (gamepad1.x)
                opQueue = AutonomousQueues.START_BLUE_FOUNDATION;
            if (gamepad1.b)
                opQueue = AutonomousQueues.START_RED_FOUNDATION;
            if (gamepad1.y)
                opQueue = AutonomousQueues.START_BLUE_STONES;
            if (gamepad1.a)
                opQueue = AutonomousQueues.START_RED_STONES;

            if (opQueue == AutonomousQueues.START_BLUE_FOUNDATION)
                telemetry.addData("Position","BLUE FOUNDATION");
            else if (opQueue == AutonomousQueues.START_RED_FOUNDATION)

                telemetry.addData("Position","RED FOUNDATION");
            else if  (opQueue == AutonomousQueues.START_BLUE_STONES)
                telemetry.addData("Position","BLUE STONES");
            else
                telemetry.addData("Position","RED STONES");
            telemetry.update();
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




