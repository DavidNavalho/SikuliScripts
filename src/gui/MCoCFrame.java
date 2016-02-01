package gui;

import gui.Layout.*;
import gui.resources.SSItem;

import javax.swing.*;
import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by davidnavalho on 21/01/16.
 */
public class MCoCFrame extends JFrame {

    public MCoCPanel topPanel;
    public ScrollableScreenshotsSetup ssControl;
    public Properties props;
    public JTabbedPane centerPanel;
    protected ScrollableScreenshotsSetup ssFight;

    public MCoCFrame(){
        super();
        this.loadProperties();
        this.setLayout(new BorderLayout(10,10));
//        this.playPanel = new PlayPanel();
//        Label label = new Label("Ctrl + S to STOP");
//        JPanel panel = new JPanel();
//        panel.add(label)
//        this.add(label);
//        this.add(playPanel);
        this.topPanel = new MCoCPanel(this);
//        this.centerPanel = new ScrollableScreenshotsSetup(this,"fight");
        this.ssControl = new ScrollableScreenshotsSetup(this, "control");
        this.ssFight = new ScrollableScreenshotsSetup(this, "fight");
        this.centerPanel = new JTabbedPane();
        this.centerPanel.addTab("Control".toUpperCase(), ssControl);
        this.centerPanel.addTab("Fight".toUpperCase(), ssFight);
//        this.ssControl.setMinimumSize(new Dimension(200,600));
        this.setMinimumSize(new Dimension(800,600));
        this.add(topPanel, BorderLayout.PAGE_START);
//        this.add(ssControl, BorderLayout.LINE_START);
        this.add(centerPanel, BorderLayout.CENTER);
        this.pack();
//        this.setSize(800,400);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setExtendedState(Frame.NORMAL);
        this.addScreenshotItems();

        this.setVisible(true);
        this.setTitle("Grim's Bot Configuration utility");

        this.revalidate();
    }

    private void loadProperties(){
        this.props = new Properties();
        InputStream input = null;
        try{
            input = new FileInputStream("config.properties");
            this.props.load(input);
        }catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void addScreenshotItems(){
        //Control Screenshots
        this.ssControl.addNewScreenshotItem(new SSItem("crystalCornucopia.png", "Crystal Cornucopia Arena"));
        this.ssControl.addNewScreenshotItem(new SSItem("catalystClashAlphaArena.png", "Alpha Catalyst Arena"));
        this.ssControl.addNewScreenshotItem(new SSItem("catalystClashBasicArena.png", "T4 BASIC Catalyst Arena"));
        this.ssControl.addNewScreenshotItem(new SSItem("catalystClashClassArena.png", "T4 CLASS Catalyst Arena"));
        this.ssControl.addNewScreenshotItem(new SSItem("accept.png", "Accept"));
        this.ssControl.addNewScreenshotItem(new SSItem("arenaInfo.png", "Arena Info"));
        this.ssControl.addNewScreenshotItem(new SSItem("backButton.png", "Back Button"));
        this.ssControl.addNewScreenshotItem(new SSItem("continue.png", "Continue Button"));
        this.ssControl.addNewScreenshotItem(new SSItem("cross.png", "Close Window"));
        this.ssControl.addNewScreenshotItem(new SSItem("findMatch.png", "Find Match Button"));
        this.ssControl.addNewScreenshotItem(new SSItem("info.png", "Info Button"));
        this.ssControl.addNewScreenshotItem(new SSItem("later.png", "Later Button"));
        this.ssControl.addNewScreenshotItem(new SSItem("mainMenuFight.png", "Main Menu Fight Button"));
        this.ssControl.addNewScreenshotItem(new SSItem("multiverseArenas.png", "Multiverse Arenas"));
        this.ssControl.addNewScreenshotItem(new SSItem("playVersus.png", "Play Versus"));
        this.ssControl.addNewScreenshotItem(new SSItem("rankRewards.png", "Rank Rewards"));
        this.ssControl.addNewScreenshotItem(new SSItem("recharging.png", "Recharging Icon"));
        this.ssControl.addNewScreenshotItem(new SSItem("reconnect.png", "Reconnect Button"));
        this.ssControl.addNewScreenshotItem(new SSItem("regenBox.png", "Request Recharge Icon"));
        this.ssControl.addNewScreenshotItem(new SSItem("stats.png", "Stats"));
        this.ssControl.addNewScreenshotItem(new SSItem("thirdAvailableSpot.png", "Empty Champ Box"));
//        this.ssControl.addNewScreenshotItem(new SSItem("", ""));
//        this.ssControl.addNewScreenshotItem(new SSItem("", ""));
//        this.ssControl.addNewScreenshotItem(new SSItem("", ""));
//        this.ssControl.addNewScreenshotItem(new SSItem("", ""));

        //Maybe these should be on a third panel??
        this.ssControl.addNewScreenshotItem(new SSItem("noRewards.png", "Arena No Rewards"));

        //Fight Screenshots
        this.ssFight.addNewScreenshotItem(new SSItem("pause.png", "Pause"));
        this.ssFight.addNewScreenshotItem(new SSItem("specialRed.png","Red Power Bar"));
    }

}
