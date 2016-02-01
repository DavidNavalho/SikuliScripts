package logic;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by jinx on 31/1/16.
 */
public class PropertiesManager {

    public Properties prop;

    //For now, the idea is simple: make sure that properties exist, and create missing ones
    //any older, existing properties should be kept!
    public PropertiesManager(){
        this.loadProperties();
        this.checkAndPopulateProperties();
        this.saveProperties();
    }

    private void saveProperties(){
        FileOutputStream fos = null;
        try{
            fos = new FileOutputStream("config.properties");
            this.prop.store(fos,"GrimsBot properties file v0.8");
        }catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadProperties(){
        this.prop = new Properties();
        InputStream input = null;
        try{
            input = new FileInputStream("config.properties");
            prop.load(input);
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


    //basically, I'll add here a list of everything existing on the properties file...
    private void checkAndPopulateProperties(){
        addIfMissing("firstArena","1");
        addIfMissing("firstArenaPriority", "5");
        addIfMissing("secondArena", "1");
        addIfMissing("secondArenaPriority", "5");
        addIfMissing("cornucopiaArena", "1");
        addIfMissing("cornucopiaArenaPriority", "5");
        addIfMissing("catalystClashAlphaArena", "0");
        addIfMissing("catalystClashAlphaArenaPriority", "8");
        addIfMissing("catalystClashBasicArena", "1");
        addIfMissing("catalystClashBasicArenaPriority", "10");
        addIfMissing("catalystClashClassArena", "0");
        addIfMissing("catalystClashClassArenaPriority", "1");
        addIfMissing("sleepMinimum", "15");
        addIfMissing("sleepMaximum", "20");
        addIfMissing("picsFolder", "iMac");
        addIfMissing("app", "player");
        addIfMissing("appWindow", "1");
        addIfMissing("timeBetweenActions", "120");
        addIfMissing("similarity", "0.90");
        addIfMissing("autoWaitTimeout", "0");
        addIfMissing("mouseSpeed", "0.2");
        addIfMissing("mouseSpeedFight", "0.2");
        addIfMissing("maxSearchForArena", "20");
        addIfMissing("maxSearchForMilestones", "10");
        addIfMissing("milestoneSleepTimer", "45");
        addIfMissing("arenaDoneSleepTimer", "2");
    }

    private void addIfMissing(String key, String value){
        if(!this.prop.containsKey(key))
            this.prop.setProperty(key, value);
    }
}
