package gui.Layout.Panels;

import gui.MCoCFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Created by davidnavalho on 21/01/16.
 */
public class Screenshot extends JPanel implements ActionListener{

    private MCoCFrame father;
    private String ssLocation;
    private JButton validity;
    private JFileChooser fc;

    public Screenshot(MCoCFrame father, String description, String exampleLocation, String screenshotLocation){
        super();
        this.father = father;
        this.ssLocation = screenshotLocation;
        this.setLayout(new FlowLayout());

        ImageIcon icon = new ImageIcon(exampleLocation);
        JLabel label = new JLabel(icon);
        this.validity = new JButton("-X-");
        this.validity.setEnabled(false);

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
        this.add(descript);
        this.add(label);
        this.add(validity);
        this.add(getSSButton);

        this.screenshotExists();
    }

    private void screenshotExists(){
        if((new File(this.ssLocation)).exists()) {
            this.validity.setBackground(Color.green);
            this.validity.setText("-V-");
        }

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
                        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
                        this.screenshotExists();
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
