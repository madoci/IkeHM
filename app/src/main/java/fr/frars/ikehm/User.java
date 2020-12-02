package fr.frars.ikehm;

public class User {

    public GlobalStatistics globalStatistics;
    public Avatar avatar;

    public User(){
        globalStatistics = new GlobalStatistics();
        avatar = new Avatar();
    }

    public void update(RunStatistics runStatistics){
        globalStatistics.update(runStatistics.getTotalDistanceRan(),runStatistics.getAverageSpeed());
    }

}
