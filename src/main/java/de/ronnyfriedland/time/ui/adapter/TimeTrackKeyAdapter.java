package de.ronnyfriedland.time.ui.adapter;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import de.ronnyfriedland.time.ui.dialog.ShowHelpFrame;
import de.ronnyfriedland.time.ui.util.TableUtils;

/**
 * {@link KeyAdapter} Implementierung für anwendungsspezifische Shortcuts.
 * 
 * @author Ronny Friedland
 */
public class TimeTrackKeyAdapter extends KeyAdapter {
    /**
     * {@inheritDoc}
     * 
     * @see java.awt.event.KeyAdapter#keyPressed(java.awt.event.KeyEvent)
     */
    @Override
    public void keyPressed(final KeyEvent e) {
        switch (e.getKeyCode()) {
        case KeyEvent.VK_ESCAPE:
            SwingUtilities.getRoot(e.getComponent()).setVisible(false);
            break;
        case KeyEvent.VK_ENTER:
            Component comp = e.getComponent();
            if (comp instanceof JButton) {
                ((JButton) comp).doClick();
            }
            break;
        case KeyEvent.VK_C:
            if (e.isControlDown() && (e.getComponent() instanceof JTable)) {
                String selection = TableUtils.getSelectedValues((JTable) e.getComponent());
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                if (null != clipboard) {
                    clipboard.setContents(new StringSelection(selection), null);
                }
            }
            break;
        case KeyEvent.VK_H:
        case KeyEvent.VK_F1:
            if (KeyEvent.VK_H == e.getKeyCode() && e.isControlDown() || KeyEvent.VK_F1 == e.getKeyCode()) {
                new ShowHelpFrame().setVisible(true);
            }
            break;
        default:
            // not implemented yet
            break;
        }
    }
}
