package de.ronnyfriedland.time.ui.adapter;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
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
		switch (e.getKeyCode()) {
		case KeyEvent.VK_ESCAPE:
			SwingUtilities.getRoot(e.getComponent()).setVisible(false);
			break;
		case KeyEvent.VK_C:
			if (e.isControlDown()) {
				if (e.getComponent() instanceof JTable) {
					copySelectedValue((JTable) e.getComponent());
				}
			}
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
		if (-1 < min && -1 < max) {
			for (int i = min; i <= max; i++) {
				sbuild.append(table.getValueAt(i, 1));
				sbuild.append(" (").append(table.getValueAt(i, 2)).append(") : ");
				sbuild.append(table.getValueAt(i, 3)).append(" h\n");
			}
		}
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(new StringSelection(sbuild.toString()), null);
	}

}
