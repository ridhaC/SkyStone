package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name="Red Park",group="Linear Opmode")
public class RedParkAutonomous extends LinearOpMode {
    private RobotHardware robot = new RobotHardware();
    private ElapsedTime elapsedTime = new ElapsedTime();

    public RedParkAutonomous() {

    }

    private OperationQueue opQueue;
    float delayTime = 0.0f;

    public void runOpMode() {
        robot = new RobotHardware();
        robot.init(this.hardwareMap);

        AutonomousQueues.initiate(robot, delayTime);

        robot.setSide(RobotHardware.Side.RED);

        opQueue = AutonomousQueues.START_RED_PARK;

        telemetry.addData("Status","Initialized");
        telemetry.update();

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
