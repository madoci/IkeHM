package fr.frars.ikehm;

public class RunSession {

    private static final double STEP_DISTANCE = 1.;  // m

    private RunStatistics statistics;

    private boolean started = false;
    private long startTime = 0;
    private double startSteps = -1;

    public RunSession() {
    }

    public void start() {
        statistics = new RunStatistics();
        started = true;
        startTime = System.currentTimeMillis();
    }

    public void stop() {
        started = false;
    }

    public void update(double steps) {
        if (startSteps == -1) {
            startSteps = steps;
        }
        double deltaSteps = steps - startSteps;
        long deltaTime = System.currentTimeMillis() - startTime;

        double distanceRan = deltaSteps * STEP_DISTANCE;
        double timeRan = deltaTime / 1000.;
        statistics.update(distanceRan, timeRan);
    }
}
