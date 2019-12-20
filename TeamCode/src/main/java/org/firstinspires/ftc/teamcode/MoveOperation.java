package org.firstinspires.ftc.teamcode;

public class MoveOperation extends Operation {

    public static final int FORWARD = 0, BACKWARD = 1, LEFT = 2, RIGHT = 3;

    private int direction;
    private double power;

    public MoveOperation(int direction, double power, float runtime) {
        super("moving",runtime);
        this.direction = direction;
        this.power = power;
    }

    public boolean defineOperation() {
        switch (direction) {
            case FORWARD:
                this.getRobot().driveForward(power);
                break;
            case BACKWARD:
                this.getRobot().driveBackward(power);
                break;
            case LEFT:
                this.getRobot().driveLeft(power);
                break;
            case RIGHT:
                this.getRobot().driveRight(power);
                break;
        }
        return true;
    }
}
