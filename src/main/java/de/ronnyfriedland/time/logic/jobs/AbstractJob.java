package de.ronnyfriedland.time.logic.jobs;

import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;

import javax.swing.JOptionPane;

import org.quartz.Job;

/**
 * Abstrakte Job Klasse
 * 
 * @author Ronny Friedland
 */
public abstract class AbstractJob implements Job {

    /**
     * Gibt an, ob der Job beim Shutdown explizit getriggert werden soll.
     * 
     * @return {@link Boolean#TRUE} falls ja, ansonsten {@link Boolean#FALSE}
     */
    public static boolean runOnShutdown() {
        return false;
    }

    /**
     * The tray icon
     */
    private TrayIcon trayIcon;

    /**
     * Get tray icon
     * 
     * @return tray icon
     */
    public TrayIcon getTrayIcon() {
        return trayIcon;
    }

    /**
     * Set the tray icon
     * 
     * @param trayIcon tray icon
     */
    public void setTrayIcon(final TrayIcon trayIcon) {
        this.trayIcon = trayIcon;
    }

    /**
     * Darstellung des Popups
     * 
     * @param showPopup Flag, ob Hinweis als Popup dargestellt werden soll.
     * @param messageText Der darzustellende Text
     */
    protected void showPopup(final boolean showPopup, final String messageText) {
        if (showPopup || null == getTrayIcon()) {
            JOptionPane.showMessageDialog(null, messageText);
        } else {
            getTrayIcon().displayMessage(null, messageText, MessageType.INFO);
        }
    }

}
