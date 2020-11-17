package fr.frars.ikehm;

public class RunSession {

    private static final double STEP_DISTANCE = .74;  // m

    private final RunStatistics statistics = new RunStatistics();

    private boolean started = false;
    private int lastStepCount = -1;
    private long lastTime = 0;

    public RunSession() {
    }

    public void start() {
        statistics.reset();
        started = true;
        lastTime = System.currentTimeMillis();
    }

    public void stop() {
        started = false;
    }

    public void update(int stepCount) {
        if (started) {
            if (lastStepCount == -1) {
                lastStepCount = stepCount;
            }
            int deltaStepCount = stepCount - lastStepCount;
            lastStepCount = stepCount;

            long currentTime = System.currentTimeMillis();
            long deltaTime = currentTime - lastTime;
            lastTime = currentTime;

            double distanceRan = deltaStepCount * STEP_DISTANCE;
            double timeRan = deltaTime / 1000.;
            statistics.update(distanceRan, timeRan);
        }
    }

    public boolean isStarted() {
        return started;
    }

    public double getTotalDistanceRan() {
        return statistics.getTotalDistanceRan();
    }

    public double getTotalTimeRan() {
        return statistics.getTotalTimeRan();
    }

    public double getAverageSpeed() {
        return statistics.getAverageSpeed();
    }

    public double getSpeedConsistency() {
        return statistics.getSpeedConsistency();
    }
}
