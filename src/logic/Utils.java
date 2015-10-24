package logic;

import org.sikuli.basics.Settings;
import org.sikuli.script.*;

import java.io.File;

public class Utils {

    public static void setImagesPath(String path){
        String pathImgDefault = new File(System.getProperty("user.dir"), "imgs"+path).getAbsolutePath();
        ImagePath.setBundlePath(pathImgDefault);
        System.out.println("Image paths: " + ImagePath.getPaths());
    }

    public static void highlightRegion(Region r){
        r.highlight(2);
    }

    public static Match isAvailable(Region r, String imagePath, int waitTime){
        try {
            return r.wait(imagePath, waitTime);
        }catch(FindFailed e){
            System.out.println("Searched for "+imagePath+", did not find it.");
            return null;
        }
    }

//    public static void clickExact(Region r, String image) throws FindFailed{
//        r.click(new Pattern(image).exact());
//    }
//
//    public static Match findExact(Region r, String imagePath){
//        try{
//
//            return r.find(new Pattern(imagePath).exact());
//        }catch(FindFailed e){
//            System.out.println("Searched for exact "+imagePath+", did not find it.");
//            return null;
//        }
//    }

    public static Match find(Region r, String imagePath){
        try{
            return r.find(imagePath);
        }catch(FindFailed e){
            System.out.println("Searched for "+imagePath+", did not find it.");
            return null;
        }
    }

    //TODO: better method of putting back default similarity
    public static Match findWithSimilarity(Region r, String imagePath, double similarity, double previousSimilarity){
        Settings.MinSimilarity = similarity;
        try{
            Match m = r.find(imagePath);
            Settings.MinSimilarity = previousSimilarity;
            return m;
        }catch(FindFailed e){
            System.out.println("Searched for "+imagePath+", did not find it.");
            Settings.MinSimilarity = previousSimilarity;
            return null;
        }
    }

    public static void sleep(int waitTime){
        try {
            Thread.sleep(waitTime*1000);
        } catch (InterruptedException e) {
            //do nothing
        }
    }

    public static void clickIfAvailable(Region r, String image){
        try {
            r.click(image);
//            Utils.click(r, image);
        }catch(FindFailed ff){
            System.out.println(image+" not available, skipping.");
        }
    }

    public static void click(Region r){
        r.click();
    }

    public static void clickLastMatch(Region r) throws FindFailed{
        r.click(r.getLastMatch());
    }

    public static void searchAndHighlight(Region r, String image){
        try{
            r.find(image).highlight();
        }catch(FindFailed ff){
            System.out.println("Failed to find/highlight "+image+".");
        }
    }
}
