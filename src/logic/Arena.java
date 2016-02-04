package logic;

import org.sikuli.script.*;

import java.util.Properties;

/**
 * Created by davidnavalho on 24/01/16.
 */
public class Arena {

    public int priority;
    private boolean active;
    private long lastActive;
    private long sleepTimer;
    private boolean stopOnMilestone = false;//default
    private long milestoneLastChecked, milestoneCheckSleepTimer;


    protected Properties props;
    //The default of all arenas is NOT to run
    protected int doneCounter = 0;//default
    protected int iterations = 0;//default
    protected Region screen;
    protected String arena, done;

    public Arena(int priority, int timesToRun, String done, Region screen, String arena, Properties props, long sleepTimer, boolean stopOnMilestone){
        this.priority = priority;
        this.active = true;
        this.lastActive = 0;
        this.iterations = timesToRun;
        this.done = done;
        this.screen = screen;
        this.arena = arena;
        this.props = props;
        this.sleepTimer = sleepTimer*60*60*1000;
        this.stopOnMilestone = stopOnMilestone;
        this.milestoneCheckSleepTimer = Utils.getIntProperty(this.props, "milestoneSleepTimer")*60*1000;//convert minutes to milliseconds
        this.milestoneLastChecked = 0;
    }

    protected void setInactive(){
        this.active = false;
        this.lastActive = System.currentTimeMillis();
    }

    //not supposed to run this, but the extended classes instead!
    public boolean runArena(){
        this.doneCounter++;
        return Utils.clickIfAvailable(screen,arena);
    }

    public boolean moveScreen(Match movable){
        try {
            this.screen.hover(new Location(movable.getTopRight().getX() - (movable.getW() / 10), movable.getY()));
            this.screen.mouseDown(Button.LEFT);
            Utils.sleepMilis(50);
            this.screen.hover(new Location(movable.getTopRight().getX() - (this.screen.getW() / 80) * 3, movable.getY()));
            Utils.sleepMilis(50);
            this.screen.hover(new Location(movable.getTopRight().getX() - (this.screen.getW() / 80) * 4, movable.getY()));
            Utils.sleepMilis(50);
            this.screen.hover(new Location(movable.getTopRight().getX() - (this.screen.getW() / 80) * 5, movable.getY()));
            Utils.sleepMilis(50);
            this.screen.hover(new Location(movable.getTopRight().getX() - (this.screen.getW() / 80) * 6, movable.getY()));
            Utils.sleepMilis(50);
            this.screen.hover(new Location(movable.getTopRight().getX() - (this.screen.getW() / 80) * 7, movable.getY()));
            Utils.sleepMilis(50);
            this.screen.mouseUp();
            Utils.sleepMilis(100);
        } catch (FindFailed ff) {
            this.screen.mouseUp();
            Utils.sleepMilis(100);
            return false;
        } catch (NullPointerException np) {
            this.screen.mouseUp();
            Utils.sleepMilis(100);
            return false;
        }
        this.screen.mouseUp();
        Utils.sleepMilis(100);
        return true;
    }

    //this is based on the imagearenas, but can be overwritten of course...//TODO: deprecated
    protected Region getClickableRegion(Match matchedArena){
        return new Region(matchedArena.getX(), matchedArena.getY()+this.screen.getH()/2, matchedArena.getW(), matchedArena.getH());
    }

    //this is also based on imagearenas, can be overwritten of course
    protected void checkForMilestone(){
        System.out.println("Checking for milestone...");
        if((this.milestoneLastChecked+this.milestoneCheckSleepTimer)>System.currentTimeMillis()) {
            System.out.println("Milestones checked recently, going in...");
            return;//do nothing
        }
        else {
            this.milestoneLastChecked = System.currentTimeMillis(); //otherwise, do the check, and update the value
        }
        boolean doneLastMilestone = false;
        if(!this.stopOnMilestone) {
            System.out.println("No stopping on miletones!");
            return;
        }
        else{
            System.out.println("Checking for milestone");
            for(int i=0;i<Utils.getIntProperty(this.props,"maxSearchForMilestones");i++){
                System.out.println("check...");
                Match m = Utils.find(this.screen, arena);
                Region milestoneRegion = new Region(m.getX(), m.getY(),this.screen.getW()-m.getX()+this.screen.getX(),m.getH());
                if(milestoneRegion.getBottomRight().getX() < this.screen.getBottomRight().getX()){
                    System.out.println("milestone outside of scope");
                    this.moveScreen(m);
                    this.moveScreen(m);
                    this.moveScreen(m);
                    this.moveScreen(m);//move screen twice to make sure it sees the no milestones message
                    Utils.sleepMilis(500);
                    if(Utils.find(milestoneRegion, done)!=null) {
                        System.out.println("milestone found");
                        doneLastMilestone = true;
                    }else
                        System.out.println("mielstone not found!");
                    break;//else milestones are still available and it should go in!
                }
                else
                    this.moveScreen(m);
            }
        }
        if(doneLastMilestone) {
            this.active = false;
            this.lastActive = System.currentTimeMillis();
        }
    }

    public boolean hasAvailableIterations(){
        if(this.doneCounter<iterations)
            return true;
        return false;
    }

    public void resetIterations(){
        this.doneCounter = 0;
    }

    public boolean arenaIsActive(){
        if(active) {
            return true;
        }
        else if(((lastActive+sleepTimer)<System.currentTimeMillis()) && ((this.milestoneLastChecked+this.milestoneCheckSleepTimer)<System.currentTimeMillis())) {
            active = true;
            return true;
        }
//        else if((this.milestoneLastChecked+this.milestoneCheckSleepTimer)>System.currentTimeMillis()){
//            System.out.println("Wasn't active but it is now - milestone");
//            active = true;
//            return true;
//        }
        return false;
    }

    @Override
    public String toString() {
        return this.arena;
    }
}
