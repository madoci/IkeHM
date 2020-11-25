package fr.frars.ikehm;

public class Avatar {

    private static final int difficulty = 10;

    private int level = 1;
    private int experience = 0;
    private int threshold;

    public Avatar() {
        updateThreshold();
    }

    public boolean addExperience(int experience){
        this.experience += experience;
        if (this.experience >= threshold) {
            level += this.experience / threshold;
            this.experience %= threshold;
            updateThreshold();
            return true;
        } else {
            return false;
        }
    }

    public int getLevel() { return this.level; }

    public int getExperience() { return this.experience; }

    private void updateThreshold() {
        threshold = level * difficulty;
    }
}
