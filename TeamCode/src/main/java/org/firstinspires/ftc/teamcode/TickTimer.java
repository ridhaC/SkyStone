package org.firstinspires.ftc.teamcode;

public class TickTimer {
    private long last;

    public TickTimer() {
        last = System.currentTimeMillis();
    }

    public float elapsedTime() {
        return ((System.currentTimeMillis()-last)/1000.0f);
    }

    public void clear() {
        last = System.currentTimeMillis();
    }
}
