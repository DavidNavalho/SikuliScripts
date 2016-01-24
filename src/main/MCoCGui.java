package main;

import gui.MCoCFrame;

import javax.swing.*;
import java.util.Properties;

/**
 * Created by davidnavalho on 21/01/16.
 */
//TODO: I really should have some logging mechanism, instead of system outs...
public class MCoCGui extends Thread{

    private MCoCFrame layout;


    public MCoCGui(){
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (Exception ex) {
            System.out.println("Failed loading Look&Feel: ");
            System.out.println(ex);
        }

        this.layout = new MCoCFrame();
    }

    public void run() {
        while (true) {
            if(this.layout.topPanel.playPanel.runBot) {
                this.layout.topPanel.playPanel.runBot = false;
                MCoC battler = new MCoC();
                battler.start();
            }
            try {
                Thread.sleep(200);
            }catch(InterruptedException e) {
                //TODO: log it instead of outputting crap
            }
        }
    }

    public static void main(String[] args) {
        new MCoCGui().start();
    }
}
