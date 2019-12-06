package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

public class RobotHardware {
    //encourage data encapsulation
    //wheel motors
    private DcMotor frontLeftDrive;
    private DcMotor frontRightDrive;
    private DcMotor backLeftDrive;
    private DcMotor backRightDrive;

    //rack drive
    private DcMotor rackDrive; //connects to both motors
    private DcMotor spoolDrive;

    private ColorSensor colorSensor;
    private DistanceSensor distanceSensor;

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
    private Servo clampServo;
        double clampClosedPosition = 0.49;
        double clampOpenPosition = 1.0;

    //foundation hook servos
    private Servo foundationHook;
        double hookClosedPosition = 0.85;
        double hookOpenPosition = 0.40;

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
            colorSensor = hMap.get(ColorSensor.class,"color_sensor");
            distanceSensor = hMap.get(DistanceSensor.class,"color_sensor");
            BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();

            parameters.mode                = BNO055IMU.SensorMode.IMU;
            parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
            parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
            parameters.loggingEnabled      = false;

            // Retrieve and initialize the IMU. We expect the IMU to be attached to an I2C port
            // on a Core Device Interface Module, configured to be a sensor of type "AdaFruit IMU",
            // and named "imu".
            imu = hMap.get(BNO055IMU.class, "imu");
            imu.initialize(parameters);

            //servos
            clampServo = hMap.get(Servo.class, "clamp");
                //outsideClampServo.setPosition(outsideclampStartingPosition);

            foundationHook = hMap.get(Servo.class, "hook");
                foundationHook.setPosition(hookOpenPosition);

        } catch (NullPointerException e) {
            //drives couldn't initialize.. let the program run anyway
        }
    }
    public int getRed() {
        if (colorSensor == null)
            return -1;
        return colorSensor.red();
    }

    public int getGreen() {
        if (colorSensor == null)
            return -1;
        return colorSensor.green();
    }

    public int getBlue() {
        if (colorSensor == null)
            return -1;
        return colorSensor.blue();
    }

    double skystoneThreshold = 1.5;
    public boolean nextToSkystone() {
        if(yellowRatio()>skystoneThreshold)
            return false;
        return true;
    }
        public double yellowRatio() {
            int redGreenAverage = (getRed()+getGreen())/2;
            double ratio = redGreenAverage/(double)getBlue();
            return ratio;
        }

    public double getDistance() {
        if (distanceSensor == null)
            return -1;
        return distanceSensor.getDistance(DistanceUnit.CM);
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

    public boolean clamped = false;
    public void toggleClamp() {
        if(clamped)
            openClamp();
        else
            closeClamp();
        clamped = !clamped;
    }

    public void closeClamp()    {
        clampServo.setPosition(clampClosedPosition);
        clamped = true;
    }

    public void openClamp()    {
        clampServo.setPosition(clampOpenPosition);
        clamped = false;
    }


    boolean hook = false;
    public void toggleHook() {
        if(!hook)
            lowerHook();
        else
            raiseHook();
        hook=!hook;
    }

    public void lowerHook() {
        foundationHook.setPosition(hookClosedPosition);
        hook = true;
    }

    public void raiseHook() {
        foundationHook.setPosition(hookOpenPosition);
        hook = false;
    }

    public boolean isHookDown() {
        return hook;
    }

    public void stopAllMotors() {
        this.stopWheels();

    }
}
