package gui.Layout.Panels;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * Created by davidnavalho on 21/01/16.
 */
public class PlayPanel extends JPanel{

    private JButton playButton;
    public boolean runBot = false;
//    private JFrame mainFrame;

    public PlayPanel(){
        super();
//        this.mainFrame = mainFrame;
        this.playButton = new JButton("Start Bot");
        this.add(playButton);
        this.playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runBot = true;
            }
        });
    }
}
