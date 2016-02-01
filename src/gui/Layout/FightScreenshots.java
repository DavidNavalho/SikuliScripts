package gui.Layout;

import gui.Layout.Panels.Screenshot;
import gui.MCoCFrame;
import logic.Utils;

import javax.swing.*;
import java.io.File;

/**
 * Created by davidnavalho on 21/01/16.
 */
public class FightScreenshots extends JPanel{

    private MCoCFrame father;
    protected String fightScreenshotsPath;
//    private LinkedList<String> screenshots;

    public FightScreenshots(MCoCFrame father){
        super();
        this.father = father;
        String local = Utils.getStringProperty(this.father.props,"picsFolder");
        String path = "/images/fight";
        this.fightScreenshotsPath = new File(System.getProperty("user.dir"), "imgs"+"/"+local+path).getAbsolutePath();
        this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));

        String pause = "/pause.png";
        String specialRed = "/specialRed.png";

//        this.screenshots = new LinkedList<String>();
//        this.screenshots.add("/pause.png");
//        this.screenshots.add("/specialRed.png");

        Screenshot pauseSS = new Screenshot(father, "Pause",System.getProperty("user.dir")+"/resources/fight"+pause,fightScreenshotsPath+pause);
        Screenshot specialRedSS = new Screenshot(father, "Special Red",System.getProperty("user.dir")+"/resources/fight"+specialRed,fightScreenshotsPath+specialRed);
        this.add(pauseSS);
        this.add(specialRedSS);
        this.setVisible(true);
    }



    //TODO: simpler method of doing this, but for now let's hack (in the hatchet sense) our way through
//    private void addScreenshots(){
//        Iterator<String> it = this.screenshots.iterator();
//        while(it.hasNext()){
//            String nextScreenshot = it.next();
//            Screenshot newScreenshot = new Screenshot(this.father, )
//        }
//    }
}
