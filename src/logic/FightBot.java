package logic;

import org.sikuli.script.FindFailed;
import org.sikuli.script.Region;

/**
 * Created by davidnavalho on 01/10/15.
 */
public class FightBot {

    Region r;
    int waitTimer = 30;
    int punchesRepeat = 2;

    Region attackRegion;
    Region specialBar;
    Region specialButton;

    public FightBot(Region r, Region attackRegion){
        this.r = r;
        this.attackRegion = attackRegion;
        this.setEnergyLocation();
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
        specialBar = new Region(centerX-xSixteenth-xQuarter, centerY+yQuarter+yEight+ySixteenth,35,15);
        specialButton = new Region(centerX-xSixteenth*2-xQuarter, centerY+yQuarter+yEight+ySixteenth,35,15);
    }


    public void fight() throws FindFailed {
        int counter = 0;
        Utils.setImagesPath("/fight");
//        this.r.wait("fightPause", 3);
        while(true){
            if(Utils.find(this.r, "pause")!=null) { //TODO: should this be exact? - maybe smaller region, too!
                System.out.println("Throwing some punches!");//TODO: swipes!
                for (int i = 0; i < 8; i++) {
                    Utils.click(attackRegion);
                }
                counter++;
                if(counter>=punchesRepeat){
                    if(Utils.find(this.specialBar, "specialRed")!=null)
                        Utils.click(this.specialButton);
                    else
                        counter=0;
                }
            }else
                break;
        }
    }
}
