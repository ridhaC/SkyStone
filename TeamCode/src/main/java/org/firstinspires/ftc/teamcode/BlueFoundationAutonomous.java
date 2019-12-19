package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name="Blue Foundation",group="Linear Opmode")
public class BlueFoundationAutonomous extends LinearOpMode {
    private RobotHardware robot = new RobotHardware();
    private ElapsedTime elapsedTime = new ElapsedTime();

    public BlueFoundationAutonomous() {

    }

    private OperationQueue opQueue;
    float delayTime = 0.0f;

    public void runOpMode() {
        robot = new RobotHardware();
        robot.init(this.hardwareMap);

        AutonomousQueues.initiate(robot, delayTime);

        robot.setSide(RobotHardware.Side.RIGHT);

        opQueue = AutonomousQueues.START_BLUE_FOUNDATION;

        telemetry.addData("Status","Initialized");
        telemetry.update();

        waitForStart();
        while (opModeIsActive()) {
            boolean t = opQueue.operate();
            String msg = "opqueue completed";
            if (t)
                msg = opQueue.getCurrentOperation().getName();
            telemetry.addData("Current Objective",msg);
            telemetry.addData("left ss chance",robot.chanceNextToSkystone(RobotHardware.LEFT_COLOR_SIDE));
            telemetry.addData("left skystone",robot.nextToSkystone(RobotHardware.LEFT_COLOR_SIDE));
            telemetry.addData("right ss chance",robot.chanceNextToSkystone(RobotHardware.RIGHT_COLOR_SIDE));
            telemetry.addData("right skystone",robot.nextToSkystone(RobotHardware.RIGHT_COLOR_SIDE));
            telemetry.addData("Angle ", robot.getAngle());
            telemetry.update();
        }
    }
}
