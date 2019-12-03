package org.firstinspires.ftc.teamcode;

import java.util.ArrayList;
import java.util.List;

public class OperationQueue {
    private List<Operation> queue;
    private RobotHardware robot;

    public OperationQueue(RobotHardware robot) {
        queue = new ArrayList<Operation>();
        this.robot = robot;
    }

    public Operation getCurrentOperation() {
        if (queue.size() == 0)
            return null;
        return queue.get(0);
    }

    public RobotHardware getRobot() {
        return robot;
    }

    public void add(Operation operation) {
        operation.setRobot(robot);
        queue.add(operation);
    }

    /**
     * Returns true if the queue is still operating
     * @return
     */
    public boolean operate() {
        Operation curOp = getCurrentOperation();
        if (curOp == null)
            return false;
        boolean completed = !curOp.operate();
        if (completed) {
            queue.remove(0);
            robot.stopAllMotors();
        }
        return !completed;
    }
}
