package fr.frars.ikehm;

import java.lang.Math;

/**
 * Class for storing a run statistics
 */
public class RunStatistics {

    protected int nbSamples = 0;

    private float totalDistanceRan = 0; // m
    private float totalTimeRan = 0; // s
    private float squareSpeedSum = 0; // for standard deviation

    public RunStatistics() {
    }

    public void reset() {
        totalDistanceRan = 0;
        totalTimeRan = 0;
        nbSamples = 0;
        speedConsistency = 0;
    }

    public void update(float distanceRan, float timeRan) {
        totalDistanceRan += distanceRan;
        totalTimeRan += timeRan;
        nbSamples += 1;
        speedConsistency += distanceRan / timeRan;
    }

    public float getTotalDistanceRan() {
        return totalDistanceRan;
    }

    public float getTotalTimeRan() {
        return totalTimeRan;
    }

    public float getAverageSpeed() {
        return totalDistanceRan / totalTimeRan * 3.6; // km/h
    }

    public float getSpeedConsistency() {
        return Math.sqrt(squareSpeedSum / nbSamples - getAverageSpeed());
    }
}
