package gui.Layout.Panels;

import gui.MCoCFrame;
import logic.Utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.*;
import java.util.Collections;
import java.util.Iterator;

/**
 * Created by davidnavalho on 21/01/16.
 */
public class Screenshot extends JPanel implements ActionListener{

    private MCoCFrame father;
    private String ssLocation;
    private JButton validity;
    private JFileChooser fc;
    private JLabel ownLabel;
    private JButton test;

    public Screenshot(final MCoCFrame father, String description, String exampleLocation, String screenshotLocation){
        super();
        this.father = father;
        this.ssLocation = screenshotLocation;
        this.setLayout(new FlowLayout());

        InputStream in;

//        String className = this.getClass().getName().replace('.', '/');
//        String classJar = this.getClass().getResource("/" + className + ".class").toString();
//        if (classJar.startsWith("jar:")) {
//            System.out.println("*** running from jar: "+exampleLocation);
//            in = getClass().getResourceAsStream("/images/control/accept.png");
//        }else
        in = this.getClass().getResourceAsStream(exampleLocation);

        BufferedImage bi = null;
        try{
            bi = ImageIO.read(in);
        }catch(IOException e){
            e.printStackTrace();
        }
        ImageIcon icon = new ImageIcon(bi);




//        ImageIcon icon = new ImageIcon(this.getClass().getResource(exampleLocation));
//        ImageIcon icon = new ImageIcon(this.getClass().getResource(exampleLocation));
        JLabel label = new JLabel(icon);
//        ImageIcon userIcon = new ImageIcon(System.getProperty("user.dir")+"/resources/other/noImage.png");
//        Image img = userIcon.getImage();
//        Image newImg = img.getScaledInstance(40, 40,  java.awt.Image.SCALE_SMOOTH);
//        userIcon = new ImageIcon(newImg);

        this.validity = new JButton("-X-");
        this.validity.setEnabled(false);

        //TODO: handle detection of the images, to see if it's identifying it correctly... (MVC would have been great for this....)
//        this.test = new JButton("Test");
//        this.test.setEnabled(true);
//        this.test.addActionListener(new ActionListener() {
//
//            public void actionPerformed(ActionEvent e) {
////                Utils.highlightRegion();
//            }
//        });

        JButton getSSButton = new JButton("Add screenshot...");
        getSSButton.addActionListener(this);

        this.fc = new JFileChooser();
        this.fc.setMultiSelectionEnabled(false);
        this.fc.setDragEnabled(true);
        this.fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

        validity.setBackground(Color.red);

        JLabel descript = new JLabel(description);
        descript.setAlignmentX(Component.RIGHT_ALIGNMENT);
        getSSButton.setAlignmentX(Component.RIGHT_ALIGNMENT);

        ImageIcon userIcon = this.getUserIcon();
//        Image img = userIcon.getImage();
//        BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
//        Graphics g = bi.createGraphics();
//        g.drawImage(img, 0, 0, icon.getIconWidth(), icon.getIconHeight(), null);
//        bi.flush();
//        ImageIcon newIcon = new ImageIcon(bi);
//        ownLabel = new JLabel(newIcon);
        ownLabel = new JLabel(userIcon);
//        ownLabel.setPreferredSize(label.getPreferredSize());
        this.add(descript);
        this.add(label);
        this.add(ownLabel);
        this.add(getSSButton);
        this.add(validity);

        this.screenshotExists();
    }

    private ImageIcon getUserIcon(){
        ImageIcon userIcon;
        if(screenshotExists()){
            userIcon = new ImageIcon(this.ssLocation);
        }else{
            userIcon = new ImageIcon(this.getClass().getResource("/images/other/noImage.png"));//System.getProperty("user.dir")+"/resources/images/other/noImage.png");
            Image img = userIcon.getImage();
            Image newImg = img.getScaledInstance(40, 40,  java.awt.Image.SCALE_SMOOTH);
            userIcon = new ImageIcon(newImg);
        }
        return userIcon;
    }

    private void updateUserIcon(){
        if(screenshotExists()){
            this.ownLabel.setIcon(new ImageIcon(this.ssLocation));
        }
    }

    private boolean screenshotExists(){
        if((new File(this.ssLocation)).exists()) {
            this.validity.setBackground(Color.green);
            this.validity.setText("-V-");
            return true;
        }
        return false;
    }

    public void actionPerformed(ActionEvent e){
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            if(file.isFile()) {
                if(file.getName().contains(".png")) {  //it's not completely correct, but it'll work
//                    File newLocation = new File(this.ssLocation);
                    try {
                        System.out.println("Writing to file");
                        Path source = file.toPath();
                        Path target = new File(this.ssLocation).toPath();
                        Files.createDirectories(target.getParent());
                        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
                        this.screenshotExists();
                        this.updateUserIcon();
                        this.ownLabel.repaint();
                        this.repaint();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
            //This is where a real application would open the file.
//            System.out.println("Opening: " + file.getName() + ".");
        } else {
//            System.out.println("Open command cancelled by user.");
        }
    }

}
