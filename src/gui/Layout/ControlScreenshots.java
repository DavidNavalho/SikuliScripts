package gui.Layout;

import gui.MCoCFrame;

import javax.swing.*;
import java.awt.*;

/**
 * Created by davidnavalho on 21/01/16.
 */
public class ControlScreenshots extends JPanel {

    private MCoCFrame father;

    public ControlScreenshots(MCoCFrame father){
        super();
        this.father = father;
        this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        this.add(new Label("Empty"));
        this.add(new Label("Empty"));
        this.add(new Label("Empty"));

        this.setVisible(true);
    }

}
