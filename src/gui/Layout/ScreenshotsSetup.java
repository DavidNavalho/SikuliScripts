package gui.Layout;

import gui.Layout.Panels.Screenshot;
import gui.MCoCFrame;
import gui.resources.SSItem;
import logic.Utils;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Created by davidnavalho on 22/01/16.
 */
public class ScreenshotsSetup extends JPanel {

    private MCoCFrame father;
    protected String screenshotsPath;
    protected String ssPath;

    public ScreenshotsSetup(MCoCFrame father, String ssPath){
        super();

        File folders = new File(System.getProperty("user.dir"), "imgs/"+Utils.getStringProperty(father.props,"picsFolder")+"/"+ssPath);
        folders.mkdirs();


        this.father = father;
        String local = Utils.getStringProperty(this.father.props, "picsFolder");
        this.ssPath = "/"+ssPath;
        this.screenshotsPath = new File(System.getProperty("user.dir"), "imgs"+"/"+local+this.ssPath).getAbsolutePath();
        this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
//        this.setLayout(new GridLayout(0,1));
//        this.add(Box.);
//        this.setLayout(new FlowLayout());

//        this.add(new JLabel(ssPath.toUpperCase()));

//        String pause = "/pause.png";
//        String specialRed = "/specialRed.png";

//        this.screenshots = new LinkedList<String>();
//        this.screenshots.add("/pause.png");
//        this.screenshots.add("/specialRed.png");

//        Screenshot pauseSS = new Screenshot(father, "Pause",System.getProperty("user.dir")+"/resources/fight"+pause,screenshotsPath+pause);
//        Screenshot specialRedSS = new Screenshot(father, "Special Red",System.getProperty("user.dir")+"/resources/fight"+specialRed,screenshotsPath+specialRed);
//
//        this.add(pauseSS);
//        this.add(specialRedSS);
        this.setVisible(true);
    }

    public void addSSItem(SSItem item){
        String fileName = "/"+item.fileName;
        String description = item.description;
        Screenshot ss = new Screenshot(this.father, description, System.getProperty("user.dir")+"/resources/"+this.ssPath+fileName,screenshotsPath+fileName);
//        ss.setAlignmentX(Component.RIGHT_ALIGNMENT);
//        ss.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.add(ss);
    }

}
