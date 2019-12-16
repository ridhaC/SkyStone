package org.firstinspires.ftc.teamcode;

public class TurnOperation extends Operation {

    double targetAngle, power;
    double initialGap;

    public TurnOperation(String opName, double targetAngle, double power) {
        super(opName);
        this.targetAngle = targetAngle;
        this.power = power;
        initialGap = this.getRobot().getAngle();
    }

    public TurnOperation(String opName, float runtime, double targetAngle, double power) {
        super(opName, runtime);
        this.targetAngle = targetAngle;
        this.power = power;
    }

    @Override
    public boolean defineOperation() {
        if (targetAngle < this.getRobot().getAngle()) {
            this.getRobot().driveRight(power);
            return withinTurnTarget();
        } else if (targetAngle > this.getRobot().getAngle()) {
            this.getRobot().driveLeft(power);
            return withinTurnTarget();
        } else
            return true;
    }

    private boolean withinTurnTarget() {
        return targetAngle - this.getRobot().getAngle() < 5 && targetAngle - this.getRobot().getAngle() < -5;
    }
}
