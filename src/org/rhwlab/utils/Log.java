package org.rhwlab.utils;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.GregorianCalendar;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
/*
 * Created on May 11, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
import javax.swing.WindowConstants;

/**
 * @author biowolp
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Log extends JPanel implements ActionListener {
    
    public JTextArea iText;
    public JScrollPane iPane;
    public String iTitle;
    protected JFrame iFrame;
    private JFileChooser iFC;
    protected JToolBar iToolBar;
    protected JMenuBar     iMenuBar;
    //protected JMenu         iMenu;
    
    public Log(String title) {
        iTitle = title;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        iToolBar = new JToolBar("");
        iToolBar.setLayout(new GridLayout(1,0));
        JButton jb1 = new JButton("Save as");
        jb1.addActionListener(this);
        iToolBar.add(jb1);
        add(iToolBar);
        iText = new JTextArea(4,50);
        iText.setMargin(new Insets(5,5,5,5));
        iText.setEditable(false);
        iText.setFont(new Font("Monospaced", Font.PLAIN, 12));
        iPane = new JScrollPane(iText);

        iPane.setMaximumSize(new Dimension(Integer.MAX_VALUE,Integer.MAX_VALUE));
        //JCheckBox jcb = new JCheckBox("always");
        //addToolBarButton(jcb);
        add(iPane);
        iFrame = new JFrame(iTitle);
        iFrame.setPreferredSize(new Dimension(600, 300));

        //iMenu = new JMenu("File");
        //iFrame.getContentPane().add(iMenu);
        iFrame.setJMenuBar(createMenuBar());
        iFC = new JFileChooser(".");
    }
    
    protected void addToolBarButton(AbstractButton ab) {
        ab.addActionListener(this);
        iToolBar.add(ab);
    }
    
    protected void addComboBox(JComboBox jcb) {
        jcb.addActionListener(this);
        iToolBar.add(jcb);
    }
    
    public void append(String s) {
        iText.append(s + NL);
        iText.setCaretPosition( iText.getDocument().getLength() );
    }
    
    public void appendx(String s) {
        iText.append(s);
    }
    
    public String getText() {
        return iText.getText();
    }

    public void read(File file) {
        try {
            iText.read(new FileReader(file), null);
        } catch(IOException ioe) {
            append("read unsuccessful for file: " + file.toString());
            append(ioe.toString());
        }
    }
    
    public String getTime() {
        return SP + new GregorianCalendar().getTime().toString();
    }
    
    public void showMe() {
        //iFrame = new JFrame(iLog.iTitle);
        iFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        //UITest newContentPane = new UITest();
        //newContentPane.setOpaque(true); //content panes must be opaque
        iFrame.setContentPane(this);
        //iFrame.setContentPane(iPane);
        iFrame.pack();
        iFrame.setVisible(true);
    }
    
    public void hideMe() {
        iFrame.setVisible(false);
    }
    
    public JFrame getFrame() {
        return iFrame;
    }
    
    protected JMenuBar createMenuBar() {
        iMenuBar = new JMenuBar();
        //iMenu = new JMenu("File");
        //iMenuBar.add(iMenu);
        //JMenuItem test = new JMenuItem(SAVEAS);
        //menu.add(test);
        //test.addActionListener(this);
        return iMenuBar;
    }
    
    @Override
	@SuppressWarnings("unused")
	public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();
        //System.out.println("Log.actionPerformed: " + s);
        if (s.equals(SAVEAS)) {
            int returnVal = iFC.showSaveDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                String dir = iFC.getCurrentDirectory().toString();
                String name = iFC.getName(iFC.getSelectedFile());
                //append(dir);
                //append(name);
                
                try {
                    PrintStream ps = new PrintStream(new FileOutputStream(iFC.getSelectedFile()));
                    ps.print(iText.getText());
                    ps.flush();
                    ps.close();
                
                } catch(IOException ioe) {
                    ioe.printStackTrace();
                }
            
            }    
        }
    }
    
    public void setLogFont(Font font) {
        iText.setFont(font);
        iText.setColumns(70);
    }
    
    private static final String
         NL = "\n"
        ,SP = " "
        ,SAVEAS = "Save as"
        ;
    
    public static void main(String[] args) {
    }
}
