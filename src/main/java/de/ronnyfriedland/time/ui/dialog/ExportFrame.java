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
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.jfree.ui.DateChooserPanel;

import de.ronnyfriedland.time.config.Configurator;
import de.ronnyfriedland.time.config.Configurator.ConfiguratorKeys;
import de.ronnyfriedland.time.config.Messages;
import de.ronnyfriedland.time.entity.Entry;
import de.ronnyfriedland.time.logic.EntityController;
import de.ronnyfriedland.time.logic.ImportExportController;

/**
 * @author ronnyfriedland
 */
public class ExportFrame extends AbstractFrame {
    private static final Logger LOG = Logger.getLogger(ExportFrame.class.getName());

    private static final long serialVersionUID = -8738367859388084898L;

    private final JLabel labelDate = new JLabel(Messages.START_DATE.getText());
    private final DateChooserPanel dateChooser = new DateChooserPanel();
    private final JLabel labelDays = new JLabel(Messages.PERIOD_OF_TIME.getText());
    private final JSlider days = new JSlider(JSlider.HORIZONTAL, 1, 365, 7);
    private final JLabel labelSelectedDays = new JLabel("");
    private final JButton export = new JButton(Messages.EXPORT.getText());

    public ExportFrame() {
        super(Messages.NEW_EXPORT.getText(), 320, 280);
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

        labelDays.setBounds(10, 170, 100, 24);

        days.setBounds(110, 170, 200, 24);
        days.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                int value = source.getValue();
                labelSelectedDays.setText(String.format("%1$d Tag(e)", value));
            }
        });

        labelSelectedDays.setBounds(110, 200, 200, 24);

        export.setBounds(10, 225, 300, 24);

        export.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                try {
                    Map<String, Object> params = new HashMap<String, Object>();

                    Calendar from = Calendar.getInstance();
                    from.setTime(dateChooser.getDate());
                    from.set(Calendar.HOUR_OF_DAY, 0);
                    from.set(Calendar.MINUTE, 0);
                    from.set(Calendar.SECOND, 0);
                    from.set(Calendar.MILLISECOND, 0);

                    Calendar to = Calendar.getInstance();
                    to.add(Calendar.DAY_OF_YEAR, days.getValue());
                    to.set(Calendar.HOUR_OF_DAY, 0);
                    to.set(Calendar.MINUTE, 0);
                    to.set(Calendar.SECOND, 0);
                    to.set(Calendar.MILLISECOND, 0);

                    params.put(Entry.PARAM_DATE_FROM, from.getTime());
                    params.put(Entry.PARAM_DATE_TO, to.getTime());

                    Collection<Entry> todayEntries = EntityController.getInstance().findResultlistByParameter(
                            Entry.class, Entry.QUERY_FIND_FROM_TO, params);

                    ImportExportController controller = new ImportExportController();
                    Workbook wb = controller.loadOrCreateWorkbook(
                            Configurator.CONFIG.getString(ConfiguratorKeys.PATH.getKey()),
                            Configurator.CONFIG.getString(ConfiguratorKeys.EXPORT_FILE.getKey()));
                    Sheet sheet = controller.loadOrCreateSheet(wb, SimpleDateFormat.getDateInstance(DateFormat.SHORT)
                            .format(from.getTime()), todayEntries);
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

        formatOk(dateChooser, days);

        getContentPane().add(labelDate);
        getContentPane().add(dateChooser);
        getContentPane().add(labelDays);
        getContentPane().add(days);
        getContentPane().add(labelSelectedDays);
        getContentPane().add(export);
    }
}