package fr.frars.ikehm;

import java.lang.Math;

/**
 * Class for storing a run statistics
 */
public class RunStatistics {

    protected int nbSamples = 0;

    private double totalDistanceRan = 0; // m
    private double totalTimeRan = 0; // s
    private double squareSpeedSum = 0; // for standard deviation

    public RunStatistics() {
    }

    public void reset() {
        totalDistanceRan = 0;
        totalTimeRan = 0;
        nbSamples = 0;
        squareSpeedSum = 0;
    }

    public void update(double distanceRan, double timeRan) {
        totalDistanceRan += distanceRan;
        totalTimeRan += timeRan;
        nbSamples += 1;
        squareSpeedSum += distanceRan / timeRan;
    }

    public double getTotalDistanceRan() {
        return totalDistanceRan;
    }

    public double getTotalTimeRan() {
        return totalTimeRan;
    }

    public double getAverageSpeed() {
        return totalTimeRan > 0 ? totalDistanceRan / totalTimeRan * 3.6 : 0.; // km/h
    }

    public double getSpeedConsistency() {
        return Math.sqrt(squareSpeedSum / nbSamples - getAverageSpeed());
    }
}
