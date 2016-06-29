package logic;

import main.MCoC;
import org.sikuli.basics.Settings;
import org.sikuli.script.Button;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Region;

import java.util.Properties;

public class FightBot {

    Region r;
    int waitTimer = 30;
    int punchesRepeat = 1;

    Region attackRegion, attackRegionBack, attackRegionForward;
    Region defenseRegion, defenseRegionBack;
    Region specialBar;
    Region specialButton;
    Region pauseRegion;

    TimedAction timedActions;

    double similarity = 0.95;
    String localExtraPath = "";

    Properties prop = null;

    protected MCoC controller;

    public FightBot(Region r, Region attackRegion, Region tinyUpTop, double defaultSimilarity, String localExtraPath, Properties prop, MCoC controller){
        this.r = r;
        this.attackRegion = attackRegion;
        this.defenseRegion = new Region((this.r.getX()-this.r.getW()+this.attackRegion.getX()), this.attackRegion.getY(), this.attackRegion.getW(), this.attackRegion.getH());
        this.defenseRegionBack = new Region(this.defenseRegion.getX()-this.defenseRegion.getW(),this.defenseRegion.getY(),this.defenseRegion.getW(),this.defenseRegion.getH());
        this.attackRegionBack = new Region(this.attackRegion.getX()-this.attackRegion.getW(),this.attackRegion.getY(),this.attackRegion.getW(),this.attackRegion.getH());
        this.attackRegionForward = new Region(this.attackRegion.getX()+this.attackRegion.getW(),this.attackRegion.getY(),this.attackRegion.getW(),this.attackRegion.getH());
        this.setEnergyLocation();
        this.pauseRegion = tinyUpTop;
        this.similarity = defaultSimilarity;
        this.localExtraPath = localExtraPath;
        this.prop = prop;
        this.timedActions = new TimedAction(new Integer(this.prop.getProperty("timeBetweenActions")));
        this.controller = controller;
    }

    private void setEnergyLocation(){
        int centerX = this.r.getCenter().x;
        int centerY = this.r.getCenter().y;
        int xQuarter = this.r.getW()/4;
        int xSixteenth = this.r.getW()/16;
        int yQuarter = this.r.getH()/4;
        int yEight = this.r.getH()/8;
        int ySixteenth = this.r.getH()/16;
        //Third Special bar red (L3 active)
        specialBar = new Region(centerX-xSixteenth-xQuarter, centerY+yQuarter+yEight+this.r.getH()/30,xSixteenth,ySixteenth);
        specialButton = new Region(centerX-xSixteenth*2-xQuarter, centerY+yQuarter+yEight,xSixteenth,ySixteenth);
    }


    private void swipeForward(){
        try {
//            System.out.println("forward");
            this.timedActions.waitForAction();
//            this.r.dragDrop(this.attackRegion, this.attackRegionForward);
            this.r.hover(this.attackRegion);
            this.r.mouseDown(Button.LEFT);
            this.r.hover(this.attackRegionForward);
            this.timedActions.waitForAction(100);
            this.r.mouseUp();
//            this.r.dragDrop(this.r.getCenter(), this.attackRegion);
        }catch(Exception e){
            System.out.println("Failed to swipe forward");
        }
    }

    private void swipeBackward(){
        try {
//            System.out.println("backward");
            this.timedActions.waitForAction();
//            this.r.dragDrop(this.attackRegion, this.attackRegionBack);
            this.r.hover(this.defenseRegion);
            this.r.mouseDown(Button.LEFT);
            this.r.hover(this.defenseRegionBack);
            this.timedActions.waitForAction(100);
            this.r.mouseUp();
        }catch(Exception e){
            System.out.println("Failed to swipe backward");
        }
    }

    private void attack(){
        try {
//            System.out.println("attack");
            this.timedActions.waitForAction();
            Utils.click(this.attackRegion);
        }catch(Exception e){
            System.out.println("Failed to attack");
        }
    }

    private void defend(){
        try {
//            System.out.println("defend");
            this.timedActions.waitForAction();
            this.r.hover(this.defenseRegion);
            this.r.mouseDown(Button.LEFT);
            this.timedActions.waitForAction(100);
            this.r.mouseUp();
        }catch(Exception e){
            System.out.println("Failed to attack");
        }
    }

    public void fight() throws FindFailed {
        //set fightspeed
        Settings.MoveMouseDelay = new Float(this.prop.getProperty("mouseSpeedFight"));
        int counter = 0;
        boolean specialActive = false;
        Utils.setImagesPath(this.localExtraPath, "/fight");
//        this.r.wait("fightPause", 3);
//        System.out.println("Looking for a fight...");
        while(true){
            this.controller.checkForPauseAndResetScreen(false);
            if(Utils.find(this.pauseRegion, "pause")!=null) {
                this.swipeForward();
                this.attack();
                this.attack();
                this.attack();
                this.swipeForward();
                //swipe forward
//                this.r.dragDrop(this.r.getCenter(),this.attackRegion);
                //attack and...

//                for (int i = 0; i < 10; i++) {
//                    Utils.click(attackRegion);
//                }//if special available, use it
                if(specialActive) {
                    Utils.click(this.specialButton);
                }
                else {
                    counter++;
                    if(counter >= punchesRepeat)
                        if(Utils.findWithSimilarity(this.specialBar, "specialRed", 0.75, this.similarity) != null)
                            specialActive = true;
                }
                this.swipeBackward();
//                this.defend();
                //and then swipe back swipes are too slow....
//                this.r.dragDrop(this.attackRegion,this.r.getCenter());
            }else {
                Settings.MoveMouseDelay = new Float(this.prop.getProperty("mouseSpeed"));   //shouldn't need this...
                break;
            }
        }
        //set speed back to normal
        Settings.MoveMouseDelay = new Float(this.prop.getProperty("mouseSpeed"));
    }
}
