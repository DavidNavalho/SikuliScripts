package tasks;

import org.sikuli.basics.HotkeyEvent;
import org.sikuli.basics.HotkeyListener;
import org.sikuli.basics.HotkeyManager;
import org.sikuli.script.Key;
import org.sikuli.script.KeyModifier;

/**
 * Created by jinx on 8/12/15.
 */
public class Control extends Thread{

    public boolean stop = false;

    public Control(){
    }

    public void run(){//Control.start()
        String key = "s";
        int modifiers = KeyModifier.CTRL;
        HotkeyListener a_CTRL = new HotkeyListener() {
            @Override
            public void hotkeyPressed(HotkeyEvent e) {
                // here goes your code for the case hotKeyPressed
                System.out.println("(S)top requested!");
                stop = true;
            }
        };
        HotkeyManager.getInstance().addHotkey(key,modifiers,a_CTRL);
        while(true){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();//Do nothing, just making sure it doesn't spin - does it? spin?
            }
            if(this.stop)
                System.exit(0);
        }
    }
}
