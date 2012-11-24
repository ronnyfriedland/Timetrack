package de.ronnyfriedland.time.logic.jobs;

import java.awt.TrayIcon;

import org.quartz.Job;

/**
 * Abstrakte Job Klasse
 * 
 * @author Ronny Friedland
 */
public abstract class AbstractJob implements Job {
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
     * @param trayIcon
     *            tray icon
     */
    public void setTrayIcon(TrayIcon trayIcon) {
        this.trayIcon = trayIcon;
    }

}
