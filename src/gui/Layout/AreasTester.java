package gui.Layout;

import logic.PropertiesManager;
import logic.Utils;
import org.sikuli.script.App;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;

import javax.swing.*;
import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by jinx on 1/2/16.
 */
public class AreasTester extends JPanel {

    protected Properties props;
    protected int X1, X2, Y1, Y2;
    protected Region s;

    public AreasTester(Properties props){
        super();
        this.props = props;
//        this.setLayout(new BoxLayout());
    }

    private Region setScreen(){
        App app = new App(Utils.getStringProperty(this.props, "app"));//"player");
        int appWindow = Utils.getIntProperty(this.props, "appWindow");
        app.focus(appWindow);//2);
//        System.out.println("appwindow: "+appWindow);
//        Utils.highlightRegion(app.window(0));
//        System.out.println("> Coordinates: "+app.window(2).getX()+","+app.window(2).getY());
//        System.out.println("> ...to: "+app.window(2).getW()+","+app.window(2).getH());
        X1 = app.window(appWindow).getX();
        Y1 = app.window(appWindow).getY();
        X2 = app.window(appWindow).getW()+X1;
        Y2 = app.window(appWindow).getH()+Y1;
        this.s = new Screen();
        return s.setRect(X1, Y1, X2-X1, Y2-Y1);
    }

}
