package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * This file illustrates the concept of driving a path based on encoder counts.
 * It uses the common Pushbot hardware class to define the drive on the robot.
 * The code is structured as a LinearOpMode
 *
 * The code REQUIRES that you DO have encoders on the wheels,
 *   otherwise you would use: PushbotAutoDriveByTime;
 *
 *  This code ALSO requires that the drive Motors have been configured such that a positive
 *  power command moves them forwards, and causes the encoders to count UP.
 *
 *   The desired path in this example is:
 *   - Drive forward for 48 inches
 *   - Spin right for 12 Inches
 *   - Drive Backwards for 24 inches
 *   - Stop and close the claw.
 *
 *  The code is written using a method called: encoderDrive(speed, leftInches, rightInches, timeoutS)
 *  that performs the actual movement.
 *  This methods assumes that each movement is relative to the last stopping place.
 *  There are other ways to perform encoder based moves, but this method is probably the simplest.
 *  This code uses the RUN_TO_POSITION mode to enable the Motor controllers to generate the run profile
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@Autonomous(name="Encoder", group="LinearOpMode ")
//@Disabled
public class EncoderTester extends LinearOpMode {

    /* Declare OpMode members. */
    DcMotor testMotor;
    private ElapsedTime runtime = new ElapsedTime();

    static final double     COUNTS_PER_MOTOR_REV    = 1440 ;    // eg: TETRIX Motor Encoder
    static final double     DRIVE_GEAR_REDUCTION    = 0.5 ;     // This is < 1.0 if geared UP
    static final double     WHEEL_DIAMETER_INCHES   = 4.0 ;     // For figuring circumference
    static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * 3.1415);
    static final double     DRIVE_SPEED             = 0.1;
    static final double     TURN_SPEED              = 0.1;

    @Override
    public void runOpMode() {

        /*
         * Initialize the drive system variables.
         * The init() method of the hardware class does all the work here
         */

        testMotor = hardwareMap.get(DcMotor.class, "test_motor");
        testMotor.setDirection(DcMotor.Direction.FORWARD);
        // Send telemetry message to signify robot waiting;
        telemetry.addData("Initial",  "Motor %7d :",
                testMotor.getCurrentPosition());

        testMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        testMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Send telemetry message to indicate successful Encoder reset
        telemetry.addData("Reset",  "Motor %7d :",
                testMotor.getCurrentPosition());
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        testMotor.setTargetPosition(1000);

        while (opModeIsActive()) {
            testMotor.setPower(0.5);
            telemetry.addData("Motor",testMotor.getCurrentPosition()+" to "+testMotor.getTargetPosition());
        }

        telemetry.addData("Path", "Complete");
        telemetry.update();
    }

    public void doEncodersWork() {
        testMotor.setDirection(DcMotor.Direction.FORWARD);
        testMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        testMotor.setPower(0.5);

        while (opModeIsActive()) {
            telemetry.addData("Encoder Posiiton",testMotor.getCurrentPosition());
            telemetry.update();
        }
    }

    public void basicEncoderDrive() {
        testMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        testMotor.setTargetPosition(10000);

        testMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        testMotor.setPower(0.5);

        int iterations = 0;

        while (opModeIsActive() && isMotorBusy(testMotor)) {
            iterations++;
            //update some telemetry stuff
            telemetry.addData("Target Position",testMotor.getTargetPosition());
            telemetry.addData("Current Position",testMotor.getCurrentPosition());
            telemetry.addData("Current Power",testMotor.getPower());
            telemetry.addData("Iterations",iterations);
            telemetry.update();
        }
    }

    /*
    using this method to get around encoder error
    where the encoder position goes in the opposite
    direction of what it should.
     */
    public boolean isMotorBusy(DcMotor motor) {
        int curPos = Math.abs(motor.getCurrentPosition());
        int targetPos = Math.abs(motor.getTargetPosition());

        if (targetPos-curPos <= 0)
            return false;
        else
            return true;
    }

    /*
     *  Method to perfmorm a relative move, based on encoder counts.
     *  Encoders are not reset as the move is based on the current position.
     *  Move will stop if any of three conditions occur:
     *  1) Move gets to the desired position
     *  2) Move runs out of time
     *  3) Driver stops the opmode running.
     */
    public void encoderDrive(double speed,
                             double leftInches, double rightInches,
                             double timeoutS) {
        int newLeftTarget;
        int newRightTarget;

        runtime.reset();
        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            testMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

            // Determine new target position, and pass to motor controller
            newLeftTarget = testMotor.getCurrentPosition() + (int)(leftInches * COUNTS_PER_INCH);
            testMotor.setTargetPosition(newLeftTarget);

            testMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            if(testMotor.getCurrentPosition()>newLeftTarget)
                testMotor.setPower(-speed);
            else
                testMotor.setPower(speed);

            // keep looping while we are still active, and there is time left, and both motors are running.
            // Note: We use (isBusy() && isBusy()) in the loop test, which means that when EITHER motor hits
            // its target position, the motion will stop.  This is "safer" in the event that the robot will
            // always end the motion as soon as possible.
            // However, if you require that BOTH motors have finished their moves before the robot continues
            // onto the next step, use (isBusy() || isBusy()) in the loop test.
            while (opModeIsActive() &&
                    (runtime.seconds() < timeoutS) &&
                    testMotor.isBusy()) {

                // Display it for the driver.
                telemetry.addData("Speed", "%.1f  Left: %.1f  Right: %.1f  CPR: %.1f", speed, leftInches, rightInches, COUNTS_PER_INCH);
                telemetry.addData("TestMotor",  "Distance to target %7d", (newLeftTarget-testMotor.getCurrentPosition()));
                telemetry.addData("TestMotor Encoder", testMotor.getCurrentPosition());
                telemetry.update();
            }

            // Stop all motion;
            testMotor.setPower(0);

            // Turn off RUN_TO_POSITION
            testMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            sleep(250);   // optional pause after each move
        }
    }
}