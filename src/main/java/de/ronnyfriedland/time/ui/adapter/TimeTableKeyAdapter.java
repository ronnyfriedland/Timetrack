package de.ronnyfriedland.time.ui.adapter;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import de.ronnyfriedland.time.ui.dialog.ShowHelpFrame;

/**
 * {@link KeyAdapter} Implementierung für anwendungsspezifische Shortcuts.
 * 
 * @author Ronny Friedland
 */
public class TimeTableKeyAdapter extends KeyAdapter {
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
        case KeyEvent.VK_C:
            if (e.isControlDown() && (e.getComponent() instanceof JTable)) {
                copySelectedValue((JTable) e.getComponent());
            }
            break;
        case KeyEvent.VK_H:
            if (e.isControlDown()) {
                new ShowHelpFrame().setVisible(true);
            }
            break;
        case KeyEvent.VK_F1:
            new ShowHelpFrame().setVisible(true);
            break;
        default:
            // not implemented yet
            break;
        }
    }

    /**
     * Kopiert die ausgewählten Daten in die Zwischenablage
     */
    private void copySelectedValue(final JTable table) {
        ListSelectionModel selectedData = table.getSelectionModel();
        int min = selectedData.getMinSelectionIndex();
        int max = selectedData.getMaxSelectionIndex();

        StringBuilder sbuild = new StringBuilder();
        if ((-1 < min) && (-1 < max)) {
            for (int i = min; i <= max; i++) {
                if (selectedData.isSelectedIndex(i)) {
                    sbuild.append(table.getValueAt(i, 2));
                    sbuild.append(" (").append(table.getValueAt(i, 3)).append(") : ");
                    sbuild.append(table.getValueAt(i, 4)).append(" h\n");
                }
            }
        }
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        if (null != clipboard) {
            clipboard.setContents(new StringSelection(sbuild.toString()), null);
        }
    }

}
