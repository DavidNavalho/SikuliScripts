package gui.Layout.Panels;

import gui.MCoCFrame;
import main.MCoC;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * Created by davidnavalho on 21/01/16.
 */
public class PlayPanel extends JPanel{

    private JButton playButton;
    private JButton pauseButton;
    public boolean runBot = false;
//    public boolean running = false;
//    private JFrame mainFrame;
    protected MCoCFrame father;

    public PlayPanel(MCoCFrame mcoc){
        super();
//        this.mainFrame = mainFrame;
        this.father = mcoc;
        this.playButton = new JButton("Start/Resume Bot");
        this.pauseButton = new JButton("Pause Bot");
        this.add(playButton);
        this.add(pauseButton);
        this.playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runBot = true;
                father.bot.pauseEnabled = false;
            }
        });
        this.pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runBot = false;
                father.bot.pauseEnabled = true;
            }
        });


    }
}
