package de.ronnyfriedland.time.ui.adapter;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.SwingUtilities;

/**
 * @author ronnyfriedland
 */
public class TimeTableKeyAdapter extends KeyAdapter {
    /**
     * (non-Javadoc)
     * 
     * @see java.awt.event.KeyAdapter#keyPressed(java.awt.event.KeyEvent)
     */
    @Override
    public void keyPressed(KeyEvent e) {
        System.err.println(SwingUtilities.getRootPane(e.getComponent()));
        if (KeyEvent.VK_ESCAPE == e.getKeyCode()) {
            SwingUtilities.getRoot(e.getComponent()).setVisible(false);
        }
    }
}
