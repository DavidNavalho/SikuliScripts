import logic.FightBot;
import logic.Utils;
import org.sikuli.basics.Settings;
import org.sikuli.script.*;

import java.io.File;

/**
 * Created by davidnavalho on 25/09/15.
 */
public class MCoC {
    public Region r;
    private int wait = 15;
    private int defaultWaitTime = 3;
    private FightBot bot = null;
    private Region fightRegion = null;

    public MCoC(String location) {
        Settings.MinSimilarity = 0.95;
        this.r = this.setScreen(location);
        Utils.setImagesPath("");
        this.setupFightBot();
    }

    private void setupFightBot(){
        int centerX = this.r.getCenter().x;
        int x = centerX + (this.r.getW()/4);
        int centerY = this.r.getCenter().y;
        this.fightRegion = new Region(x, centerY,50,50);
        this.bot = new FightBot(this.r, fightRegion);
    }

    private Region getArenaRegion(String arena){
        int centerX = this.r.getCenter().x;
        int centerY = this.r.getCenter().y;
        int xQuarter = this.r.getW()/4;
        int xEight = this.r.getW()/8;
        int xSixteenth = this.r.getW()/16;
        int yQuarter = this.r.getH()/4;
        int yEight = this.r.getH()/8;
        if(arena.equalsIgnoreCase("first")) {
            System.out.println("First Arena Button chosen.");
            return new Region(centerX - xEight - xSixteenth, centerY + yQuarter + yEight, 50, 25);
        }
        else if(arena.equalsIgnoreCase("second")) {
            System.out.println("Second Arena Button chosen.");
            return new Region(centerX + xQuarter, centerY + yQuarter + yEight, 50, 25);
        }
        //return first arena by default (i.e.: error, etc
        System.out.println("No known arena identified, choosing first");
        return new Region(centerX-xEight-xSixteenth, centerY+yQuarter+yEight,50,25);
    }


    public Region setScreen(String location){
        int sn = 0;
        int X1 = 0;
        int Y1 = 0;
        int X2 = 0;
        int Y2 = 0;

        /*if(location.equalsIgnoreCase("macbook_homeExternalScreen")) {
            sn = 1;
            X1 = 840;  //805  (~35)
            Y1 = 145;  //90   (~55)
            X2 = 1265;  //1230   (~35)
            Y2 = 780;   //725    (~55)
            Y1 -= 1080;
            Y2 -= 1080;
        }else*/ if(location.equalsIgnoreCase("macbook_FCTUNLExternalScreenLarge")){
            sn = 1;
            X1 = 480;
            Y1 = 45;
            X2 = 1230;
            Y2 = 460;
        }else
        if(location.equalsIgnoreCase("macbook_screen")){
            sn = 0;
            X1 = 480;
            Y1 = 45;
            X2 = 1230;
            Y2 = 460;
//            Y1 -= 1080;
//            Y2 -= 1080;
        }

        Screen s = new Screen(sn);
        return s.setRect(X1, Y1, X2-X1, Y2-Y1);
    }

    private void ifExistsClick(String imageExists, String imageToClick){
        try {
            if(Utils.findExact(this.r, imageExists)!=null)
                Utils.clickExact(this.r, imageToClick);
        }catch(FindFailed ff){
            System.out.println(imageExists+" or "+imageToClick+" not available, skipping.");
        }
    }

    private void clickIfAvailable(String image){
        try {
            Utils.clickExact(this.r, image);
        }catch(FindFailed ff){
            System.out.println(image+" not available, skipping.");
        }
    }

    private void clickWhileAvailable(String image){
        while(Utils.findExact(this.r, image)!=null) {
            this.r.getLastMatch().click();
            Utils.sleep(this.defaultWaitTime/2);
        }
    }

    private void attemptFight(){
        try {
            this.bot.fight();
        }catch(FindFailed e){
            System.out.println("Fight failed somehow");
        }
        //Set back the path TODO: have the previous path as a variable?
        Utils.setImagesPath("/control");
    }

    private int secondArenaDoneCounter = 0;
    private void chooseArena(String fix){
        if(fix.equalsIgnoreCase("second"))
            Utils.click(this.getArenaRegion("second"));
        else if(fix.equalsIgnoreCase("first"))
            Utils.click(this.getArenaRegion("first"));
        else if(secondArenaDoneCounter<4){
            secondArenaDoneCounter++;
            Utils.click(this.getArenaRegion("second"));
        }else{
            secondArenaDoneCounter = 0;
            Utils.click(this.getArenaRegion("first"));
        }
    }

    //TODO: this is currently optimized for the Macbook screen...
    public void controller(String fixedArena){
        while(true) {
            Utils.setImagesPath("/control");
            //botFight
            this.attemptFight();
            //Reconnect menu TODO: make it sleep for at least some minutes before reconnecting
            this.clickIfAvailable("reconnect");
            //Attempt at any continue (may need 2 (highlited) or more(all different))
            this.clickIfAvailable("continue");
            this.clickIfAvailable("accept");
            //Main Screen
            this.clickIfAvailable("mainMenuFight");
            //Play Versus
            this.clickIfAvailable("playVersus");
            //botFight
            this.attemptFight();
            //Check for Arenas, enter one:
            Match match = Utils.findExact(this.r, "multiverseArenas");
            if (match != null)
                this.chooseArena(fixedArena);
                //Utils.click(match.offset(0, 310));
            //Handle regen boxes
            this.clickWhileAvailable("regenBox");
            //handle clicking on champ by mistake
            this.ifExistsClick("info", "cross");
            //Edit Team:
            try {             //TODO: maybe not base it on edit team, but on empty box instead...??
                Match boxMatch = Utils.findExact(this.r, "availableSpot");
                if (boxMatch != null) {
                    Match editTeam = Utils.findExact(this.r, "editTeam");
                    Region champ = editTeam.offset(-200, 100);
                    Region emptyBox = editTeam.offset(-380, 135);
                    this.r.dragDrop(champ, emptyBox);
                }
            } catch (FindFailed e) {
                System.out.println("Failed on Edit Team...");
            }
            try {
                if ((Utils.findExact(this.r, "editTeam") != null) && (Utils.findExact(this.r, "availableSpot") == null))
                    Utils.clickExact(this.r, "findMatch");
            } catch (FindFailed e) {
                System.out.println("Failed somehow on clicking findMatch");
            }
            //fight won (lost too?)
            Match fightFinished = Utils.findExact(this.r, "stats");
            if(fightFinished!=null){
                Region continueButton = fightFinished.offset(0,75);
                Utils.click(continueButton);
            }
            //fight lost (won too?)
            Match fightFinishedKO = Utils.findExact(this.r, "stats2");
            if(fightFinishedKO!=null){
                Region continueButton = fightFinishedKO.offset(0,75);
                Utils.click(continueButton);
            }
//            'random' click from fight region
            Utils.click(this.fightRegion);
            //botFight
            this.attemptFight();
//            Utils.sleep(5);
        }
    }

    public void tests(){
        try {
//            Utils.setImagesPath("/fight");
//            int centerX = this.r.getCenter().x;
//            int centerY = this.r.getCenter().y;
//            int xQuarter = this.r.getW()/4;
//            int xSixteenth = this.r.getW()/16;
//            int yQuarter = this.r.getH()/4;
//            int yEight = this.r.getH()/8;
//            int ySixteenth = this.r.getH()/16;
//            //Third Special bar red (L3 active)
//            Region specialBar = new Region(centerX-xSixteenth-xQuarter, centerY+yQuarter+yEight+ySixteenth,35,15);
//            Region specialButton = new Region(centerX-xSixteenth*2-xQuarter, centerY+yQuarter+yEight+ySixteenth,35,15);
//
//
//
//            if(Utils.findExact(specialBar, "specialRed")!=null)
//                Utils.click(specialButton);
            Utils.setImagesPath("/fight");
            Settings.MinSimilarity = 0.95;
            Utils.find(this.r, "pause");
//            Utils.clickLastMatch(this.r);

//        this.clickWhileAvailable("regenBox");
//            Utils.findExact(this.r, "stats");
//            Region continueButton = Utils.findExact(this.r, "stats").offset(0, 75);
//            this.r.click(new Pattern("availableSpot").exact());
            Utils.highlightRegion(this.r.getLastMatch());
//            Utils.highlightRegion(continueButton);
//            Utils.searchAndHighlight(this.r, "pause");
//            this.ifExistsClick("info", "cross");
//        Utils.searchAndHighlight(this.r,"mainMenuFight");
//        Utils.searchAndHighlight(this.r,"multiverseArenas");
//        Utils.searchAndHighlight(this.r,"availableSpot");
//        Utils.searchAndHighlight(this.r,"editTeam");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        MCoC battler = new MCoC("macbook_screen");
//        MCoC battler = new MCoC("macbook_FCTUNLExternalScreenLarge");
//        Utils.highlightRegion(battler.r);
//        battler.tests();
        battler.controller("second");

//        int centerX = battler.r.getCenter().x;
//        int x = centerX + (battler.r.getW()/4);
//        int centerY = battler.r.getCenter().y;
//        Region fightRegion = new Region(x, centerY,50,50);
//        Utils.highlightRegion(testRegion);

//        FightBot bot = new FightBot(battler.r, fightRegion);
//        try {
//            bot.fight();
//        }catch(Exception e){
//            e.printStackTrace();
//        }

    }
}
