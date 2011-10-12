package de.ronnyfriedland.time.ui.dialog;

import java.awt.Color;
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
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.jfree.ui.DateChooserPanel;

import de.ronnyfriedland.time.config.Configurator;
import de.ronnyfriedland.time.config.Configurator.ConfiguratorKeys;
import de.ronnyfriedland.time.entity.Entry;
import de.ronnyfriedland.time.logic.EntityController;
import de.ronnyfriedland.time.logic.ImportExportController;

/**
 * @author ronnyfriedland
 */
public class ExportFrame extends JFrame {
    private static final Logger LOG = Logger.getLogger(ExportFrame.class.getName());

    private static final long serialVersionUID = -8738367859388084898L;

    public ExportFrame() {
        createUI();
    }

    private void createUI() {
        setLayout(null);
        setBounds(0, 0, 280, 280);
        setTitle("Bericht erstellen");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        // init
        final JLabel labelDate = new JLabel("Startdatum: ");
        final DateChooserPanel dateChooser = new DateChooserPanel();
        final JLabel labelDays = new JLabel("Zeitspanne: ");
        final JSlider days = new JSlider(JSlider.HORIZONTAL, 1, 365, 7);
        final JLabel labelSelectedDays = new JLabel("1 Tag(e)");
        final JButton export = new JButton("Daten exportieren");

        // configure
        labelDate.setBounds(10, 10, 100, 24);

        dateChooser.setBounds(110, 10, 150, 150);
        dateChooser.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        labelDays.setBounds(10, 170, 100, 24);

        days.setBounds(110, 170, 150, 24);
        days.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        days.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                int value = source.getValue();
                labelSelectedDays.setText(String.format("%1$d Tag(e)", value));
            }
        });

        labelSelectedDays.setBounds(110, 200, 150, 24);

        export.setBounds(10, 225, 250, 24);

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
                    to.add(Calendar.DAY_OF_MONTH, days.getValue());
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
                            Configurator.CONFIG.getString(ConfiguratorKeys.EXPORT_PATH.getKey()),
                            Configurator.CONFIG.getString(ConfiguratorKeys.EXPORT_FILE.getKey()));
                    Sheet sheet = controller.loadOrCreateSheet(wb, SimpleDateFormat.getDateInstance(DateFormat.SHORT)
                            .format(from.getTime()), todayEntries);
                    controller.addSheetToOverview(wb, sheet.getSheetName());
                    controller.persistWorkbook(wb,
                            Configurator.CONFIG.getString(ConfiguratorKeys.EXPORT_PATH.getKey()),
                            Configurator.CONFIG.getString(ConfiguratorKeys.EXPORT_FILE.getKey()));

                    setVisible(false);
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, "Error exporting data", ex);
                }

            }
        });

        getContentPane().add(labelDate);
        getContentPane().add(dateChooser);
        getContentPane().add(labelDays);
        getContentPane().add(days);
        getContentPane().add(labelSelectedDays);
        getContentPane().add(export);
    }
}