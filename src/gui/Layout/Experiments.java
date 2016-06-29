package gui.Layout;

import gui.MCoCFrame;
import org.sikuli.script.App;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created by davidnavalho on 18/02/16.
 */
public class Experiments extends JPanel {

    protected JPanel experimentsPanel;
    protected MCoCFrame father;
    protected Properties props;

    protected JTextField textField;
    protected JButton runCommandButton;
    protected JButton pauseBot;
    protected JButton resumeBot;

    protected JButton detectGenyApp;
    protected JButton detectEmulator;

    public Experiments(MCoCFrame father, Properties props){
        super();
        this.father = father;
        this.props = props;
        this.experimentsPanel = new JPanel();
        this.add(experimentsPanel);
        this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        this.addExperiments();
        this.setVisible(true);
    }

    protected void detections(){
        this.detectGenyApp = new JButton("Detect Genymotion");
        this.detectEmulator = new JButton("Detect Emulator");
        this.detectGenyApp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                App app = new App(props.getProperty("genymotionApp"));
                int chosenWindow = 0;
                int windowArea = 0;
                System.out.println("Testing windows....");
                for(int i=0;i<10;i++){
                    Region r = app.window(i);
                    if(r!=null) {
                        int newArea = app.window(i).getH()*app.window(i).getW();
                        System.out.println("Window "+i+" is available, with an area of: "+newArea);
                        if(newArea>windowArea){
                            chosenWindow = i;
                            windowArea = newArea;
                        }
                    }
                }
                app.focus(chosenWindow);//2);
                int X1 = app.window(chosenWindow).getX();
                int Y1 = app.window(chosenWindow).getY();
                int X2 = app.window(chosenWindow).getW() + X1;
                int Y2 = app.window(chosenWindow).getH() + Y1;
                Screen s = new Screen();
                Region r = s.setRect(X1, Y1, X2-X1, Y2-Y1);
                r.highlight(3);
            }
        });
        this.detectEmulator.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                App app = new App(props.getProperty("app"));
                int chosenWindow = 0;
                int windowArea = 0;
                System.out.println("Testing windows....");
                for(int i=0;i<10;i++){
                    Region r = app.window(i);
                    if(r!=null) {
                        int newArea = app.window(i).getH()*app.window(i).getW();
                        System.out.println("Window "+i+" is available, with an area of: "+newArea);
                        if(newArea>windowArea){
                            chosenWindow = i;
                            windowArea = newArea;
                        }
                    }
                }
                app.focus(chosenWindow);//2);
                int X1 = app.window(chosenWindow).getX();
                int Y1 = app.window(chosenWindow).getY();
                int X2 = app.window(chosenWindow).getW() + X1;
                int Y2 = app.window(chosenWindow).getH() + Y1;
                Screen s = new Screen();
                Region r = s.setRect(X1, Y1, X2-X1, Y2-Y1);
                r.highlight(3);
            }
        });
    }

    protected void addExperiments(){
        this.textField = new JTextField("Write your command here");
        this.textField.setMinimumSize(textField.getMinimumSize());
        this.runCommandButton = new JButton("Run Command");
        this.pauseBot = new JButton("Pause bot");
        this.resumeBot = new JButton("Resume bot");
        this.detections();
        this.pauseBot.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                father.bot.pauseEnabled = true;
                father.topPanel.playPanel.runBot = false;
            }
        });
        this.resumeBot.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                father.bot.pauseEnabled = false;
                father.topPanel.playPanel.runBot = true;
            }
        });

        this.runCommandButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Windows: taskkill /F /IM player.exe
                //System.getProperty("os.name") -> contains "Windows"
                System.out.println("I WAS PRESSED! OMG! Here's what I have to say about that:\r\n > "+textField.getText());
                try {
                    String line;
                    Process p = Runtime.getRuntime().exec(textField.getText());
                    BufferedReader bri = new BufferedReader
                            (new InputStreamReader(p.getInputStream()));
                    BufferedReader bre = new BufferedReader
                            (new InputStreamReader(p.getErrorStream()));
                    while ((line = bri.readLine()) != null) {
                        System.out.println(line);
                    }
                    bri.close();
                    while ((line = bre.readLine()) != null) {
                        System.out.println(line);
                    }
                    bre.close();
                    p.waitFor();
                    System.out.println("Done.");

                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        });

        this.experimentsPanel.add(textField);
        this.experimentsPanel.add(runCommandButton);
        this.experimentsPanel.add(detectEmulator);
        this.experimentsPanel.add(detectGenyApp);
        this.experimentsPanel.add(pauseBot);
        this.experimentsPanel.add(resumeBot);
        this.experimentsPanel.setLayout(new FlowLayout());
        this.experimentsPanel.setVisible(true);

//        this.experimentsPanel.add(new JButton("test"));
        //Clickable Button;
        //String before button, explaining what it does?
        //text box to identify app to close??
        //        this.test = new JButton("Test");
//        this.test.setEnabled(true);
//        this.test.addActionListener(new ActionListener() {
//
//            public void actionPerformed(ActionEvent e) {
////                Utils.highlightRegion();
//            }
//        });

    }

//    public void addNewScreenshotItem(SSItem item){
//        this.ssSetup.addSSItem(item);
//    }
}
