package logic;

/**
 * Created by davidnavalho on 24/12/15.
 */
public class TimedAction {
    private long lastAction = 0;
    private long minimumTime = 0;

    public TimedAction(int timeBetweenActions){
        this.minimumTime = timeBetweenActions;
    }


    public void waitForAction(){
        long currentTime = System.currentTimeMillis();
        //
        if(currentTime>=(lastAction+minimumTime)) {
//            System.out.println("Been a long time! go forward!");
            this.lastAction = currentTime;
        }else{
//            System.out.println("Hold yer horses, boy!, Sleep a little: "+((lastAction+minimumTime)-currentTime));
            Utils.sleepMilis((lastAction+minimumTime)-currentTime);
            this.lastAction = currentTime;
            //
        }
    }

    public void waitForAction(int waitTime){
        long currentTime = System.currentTimeMillis();
        if(currentTime>=(lastAction+waitTime)) {
//            System.out.println("Been a long time! go forward!");
            this.lastAction = currentTime;
        }else{
//            System.out.println("Hold yer horses, boy!, Sleep a little: "+((lastAction+waitTime)-currentTime));
            Utils.sleepMilis((lastAction+waitTime)-currentTime);
            this.lastAction = currentTime;
        }
    }
}
