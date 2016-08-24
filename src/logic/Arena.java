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
    protected String arena, done, arenaType;
    protected Region leftSide, rightSide;


    public Arena(int priority, int timesToRun, String done, Region screen, String arena, Properties props, long sleepTimer, boolean stopOnMilestone, String arenaType){
        this.priority = priority;
        this.active = true;
        this.lastActive = 0;
        this.iterations = timesToRun;
        this.done = done;
        this.screen = screen;
        this.arena = arena;
        this.props = props;
        this.sleepTimer = sleepTimer*60*1000;
        this.stopOnMilestone = stopOnMilestone;
        this.milestoneCheckSleepTimer = Utils.getIntProperty(this.props, "milestoneSleepTimer")*60*1000;//convert minutes to milliseconds
        this.milestoneLastChecked = 0;
        this.arenaType = arenaType;
        int X1, Y1, width, height;
//        App app = new App(Utils.getStringProperty(this.props, "app"));//"player");
//        int appWindow = Utils.getIntProperty(this.props, "appWindow");
//        app.focus(appWindow);//2);
        X1 = this.screen.getX(); //app.window(appWindow).getX();
        Y1 = this.screen.getY();
        width = this.screen.getW();
        height = this.screen.getH();
        this.leftSide = new Region(X1, Y1, width/2, height);
        this.rightSide = new Region(X1+width/2, Y1, width/2, height);
    }

    //3-star: 222070
    //4-star: 660395

    protected void setInactive(){
        this.active = false;
        this.lastActive = System.currentTimeMillis();
    }

    protected boolean canRunPositionArena(){
        if(Utils.find(this.screen, "crystalCornucopia")!=null) {//found a cornucopia, thus they are not running!
            System.out.println("Found Cornucopia Arena, no regular Arenas available!");
            return false;
        }
//        else{
//            System.out.println("Checking for Catalyst arenas on the left side...");
//            if(Utils.find(this.leftSide, "catalystClashAlphaArena")!=null) return false;
//            if(Utils.find(this.leftSide, "catalystClashBasicArena")!=null) return false;
//            if(Utils.find(this.leftSide, "catalystClashClassArena")!=null) return false;
//        }
//        System.out.println("No Catalyst Arenas on the left side, running regular Arena!");
        return true;//if no arena was found, return true! Problem: special Arena!TODO!!!
    }

    protected void moveIfNeeded(String arena){
        if(Utils.find(this.leftSide, arena)!=null) {
            System.out.println("Found a catalyst arena, moving screen before entering a regular arena");
            this.moveScreen("3vs3", Utils.getIntProperty(this.props, "maxSearchForArena"));
        }
    }

    protected void clickPositionArena(){
        this.moveIfNeeded("catalystClashAlphaArena");
        this.moveIfNeeded("catalystClashBasicArena");
        this.moveIfNeeded("catalystClashClassArena");
        if(this.arena.equalsIgnoreCase("first"))//3-star arena, leftSide
            Utils.clickIfAvailable(this.leftSide, "3vs3");
        else//click on the rightSide
            Utils.clickIfAvailable(this.rightSide, "3vs3");
    }

    //New method for Arenas: divide screen on left and right side;
        //if we are looking for the 3 or 4-star Arena, then we first check what is on the left side of the screen:
        //if cornucopia is available on left OR right (whole screen), regular arenas are not running!
        //if the left side contains any of the imageArenas (including a possible extra), then we run the screen all the way to the right!
        //otherwise, they're available, and we should click them, by searching for the 3vs3 icon! (which we will also use to move!
        //3-star arena will be left, 4-star arena will be right!
    //On the other hand, if it's an image arena, we need to check if it's available on the screen. If it's not, then we move the screen all the way to the left!
        //If it still does not find the imageArena, then it should sleep on it, and skip to other arenas!
    //The arena extensions shouldn't implement this method, instead they are used as part of the logic now.
    public boolean runArena(){
        if(this.doneCounter>=this.iterations)
            return false;
        this.doneCounter++;
        if(this.arenaType.equalsIgnoreCase("position")){
            if(this.canRunPositionArena()) {
                this.clickPositionArena();
                return true;
            }
        }else
        if(this.arenaType.equalsIgnoreCase("image")){
            if(Utils.clickIfAvailable(this.screen, this.arena))
                return true;
            else{
                this.moveScreen("3vs3", Utils.getIntProperty(this.props, "maxSearchForArena"));
                if(Utils.clickIfAvailable(this.screen, this.arena))
                    return true;
                else {//did not manage to run the special arena, and should sleep on it!
                    this.lastActive = System.currentTimeMillis();
                    this.active = false;
                    System.out.println("Image Arena not found, sleeping for a while before I search for it again!");
                    Utils.clickIfAvailable(this.leftSide, "backButton");
                }
            }
        }
        //did not find the arena, therefore....
        return false;
    }

    public boolean moveScreen(String thingToMove, int repeats){
        for(int i=0;i<repeats;i++) {
            Match movable = Utils.find(this.screen, thingToMove);
            try {
                this.screen.mouseUp();
                Utils.sleepMilis(150);
                this.screen.hover(new Location(movable.getTopRight().getX() - (movable.getW() / 10), movable.getY()));
                this.screen.mouseDown(Button.LEFT);
                Utils.sleepMilis(250);
                this.screen.hover(new Location(movable.getTopRight().getX() - (this.screen.getW() / 80) * 3, movable.getY()));
                Utils.sleepMilis(150);
                this.screen.hover(new Location(movable.getTopRight().getX() - (this.screen.getW() / 80) * 4, movable.getY()));
                Utils.sleepMilis(150);
                this.screen.hover(new Location(movable.getTopRight().getX() - (this.screen.getW() / 80) * 5, movable.getY()));
                Utils.sleepMilis(150);
                this.screen.hover(new Location(movable.getTopRight().getX() - (this.screen.getW() / 80) * 6, movable.getY()));
                Utils.sleepMilis(150);
                this.screen.hover(new Location(movable.getTopRight().getX() - (this.screen.getW() / 80) * 7, movable.getY()));
                Utils.sleepMilis(150);
                this.screen.mouseUp();
                Utils.sleepMilis(250);
            } catch (FindFailed ff) {
                System.out.println("FindFailed");
                this.screen.mouseUp();
                Utils.sleepMilis(250);
                return false;
            } catch (NullPointerException np) {
//                System.out.println("NullPointer: ");
                this.screen.mouseUp();
                Utils.sleepMilis(250);
                return false;
            }
            this.screen.mouseUp();
            Utils.sleepMilis(250);
        }
        return true;
    }

    //this is based on the imagearenas, but can be overwritten of course...//TODO: deprecated
    protected Region getClickableRegion(Match matchedArena){
        return new Region(matchedArena.getX(), matchedArena.getY()+this.screen.getH()/2, matchedArena.getW(), matchedArena.getH());
    }

    //this is also based on imagearenas, can be overwritten of course
    protected void checkForMilestone(){
        System.out.println("Checking for milestone...NOT WORKING RIGHT NOW");
//        if((this.milestoneLastChecked+this.milestoneCheckSleepTimer)>System.currentTimeMillis()) {
//            System.out.println("Milestones checked recently, going in...");
//            return;//do nothing
//        }
//        else {
//            this.milestoneLastChecked = System.currentTimeMillis(); //otherwise, do the check, and update the value
//        }
//        boolean doneLastMilestone = false;
//        if(!this.stopOnMilestone) {
//            System.out.println("No stopping on miletones!");
//            return;
//        }
//        else{
//            System.out.println("Checking for milestone");
//            for(int i=0;i<Utils.getIntProperty(this.props,"maxSearchForMilestones");i++){
//                System.out.println("check...");
//                Match m = Utils.find(this.screen, arena);
//                Region milestoneRegion = new Region(m.getX(), m.getY(),this.screen.getW()-m.getX()+this.screen.getX(),m.getH());
//                if(milestoneRegion.getBottomRight().getX() < this.screen.getBottomRight().getX()){
//                    System.out.println("milestone outside of scope");
//                    this.moveScreen(m);
//                    this.moveScreen(m);
//                    this.moveScreen(m);
//                    this.moveScreen(m);//move screen twice to make sure it sees the no milestones message
//                    Utils.sleepMilis(500);
//                    if(Utils.find(milestoneRegion, done)!=null) {
//                        System.out.println("milestone found");
//                        doneLastMilestone = true;
//                    }else
//                        System.out.println("mielstone not found!");
//                    break;//else milestones are still available and it should go in!
//                }
//                else
//                    this.moveScreen(m);
//            }
//        }
//        if(doneLastMilestone) {
//            this.active = false;
//            this.lastActive = System.currentTimeMillis();
//        }
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
//        System.out.println("lastActive: "+lastActive+", sleepTimer: "+sleepTimer+", currentTime: "+System.currentTimeMillis());
        if(active) {
            return true;
        }
        else if(((lastActive+sleepTimer)<System.currentTimeMillis()) && ((this.milestoneLastChecked+this.milestoneCheckSleepTimer)<System.currentTimeMillis())) {
            active = true;
            return true;
        }
        this.doneCounter = iterations;
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
