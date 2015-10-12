import org.sikuli.script.*;

import java.io.File;

/**
 * Created by davidnavalho on 11/05/15.
 */
public class QuickTester {

    public Region r;
    private int wait = 15;

    public QuickTester(String location) {
        this.r = this.setScreen(location);
        this.setImagesPath("");
    }

    public void highlightRegion(){
        this.r.highlight(2);
    }

    public Region setScreen(String location){
        int sn = 0;
        int X1 = 0;
        int Y1 = 0;
        int X2 = 0;
        int Y2 = 0;

        if(location.equalsIgnoreCase("macbook_homeExternalScreen")) {
            sn = 1;
            X1 = 840;  //805  (~35)
            Y1 = 145;  //90   (~55)
            X2 = 1265;  //1230   (~35)
            Y2 = 780;   //725    (~55)
            Y1 -= 1080;
            Y2 -= 1080;
        }else if(location.equalsIgnoreCase("macbook_FCTUNLExternalScreenLarge")){
            sn = 1;
            X1 = 1290;
            Y1 = 105;
            X2 = 1870;
            Y2 = 980;
            Y1 -= 1080;
            Y2 -= 1080;
        }

        Screen s = new Screen(sn);
        return s.setRect(X1, Y1, X2-X1, Y2-Y1);
    }

    private void clicky() throws FindFailed{
        this.r.click(r.getLastMatch());
    }
    private void doubleClicky() throws FindFailed{
        this.r.doubleClick(r.getLastMatch());
    }

    private boolean testAndClickWithOffset(String image, int waitTime, int xOffset, int yOffset){
        try{
            Pattern pImage = new Pattern(image).targetOffset(xOffset,yOffset);
            Match match = r.wait(pImage, waitTime);
            this.doubleClicky();
        }catch(FindFailed ff) {
            return false;
        }
        return true;
    }

    private void test(){
        try{
            Match match = r.wait("glovedHand",5);
            match.highlight();
            out("score:" + match.getScore());

        } catch(FindFailed ff) {

        }
    }

    private boolean testAndClick(String image, int waitTime, boolean doubleClick){
        try{
            Match match = r.wait(image, waitTime);
//            this.out("Found something, highlighting...");
//            match.highlight();
            if(doubleClick)
                doubleClicky();
            else
                clicky();
        }catch(FindFailed ff) {
            return false;
        }
        return true;
    }

    private void delay(int delay){
        try {
            Thread.sleep(delay * 1000);
        } catch (InterruptedException e) {
            //TODO?
        }
    }

    private boolean testAndClickWithDelay(String image, int waitTime, int delay, boolean doubleClick){
        try{
            r.wait(image, waitTime);
            this.delay(delay);
            if(doubleClick)
                doubleClicky();
            else
                clicky();
        }catch(FindFailed ff) {
            return false;
        }
        return true;
    }

    private void out(String out){
        System.out.println("\t> "+out);
    }

    private Match findDungeon(String dungeon){
        try{
            return r.find(dungeon);
        }catch(FindFailed ff){
            System.out.println("Dungeon "+dungeon+" not found!");
        }
        return null;
    }

    private Match findEvent() {
        this.setImagesPath("");
        try{
            r.doubleClick("event");
            r.wait("realms",wait);
            Match m = null;
            m = this.findDungeon("elemental");
            if(m!=null){
                System.out.println("Elemental Dungeon found!");
                this.elementalDungeon();
            }
        }catch(FindFailed ff){
            System.out.println("Event find failed");
        }
        return null;
    }

    private void setImagesPath(String path){
        String pathImgDefault = new File(System.getProperty("user.dir"), "imgs"+path).getAbsolutePath();
        ImagePath.setBundlePath(pathImgDefault);
        System.out.println("Image paths: " + ImagePath.getPaths());
    }

    private void battle(){
        this.setImagesPath("/battle");
        out("Battle");
        try{
            testAndClickWithDelay("auto", wait*2, 5, false);
//            r.wait("auto",wait*2);
//            clicky();
            out("Autoing Battle...");
            //r.wait("cancelAuto");//TODO: while CancelAuto detected, wait a little bit more
            //r.wait("blueLoading",30);//give at least 30seconds for the fight...but i want it to go to the next step...and it may miss...doesnt work currently like this
            r.wait("battleResults",wait*10);
            out("Battle Results! > Next");
            this.testAndClickWithDelay("next", 5, 5, false);
            out("Next pressed");
            this.delay(5);
            this.testAndClickWithDelay("next", 5, 5, false);
            out("Exp Screen Next pressed");
            this.delay(5);
            //TODO: items earning, etc (many OKs)
            //e aqui pode haver ou não um (ou mais) OK....crap!
            //seguido do ecrã de 'Loot' e um 'Next'
            //e está feito... (loading preto)

        }catch(FindFailed ff){
            //TODO
            //increment fail counter;
            //maybe run some extra clicks/checks/etc
            //exit when fails
        }
    }

    private void fireCrystalAwakens(){
        this.setImagesPath("/events/fireCrystal");
        out("Running Fire Crystal Awakens Event! No boss :(");
        testAndClick("part1", 1, true);
        testAndClick("beginBattle", 5, false);
        testAndClick("go!", 5, false);
        this.battle();
    }

    private void elementalDungeon(){
        this.setImagesPath("/monday");
        out("Running Monday Dungeon!");
        try {
            out("Entering Dungeon");
            clicky();
            r.wait("hardX",wait);
            out("Hard");
            clicky();
            r.wait("go!",wait);
            out("go!");
            clicky();
            r.wait("go!",wait);
            out("go! again");
            clicky();
            r.wait("part1H",wait);
            out("Part 1 - Hard");
            clicky();
            r.wait("beginBattle");
            clicky();
            out("Begin Battle");
            //and battle screen! (function for battle...)
        }catch(FindFailed ff){
            //TODO
            //incremente fail counter;
            //maybe run some extra clicks/checks/etc
            //exit when fails
        }
    }

    private Boolean staminaAvailable(String originalPath){
        this.setImagesPath("/errors");
        Match match = this.r.exists("restoreStamina", 5);
        if(match!=null) {
            out("Not enough stamina...sleeping for 10 minutes...");
            this.delay(60 * 10);
            out("Back to work!");
            this.testAndClick("back",5,false);
            out("Clicked back!");
        }
        this.setImagesPath(originalPath);
        if(match!=null)
            return false;
        return true;
    }

    public void checkTheHand(){
        try {
            this.r.wait("camp", 10);
            out("Dungeon Screen detected, searching for the next level...");
            this.testAndClickWithOffset("glovedHand", 5, 20, 0);
            out("Hand clicked myself!");
            this.delay(5);
        }catch(FindFailed ff){
            //TODO
        }
    }

    public void gilDungeon(){
        String localPath = "";
        int i=0;
        while(true) {
            this.setImagesPath(localPath);
            while(true) {
                this.checkTheHand();
                //            this.testAndClickWithOffset("glovedHand", 5, 20, 0);
                //            this.delay(5);
                if(this.staminaAvailable(localPath))
                    break;
            }
            this.testAndClick("beginBattle", 5, false);
            this.delay(5);
            this.battle();
            //TODO:check for dungeon completeness
            i++;
            if(i==3) {
                out("i==3");
                break;
            }
        }
    }

    public void gilDungeonSelect(){
        this.setImagesPath("");
        out("Running Gil Dungeon!");
        this.testAndClickWithDelay("heroic", 5, 1, true);
        this.delay(5);
        this.testAndClick("go!", 5, false);
        this.delay(5);
        this.testAndClick("go!", 5, false);
        this.delay(5);
        this.gilDungeon();
    }

    public void go(){
//        Match event = this.findEvent(); skip for now, consider already inside gil event...
//          Match event = this.findDungeon("gil");
//        this.gilDungeon();//3x...2 times now...
        this.gilDungeonSelect();
    }

    public static void main(String[] args) {


//        QuickTester qt = new QuickTester("macbook_FCTUNLExternalScreenLarge");
        QuickTester qt = new QuickTester("macbook_homeExternalScreen");
//        qt.test();
        qt.highlightRegion();
        while(true)
            qt.go();

//        qt.staminaAvailable();
//        qt.fireCrystalAwakens();
//        qt.go();
//        r.hover();
//        s1.hover();
//        s2.hover();
//        try{
////            r.find("event");
////            int res = r.click("imgs/event.png");
//            r.exists("event.png");
////            System.out.println(res);
////            System.out.println("match:"+match.getTarget());
//
//            r.doubleClick(r.getLastMatch());
////            r.click("imgs/Event.png");
//        }
//        catch(FindFailed e){
//            e.printStackTrace();
//        }
    }
}
