package gui.Layout;

import gui.MCoCFrame;
import logic.PropertiesManager;

import javax.swing.*;
import java.awt.*;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by davidnavalho on 09/04/16.
 */
public class SettingsTab extends JPanel {

    protected MCoCFrame father;
    protected PropertiesManager pm;

    protected JPanel panel;
    protected JScrollPane pane;

    protected JButton save;

    public SettingsTab(MCoCFrame father, PropertiesManager pm){
        super();
        this.father = father;
        this.pm = pm;
        this.panel = new JPanel();
        this.panel.setLayout(new BoxLayout(this.panel,BoxLayout.Y_AXIS));
        this.pane = new JScrollPane(panel);
        this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        this.add(pane);
        this.setVisible(true);
//        this.panel.setVisible(true);
        this.listProperties();
    }

    protected LinkedList<PropertyItemUI> ll = new LinkedList<PropertyItemUI>();

    private void listProperties(){
        Iterator<PropertiesManager.PropertyItem> it = pm.properties.iterator();
        while(it.hasNext()){
            PropertiesManager.PropertyItem property = it.next();
            PropertyItemUI ui = new PropertyItemUI(property);
            this.panel.add(ui);
            this.ll.add(ui);
        }
    }

    protected class PropertyItemUI extends JPanel{
        public JLabel key;
        public JTextField value;

        public PropertyItemUI(PropertiesManager.PropertyItem pi){
            super();
            this.setLayout(new FlowLayout());
            this.key = new JLabel(pi.key);
            this.value = new JTextField(pi.value);
//            this.value.setSize(new Dimension(100,20));
            this.add(this.key);
            this.add(this.value);
            this.add(new JSeparator(SwingConstants.HORIZONTAL));
            this.add(new JSeparator(SwingConstants.HORIZONTAL));
            this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
//            this.setVisible(true);
        }
    }
}
