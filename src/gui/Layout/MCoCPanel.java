package gui.Layout;

import gui.Layout.Panels.PlayPanel;
import gui.MCoCFrame;

import javax.swing.*;
import java.awt.*;

/**
 * Created by davidnavalho on 21/01/16.
 */
public class MCoCPanel extends JPanel {

    public PlayPanel playPanel;
    private MCoCFrame father;

    //TODO: make sure it closes everything...
    public MCoCPanel(MCoCFrame father){
        super();
        this.father = father;
        this.setLayout(new FlowLayout());
        this.playPanel = new PlayPanel();
        Label label = new Label("Ctrl + S to STOP");
//        JPanel panel = new JPanel();
//        panel.add(label)
        this.add(label);
        this.add(playPanel);
//        this.pack();
        this.setVisible(true);
//        this.setTitle("Grim's Bot Configuration utility");
//        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        this.setExtendedState(Frame.NORMAL);
    }

}
