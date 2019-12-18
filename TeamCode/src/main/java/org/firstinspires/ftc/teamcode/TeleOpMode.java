package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name="Tele", group="Linear Opmode")

public class TeleOpMode extends LinearOpMode {

    private RobotHardware hardware;

    boolean turning = false;
    public TeleOpMode() {

    }

    private double movementSpeed = 1.0;
    private boolean dpadType = false;

    ControllerCommand dpadTypeCommand = new ControllerCommand(ControllerCommand.actionable.onPress) {
        @Override
        public void defineOperation() {
            dpadType=!dpadType;
        }
    };
    ControllerCommand dpadMoveRightCommand = new ControllerCommand(ControllerCommand.actionable.whileDown) {
        @Override
        public void defineOperation() {
            hardware.driveRight(0.5);
        }
    };

    ControllerCommand dpadMoveLeftCommand = new ControllerCommand(ControllerCommand.actionable.whileDown) {
        @Override
        public void defineOperation() {
            hardware.driveLeft(0.5);
        }
    };
    ControllerCommand dpadMoveForwardCommand = new ControllerCommand(ControllerCommand.actionable.whileDown) {
        @Override
        public void defineOperation() {
            hardware.driveForward(0.5);
        }
    };
    ControllerCommand dpadMoveBackwardCommand = new ControllerCommand(ControllerCommand.actionable.whileDown) {
        @Override
        public void defineOperation() {
            hardware.driveBackward(0.5);
        }
    };
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
    ControllerCommand increaseDriveSpeed = new ControllerCommand(ControllerCommand.actionable.onPress) {
        @Override
        public void defineOperation() {
            movementSpeed += 0.2;
            if (movementSpeed > 1)
                movementSpeed = 1;
        }
    };
    ControllerCommand decreaseDriveSpeed = new ControllerCommand(ControllerCommand.actionable.onPress) {
        public void defineOperation() {
            movementSpeed -= 0.2;
            if (movementSpeed < 0.2)
                movementSpeed = 0.2;
        }
    };

    ControllerCommand openServos = new ControllerCommand(ControllerCommand.actionable.onPress) {
        @Override
        public void defineOperation() {
            hardware.fullOpenClamp();
        }
    };

    ControllerCommand compactClampCommand= new ControllerCommand(ControllerCommand.actionable.onPress) {
        public void defineOperation() {hardware.initialClamps();}
    };

    @Override
    public void runOpMode() {

        hardware = new RobotHardware();
        hardware.init(this.hardwareMap);

        telemetry.addData("Status","Initialized");
        telemetry.update();

        telemetry.addData("READY","");
        telemetry.update();

        waitForStart();

        double spoolPower;
        double rackPower;


        long last = System.currentTimeMillis();
            //looping condition.. while teleop mode is active.. obvious
        while (opModeIsActive()) {
            getRuntime();

            clampCommand.operate(gamepad1.b);
            hookCommand.operate(gamepad1.y);
            rackSpoolSpeedToggleCommand.operate(gamepad1.x);
            compactClampCommand.operate(gamepad1.a);

            this.openServos.operate(gamepad1.back);
            decreaseDriveSpeed.operate(gamepad1.dpad_down);
            increaseDriveSpeed.operate(gamepad1.dpad_up);

            double ly = gamepad1.left_stick_y, lx = gamepad1.left_stick_x, rx = gamepad1.right_stick_x, ry = gamepad1.right_stick_y;
            telemetry.addData("Joysticks","Left: ("+lx+", "+ly+") Right: ("+rx+", "+ry+")");
            hardware.strafe(ly,-lx,rx,movementSpeed);
            telemetry.addData("Power",hardware.getPowerString());
            rackPower = gamepad1.right_trigger - gamepad1.left_trigger;
            if(gamepad1.right_bumper)
                spoolPower=1;
            else if (gamepad1.left_bumper)
                spoolPower=-1;
            else
                spoolPower=0;
            hardware.driveRack(rackPower);
            hardware.driveSpool(spoolPower);
            telemetry.addData("Rack Power",rackPower);

            telemetry.addData("Speed",movementSpeed);

            long now = System.currentTimeMillis();
            telemetry.addData("DT (ms)",now-last);
            last = now;
            telemetry.addData("Angle", this.hardware.getAngle());


            telemetry.update();
        }
        //after teleop is stopped
        telemetry.addData("Status","Finished");
        telemetry.update();
    }
}
