package de.ronnyfriedland.time.ui.dialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.jfree.ui.DateChooserPanel;

import de.ronnyfriedland.time.config.Configurator;
import de.ronnyfriedland.time.config.Configurator.ConfiguratorKeys;
import de.ronnyfriedland.time.config.Messages;
import de.ronnyfriedland.time.entity.Entry;
import de.ronnyfriedland.time.logic.EntityController;
import de.ronnyfriedland.time.logic.ExportController;
import de.ronnyfriedland.time.ui.adapter.TimeTableKeyAdapter;

/**
 * @author Ronny Friedland
 */
public class ExportFrame extends AbstractFrame {
    private static final Logger LOG = Logger.getLogger(ExportFrame.class.getName());

    private static final long serialVersionUID = -8738367859388084898L;

    private static final String LABEL_SELECTED_DAYS_VALUE = "%1$d Tag(e)";
    private static final String LABEL_SELECTED_EXPORT_DATA = "%1$.2f Stunden für %2$d Tage ausgewählt.";

    private static final String[] TABLE_HEADERS = new String[] { Messages.DATE.getMessage(),
            Messages.DESCRIPTION.getMessage(), Messages.PROJECT_NAME.getMessage(), Messages.DURATION.getMessage() };

    private final JLabel labelDate = new JLabel(Messages.START_DATE.getMessage());
    private final DateChooserPanel dateChooser = new DateChooserPanel();
    private final JLabel labelDays = new JLabel(Messages.PERIOD_OF_TIME.getMessage());
    private final JSlider days = new JSlider(JSlider.HORIZONTAL, 1, 365, 7);
    private final JLabel labelDelete = new JLabel(Messages.EXPORT_DELETE.getMessage());
    private final JRadioButton deleteYes = new JRadioButton(Messages.YES.getMessage());
    private final JRadioButton deleteNo = new JRadioButton(Messages.NO.getMessage());
    private final JLabel labelSelectedDays = new JLabel("");
    private final JButton preview = new JButton(Messages.PREVIEW.getMessage());
    private final JButton export = new JButton(Messages.EXPORT.getMessage());
    private final JLabel summary = new JLabel();
    private final JTextArea description = new JTextArea();
    private final DefaultTableModel tableModel = new DefaultTableModel(TABLE_HEADERS, 0) {
        private static final long serialVersionUID = 2177197508177608415L;

        @Override
        public boolean isCellEditable(final int row, final int column) {
            return false;
        };
    };
    private final TableCellRenderer tableCellRenderer = new TableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(final JTable table, final Object value,
                final boolean isSelected, final boolean hasFocus, final int row, final int column) {
            JTextField component = new JTextField();
            if ((row % 2) == 0) {
                component.setBackground(Color.WHITE);
            } else {
                component.setBackground(Color.LIGHT_GRAY);
            }
            if (isSelected) {
                component.setBackground(Color.GRAY);
                component.setForeground(Color.WHITE);
            } else {
                component.setForeground(Color.BLACK);
            }
            component.setText(value.toString());
            component.setToolTipText(value.toString());
            component.setBorder(BorderFactory.createEmptyBorder());
            return component;
        }
    };
    private final JTable table = new JTable();
    {
        table.setModel(tableModel);
        table.setDefaultRenderer(Object.class, tableCellRenderer);
        table.addKeyListener(new TimeTableKeyAdapter());
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.setTransferHandler(null); // remove default TransferHandler
    }
    private final JScrollPane scrollPane = new JScrollPane(table);

    public ExportFrame() {
        super(Messages.NEW_EXPORT.getMessage(), 500, 510);
        createUI();
    }

    /**
     * (non-Javadoc)
     * 
     * @see de.ronnyfriedland.time.ui.dialog.AbstractFrame#createUI()
     */
    @Override
    protected void createUI() {
        description.setBounds(10, 10, 485, 115);
        description.setEditable(false);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        description.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        description.setText(Messages.EXPORT_DESCRIPTION.getMessage());

        // configure
        labelDate.setBounds(10, 130, 100, 24);

        dateChooser.setBounds(110, 130, 200, 150);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, 2);
        dateChooser.setDate(cal.getTime());
        dateChooser.setChosenDateButtonColor(Color.LIGHT_GRAY);
        dateChooser.setYearSelectionRange(2);
        dateChooser.setBorder(BorderFactory.createEmptyBorder());
        dateChooser.addKeyListener(new TimeTableKeyAdapter());

        labelDays.setBounds(340, 130, 150, 24);

        days.setBounds(340, 155, 150, 30);
        days.setValue(7);
        days.addKeyListener(new TimeTableKeyAdapter());
        days.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                labelSelectedDays.setText(String.format(LABEL_SELECTED_DAYS_VALUE, source.getValue()));
            }
        });

        labelSelectedDays.setBounds(340, 190, 200, 24);
        labelSelectedDays.setText(String.format(LABEL_SELECTED_DAYS_VALUE, days.getValue()));

        labelDelete.setBounds(340, 230, 150, 24);

        deleteYes.setBounds(340, 250, 60, 24);
        deleteNo.setBounds(400, 250, 60, 24);
        deleteNo.setSelected(true);

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(deleteYes);
        buttonGroup.add(deleteNo);

        export.setBounds(265, 285, 230, 24);
        export.addKeyListener(new TimeTableKeyAdapter());
        export.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                try {
                    Calendar from = getStartDate();
                    Calendar to = getEndDate();
                    Collection<Entry> entries = getFilteredData(from, to);

                    if (0 < entries.size()) {
                        ExportController controller = new ExportController();
                        Workbook wb = controller.loadOrCreateWorkbook(
                                Configurator.CONFIG.getString(ConfiguratorKeys.PATH.getKey()),
                                Configurator.CONFIG.getString(ConfiguratorKeys.EXPORT_FILE.getKey()));
                        Sheet sheet = controller.loadOrCreateSheet(wb,
                                SimpleDateFormat.getDateInstance(DateFormat.SHORT).format(from.getTime()), entries);
                        controller.addSheetToOverview(wb, sheet.getSheetName());
                        controller.persistWorkbook(wb, Configurator.CONFIG.getString(ConfiguratorKeys.PATH.getKey()),
                                Configurator.CONFIG.getString(ConfiguratorKeys.EXPORT_FILE.getKey()));

                        if (deleteYes.isSelected()) {
                            for (Entry entry : entries) {
                                EntityController.getInstance().delete(entry);
                            }
                        }

                        setVisible(false);

                        JOptionPane.showMessageDialog(null, Messages.EXPORT_SUCCESSFUL.getMessage(Configurator.CONFIG
                                .getString(ConfiguratorKeys.PATH.getKey())));
                    }
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, "Error exporting data", ex);
                    formatError(export);
                }

            }
        });

        preview.setBounds(10, 285, 230, 24);
        preview.addKeyListener(new TimeTableKeyAdapter());
        preview.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                tableModel.setDataVector(new Object[0][0], TABLE_HEADERS);

                Calendar from = getStartDate();
                Calendar to = getEndDate();
                Collection<Entry> entries = getFilteredData(from, to);

                float hours = 0;
                for (Entry entry : entries) {
                    Float duration = Float.valueOf(entry.getDuration());
                    hours += duration;
                    tableModel.addRow(new Object[] { entry.getDateString(), entry.getDescription(),
                            entry.getProject().getName(), String.format("%1$.2f", duration) });
                }

                summary.setText(String.format(LABEL_SELECTED_EXPORT_DATA, hours, days.getValue()));
            }
        });

        scrollPane.setBounds(10, 315, 485, 150);

        summary.setBounds(10, 465, 480, 20);

        formatOk(dateChooser, days);

        getContentPane().add(labelDate);
        getContentPane().add(dateChooser);
        getContentPane().add(labelDays);
        getContentPane().add(days);
        getContentPane().add(labelSelectedDays);
        getContentPane().add(labelDelete);
        getContentPane().add(deleteYes);
        getContentPane().add(deleteNo);
        getContentPane().add(scrollPane);
        getContentPane().add(preview);
        getContentPane().add(export);
        getContentPane().add(summary);
        getContentPane().add(description);
    }

    /**
     * Liefert das Anfangsdatum für den Export.
     * 
     * @return Calendar
     */
    private Calendar getStartDate() {
        Calendar from = Calendar.getInstance();
        from.setTime(dateChooser.getDate());
        from.set(Calendar.HOUR_OF_DAY, 0);
        from.set(Calendar.MINUTE, 0);
        from.set(Calendar.SECOND, 0);
        from.set(Calendar.MILLISECOND, 0);
        return from;
    }

    /**
     * Liefert das Enddatum für den Export.
     * 
     * @return Calendar
     */
    private Calendar getEndDate() {
        Calendar to = Calendar.getInstance();
        to.setTime(dateChooser.getDate());
        to.add(Calendar.DAY_OF_YEAR, days.getValue());
        to.set(Calendar.HOUR_OF_DAY, 0);
        to.set(Calendar.MINUTE, 0);
        to.set(Calendar.SECOND, 0);
        to.set(Calendar.MILLISECOND, 0);
        return to;
    }

    /**
     * Liefert die gespeicherten Daten anhand des Filters
     * 
     * @return Liste der Einträge
     */
    private Collection<Entry> getFilteredData(final Calendar from, final Calendar to) {
        Map<String, Object> params = new HashMap<String, Object>();

        params.put(Entry.PARAM_DATE_FROM, from.getTime());
        params.put(Entry.PARAM_DATE_TO, to.getTime());

        return EntityController.getInstance().findResultlistByParameter(Entry.class, Entry.QUERY_FIND_FROM_TO, params);
    }

}
