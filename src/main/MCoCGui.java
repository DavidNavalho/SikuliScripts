package main;

import gui.MCoCFrame;
import logic.RebootHandler;

import javax.swing.*;
import java.util.Properties;

/**
 * Created by davidnavalho on 21/01/16.
 */
//TODO: I really should have some logging mechanism, instead of system outs...
public class MCoCGui extends Thread{

    private MCoCFrame layout;
    public RebootHandler rebootHandler;

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
        System.out.println("1");
        this.layout.setBot(new MCoC());
        System.out.println("2");
        this.layout.bot.pauseEnabled = true;
        System.out.println("3");
        this.layout.bot.start();
        System.out.println("4");
        this.rebootHandler = new RebootHandler(this.layout.props, this.layout.bot);
        System.out.println("5");
        this.rebootHandler.start();
        System.out.println("6");
    }

    public void run() {
        while (true) {
            if(this.layout.topPanel.playPanel.runBot) {
                System.out.println("Running!");
                this.layout.topPanel.playPanel.runBot = false;
                this.layout.bot.pauseEnabled = false;

//                MCoC battler = new MCoC();
//                battler.start();
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

    //TODO list:
    //- feature to forcefully shut down the VM player, and restart it, requires:
    //  - Identifying OS (Windows, other) and running the correct command to shut it down
    //  - the commands can be part of the properties file
    //  - Setting up a timer to run it (also a variable on the properties file)
    //  - identifying the Genymotion Window, so the VM can be relaunched from there
    //  - After the VM is relaunched, identify that it's not in portrait mode, and should wait for the MCoC icon
    //  - press the MCoC icon
    //  - identify that it changed to portrait mode, and should resume the bot's regular activities
    //- Make it so the bot can be paused, instead of stopped.
    //  - basically, make it so the bot is a thread, and that I can kill it, while 'keeping the bot itself alive'
    //  - Bonus: make a 'pause' button instead of the current full force stop
    //- Add a new tab to setup the bot's properties nicely, completely escaping the need to manually edit them!
    //- Arena Milestones detection
    //- Better handling of tabs, regarding screenshots setup, so that it becomes clearer where the images should be found
    //  - Bonus: Color-system, to properly identify that all the required images have been taken
    //  - Bonus: Version-based images, so that when something changes, the system can *deprecate* the older ones, making sure a new and correct one is taken
    //  - Bonus: Pass along the color-coded system of required/extra images to the play button, to make it clear when the bot is ready to run
    //- Image-based (lower row) solution for Arena priority-handling, to make it clearer how it works, and also easier to setup
    //- Run Hard-mode Catalyst maps??
    //- Notifications to mobile device?
    //  - Remote control through mobile device! :D
}
