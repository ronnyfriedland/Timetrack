package de.ronnyfriedland.time.ui.util;

import javax.swing.*;

/**
 * Created by ronnyfriedland on 20.06.14.
 */
public final class TableUtils {

    /**
     * Creates a new instance of {@link TableUtils}
     */
    private TableUtils() {
        // nothing to do
    }

    /**
     * Return the values of the selected fields in table
     * @param table the source table
     * @return the selected values
     */
    public static String getSelectedValues(final JTable table) {
        StringBuilder sbuild = new StringBuilder();
        if (0 < table.getSelectedRowCount()) {
            int[] selectedRows = table.getSelectedRows();
            int[] selectedColumns = table.getSelectedColumns();

            for (int row : selectedRows) {
                for (int col : selectedColumns) {
                    sbuild.append(table.getValueAt(row, col)).append(" ");
                }
                sbuild.append("\n");
            }
        }
        return sbuild.toString();
    }

}
