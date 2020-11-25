package fr.frars.ikehm;

public class Avatar {

    private int level = 1;
    private int experience = 0;
    private static final int thresholdExperience = 100;

    public Avatar() {
    }

    public void addExperience(int experience){
        this.experience = this.experience + experience;
        this.level += this.experience / thresholdExperience;
        this.experience = this.experience % thresholdExperience;
    }

    public int getLevel() { return this.level; }

    public int getExperience() { return this.experience; }

    public int getThresholdExperience(){ return this.thresholdExperience; }
}
