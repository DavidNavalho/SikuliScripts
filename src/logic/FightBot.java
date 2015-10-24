package logic;

import org.sikuli.script.FindFailed;
import org.sikuli.script.Region;

public class FightBot {

    Region r;
    int waitTimer = 30;
    int punchesRepeat = 2;

    Region attackRegion;
    Region specialBar;
    Region specialButton;
    Region pauseRegion;

    public FightBot(Region r, Region attackRegion, Region tinyUpTop){
        this.r = r;
        this.attackRegion = attackRegion;
        this.setEnergyLocation();
        this.pauseRegion = tinyUpTop;
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


    public void fight() throws FindFailed {
        int counter = 0;
        boolean specialActive = false;
        Utils.setImagesPath("/fight");
//        this.r.wait("fightPause", 3);
        System.out.println("Looking for a fight...");
        while(true){
            if(Utils.find(this.pauseRegion, "pause")!=null) {
                System.out.println("Throwing some punches!");//TODO: swipes!
                for (int i = 0; i < 12; i++) {
                    Utils.click(attackRegion);
                }
                if(specialActive)
                    Utils.click(this.specialButton);
                else {
                    counter++;
                    if(counter >= punchesRepeat)
                        if(Utils.findWithSimilarity(this.specialBar, "specialRed", 0.75) != null)
                            specialActive = true;
                }
            }else
                break;
        }
    }
}
