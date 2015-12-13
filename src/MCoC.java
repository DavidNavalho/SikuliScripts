import logic.FightBot;
import logic.Utils;
import org.sikuli.basics.Settings;
import org.sikuli.script.*;
import tasks.Control;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

public class MCoC {
    public Region r;
    private int X1,X2,Y1,Y2;
    private int wait = 15;
    private int defaultWaitTime = 3;
    private FightBot bot = null;
    private Region fightRegion = null;
    private int defaultReconnectWait = 15; //minutes
    private int defaultMaxReconnectWait = 20; //minutes
    private int first = 1;
    private int second = 1;
    private double similarity = 0.90;
    private String local = "";

    private Region topMiddle, lowerRight, lowerLeft, regenRegion, thirdBoxRegion, middleColumn, middleLowerColumn;

    //TODO: make reconnect a bit more aggressive: the game sometimes goes through multiple reconnects. Make an internal timeout before it gives up pressing reconnect again
    //TODO: remote enable/disable would be awesome! no clue how to do that, though...
    //TODO: core images actually required to make it work; fight images; extra images that are always clickable, and can be added to a folder!

    private Screen s = null;
    private Properties prop;
    public MCoC() {
        this.loadProperties();
        this.s = new Screen();
        this.local = "/"+this.getStringProperty("picsFolder");//localExtraPath;
        this.similarity = this.getDoubleProperty("similarity");
        Settings.MinSimilarity = this.similarity;
        Settings.AutoWaitTimeout = this.getIntProperty("autoWaitTimeout");//TODO: default waittimeout is 3, changed it to 1! what about 0?
        this.first = this.getIntProperty("firstArena");
        this.second = this.getIntProperty("secondArena");
        this.setCatalystClash(this.getIntProperty("catalystClashArena"));
        this.setCornucopia(this.getIntProperty("cornucopiaArena"));
        this.r = this.setScreen();
//        Utils.setImagesPath(this.local,"");
        this.setupRegions();
        this.setupFightBot();
        Utils.highlightRegion(this.r);
        this.ctl = new Control();
        ctl.start();
    }
    private Control ctl;

    private void loadProperties(){
        this.prop = new Properties();
        InputStream input = null;
        try{
            input = new FileInputStream("config.properties");
            prop.load(input);
        }catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String getStringProperty(String key){
        return this.prop.getProperty(key);
    }

    private int getIntProperty(String key){
        return new Integer(this.prop.getProperty(key));
    }

    private double getDoubleProperty(String key){
        return new Double(this.prop.getProperty(key));
    }

    public void setCatalystClash(int number){
        this.catalystClash = number;
    }

    public void setCornucopia(int number){
        this.cornucopia = number;
    }

    private void setupRegions(){
        int height = this.r.getH();
        int width = this.r.getW();
        int quarterHeight = height/4;
        int quarterWidth = width/4;
        int eightWidth = width/8;
        //topMiddle: x,y => X1+quarterWidth, Y1; size: experiment
        this.topMiddle = new Region(X1+quarterWidth, Y1, width/2, height/2);
//        this.tinyUpTop = new Region(X1+quarterWidth+eightWidth+width/16, Y1, eightWidth, height/8);
        this.lowerRight = new Region(X2-width/2, Y2-quarterHeight, width/2, quarterHeight);
//        this.tinyLowerRight = new Region(X2-width/6, Y2-height/9, width/6, height/9);
        this.regenRegion = new Region(X1+quarterWidth, Y1+quarterHeight, eightWidth, quarterHeight);//TODO: could do with enlarging it...
        this.thirdBoxRegion = new Region(X1+width/16, Y1+2*height/5, 3*width/20, 3*height/18);
        this.middleColumn = new Region(X1+quarterWidth, Y1, width/2, height);
        this.middleLowerColumn = new Region(X1+quarterWidth, Y1+height/2, width/2, height/2);
        this.lowerLeft = new Region(X1, Y2-quarterHeight, quarterWidth, quarterHeight);
//        Utils.highlightRegion(this.lowerLeft);
    }

    private void setupFightBot(){
        int centerX = this.r.getCenter().x;
        int x = centerX + (this.r.getW()/4);
        int centerY = this.r.getCenter().y;
        this.fightRegion = new Region(x, centerY,50,50);
        this.bot = new FightBot(this.r, fightRegion, this.topMiddle, this.similarity,this.local);
    }//TODO: randomly vary amount of hits during fight;
//TODO: the continue REALLY needs to be on the small enclosed box, i think....


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

    private Region setScreen(){
        App app = new App(this.getStringProperty("app"));//"player");
        int appWindow = this.getIntProperty("appWindow");
        app.focus(appWindow);//2);
//        System.out.println("appwindow: "+appWindow);
//        Utils.highlightRegion(app.window(0));
//        System.out.println("> Coordinates: "+app.window(2).getX()+","+app.window(2).getY());
//        System.out.println("> ...to: "+app.window(2).getW()+","+app.window(2).getH());
        X1 = app.window(appWindow).getX();
        Y1 = app.window(appWindow).getY();
        X2 = app.window(appWindow).getW()+X1;
        Y2 = app.window(appWindow).getH()+Y1;
        this.s = new Screen();
        return s.setRect(X1, Y1, X2-X1, Y2-Y1);
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
        }else if(location.equalsIgnoreCase("iMac_bluestacks")){
            sn = 0;
            X1 = 1280;
            Y1 = 45;
            X2 = 2560;
            Y2 = 715;
        }else if(location.equalsIgnoreCase("macbook_bluestacks")){
            sn = 0;
            X1 = 130;
            Y1 = 45;
            X2 = 1280;
            Y2 = 645;
        }
        this.s = new Screen(sn);
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

    //TODO: handle error of not finding catalyst area (e.g. when i manually 'help' it find it....(NullPointerException)
    private void enterCatalystArena(){
        //repeat up to 3 times
        for(int i=0;i<15;i++) {//TODO: problems with catalyst arena not found and null pointers...
            if(Utils.find(this.r, "catalystClashArena")==null) {//then it didn't find it, and should 'look' for it'
                Match m = Utils.find(this.r, "arenaInfo");
                try {
                    Location newLocation = new Location(m.getX() - this.r.getW()/4, m.getY());
//                    this.r.dragDrop(m, newReg);
//                    this.r.dragDrop(m, newLocation);
                    this.s.hover(m);
                    this.s.mouseDown(Button.LEFT);
                    Utils.sleep(1);
                    this.s.hover(m.offset(-1,0));
                    Utils.sleep(1);
//                    this.s.hover(new Location(m.getX() - 100, m.getY()));
//                    Utils.sleep(1);
                    this.s.hover(new Location(m.getX() - (this.r.getW()/40), m.getY()));
                    Utils.sleep(1);
                    this.s.hover(new Location(m.getX() - (this.r.getW()/40)*2, m.getY()));
                    Utils.sleep(1);
                    this.s.hover(new Location(m.getX() - (this.r.getW()/40)*3, m.getY()));
                    Utils.sleep(1);
                    this.s.hover(new Location(m.getX() - (this.r.getW()/40)*4, m.getY()));
                    Utils.sleep(1);
                    this.s.hover(new Location(m.getX() - (this.r.getW()/40)*5, m.getY()));
                    Utils.sleep(1);
                    this.s.mouseUp();
                    Utils.sleep(2);
                }catch(FindFailed ff){
                    System.out.println("Did not find Arena to slide.");
                }
            }
        }
        Match match = null;
        if((match = Utils.find(this.r, "catalystClashArena"))!=null){//then it found it and should enter it!
            Region arena = new Region(match.getX(), match.getY()+this.r.getH()/2,match.getW(),match.getH()/4);
            Utils.click(arena);
        }else {//TODO:otherwise just 'enter' a random one???
            System.out.println("Failed to Find CatalystArena...");
        }
    }

    //TODO: enter cornucopia based on relative position to cornucopia found image, not a fixed position
    //position may change due to looking for catalystClash
    private void enterCornucopia(){
        Match m =  Utils.find(this.r, "crystalCornucopia");
        if(m!=null){
            Region clickRegion = new Region(m.getX(), m.getY()+this.r.getH()/2, m.getW(), m.getH());
            Utils.click(clickRegion);
        }else
            System.out.println("Couldn't find Crystal Cornucopia");
//        Utils.clickIfAvailable(this.r, );
    }

    private boolean cornucopiaActive(){
        if(Utils.find(this.r, "crystalCornucopia")!=null)
            return true;
        return false;
    }

    private int secondArenaDoneCounter = 0;
    private int firstArenaDoneCounter = 0;
    private int catalystClashDoneCounter = 0;
    private int catalystClash = 0;
    private int cornucopia = 1;
    private int cornucopiaDoneCounter = 0;
    //Pre: sum(arenas) must always be>0
    private void chooseArena(){
        if(this.catalystClashDoneCounter>0) {
            this.catalystClashDoneCounter--;
            this.enterCatalystArena();
        }else if(this.cornucopiaActive() && this.cornucopiaDoneCounter>0){
            this.cornucopiaDoneCounter--;
            //if cornucopia is active, the others are not, thus set them to zero
            this.firstArenaDoneCounter = 0;
            this.secondArenaDoneCounter = 0;
            this.enterCornucopia();
        }else if(this.secondArenaDoneCounter>0){
            this.secondArenaDoneCounter--;
            Utils.click(this.getArenaRegion("second"));
        }else if(this.firstArenaDoneCounter>0){
            this.firstArenaDoneCounter--;
            Utils.click(this.getArenaRegion("first"));
        }else{//If it reaches this, then it's iterated
            this.firstArenaDoneCounter = this.first;
            this.secondArenaDoneCounter = this.second;
            this.catalystClashDoneCounter = this.catalystClash;
            this.cornucopiaDoneCounter = this.cornucopia;
        }
    }

    private void uncommonOperations(){
        Utils.setImagesPath(this.local,"/control");
        //Reconnect menu TODO: make it sleep for at least some minutes before reconnecting
        Match m = Utils.find(this.middleColumn, "reconnect");
        if(m!=null) {
            Random rnd = new Random();
            int wait = rnd.nextInt(this.defaultMaxReconnectWait-this.defaultReconnectWait);
            System.out.println("Reconnect found, sleeping " + (this.defaultReconnectWait+wait) + " minutes.");
            Utils.sleep((this.defaultReconnectWait+wait)*60);
            Utils.clickIfAvailable(this.middleColumn, "reconnect");
        }
        this.attemptFight("/control");
        //Main Screen
        Utils.clickIfAvailable(this.r, "mainMenuFight");
        //Play Versus
        Utils.clickIfAvailable(this.r, "playVersus");
        this.attemptFight("/control");
        //handle clicking on champ by mistake
        this.ifExistsClick("info", "cross");
        //dismiss rate us
    }

    private void rareOperations(){
        Utils.setImagesPath(this.local,"/click");
        Utils.clickIfAvailable(this.r, "later");
    }

    private void commonOperations(){
        Utils.setImagesPath(this.local,"/control");
        //Check for Arenas, enter one:
        Match match = Utils.find(this.topMiddle, "multiverseArenas");
        if (match != null)
            this.chooseArena();
        //fill up boxes
        this.fillBoxes();
        this.attemptFight("/control");
        //click findMatch if available
        Utils.clickIfAvailable(this.lowerLeft, "findMatch");
        //seriesMatchScreen
        Utils.clickIfAvailable(this.lowerRight, "accept");
//        Utils.highlightRegion(this.tinyLowerRight);
        Utils.clickIfAvailable(this.lowerRight, "continue");
        //fightStuff
        this.attemptFight("/control");
        //statsKO, victory
        this.ifExistsClick("stats","stats");

    }

    private void takeCareofRegens(){
        while(Utils.find(this.r, "regenBox")!=null) {
//            Utils.clickIfAvailable(this.r, "cross");
            Utils.clickIfAvailable(this.r, "regenBox");
        }
    }

    private void fillBoxes(){
        try {
            while (Utils.find(this.thirdBoxRegion, "thirdAvailableSpot") != null) {
                takeCareofRegens();//needs to be part of the logic here;
                //TODO: 'fallback' if the first one has that clock icon - means this roster is full, and we should go back and do another arena
                Utils.clickIfAvailable(this.r, "cross");
                Utils.sleep(3);
                takeCareofRegens();
                this.r.dragDrop(this.regenRegion,this.thirdBoxRegion);
            }
        }catch(FindFailed e){
            System.out.println("Unexpected error at Edit Team Screen...");
        }
    }

    //TODO: this is currently optimized for the Macbook screen...
    public void controller(){
        while(true) {
            Utils.setImagesPath(this.local,"/control");
            uncommonOperations();
            rareOperations();

            this.attemptFight("/control");
            commonOperations();
            this.attemptFight("/control");
            commonOperations();
            this.attemptFight("/control");
            commonOperations();
            this.attemptFight("/control");
        }
    }

    public void tests(){
        try {
//            Utils.setImagesPath("/fight");
            Utils.setImagesPath(this.local,"/control");
            this.enterCatalystArena();

////            this.attemptFight("/control");
//                Utils.highlightRegion(Utils.find(this.r,"catalystClashArena"));
//            Match m = Utils.find(this.r, "catalystClashArena");
////
//////            Utils.highlightRegion(middleLowerColumn);
//////            Utils.searchAndHighlight(this.r, "continue");
//////            Match match = Utils.find(this.topMiddle, "multiverseArenas");
//////            Utils.highlightRegion(this.tinyLowerRight);
//////            Utils.highlightRegion(this.r);
//            int centerX = this.r.getCenter().x;
//            int centerY = this.r.getCenter().y;
//            int xQuarter = this.r.getW()/4;
//            int xEight = this.r.getW()/8;
//            int xSixteenth = this.r.getW()/16;
//            int yHalf = this.r.getH()/2;
//            int yQuarter = this.r.getH()/4;
//            int yEight = this.r.getH()/8;
////
//            Region enter = new Region(m.getX(), m.getY()+this.r.getH()/2,m.getW(),m.getH()/4);
//            Utils.highlightRegion(enter);

//            Region reg = new Region(centerX, centerY+yQuarter+yEight,50,25);
//            Utils.highlightRegion(reg);
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


    //Catalyst start: Sun, 15Nov, 23:00
    //TODO: better method of identifying catalystClash appearance, even if by date?
    public static void main(String[] args) {
//        MCoC battler = new MCoC("macbook_screen", firstCounter, secondCounter, 0.90, "macbook");
        MCoC battler = new MCoC();
        battler.controller();

    }
}
