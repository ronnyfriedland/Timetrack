package de.ronnyfriedland.time.ui;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import de.ronnyfriedland.time.ui.dialog.NewEntryFrame;
import de.ronnyfriedland.time.ui.dialog.NewProjectFrame;

/**
 * @author ronnyfriedland
 */
public class TimeTableUI {

    private static final Logger LOG = Logger.getLogger(TimeTableUI.class.getName());

    public static void main(String[] args) {
        /* Use an appropriate Look and Feel */
        try {
            // UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        /* Turn off metal's use of bold fonts */
        UIManager.put("swing.boldMetal", Boolean.FALSE);
        // Schedule a job for the event-dispatching thread:
        // adding TrayIcon.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new TimeTableUI().createAndShowGUI();
            }
        });
    }

    private void createAndShowGUI() {
        // Check the SystemTray support
        if (!SystemTray.isSupported()) {
            LOG.severe("SystemTray not supported!");
        } else {
            final PopupMenu popup = new PopupMenu();
            final TrayIcon trayIcon = new TrayIcon(createImage("bulb.gif", "tray icon"));
            final SystemTray tray = SystemTray.getSystemTray();

            // Create a popup menu components
            MenuItem newProject = new MenuItem("Neues Projekt");
            MenuItem newItem = new MenuItem("Neuer Eintrag");
            MenuItem aboutItem = new MenuItem("About");
            MenuItem exitItem = new MenuItem("Exit");

            // Add components to popup menu
            popup.add(newProject);
            popup.add(newItem);
            popup.addSeparator();
            popup.add(aboutItem);
            popup.add(exitItem);

            trayIcon.setPopupMenu(popup);

            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                System.out.println("TrayIcon could not be added.");
                return;
            }

            trayIcon.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JOptionPane.showMessageDialog(null, "This dialog box is run from System Tray");
                }
            });

            newProject.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    new NewProjectFrame().setVisible(true);
                }
            });

            newItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    new NewEntryFrame().setVisible(true);
                }
            });

            aboutItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JOptionPane.showMessageDialog(null, "This dialog box is run from the About menu item");
                }
            });

            exitItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    tray.remove(trayIcon);
                    System.exit(0);
                }
            });
        }
    }

    private static Image createImage(String path, String description) {
        Image result = null;
        URL imageURL = Thread.currentThread().getContextClassLoader().getResource(path);

        if (imageURL == null) {
            LOG.severe(String.format("Resource %1$s not found!", path));
        } else {
            result = (new ImageIcon(imageURL, description)).getImage();
        }
        return result;
    }
}
