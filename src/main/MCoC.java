package main;

import logic.*;
import org.sikuli.basics.Settings;
import org.sikuli.script.*;
import tasks.Control;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

public class MCoC extends Thread{
    public boolean execute = true;

    public Region r;
    private int X1,X2,Y1,Y2;
    private FightBot bot = null;
    private Region fightRegion = null;
    private int defaultReconnectWait = 10; //default minutes
    private int defaultMaxReconnectWait = 15; //default minutes
    private double similarity = 0.90;
    private String local = "";

    private Region topMiddle, lowerRight, lowerLeft, regenRegion, thirdBoxRegion, middleColumn, middleLowerColumn;

    //TODO: make reconnect a bit more aggressive: the game sometimes goes through multiple reconnects. Make an internal timeout before it gives up pressing reconnect again
    //TODO: remote enable/disable would be awesome! no clue how to do that, though...
    //TODO: core images actually required to make it work; fight images; extra images that are always clickable, and can be added to a folder!

    private Screen s = null;
    private Properties prop;

    private ArenasHandler arenasHandler;
    public MCoC() {
        this.loadProperties();
        this.defaultReconnectWait = this.getIntProperty("sleepMinimum");
        this.defaultMaxReconnectWait = this.getIntProperty("sleepMaximum");
        this.s = new Screen();
        this.local = "/"+this.getStringProperty("picsFolder");//localExtraPath;
        this.similarity = this.getDoubleProperty("similarity");
        Settings.ActionLogs = false;
        Settings.MinSimilarity = this.similarity;
        Settings.AutoWaitTimeout = this.getIntProperty("autoWaitTimeout");//TODO: default waittimeout is 3, changed it to 1! what about 0?
        Settings.MoveMouseDelay = new Float(this.prop.getProperty("mouseSpeed"));
//        this.first = this.getIntProperty("firstArena");
//        this.second = this.getIntProperty("secondArena");
//        this.setCatalystClashBasic(this.getIntProperty("catalystClashBasicArena"));
//        this.setCornucopia(this.getIntProperty("cornucopiaArena"));
        this.r = this.setScreen();
//        Utils.setImagesPath(this.local,"");
        this.setupRegions();
        this.setupFightBot();
        this.arenasHandler = new ArenasHandler(this.prop);
        this.setupArenas();
        Utils.highlightRegion(this.r); //TODO: make a tester button outside instead
        this.ctl = new Control(this);
        ctl.start();
    }

    private void setupArenas(){
        //Screen based arenas arenas: first and second arenas regular arenas (3, 4star)
        //int priority, int timesToRun, String done, Region screen, String arena, Properties props, long sleepTimer, Region clickRegion, boolean stopOnMilestone) {
        int threeStarRuns = Utils.getIntProperty(this.prop, "firstArena");
        int fourStarRuns = Utils.getIntProperty(this.prop, "secondArena");
        Arena threeStarArena = new ScreenPositionArena(Utils.getIntProperty(this.prop, "firstArenaPriority"), threeStarRuns, null, this.r, "first", this.prop, 0, false);
        Arena fourStarArena = new ScreenPositionArena(Utils.getIntProperty(this.prop, "secondArenaPriority"), fourStarRuns, null, this.r, "second", this.prop, 0, false);
        if(threeStarRuns>0)
            this.arenasHandler.addArena(threeStarArena);
        if(fourStarRuns>0)
            this.arenasHandler.addArena(fourStarArena);

        //Image Arenas: crystal cornucopia, other crystal arenas
        int cornucopia = Utils.getIntProperty(this.prop, "cornucopiaArena");
        int basic = Utils.getIntProperty(this.prop, "catalystClashBasicArena");
        int alpha = Utils.getIntProperty(this.prop, "catalystClashAlphaArena");
        int classs = Utils.getIntProperty(this.prop, "catalystClashClassArena");
        Arena crystalCornucopia = new ImageArena(Utils.getIntProperty(this.prop, "cornucopiaArenaPriority"), cornucopia, "noRewards", this.r, "crystalCornucopia", this.prop, 0, false);
        Arena catalystClashBasic = new ImageArena(Utils.getIntProperty(this.prop, "catalystClashBasicArenaPriority"), basic, "noRewards", this.r, "catalystClashBasicArena", this.prop, Utils.getIntProperty(this.prop, "arenaDoneSleepTimer"), true);
        Arena catalystClashAlpha = new ImageArena(Utils.getIntProperty(this.prop, "catalystClashAlphaArenaPriority"), alpha, "noRewards", this.r, "catalystClashAlphaArena", this.prop, Utils.getIntProperty(this.prop, "arenaDoneSleepTimer"), true);
        Arena catalystClashClass = new ImageArena(Utils.getIntProperty(this.prop, "catalystClashClassArenaPriority"), classs, "noRewards", this.r, "catalystClashClassArena", this.prop, Utils.getIntProperty(this.prop, "arenaDoneSleepTimer"), true);
        if(cornucopia>0)
            this.arenasHandler.addArena(crystalCornucopia);
        if(basic>0)
            this.arenasHandler.addArena(catalystClashBasic);
        if(alpha>0)
            this.arenasHandler.addArena(catalystClashAlpha);
        if(classs>0)
            this.arenasHandler.addArena(catalystClashClass);
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

    //TODO: change what's used here in terms of properties to using the Utils version
    private String getStringProperty(String key){
        return this.prop.getProperty(key);
    }

    private int getIntProperty(String key){
        return new Integer(this.prop.getProperty(key));
    }

    private double getDoubleProperty(String key){
        return new Double(this.prop.getProperty(key));
    }

//    public void setCatalystClashBasic(int number){
//        this.catalystClashBasic = number;
//    }

//    public void setCornucopia(int number){
//        this.cornucopia = number;
//    }

    private void setupRegions(){
        int height = this.r.getH();
        int width = this.r.getW();
        int quarterHeight = height/4;
        int quarterWidth = width/4;
        int eightWidth = width/8;
        int sixteenthWidth = width/16;

        //topMiddle: x,y => X1+quarterWidth, Y1; size: experiment
        this.topMiddle = new Region(X1+quarterWidth, Y1, width/2, height/2);
//        this.tinyUpTop = new Region(X1+quarterWidth+eightWidth+width/16, Y1, eightWidth, height/8);
        this.lowerRight = new Region(X2-width/2, Y2-quarterHeight, width/2, quarterHeight);
//        this.tinyLowerRight = new Region(X2-width/6, Y2-height/9, width/6, height/9);
        this.regenRegion = new Region(X1+quarterWidth, Y1+quarterHeight, eightWidth, quarterHeight);
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
        this.bot = new FightBot(this.r, fightRegion, this.topMiddle, this.similarity,this.local, this.prop);
    }//TODO: randomly vary amount of hits during fight;
//TODO: the continue REALLY needs to be on the small enclosed box, i think....


//    private Region getArenaRegion(String arena){
//        int centerX = this.r.getCenter().x;
//        int centerY = this.r.getCenter().y;
//        int xQuarter = this.r.getW()/4;
//        int xEight = this.r.getW()/8;
//        int xSixteenth = this.r.getW()/16;
//        int yQuarter = this.r.getH()/4;
//        int yEight = this.r.getH()/8;
//        if(arena.equalsIgnoreCase("first")) {
//            System.out.println("First Arena Button chosen.");
//            return new Region(centerX - xEight - xSixteenth, centerY + yQuarter + yEight, 50, 25);
//        }
//        else if(arena.equalsIgnoreCase("second")) {
//            System.out.println("Second Arena Button chosen.");
//            return new Region(centerX + xQuarter, centerY + yQuarter + yEight, 50, 25);
//        } else if(arena.equalsIgnoreCase("single")){
//            System.out.println("Single Arena Button chosen.");
//            return new Region(centerX, centerY+yQuarter+yEight,50,25);
//        }
//        //return first arena by default (i.e.: error, etc
//        System.out.println("No known arena identified, choosing first");
//        return new Region(centerX-xEight-xSixteenth, centerY+yQuarter+yEight,50,25);
//    }

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


//    private Region setScreen(String location){
//        int sn = 0;
//        X1 = 0;
//        Y1 = 0;
//        X2 = 0;
//        Y2 = 0;
//        if(location.equalsIgnoreCase("macbook_screen")){
//            sn = 0;
//            X1 = 480;
//            Y1 = 45;
//            X2 = 1230;
//            Y2 = 465;
////            Y1 -= 1080;
////            Y2 -= 1080;
//        }else if(location.equalsIgnoreCase("iMac_screen")){
//            sn = 0;
//            X1 = 1120;
//            Y1 = 45;
//            X2 = 2510;
//            Y2 = 825;
//        }else if(location.equalsIgnoreCase("iMac_bluestacks")){
//            sn = 0;
//            X1 = 1280;
//            Y1 = 45;
//            X2 = 2560;
//            Y2 = 715;
//        }else if(location.equalsIgnoreCase("macbook_bluestacks")){
//            sn = 0;
//            X1 = 130;
//            Y1 = 45;
//            X2 = 1280;
//            Y2 = 645;
//        }
//        this.s = new Screen(sn);
//        return s.setRect(X1, Y1, X2-X1, Y2-Y1);
//    }

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

    private boolean moveScreen(Match movable){
        try {
            this.s.hover(new Location(movable.getTopRight().getX() - (movable.getW() / 10), movable.getY()));
            this.s.mouseDown(Button.LEFT);
            Utils.sleepMilis(50);
            this.s.hover(new Location(movable.getTopRight().getX() - (this.r.getW() / 80) * 3, movable.getY()));
            Utils.sleepMilis(50);
            this.s.hover(new Location(movable.getTopRight().getX() - (this.r.getW() / 80) * 4, movable.getY()));
            Utils.sleepMilis(50);
            this.s.hover(new Location(movable.getTopRight().getX() - (this.r.getW() / 80) * 5, movable.getY()));
            Utils.sleepMilis(50);
            this.s.hover(new Location(movable.getTopRight().getX() - (this.r.getW() / 80) * 6, movable.getY()));
            Utils.sleepMilis(50);
            this.s.hover(new Location(movable.getTopRight().getX() - (this.r.getW() / 80) * 7, movable.getY()));
            Utils.sleepMilis(50);
            this.s.mouseUp();
            Utils.sleepMilis(100);
        } catch (FindFailed ff) {
            this.s.mouseUp();
            Utils.sleepMilis(100);
            return false;
        } catch (NullPointerException np) {
            this.s.mouseUp();
            Utils.sleepMilis(100);
            return false;
        }

        this.s.mouseUp();
        Utils.sleepMilis(100);
        return true;
    }

    //TODO: handle error of not finding catalyst area (e.g. when i manually 'help' it find it....(NullPointerException)
    private void enterCatalystArena(){
        try {
            for (int i = 0; i < 35; i++)  //TODO: 35 should be on the configure file
                if (Utils.find(this.r, "catalystClashBasicArena") == null) //then it didn't find it, and should 'look' for it'
                    if(!this.moveScreen(Utils.find(this.r, "arenaInfo")))
                        Utils.clickIfAvailable(this.r, "backButton");

            Match match = null;
            if ((match = Utils.find(this.r, "catalystClashBasicArena")) != null) {//then it found it and should enter it!
                Region arena = new Region(match.getX(), match.getY()+this.r.getH()/2, match.getW(), match.getH());
                Utils.click(arena);
            } else {//TODO:otherwise go back and try whatever comes next
                System.out.println("Failed to Find CatalystArena, going back...");
                Utils.clickIfAvailable(this.r, "backButton");
            }
        }catch(Exception e){
            this.s.mouseUp();
            Utils.sleepMilis(200);
            System.out.println("Some error found related with catalyst arena...");
            e.printStackTrace();
            Utils.clickIfAvailable(this.r, "backButton");
        }
    }

    //TODO: enter cornucopia based on relative position to cornucopia found image, not a fixed position
    //position may change due to looking for catalystClashBasic
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
    private int catalystClashBasicDoneCounter = 0;
    private int catalystClashBasic = 0;
    private int cornucopia = 1;
    private int cornucopiaDoneCounter = 0;
    //Pre: sum(arenas) must always be>0
//    private void chooseArena(){
//        if(this.cornucopiaActive() && this.cornucopiaDoneCounter>0){
//            this.cornucopiaDoneCounter--;
//            //if cornucopia is active, the others are not, thus set them to zero
//            this.firstArenaDoneCounter = 0;
//            this.secondArenaDoneCounter = 0;
//            this.enterCornucopia();
//        }else if(this.catalystClashBasicDoneCounter >0) {
//            this.catalystClashBasicDoneCounter--;
//            this.enterCatalystArena();
//        }else if(this.secondArenaDoneCounter>0){
//            this.secondArenaDoneCounter--;
//            Utils.click(this.getArenaRegion("second"));
//        }else if(this.firstArenaDoneCounter>0){
//            this.firstArenaDoneCounter--;
//            Utils.click(this.getArenaRegion("first"));
//        }else{//If it reaches this, then it's iterated
//            this.firstArenaDoneCounter = this.first;
//            this.secondArenaDoneCounter = this.second;
//            this.catalystClashBasicDoneCounter = this.catalystClashBasic;
//            this.cornucopiaDoneCounter = this.cornucopia;
//        }
//    }

    private void runArena(){

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
        //Arenas insufficient Funds
        this.ifExistsClick("insufficientFunds", "cross");
        //Play Versus
        Utils.clickIfAvailable(this.r, "playVersus");
//        Utils.clickIfAvailable(this.r, "playVersus2");
        this.attemptFight("/control");
        //handle clicking on champ by mistake
        this.ifExistsClick("info", "cross");    //TODO:the hell is this? put this on the Utils!!
        //dismiss rate us
        this.ifExistsClick("rankRewards","backButton");
    }

    private void rareOperations(){
//        Utils.setImagesPath(this.local,"/click");
        Utils.clickIfAvailable(this.r, "later");
    }

    //TODO: when adding files, button doesnt change to green!
    private void commonOperations(){
        Utils.setImagesPath(this.local,"/control");
        //Check for Arenas, enter one:
        Match match = Utils.find(this.topMiddle, "multiverseArenas");
        if (match != null)
            this.arenasHandler.runNextArena();//chooseArena();
        //fill up boxes
        this.fillBoxes();
        this.attemptFight("/control");
        //click findMatch if available
        Utils.clickIfAvailable(this.lowerLeft, "findMatch");
        //seriesMatchScreen
        Utils.clickIfAvailable(this.lowerRight, "accept");
//        Utils.highlightRegion(this.tinyLowerRight);
        if(Utils.isAvailable(this.r,"multiverseArenas",0)==null)
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
            Region dragRegion = this.regenRegion;

            while (Utils.find(this.thirdBoxRegion, "thirdAvailableSpot") != null) {
                takeCareofRegens();//needs to be part of the logic here;
                //TODO: 'fallback' if the first one has that clock icon - means this roster is full, and we should go back and do another arena
                Utils.clickIfAvailable(this.r, "cross");
                Utils.sleep(3);
                takeCareofRegens();
                this.r.dragDrop(dragRegion, this.thirdBoxRegion);
            }
        }catch(FindFailed e){
            System.out.println("Unexpected error at Edit Team Screen...");
        }
    }

    //TODO: this is currently optimized for the Macbook screen...
    public void controller(){
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

//    private boolean detectIfArenaDone(String arena){
//        boolean done = false;
//        Match m = Utils.find(this.r, arena);
//        Region bigPicture = new Region(m.getX(), m.getY(),m.getW()*2+m.getH(),m.getW()*2);
//        Match result = Utils.find(bigPicture,"dummy");
//        if(result!=null)
//
//
//    }

    private boolean detectIfArenaDone(String arena){
        for(int i=0;i<10;i++) {
            Match m = Utils.find(this.r, arena);
            Region pictureReg = new Region(m.getX(), m.getY(), m.getW() * 2 + m.getH(), m.getW() * 2);
            Utils.highlightRegion(pictureReg);
            if(pictureReg.getBottomRight().getX() < this.r.getBottomRight().getX())
                return Utils.find(pictureReg,"dummy")!=null;
            else
                moveScreen(m);
        }
        return false;
    }

    public void tests(){
        try {
//            Utils.setImagesPath("/fight");
            Utils.setImagesPath(this.local,"/control");
//            this.enterCatalystArena();
            Match m = Utils.find(this.r, "catalystClashBasicArena");
            Utils.highlightRegion(m);
            Region newReg = new Region(m.getX(), m.getY(),m.getW()+m.getW(),m.getH());
            Utils.highlightRegion(newReg);
//            Region test = new Region(this.r.getX(), this.r.getY(),this.r.getBottomRight().getX(), this.r.getBottomRight().getY());
//            Region newReg = new Region(m.getX(), m.getY(),m.getW()*2+m.getH(),m.getW()*2);
//            if(this.detectIfArenaDone( "catalystClashBasicArena"))
//                System.out.println("DONE");
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    //Clash End: Wed 23Dec, 23:00

    //Catalyst start: Sun, 15Nov, 23:00
    //TODO: better method of identifying catalystClashBasic appearance, even if by date?
    public static void main(String[] args) {
//        main.MCoC battler = new main.MCoC("macbook_screen", firstCounter, secondCounter, 0.90, "macbook");
        MCoC battler = new MCoC();
//          battler.tests();
        while(true) {
            battler.controller();
        }
    }

    public void run(){
        while(this.execute) {
            this.controller();
        }
    }

    public void stopExecuting(){
        this.execute = false;
    }
}
