package gui.Layout;

import gui.MCoCFrame;
import gui.resources.SSItem;

import javax.swing.*;
import java.awt.*;

/**
 * Created by davidnavalho on 22/01/16.
 */
public class ScrollableScreenshotsSetup extends JPanel {

    protected ScreenshotsSetup ssSetup;

    public ScrollableScreenshotsSetup(MCoCFrame father, String ssPath){
        super();
        this.ssSetup = new ScreenshotsSetup(father, ssPath);
        JScrollPane pane = new JScrollPane(ssSetup);
        this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        this.add(pane);
        this.setVisible(true);
    }

    public void addNewScreenshotItem(SSItem item){
        this.ssSetup.addSSItem(item);
    }
}
