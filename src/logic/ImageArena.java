package logic;

import org.sikuli.script.Match;
import org.sikuli.script.Region;

import java.util.Properties;

/**
 * Created by davidnavalho on 24/01/16.
 */
public class ImageArena extends Arena{


    public ImageArena(int priority, int timesToRun, String done, Region screen, String arena, Properties props, long sleepTimer, boolean stopOnMilestone) {
        super(priority, timesToRun, done, screen, arena, props, sleepTimer, stopOnMilestone);
    }


    @Override
    protected Region getClickableRegion(Match matchedArena){
        return matchedArena;
//        return new Region(matchedArena.getX(), matchedArena.getY()+this.screen.getH()/2, matchedArena.getW(), matchedArena.getH());
    }

    //returns false if it couldn't run for any reason. Must make sure to make inactive outside if it returns false!
    public boolean findArena(){
        Match match = null;
        for(int i=0;i<Utils.getIntProperty(this.props,"maxSearchForArena");i++)
            if(Utils.find(this.screen, arena)==null)
                if(!this.moveScreen(Utils.find(this.screen, "arenaInfo"))) {
                    Utils.clickIfAvailable(this.screen, "backButton");  //TODO:may have just missed it; try the next step
                    return false;
                }
        if((match = Utils.find(this.screen, arena))!=null) {//found the arena, should click it, count it, return true
            //first, check for milestone existence
            this.checkForMilestone();
            //if milestones are done, then exit
            if(this.arenaIsActive()){
                Utils.clickIfAvailable(this.screen, arena);
                this.doneCounter++;
                return true;
            }
        }
        Utils.clickIfAvailable(this.screen, "backButton");
        this.setInactive();
        return false;
    }

    //check for doneCounters first
    //check if image is available
    //if it's not, search for it
        //if it doesn't find it, return false
    //if it finds it, check for milestonesExistence
        //existence should have a timer for checking, so it isn't always checking
    //if it's not yet done, then enter it
    //if it runs, don't forget to increase a doneCounter
    @Override
    public boolean runArena(){
        if(this.doneCounter>=this.iterations) {
            return false;
        }
        doneCounter++;
        Match m = Utils.find(this.screen, arena);
        if(m==null) {
            if (arena.equalsIgnoreCase("crystalCornucopia"))  //TODO: shouldn't need to do this...
                return false;
        }
        return this.findArena();
    }
}
