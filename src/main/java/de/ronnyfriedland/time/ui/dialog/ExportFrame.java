package de.ronnyfriedland.time.ui.dialog;

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

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

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
 * @author ronnyfriedland
 */
public class ExportFrame extends AbstractFrame {
    private static final Logger LOG = Logger.getLogger(ExportFrame.class.getName());

    private static final long serialVersionUID = -8738367859388084898L;

    private static final String LABEL_SELECTED_DAYS_VALUE = "%1$d Tag(e)";
    private static final String[] TABLE_HEADERS = new String[] { Messages.DATE.getText(),
            Messages.DESCRIPTION.getText(), Messages.DURATION.getText() };

    private final JLabel labelDate = new JLabel(Messages.START_DATE.getText());
    private final DateChooserPanel dateChooser = new DateChooserPanel();
    private final JLabel labelDays = new JLabel(Messages.PERIOD_OF_TIME.getText());
    private final JSlider days = new JSlider(JSlider.HORIZONTAL, 1, 365, 7);
    private final JLabel labelSelectedDays = new JLabel("");
    private final DefaultTableModel tableModel = new DefaultTableModel(TABLE_HEADERS, 0) {
        private static final long serialVersionUID = 2177197508177608415L;

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        };
    };
    private final JScrollPane scrollPane = new JScrollPane(new JTable(tableModel));
    private final JButton preview = new JButton(Messages.PREVIEW.getText());
    private final JButton export = new JButton(Messages.EXPORT.getText());

    public ExportFrame() {
        super(Messages.NEW_EXPORT.getText(), 320, 440);
        createUI();
    }

    /**
     * (non-Javadoc)
     * 
     * @see de.ronnyfriedland.time.ui.dialog.AbstractFrame#createUI()
     */
    @Override
    protected void createUI() {
        // configure
        labelDate.setBounds(10, 10, 100, 24);

        dateChooser.setBounds(110, 10, 200, 150);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, 2);
        dateChooser.setDate(cal.getTime());
        dateChooser.addKeyListener(new TimeTableKeyAdapter());

        labelDays.setBounds(10, 170, 100, 24);

        days.setBounds(110, 170, 200, 24);
        days.setValue(7);
        days.addKeyListener(new TimeTableKeyAdapter());
        days.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                labelSelectedDays.setText(String.format(LABEL_SELECTED_DAYS_VALUE, source.getValue()));
            }
        });

        labelSelectedDays.setBounds(110, 200, 200, 24);
        labelSelectedDays.setText(String.format(LABEL_SELECTED_DAYS_VALUE, days.getValue()));

        export.setBounds(170, 225, 140, 24);
        export.addKeyListener(new TimeTableKeyAdapter());
        export.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                try {
                    Calendar from = getStartDate();
                    Calendar to = getEndDate();
                    Collection<Entry> entries = getFilteredData(to, from);

                    ExportController controller = new ExportController();
                    Workbook wb = controller.loadOrCreateWorkbook(
                            Configurator.CONFIG.getString(ConfiguratorKeys.PATH.getKey()),
                            Configurator.CONFIG.getString(ConfiguratorKeys.EXPORT_FILE.getKey()));
                    Sheet sheet = controller.loadOrCreateSheet(wb, SimpleDateFormat.getDateInstance(DateFormat.SHORT)
                            .format(from.getTime()), entries);
                    controller.addSheetToOverview(wb, sheet.getSheetName());
                    controller.persistWorkbook(wb, Configurator.CONFIG.getString(ConfiguratorKeys.PATH.getKey()),
                            Configurator.CONFIG.getString(ConfiguratorKeys.EXPORT_FILE.getKey()));

                    setVisible(false);
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, "Error exporting data", ex);
                    formatError(export);
                }

            }
        });

        preview.setBounds(10, 225, 140, 24);
        preview.addKeyListener(new TimeTableKeyAdapter());
        preview.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                tableModel.setDataVector(new Object[0][0], TABLE_HEADERS);

                Calendar from = getStartDate();
                Calendar to = getEndDate();
                Collection<Entry> entries = getFilteredData(from, to);

                for (Entry entry : entries) {
                    tableModel.addRow(new Object[] { entry.getDateString(), entry.getDescription(), entry.getDuration() });
                }
            }
        });

        scrollPane.setBounds(10, 260, 300, 150);

        formatOk(dateChooser, days);

        getContentPane().add(labelDate);
        getContentPane().add(dateChooser);
        getContentPane().add(labelDays);
        getContentPane().add(days);
        getContentPane().add(labelSelectedDays);
        getContentPane().add(scrollPane);
        getContentPane().add(preview);
        getContentPane().add(export);
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
    private Collection<Entry> getFilteredData(Calendar from, Calendar to) {
        Map<String, Object> params = new HashMap<String, Object>();

        params.put(Entry.PARAM_DATE_FROM, from.getTime());
        params.put(Entry.PARAM_DATE_TO, to.getTime());

        return EntityController.getInstance().findResultlistByParameter(Entry.class, Entry.QUERY_FIND_FROM_TO, params);
    }

}
