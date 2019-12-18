package org.firstinspires.ftc.teamcode;

public class TurnOperation extends Operation {

    double targetAngle, power;
    double initialGap;

    public TurnOperation(String opName, double targetAngle, double power) {
        super(opName);
        this.targetAngle = targetAngle;
        this.power = power;
        initialGap = Math.abs(this.getRobot().getAngle()-targetAngle);
    }

    public TurnOperation(String opName, float runtime, double targetAngle, double power) {
        super(opName, runtime);
        this.targetAngle = targetAngle;
        this.power = power;
    }
    double turnPower = Math.abs(targetAngle - this.getRobot().getAngle())/initialGap;
    @Override
    public boolean defineOperation() {
        if(Math.abs(targetAngle-this.getRobot().getAngle())>20)
            turnPower = Math.abs(targetAngle - this.getRobot().getAngle())/initialGap;
        else
            turnPower = 0.2;
        if (targetAngle < this.getRobot().getAngle()) {
            this.getRobot().turnRight(turnPower);
            return withinTurnTarget();
        } else if (targetAngle > this.getRobot().getAngle()) {
            this.getRobot().turnLeft(turnPower);
            return withinTurnTarget();
        } else
            return true;
    }

    private boolean withinTurnTarget() {
        return targetAngle - this.getRobot().getAngle() < 5 && targetAngle - this.getRobot().getAngle() < -5;
    }
}
