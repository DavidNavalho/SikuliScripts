package logic;

import java.io.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;

/**
 * Created by jinx on 31/1/16.
 */
public class PropertiesManager {

    public LinkedList<PropertyItem> properties;

    public Properties prop;

    //For now, the idea is simple: make sure that properties exist, and create missing ones
    //any older, existing properties should be kept!
    public PropertiesManager(){
        this.setProperties();
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
        File file = null;
        try{
            file = new File("config.properties");
            if(!file.exists())
                file.createNewFile();
            input = new FileInputStream(file);
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

    private void setProperties(){
        this.properties = new LinkedList<PropertyItem>();
        this.properties.add(new PropertyItem("firstArena","1"));
        this.properties.add(new PropertyItem("firstArenaPriority", "5"));
        this.properties.add(new PropertyItem("secondArena", "1"));
        this.properties.add(new PropertyItem("secondArenaPriority", "5"));
        this.properties.add(new PropertyItem("cornucopiaArena", "1"));
        this.properties.add(new PropertyItem("cornucopiaArenaPriority", "5"));
        this.properties.add(new PropertyItem("catalystClashAlphaArena", "0"));
        this.properties.add(new PropertyItem("catalystClashAlphaArenaPriority", "8"));
        this.properties.add(new PropertyItem("catalystClashBasicArena", "1"));
        this.properties.add(new PropertyItem("catalystClashBasicArenaPriority", "10"));
        this.properties.add(new PropertyItem("catalystClashClassArena", "0"));
        this.properties.add(new PropertyItem("catalystClashClassArenaPriority", "1"));
        this.properties.add(new PropertyItem("sleepMinimum", "15"));
        this.properties.add(new PropertyItem("sleepMaximum", "20"));
        this.properties.add(new PropertyItem("picsFolder", "iMac"));
        this.properties.add(new PropertyItem("app", "player"));
        this.properties.add(new PropertyItem("appWindow", "1"));
        this.properties.add(new PropertyItem("timeBetweenActions", "120"));
        this.properties.add(new PropertyItem("similarity", "0.90"));
        this.properties.add(new PropertyItem("autoWaitTimeout", "0"));
        this.properties.add(new PropertyItem("mouseSpeed", "0.2"));
        this.properties.add(new PropertyItem("mouseSpeedFight", "0.2"));
        this.properties.add(new PropertyItem("maxSearchForArena", "20"));
        this.properties.add(new PropertyItem("maxSearchForMilestones", "10"));
        this.properties.add(new PropertyItem("milestoneSleepTimer", "45"));
        this.properties.add(new PropertyItem("arenaDoneSleepTimer", "2"));
        this.properties.add(new PropertyItem("WindowsCommand","Taskkill -f -im player.exe"));
        this.properties.add(new PropertyItem("isWindowsMachine","true"));//TODO: if unexistent, make special case, to detect!
        this.properties.add(new PropertyItem("isOSXMachine","false"));//TODO: if unexistent, make special case, to detect!
        this.properties.add(new PropertyItem("rebootTimer","240")); //in minutes

        this.properties.add(new PropertyItem("OSX","killall player"));

        this.properties.add(new PropertyItem("rebootWaitForTakedownSeconds","10"));
        this.properties.add(new PropertyItem("genymotionApp","Genymotion"));//they probably need to change this one!
        this.properties.add(new PropertyItem("genymotionAppWindow","1"));//maybe this one too, dunno - probably good idea to add a test for them!
    }

    //basically, I'll add here a list of everything existing on the properties file...
    private void checkAndPopulateProperties(){
        Iterator<PropertyItem> it = this.properties.iterator();
        while(it.hasNext()){
            PropertyItem pi = it.next();
            this.addIfMissing(pi.key, pi.value);
        }
//        addIfMissing("firstArena","1");
//        addIfMissing("firstArenaPriority", "5");
//        addIfMissing("secondArena", "1");
//        addIfMissing("secondArenaPriority", "5");
//        addIfMissing("cornucopiaArena", "1");
//        addIfMissing("cornucopiaArenaPriority", "5");
//        addIfMissing("catalystClashAlphaArena", "0");
//        addIfMissing("catalystClashAlphaArenaPriority", "8");
//        addIfMissing("catalystClashBasicArena", "1");
//        addIfMissing("catalystClashBasicArenaPriority", "10");
//        addIfMissing("catalystClashClassArena", "0");
//        addIfMissing("catalystClashClassArenaPriority", "1");
//        addIfMissing("sleepMinimum", "15");
//        addIfMissing("sleepMaximum", "20");
//        addIfMissing("picsFolder", "iMac");
//        addIfMissing("app", "player");
//        addIfMissing("appWindow", "1");
//        addIfMissing("timeBetweenActions", "120");
//        addIfMissing("similarity", "0.90");
//        addIfMissing("autoWaitTimeout", "0");
//        addIfMissing("mouseSpeed", "0.2");
//        addIfMissing("mouseSpeedFight", "0.2");
//        addIfMissing("maxSearchForArena", "20");
//        addIfMissing("maxSearchForMilestones", "10");
//        addIfMissing("milestoneSleepTimer", "45");
//        addIfMissing("arenaDoneSleepTimer", "2");
    }

    private void addIfMissing(String key, String value){
        if(!this.prop.containsKey(key))
            this.prop.setProperty(key, value);
    }

    public class PropertyItem{
        public String key;
        public String value;

        public PropertyItem(String key, String value){
            this.key = key;
            this.value = value;
        }
    }
}
