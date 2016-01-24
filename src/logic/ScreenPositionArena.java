package logic;

import org.sikuli.script.Match;
import org.sikuli.script.Region;

import java.util.Properties;

/**
 * Created by davidnavalho on 24/01/16.
 */
public class ScreenPositionArena extends Arena {


    public ScreenPositionArena(int priority, int timesToRun, String done, Region screen, String arena, Properties props, long sleepTimer, boolean stopOnMilestone){
        super(priority, timesToRun, done, screen,arena, props, sleepTimer, stopOnMilestone);
    }

    private Region getArenaRegion(String arena){
        int centerX = this.screen.getCenter().x;
        int centerY = this.screen.getCenter().y;
        int xQuarter = this.screen.getW()/4;
        int xEight = this.screen.getW()/8;
        int xSixteenth = this.screen.getW()/16;
        int yQuarter = this.screen.getH()/4;
        int yEight = this.screen.getH()/8;
        if(arena.equalsIgnoreCase("first")) {
//            System.out.println("First Arena Button chosen.");
            return new Region(centerX - xEight - xSixteenth, centerY + yQuarter + yEight, 50, 25);
        }
        else if(arena.equalsIgnoreCase("second")) {
//            System.out.println("Second Arena Button chosen.");
            return new Region(centerX + xQuarter, centerY + yQuarter + yEight, 50, 25);
        } else if(arena.equalsIgnoreCase("single")){ //TODO: deprecated?
//            System.out.println("Single Arena Button chosen.");
            return new Region(centerX, centerY+yQuarter+yEight,50,25);
        }
        //return first arena by default (i.e.: error, etc
//        System.out.println("No known arena identified, choosing first");
        return new Region(centerX-xEight-xSixteenth, centerY+yQuarter+yEight,50,25);
    }

    //find arena - it's screen-based, and also based on no moves, so assume it's already available?
    //unless it's the cornucopia today!
    //so let's do it backwards, detect if an arena does not exist (cornucopia)
    @Override
    public boolean runArena(){
        if(this.doneCounter>=this.iterations)
            return false;
        Match m = Utils.find(this.screen, "crystalCornucopia");//if cornucopia is available, return false, otherwise run the screen arenas
        if(m==null) {
            Utils.click(this.getArenaRegion(arena));//clicked the arena, so remove an iteration
            return true;
        }
        this.doneCounter++;
        return false;
    }
}
