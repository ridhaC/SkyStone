package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name="Tele", group="Linear Opmode")

public class TeleOpMode extends LinearOpMode {

    private RobotHardware hardware;

    boolean turning = false;
    public TeleOpMode() {

    }

    private double fastMovementSpeed = 0.9, slowMovementSpeed = 0.6, movementSpeed = fastMovementSpeed;

    @Override
    public void runOpMode() {
        hardware = new RobotHardware();
        hardware.init(this.hardwareMap);

        telemetry.addData("Status","Initialized");
        telemetry.update();

        waitForStart();
        double spoolPower;
        double rackPower;

        ControllerCommand clampCommand = new ControllerCommand(ControllerCommand.actionable.onPress) {
            @Override
            public void defineOperation() {
                hardware.toggleClamp();
            }
        };
        ControllerCommand hookCommand = new ControllerCommand(ControllerCommand.actionable.onRelease) {
            @Override
            public void defineOperation() {
                hardware.toggleHook();
            }
        };
        ControllerCommand rackSpoolSpeedToggleCommand = new ControllerCommand(ControllerCommand.actionable.onPress) {
            @Override
            public void defineOperation() {
                hardware.rackSpoolSpeedToggle();
            }
        };
        ControllerCommand driveSpeedSlow = new ControllerCommand(ControllerCommand.actionable.onRelease) {
            @Override
            public void defineOperation() {
                movementSpeed = fastMovementSpeed;
            }
        };
        ControllerCommand driveSpeedFast = new ControllerCommand(ControllerCommand.actionable.onPress) {
            public void defineOperation() {
                movementSpeed = slowMovementSpeed;
            }
        };

        ControllerCommand compactClampCommand= new ControllerCommand(ControllerCommand.actionable.onPress) {
            public void defineOperation() {hardware.initialClamps();}
        };

        long last = System.currentTimeMillis();
        //looping condition.. while teleop mode is active.. obvious
        while (opModeIsActive()) {
            getRuntime();
            double ly = gamepad1.left_stick_y, lx = gamepad1.left_stick_x, rx = gamepad1.right_stick_x, ry = gamepad1.right_stick_y;
            telemetry.addData("Joysticks","Left: ("+lx+", "+ly+") Right: ("+rx+", "+ry+")");
            hardware.strafe(ly,-lx,rx,movementSpeed);
            telemetry.addData("Power",hardware.getPowerString());
            rackPower = gamepad1.right_trigger - gamepad1.left_trigger;
            if(gamepad1.right_bumper)
                spoolPower=-1;
            else if (gamepad1.left_bumper)
                spoolPower=1;
            else
                spoolPower=0;
            hardware.driveRack(rackPower);
            hardware.driveSpool(spoolPower);
            telemetry.addData("Rack Power",rackPower);
            telemetry.addData("Color data","rgb("+hardware.getRed()+","+hardware.getGreen()+","+hardware.getBlue()+")");
            telemetry.addData("Distance",hardware.getDistance()+"");

            telemetry.addData("Skystone", hardware.nextToSkystone());
            telemetry.addData("chance",hardware.chanceNextToSkystone());
            telemetry.addData("Angle ", hardware.getAngle());
            telemetry.addData("Acceleration X ", hardware.getAccelerationX());
            telemetry.addData("Acceleration Y ", hardware.getAccelerationY());
            telemetry.addData("Acceleration Z ", hardware.getAccelerationZ());
            telemetry.addData("stage", RobotHardware.clamp);
            long now = System.currentTimeMillis();
            telemetry.addData("DT (ms)",now-last);
            last = now;
            clampCommand.operate(gamepad1.b);
            hookCommand.operate(gamepad1.y);
            rackSpoolSpeedToggleCommand.operate(gamepad1.x);
            compactClampCommand.operate(gamepad1.a);
            driveSpeedSlow.operate(gamepad1.left_stick_button);
            driveSpeedFast.operate(gamepad1.left_stick_button);

            telemetry.update();
        }
        //after teleop is stopped
        telemetry.addData("Status","Finished");
        telemetry.update();
    }
}
