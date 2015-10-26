import logic.FightBot;
import logic.Utils;
import org.sikuli.basics.Settings;
import org.sikuli.script.*;

import java.io.File;

public class MCoC {
    public Region r;
    private int X1,X2,Y1,Y2;
    private int wait = 15;
    private int defaultWaitTime = 3;
    private FightBot bot = null;
    private Region fightRegion = null;
    private int defaultReconnectWait = 15; //minutes
    private int first = 1;
    private int second = 1;
    private double similarity = 0.90;
    private String local = "";

    private Region topMiddle, tinyUpTop, lowerRight, tinyLowerRight, lowerLeft, regenRegion, thirdBoxRegion, middleColumn, middleLowerColumn;

    public MCoC(String location, int first, int second, double similarity, String localExtraPath) {
        this.local = localExtraPath;
        this.similarity = similarity;
        Settings.MinSimilarity = this.similarity;
        this.first = 1;
        this.second = 2;
        this.r = this.setScreen(location);
        Utils.setImagesPath(this.local,"");
        this.setupRegions();
        this.setupFightBot();
        Utils.highlightRegion(this.r);
    }

    private void setupRegions(){
        int height = this.r.getH();
        int width = this.r.getW();
        int quarterHeight = height/4;
        int quarterWidth = width/4;
        int eightWidth = width/8;
        //topMiddle: x,y => X1+quarterWidth, Y1; size: experiment
        this.topMiddle = new Region(X1+quarterWidth, Y1, width/2, height/2);
        this.tinyUpTop = new Region(X1+quarterWidth+eightWidth+width/16, Y1, eightWidth, height/8);
        this.lowerRight = new Region(X2-width/2, Y2-quarterHeight, width/2, quarterHeight);
        this.tinyLowerRight = new Region(X2-width/6, Y2-height/9, width/6, height/9);
        this.regenRegion = new Region(X1+quarterWidth, Y1+quarterHeight, eightWidth, quarterHeight);
        this.thirdBoxRegion = new Region(X1+width/16, Y1+2*height/5, 3*width/20, 3*height/20);
        this.middleColumn = new Region(X1+quarterWidth, Y1, width/2, height);
        this.middleLowerColumn = new Region(X1+quarterWidth, Y1+height/2, width/2, height/2);
        this.lowerLeft = new Region(X1, Y2-quarterHeight, quarterWidth, quarterHeight);
    }

    private void setupFightBot(){
        int centerX = this.r.getCenter().x;
        int x = centerX + (this.r.getW()/4);
        int centerY = this.r.getCenter().y;
        this.fightRegion = new Region(x, centerY,50,50);
        this.bot = new FightBot(this.r, fightRegion, this.tinyUpTop, this.similarity,this.local);
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
        } else if(arena.equalsIgnoreCase("single")){
            System.out.println("Single Arena Button chosen.");
            return new Region(centerX, centerY+yQuarter+yEight,50,25);
        }
        //return first arena by default (i.e.: error, etc
        System.out.println("No known arena identified, choosing first");
        return new Region(centerX-xEight-xSixteenth, centerY+yQuarter+yEight,50,25);
    }


    private Region setScreen(String location){
        int sn = 0;
        X1 = 0;
        Y1 = 0;
        X2 = 0;
        Y2 = 0;
        if(location.equalsIgnoreCase("macbook_screen")){
            sn = 0;
            X1 = 480;
            Y1 = 45;
            X2 = 1230;
            Y2 = 465;
//            Y1 -= 1080;
//            Y2 -= 1080;
        }else if(location.equalsIgnoreCase("iMac_screen")){
            sn = 0;
            X1 = 1120;
            Y1 = 45;
            X2 = 2510;
            Y2 = 825;
        }
        Screen s = new Screen(sn);
        return s.setRect(X1, Y1, X2-X1, Y2-Y1);
    }

    private void ifExistsClick(String imageExists, String imageToClick){
        if(Utils.find(this.r, imageExists)!=null)
            Utils.clickIfAvailable(this.r, imageToClick);
    }

//    private void clickIfAvailable(String image){
//        try {
//            Utils.clickExact(this.r, image);
//        }catch(FindFailed ff){
//            System.out.println(image+" not available, skipping.");
//        }
//    }


    private void attemptFight(String previousImagesPath){
        try {
            this.bot.fight();
        }catch(FindFailed e){
            System.out.println("Fight failed somehow");
        }
        Utils.setImagesPath(this.local,previousImagesPath);
    }

    private int secondArenaDoneCounter = 0;
    private int firstArenaDoneCounter = 0;
    //Pre: first+second must always be>0
    private void chooseArena(String fix){
        if(fix.equalsIgnoreCase("single"))
            Utils.click(this.getArenaRegion("single"));
        if(fix.equalsIgnoreCase("second"))
            Utils.click(this.getArenaRegion("second"));
        else if(fix.equalsIgnoreCase("first"))
            Utils.click(this.getArenaRegion("first"));
        else if(this.secondArenaDoneCounter>0){
            this.secondArenaDoneCounter--;
            Utils.click(this.getArenaRegion("second"));
        }else if(this.firstArenaDoneCounter>0){
            this.firstArenaDoneCounter--;
            Utils.click(this.getArenaRegion("first"));
        }else{//If it reaches this, then it's iterated
            this.firstArenaDoneCounter = this.first;
            this.secondArenaDoneCounter = this.second;
            this.chooseArena(fix);
        }
    }

    private void uncommonOperations(){
        Utils.setImagesPath(this.local,"/control");
        //Reconnect menu TODO: make it sleep for at least some minutes before reconnecting
        Match m = Utils.find(this.middleColumn, "reconnect");
        if(m!=null) {
            System.out.println("Reconnect found, sleeping " + this.defaultReconnectWait+" minutes.");
            Utils.sleep(this.defaultReconnectWait*60);
            Utils.clickIfAvailable(this.middleColumn, "reconnect");
        }
        this.attemptFight("/control");
        //Main Screen
        Utils.clickIfAvailable(this.r, "mainMenuFight");
        //Play Versus
        Utils.clickIfAvailable(this.r, "playVersus");
        Utils.clickIfAvailable(this.r, "playVersus2");
        this.attemptFight("/control");
        //handle clicking on champ by mistake
        this.ifExistsClick("info", "cross");
        //dismiss rate us
        Utils.clickIfAvailable(this.middleLowerColumn, "later");//TODO:no Macbook Later
    }

    private void commonOperations(String fixedArena){
        //Check for Arenas, enter one:
        Match match = Utils.find(this.topMiddle, "multiverseArenas");
        if (match != null)
            this.chooseArena(fixedArena);
        //fill up boxes
        this.fillBoxes();
        this.attemptFight("/control");
        //click findMatch if available
        Utils.clickIfAvailable(this.lowerLeft, "findMatch");
        //seriesMatchScreen
        this.ifExistsClick("findNewMatch", "continue");
        Utils.clickIfAvailable(this.lowerRight, "accept");
        Utils.clickIfAvailable(this.tinyLowerRight, "continue");
        //fightStuff
        this.attemptFight("/control");
        //statsKO, victory
        this.ifExistsClick("statsKO","continueHighlighted");
        this.ifExistsClick("stats","continueHighlighted");

    }

    private void takeCareofRegens(){
        while(Utils.find(this.regenRegion, "regenBox")!=null) {
            Utils.clickIfAvailable(this.r, "cross");
            Utils.click(this.regenRegion.getLastMatch());
        }
    }

    private void fillBoxes(){
        try {
            while (Utils.find(this.thirdBoxRegion, "availableSpot") != null) {
                takeCareofRegens();//needs to be part of the logic here;
                //TODO: 'fallback' if the first one has that clock icon - means this roster is full, and we should go back and do another arena
                Utils.clickIfAvailable(this.r, "cross");
                this.r.dragDrop(this.regenRegion,this.thirdBoxRegion);
            }
        }catch(FindFailed e){
            System.out.println("Unexpected error at Edit Team Screen...");
        }
    }

    //TODO: this is currently optimized for the Macbook screen...
    public void controller(String fixedArena){
        while(true) {
            Utils.setImagesPath(this.local,"/control");
            uncommonOperations();

            this.attemptFight("/control");
            commonOperations(fixedArena);
            this.attemptFight("/control");
            commonOperations(fixedArena);
            this.attemptFight("/control");
            commonOperations(fixedArena);
            this.attemptFight("/control");
            //botFight

            //Attempt at any continue (may need 2 (highlited) or more(all different))
//            this.clickIfAvailable("continue");
//            this.clickIfAvailable("accept");

                //Utils.click(match.offset(0, 310));

            //Edit Team:
//            try {             //TODO: maybe not base it on edit team, but on empty box instead...??
//                Match boxMatch = Utils.findExact(this.r, "availableSpot");
//                if (boxMatch != null) {
//                    Match editTeam = Utils.findExact(this.r, "editTeam");
//                    Region champ = editTeam.offset(-200, 100);
//                    Region emptyBox = editTeam.offset(-380, 135);
//                    this.r.dragDrop(champ, emptyBox);
//                }
//            } catch (FindFailed e) {
//                System.out.println("Failed on Edit Team...");
//            }
//            try {
//                if ((Utils.findExact(this.r, "editTeam") != null) && (Utils.findExact(this.r, "availableSpot") == null))
//                    Utils.clickExact(this.r, "findMatch");
//            } catch (FindFailed e) {
//                System.out.println("Failed somehow on clicking findMatch");
//            }
            //fight won (lost too?)
//            Match fightFinished = Utils.find(this.r, "stats");
//            if(fightFinished!=null){
//                Region continueButton = fightFinished.offset(0,75);
//                Utils.click(continueButton);
//            }
//            //fight lost (won too?)
//            Match fightFinishedKO = Utils.find(this.r, "stats2");
//            if(fightFinishedKO!=null){
//                Region continueButton = fightFinishedKO.offset(0,75);
//                Utils.click(continueButton);
//            }
////            'random' click from fight region
//            Utils.click(this.fightRegion);
//            //botFight
//            this.attemptFight("/control");
//            Utils.sleep(5);
        }
    }

    public void tests(){
        try {
//            Utils.setImagesPath("/fight");
            Utils.setImagesPath(this.local,"/control");
//            this.attemptFight("/control");

//            Utils.highlightRegion(middleLowerColumn);
//            Utils.searchAndHighlight(this.r, "continue");
//            Match match = Utils.find(this.topMiddle, "multiverseArenas");
//            Utils.highlightRegion(this.tinyLowerRight);
//            Utils.highlightRegion(this.r);
            int centerX = this.r.getCenter().x;
            int centerY = this.r.getCenter().y;
            int xQuarter = this.r.getW()/4;
            int xEight = this.r.getW()/8;
            int xSixteenth = this.r.getW()/16;
            int yQuarter = this.r.getH()/4;
            int yEight = this.r.getH()/8;
            Region reg = new Region(centerX, centerY+yQuarter+yEight,50,25);
            Utils.highlightRegion(reg);
////            //Third Special bar red (L3 active)
////            Region specialBar = new Region(centerX-xSixteenth-xQuarter, centerY+yQuarter+yEight+ySixteenth,35,15);
////            Region specialButton = new Region(centerX-xSixteenth*2-xQuarter, centerY+yQuarter+yEight+ySixteenth,35,15);
//            Region sb = new Region(centerX-xSixteenth-xQuarter, centerY+yQuarter+yEight+this.r.getH()/30,xSixteenth,ySixteenth);
//            Utils.highlightRegion(sb);
//
//            if(Utils.findExact(specialBar, "specialRed")!=null)
//                Utils.click(specialButton);
//            Settings.MinSimilarity = 0.95;
//            Utils.find(this.r, "pause");
//            Utils.clickLastMatch(this.r);
//        this.clickWhileAvailable("regenBox");
//            Utils.findExact(this.r, "stats");
//            Region continueButton = Utils.findExact(this.r, "stats").offset(0, 75);
//            this.r.click(new Pattern("availableSpot").exact());
//            Utils.highlightRegion(this.r.getLastMatch());
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
        //Args:
        int firstCounter = 1;
        int secondCounter = 5;
//        MCoC battler = new MCoC("macbook_screen", firstCounter, secondCounter, 0.90, "macbook");
//        MCoC battler = new MCoC("iMac_small_screen", firstCounter, secondCounter, 0.90, "macbook");
        MCoC battler = new MCoC("iMac_screen", firstCounter, secondCounter, 0.90, "");
//        MCoC battler = new MCoC("macbook_FCTUNLExternalScreenLarge");
//        Utils.highlightRegion(battler.r);
//        battler.tests();
        battler.controller("none");

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
