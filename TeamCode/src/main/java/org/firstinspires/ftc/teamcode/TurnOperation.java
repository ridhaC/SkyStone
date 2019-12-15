package org.firstinspires.ftc.teamcode;

public class TurnOperation extends Operation{

    double targetAngle, power;
    public TurnOperation(String opName, double targetAngle, double power) {
        super(opName);
    }

    public TurnOperation(String opName, float runtime, double targetAngle, double power) {
        super(opName, runtime);
    }

    @Override
    public boolean defineOperation() {
        if(targetAngle<0) {
            this.getRobot().driveRight(power);
            return this.getRobot().getAngle()<targetAngle;
        }
        else if(targetAngle>0) {
            this.getRobot().driveLeft(power);
            return this.getRobot().getAngle()>targetAngle;
        }
        else
            return true;
    }
}
