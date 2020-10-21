package fr.frars.ikehm;

/**
 * Class for storing the user's general run statistics
 */
public class Statistics {

    private float totalDistanceRan = 0; // km
    private float totalTimeRan = 0; // h
    private float averageSpeed = 0; // km/h
    private float speedConsistency = 0; // standard deviation

    public Statistics() {
    }

    public float getTotalDistanceRan() {
        return totalDistanceRan;
    }

    public void setTotalDistanceRan(float totalDistanceRan) {
        this.totalDistanceRan = totalDistanceRan;
    }

    public float getTotalTimeRan() {
        return totalTimeRan;
    }

    public void setTotalTimeRan(float totalTimeRan) {
        this.totalTimeRan = totalTimeRan;
    }

    public float getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(float averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public float getSpeedConsistency() {
        return speedConsistency;
    }

    public void setSpeedConsistency(float speedConsistency) {
        this.speedConsistency = speedConsistency;
    }
}
