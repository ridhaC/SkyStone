package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.*;
import com.qualcomm.robotcore.hardware.*;
import org.firstinspires.ftc.robotcore.external.navigation.*;

public class RobotHardware {
    //this is used for autonomous. not necessary in teleop
    public static enum Side {
        RIGHT,
        LEFT
    }
    private Side side = Side.RIGHT;
    public void setSide(Side side) {
        this.side = side;
    }

    public static final int LEFT_COLOR_SIDE = 0, RIGHT_COLOR_SIDE = 1;

    private int skystonePosition = -1;

    //encourage data encapsulation
    //wheel motors
    private DcMotor frontLeftDrive;
    private DcMotor frontRightDrive;
    private DcMotor backLeftDrive;
    private DcMotor backRightDrive;

    //rack drive
    private DcMotor rackDrive; //connects to both motors
    private DcMotor spoolDrive;

    private ColorSensor frontLeftColorSensor;
    private DistanceSensor frontLeftDistanceSensor;
    public ColorSensor frontRightColorSensor;
    public DistanceSensor frontRightDistanceSensor;
    private ColorSensor bottomColorSensor;
    private DistanceSensor bottomDistanceSensor;
    //private DistanceSensor backDistanceSensor;

    BNO055IMU imu;
    Orientation lastAngles = new Orientation();
    double globalAngle = 0;

    //information about wheel movement.
    static final double     COUNTS_PER_MOTOR_REV    = 1440 ;    // eg: TETRIX Motor Encoder
    static final double     DRIVE_GEAR_REDUCTION    = 0.5 ;     // This is < 1.0 if geared UP
    static final double     WHEEL_DIAMETER_INCHES   = 4.0 ;     // For figuring circumference
    static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * 3.1415);

    //clamp servo
    private Servo insideClampServo;
    private Servo outsideClampServo;
    double outsideClampStartingPosition = 0.05;
    double insideClampStartingPosition = 0.43;
    double outsideClampClosedPosition = 0.0;
    double insideClampClosedPosition = 1.0;
    double outsideClampOpenPosition = 0.7;
    double insideClampOpenPosition = 0.9;

    //foundation hook servos
    private Servo leftHook, rightHook;
    double leftHookClosedPosition = 1.0;
    double leftHookOpenPosition = 0.5;
    double rightHookClosedPosition = 0.0;
    double rightHookOpenPosition = 0.5;

    public RobotHardware() {

    }

    private HardwareMap hMap;

    public void init(HardwareMap ahMap) {
        hMap = ahMap;
        try {
            frontLeftDrive = hMap.get(DcMotor.class, "front_left_drive");
            frontRightDrive = hMap.get(DcMotor.class, "front_right_drive");
            backLeftDrive = hMap.get(DcMotor.class, "back_left_drive");
            backRightDrive = hMap.get(DcMotor.class, "back_right_drive");

            frontLeftDrive.setDirection(DcMotor.Direction.FORWARD);
            frontRightDrive.setDirection(DcMotor.Direction.REVERSE);
            backLeftDrive.setDirection(DcMotor.Direction.FORWARD);
            backRightDrive.setDirection(DcMotor.Direction.REVERSE);

            //use encoders
            frontLeftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            frontRightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            backLeftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            backRightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

            frontLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            frontRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            backLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            backRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            stopAllMotors();

            //racks
            rackDrive = hMap.get(DcMotor.class,"rack");
            rackDrive.setDirection(DcMotor.Direction.REVERSE);
            rackDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            rackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            rackDrive.setPower(0);

            //spool
            spoolDrive = hMap.get(DcMotor.class,"spool");
            spoolDrive.setDirection(DcMotor.Direction.FORWARD);
            spoolDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            spoolDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            spoolDrive.setPower(0);

            //sensors
            frontLeftColorSensor = hMap.get(ColorSensor.class,"front_left_color_sensor");
            frontLeftDistanceSensor = hMap.get(DistanceSensor.class,"front_left_color_sensor");

            frontRightColorSensor = hMap.get(ColorSensor.class,"front_right_color_sensor");
            frontRightDistanceSensor = hMap.get(DistanceSensor.class,"front_right_color_sensor");

            bottomColorSensor = hMap.get(ColorSensor.class,"bottom_color_sensor");
            bottomDistanceSensor = hMap.get(DistanceSensor.class,"bottom_color_sensor");

            //backDistanceSensor = hMap.get(DistanceSensor.class, "back_distance_sensor");

            BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
            parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
            parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
            parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample opmode
            parameters.loggingEnabled      = true;
            parameters.loggingTag          = "IMU";
            parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();

            // Retrieve and initialize the IMU. We expect the IMU to be attached to an I2C port
            // on a Core Device Interface Module, configured to be a sensor of type "AdaFruit IMU",
            // and named "imu".
            imu = hMap.get(BNO055IMU.class, "imu");
            imu.initialize(parameters);

            imu.startAccelerationIntegration(new Position(), new Velocity(), 1000);

            //servos
            outsideClampServo = hMap.get(Servo.class, "outside_clamp");
            insideClampServo = hMap.get(Servo.class, "inside_clamp");

            leftHook = hMap.get(Servo.class,"left_hook");
            rightHook = hMap.get(Servo.class,"right_hook");
        } catch (NullPointerException e) {
            //drives couldn't initialize.. let the program run anyway
        }
    }

    public int getRed(int side) {
        switch (side) {
            case RIGHT_COLOR_SIDE:
                return this.frontRightColorSensor.red();
            case LEFT_COLOR_SIDE:
                return this.frontLeftColorSensor.red();
        }
        return -1; //no valid side specified
    }

    public int getGreen(int side) {
        switch (side) {
            case RIGHT_COLOR_SIDE:
                return this.frontRightColorSensor.green();
            case LEFT_COLOR_SIDE:
                return this.frontLeftColorSensor.green();
        }
        return -1; //no valid side specified
    }

    public int getBlue(int side) {
        switch (side) {
            case RIGHT_COLOR_SIDE:
                return this.frontRightColorSensor.blue();
            case LEFT_COLOR_SIDE:
                return this.frontLeftColorSensor.blue();
        }
        return -1; //no valid side specified
    }

    //TODO
    //public double getBackDistance()    {
    //    return backDistanceSensor.getDistance(DistanceUnit.CM);
    //}

    /**
     * Returns the chance that a specified color sensor is in front of a Skystone
     * @param side The integer value of the side to check
     * @return The confidence that the sensor is in front of a Skystone
     */
    public double chanceNextToSkystone(int side) {
        double red, green, blue, dist, value, e, chance;
        switch (side) {
            case LEFT_COLOR_SIDE:
                //scale the data according to what was used in the logistic regression
                red = (getRed(side) - 231.0) / 398.0;
                green = (getGreen(side) - 364.791667) / 745.0;
                blue = (getBlue(side) - 155.5) / 393.0;
                dist = (getDistance(side) - 3.475) / 1.5;
                //apply the weights to get the predicted value
                value = 2.02173 - 38.9551 * red + 29.4573 * green + 43.8142 * blue - 5.1269 * dist;
                //apply the sigmoid function to determine chance of skystone
                e = Math.exp(-value);
                chance = 1 / (1 + e);
                //if that chance is >0.5, we are more than likely next to a skystone
                return chance;
            case RIGHT_COLOR_SIDE:
                //scale the data according to what was used in the logistic regression
                red = (getRed(side) - 595.0) / 2826.0;
                green = (getGreen(side) - 957.690) / 4270.0;
                blue = (getBlue(side) - 337.172) / 1194.0;
                dist = (getDistance(side) - 2.780) / 2.4;
                //apply the weights to get the predicted value
                value = -4.019 - 60.019 * red - 44.996 * green + 51.616 * blue - 5.26127 * dist;
                //apply the sigmoid function to determine chance of skystone
                e = Math.exp(-value);
                chance = 1 / (1 + e);
                //if that chance is >0.5, we are more than likely next to a skystone
                return chance;
        }
        return 0.0f; //if no valid side given then 0% chance.
    }

    /**
     * Uses the confidence value that the specified color sensor to determine if
     * that side is in front of a Skystone.
     * Uses a confidence threshold of at least 50% confidence.
     * @param side
     * @return True if next to SS, False if not.
     */
    public boolean nextToSkystone(int side) {
        return chanceNextToSkystone(side) > 0.5;
    }

    public double getDistance(int side) {
        DistanceSensor sensor = null;
        switch (side) {
            case LEFT_COLOR_SIDE:
                sensor = frontLeftDistanceSensor;
                break;
            case RIGHT_COLOR_SIDE:
                sensor = frontRightDistanceSensor;
        }
        if (sensor == null)
            return -1;
        else
            return sensor.getDistance(DistanceUnit.CM);
    }

    private int sum(ColorSensor cs) {
        return cs.blue()+cs.red()+cs.green();
    }

    public double blueRatio() {
        return (double)bottomColorSensor.blue()/sum(bottomColorSensor);
    }

    public double redRatio() {
        return (double)bottomColorSensor.red()/sum(bottomColorSensor);
    }

    public boolean overBlueStripe() {
        return blueRatio() > 0.32;
    }

    public boolean overRedStripe() {
        return redRatio() > 0.32;
    }

    public double getAccelerationX()    {
        return imu.getVelocity().xVeloc;
    }

    public double getAccelerationY()    {
        return imu.getVelocity().yVeloc;
    }

    public double getAccelerationZ()    {
        return imu.getVelocity().zVeloc;
    }

    double movementSpeed = 0.7;
    public void driveWheels(double lfPower, double rfPower, double lbPower, double rbPower) {
        frontLeftDrive.setPower(lfPower);
        frontRightDrive.setPower(rfPower);
        backLeftDrive.setPower(lbPower);
        backRightDrive.setPower(rbPower);
    }

    public void driveForward(double power) {
        strafe(-1,0,0,power);
    }

    public void driveBackward(double power) {
        strafe(1,0,0,power);
    }

    public void driveLeft(double power) {
        strafe(0,1,0,power);
    }

    public void driveRight(double power) {
        strafe(0,-1,0,power);
    }

    /**
     * Moves the robot in a direction defined by theta in radians
     * Theta = 0 - the robot goes right
     * Theta = pi/2 - the robot goes forward... and so on
     * @param theta
     * @param power
     */
    public void driveAngle(double theta, double power) {
        double y = Math.sin(theta);
        double x = Math.cos(theta);
        strafe(y,x,0,power);
    }

    /**
     * Moves the robot in a direction defined by a degree
     * 0 deg - robot goes left
     * 90 deg - robot goes forward
     * @param degrees
     * @param power
     */
    public void driveAngle(int degrees, double power) {
        driveAngle(degrees/180*Math.PI,power);
    }

    public void turnRight(double power) {
        strafe(0,0,1, power);
    }

    public void turnLeft(double power) {
        strafe(0,0,-1,power);
    }

    double lf = 0, rf = 0, rb = 0, lb = 0;
    public void strafe(double drive, double strafe, double turn, double power) {
        //drive and strafe are on scale of [-1, 1]
        //drive is the y component for forward movement
        //          lf, rf, lb, rb
        //forward - +1, +1, +1, +1
        //backward- -1, -1, -1, -1
        //right   - +1, -1, -1  +1
        //left    - -1, +1, +1, -1
        turn *= 0.8; //limit the turn speed a bit
        lf = drive + strafe - turn;
        rf = drive - strafe + turn;
        rb = drive + strafe + turn;
        lb = drive - strafe - turn;

        //adjust motor powers by a scalar
        lf*=power;
        rf*=power;
        rb*=power;
        lb*=power;
        //set the powers
        driveWheels(lf,rf,lb,rb);
    }

    public String getPowerString() {
        return "FL: "+frontLeftDrive.getPower()+", FR: "+frontRightDrive.getPower()+", BL: "+backLeftDrive.getPower()+", BR: "+backRightDrive.getPower();
    }

    public void setWheelEncoderPositions(int lf, int rf, int lb, int rb) {
        frontLeftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontLeftDrive.setTargetPosition(lf);
        frontRightDrive.setTargetPosition(rf);
        backLeftDrive.setTargetPosition(lb);
        backRightDrive.setTargetPosition(rb);
        frontLeftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontRightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    //updates the powers of motors that are trying to reach an encoder value.
    public void updateBusyMotors() {
        DcMotor[] motors = {frontLeftDrive,frontRightDrive,backLeftDrive,backRightDrive};
        for (DcMotor motor : motors) {
            if (isMotorBusy(motor)) {
                //what is the difference to the target position
                //uses absolute value to accomodate for faulty encoder stuff
                int difference = Math.abs(motor.getTargetPosition())-Math.abs(motor.getCurrentPosition());
                //the percent of the way to reaching the target position
                double percent = (double)difference/Math.abs(motor.getTargetPosition());
                if (percent > 0.7) {
                    percent = (percent-0.7)/0.3;
                    double power = motor.getPower()*((1-percent)*0.05);
                    if (power < 0.2)
                        power = 0.2;
                    motor.setPower(power);
                }
            } else {
                motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            }
        }
    }

    boolean firstRun = true, turnFinished = false;
    double targetAngle = 0, pointTurnPower;
    public boolean turnDegrees(double degrees)  {
        if(firstRun)    {
            targetAngle = getAngle()+degrees;
            firstRun = false;
            turnFinished = false;
        }
        pointTurnPower = degrees/Math.abs(degrees);
        pointTurnPower = targetAngle-getAngle()/Math.abs(degrees);
        turnFinished = isTurnBusy();
        driveWheels(pointTurnPower, -pointTurnPower, pointTurnPower, -pointTurnPower);
        if(turnFinished)   {
            resetAngle();
            firstRun = true;
            stopAllMotors();
        }
        return turnFinished;
    }
    double accuracyThreshold = 5;
    public boolean isTurnBusy() {
        if (Math.abs(targetAngle)-Math.abs(getAngle())<accuracyThreshold)
            return false;
        return true;
    }

    public double getAngle()    {
        // We experimentally determined the Z axis is the axis we want to use for heading angle.
        // We have to process the angle because the imu works in euler angles so the Z axis is
        // returned as 0 to +180 or 0 to -180 rolling back to -179 or +179 when rotation passes
        // 180 degrees. We detect this transition and track the total cumulative angle of rotation.

        Orientation angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

        double deltaAngle = angles.firstAngle - lastAngles.firstAngle;

        if (deltaAngle < -180)
            deltaAngle += 360;
        else if (deltaAngle > 180)
            deltaAngle -= 360;

        globalAngle += deltaAngle;

        lastAngles = angles;

        return globalAngle;
    }

    public double getAngleRadians() {
        return Math.toRadians(this.getAngle());
    }

    private void resetAngle() {
        lastAngles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

        globalAngle = 0;
    }

    public void stopWheels() {
        frontLeftDrive.setPower(0);
        frontRightDrive.setPower(0);
        backLeftDrive.setPower(0);
        backRightDrive.setPower(0);
    }

    double rackPowerCoefiicent = 0.7;
    public void driveRack(double power) {rackDrive.setPower(power*rackPowerCoefiicent);}

    double spoolPowerCoeficcent = 0.6;
    public void driveSpool(double power)    {
        spoolDrive.setPower(power*spoolPowerCoeficcent);
    }
    public void rackSpoolSpeedToggle()   {
        if(rackPowerCoefiicent == 0.7) {
            rackPowerCoefiicent = 0.5;
            spoolPowerCoeficcent = 0.3;
        }
        else    {
            rackPowerCoefiicent = 0.7;
            spoolPowerCoeficcent = 0.5;
        }
    }

    private boolean fast = true;
    public void driveSpeedToggle()   {
        fast = !fast;
        if (fast)
            movementSpeed = 0.7;
        else
            movementSpeed = 0.4;
    }

    public boolean isMotorBusy(DcMotor motor) {
        int curPos = Math.abs(motor.getCurrentPosition());
        int targetPos = Math.abs(motor.getTargetPosition());

        if (targetPos-curPos <= 0)
            return false;
        else
            return true;
    }

    //returns true if any of the drive motors are busy reaching an encoder value
    public boolean isDriveBusy() {
        return isMotorBusy(frontLeftDrive) || isMotorBusy(frontRightDrive) || isMotorBusy(backLeftDrive) || isMotorBusy(backRightDrive);
    }

    public int clamp = 0;
    public void toggleClamp() {
        switch (clamp) {
            case 0:
                closeClamp();
                clamp = 1;
                break;
            case 1:
                openClamp();
                clamp = 0;
                break;
        }
    }

    public void initialClamps()  {
        outsideClampServo.setPosition(outsideClampStartingPosition);
        insideClampServo.setPosition(insideClampStartingPosition);
    }

    public void closeClamp() {
        outsideClampServo.setPosition(outsideClampClosedPosition);
        insideClampServo.setPosition(insideClampClosedPosition);
    }

    public void openClamp() {
        outsideClampServo.setPosition(outsideClampOpenPosition);
        insideClampServo.setPosition(insideClampOpenPosition);
    }

    public void fullOpenClamp() {
        outsideClampServo.setPosition(outsideClampOpenPosition);
        insideClampServo.setPosition(insideClampStartingPosition);
    }

    boolean hook = false;
    public void toggleHook() {
        if(!hook) {
            lowerHook();
        }
        else {
            raiseHook();
        }
        hook=!hook;
    }


    public void lowerHook() {
        leftHook.setPosition(leftHookClosedPosition);
        rightHook.setPosition(rightHookClosedPosition);
    }

    public void raiseHook() {
        leftHook.setPosition(leftHookOpenPosition);
        rightHook.setPosition(rightHookOpenPosition);
    }

    public boolean isHookDown() {
        return hook;
    }

    public void stopAllMotors() {
        this.stopWheels();
       // this.driveSpool(0);
       // this.driveRack(0);
    }

    public int getSkystonePosition() {
        return this.skystonePosition;
    }

    public void setSkystonePosition(int skystonePosition) {
        this.skystonePosition = skystonePosition;
    }
}