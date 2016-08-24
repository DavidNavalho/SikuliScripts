package logic;

import main.MCoC;
import org.sikuli.script.App;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created by davidnavalho on 09/04/16.
 */
public class RebootHandler extends Thread{

    protected Properties props;
    protected MCoC bot;

    public RebootHandler(Properties props, MCoC bot){
        this.props = props;
        this.bot = bot;
    }

    private void executeCommand(String order){
        System.out.println("Executing command: "+order);
        try {
            String line;
            Process p = Runtime.getRuntime().exec(order);
            BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            while ((line = bri.readLine()) != null) {
                System.out.println(line);
            }
            bri.close();
            while ((line = bre.readLine()) != null) {
                System.out.println(line);
            }
            bre.close();
            p.waitFor();
            System.out.println("BOOM!");
        } catch (Exception e) {
            e.printStackTrace();
            bot.pauseEnabled = true;
        }
    }

    private boolean takePlayerDown(){
        String command = "";
        //detect machine
        if(this.props.getProperty("isWindowsMachine").equalsIgnoreCase("true")){
            command = this.props.getProperty("WindowsCommand");
        }else if(this.props.getProperty("isWindowsMachine").equalsIgnoreCase("true")){
            command = this.props.getProperty("OSX");
        }
        if(command!=""){
            this.executeCommand(command);
            return true;
        }else
            return false;
    }

    protected void startVM(){
        App app = new App(this.props.getProperty("genymotionApp"));
//        int appWindow = new Integer(this.props.getProperty("genymotionAppWindow"));
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
        app.focus(chosenWindow);
        Utils.sleep(3);
        int X1 = app.window(chosenWindow).getX();
        int Y1 = app.window(chosenWindow).getY();
        int X2 = app.window(chosenWindow).getW() + X1;
        int Y2 = app.window(chosenWindow).getH() + Y1;

        Screen s = new Screen();
        Region r = s.setRect(X1, Y1, X2-X1, Y2-Y1);
        if(Utils.isAvailable(r, "GenyGenyPlayButton", 0)!=null)
            Utils.clickIfAvailable(r, "GenyPlayButton");
        else if(Utils.isAvailable(r, "GenyPlayButtonHighlighted", 0)!=null)
            Utils.clickIfAvailable(r, "GenyPlayButtonHighlighted");
        Utils.sleep(45);//sleep for a bit, make sure Emulator is being launched (it takes a while anyways)
        System.out.println("Waited long enough, run wild!");
    }

    protected void handleReboot(){
        //detect player, and shut it down!
        this.takePlayerDown();
        //wait for a bit...
        Utils.sleep(new Integer(this.props.getProperty("rebootWaitForTakedownSeconds")));
        System.out.println("Slept long enough, booting VM!");
        //detect Genymotion window, focus on it, and click the Play button
        this.startVM();
        //start the bot!! Make sure that the bot constantly recheks for the window size, and looks for the icon!
        System.out.println("Waiting 3 minutes for VM to be launched....");
        Utils.sleep(60*3);
        System.out.println("Snooze done!");
    }

    public void run() {
        System.out.println("Reboot timer started...tick tock tick tock");
        Integer timerInMinutes = new Integer(this.props.getProperty("rebootTimer"));
//        while(true){
//            Utils.sleep(timerInMinutes*60);//it's in seconds, so multiply the minutes by 60...
//            bot.pauseEnabled = true;
//            System.out.println("I Woke up! Time for a Big Bang!\r\n(╯°□°）╯︵ ┻━┻ ");
//            this.handleReboot();
//            bot.setScreen(true);
//            bot.pauseEnabled = false;
//        }

    }

}
