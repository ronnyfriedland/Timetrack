package de.ronnyfriedland.time.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.jfree.ui.DateChooserPanel;

import de.ronnyfriedland.time.config.Configurator;
import de.ronnyfriedland.time.config.Configurator.ConfiguratorKeys;
import de.ronnyfriedland.time.config.Const;
import de.ronnyfriedland.time.config.Messages;
import de.ronnyfriedland.time.entity.Entry;
import de.ronnyfriedland.time.entity.Project;
import de.ronnyfriedland.time.entity.Protocol;
import de.ronnyfriedland.time.logic.EntityController;
import de.ronnyfriedland.time.logic.ExportController;
import de.ronnyfriedland.time.logic.ProtocolController;
import de.ronnyfriedland.time.sort.SortParam;
import de.ronnyfriedland.time.sort.SortParam.SortOrder;
import de.ronnyfriedland.time.ui.adapter.TimeTrackKeyAdapter;
import de.ronnyfriedland.time.ui.util.TableUtils;

/**
 * @author Ronny Friedland
 */
public class ExportFrame extends AbstractFrame {
    private static final Logger LOG = Logger.getLogger(ExportFrame.class.getName());

    private static final long serialVersionUID = -8738367859388084898L;

    private static final String[] TABLE_HEADERS = new String[] { null, Messages.DATE.getMessage(),
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
    private final JLabel labelProjects = new JLabel(Messages.PROJECT_FILER.getMessage());
    private final JComboBox<String> projects = new JComboBox<String>();
    private final JPopupMenu popup = new JPopupMenu();

    private final DefaultTableModel tableModel = new DefaultTableModel(TABLE_HEADERS, 0) {
        private static final long serialVersionUID = 1L;

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
            if (row % 2 == 0) {
                component.setBackground(Color.WHITE);
            } else {
                component.setBackground(new Color(225, 225, 225));
            }
            if (isSelected) {
                component.setBackground(Const.COLOR_SELECTION);
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
    private final TableColumnModel columnModel = new DefaultTableColumnModel() {
        private static final long serialVersionUID = 1L;

        public boolean isColumnVisible(final int column) {
            return 0 < column;
        }

        @Override
        public TableColumn getColumn(final int columnIndex) {
            TableColumn col = super.getColumn(columnIndex);
            if (!isColumnVisible(columnIndex)) { // hide uuid
                col.setWidth(0);
                col.setMinWidth(0);
                col.setMaxWidth(0);
            }
            if (1 == columnIndex || 4 == columnIndex) { // date and duration
                col.setWidth(200);
                col.setMinWidth(200);
                col.setMaxWidth(200);
            }
            return col;
        }
    };
    private final TableRowSorter<DefaultTableModel> rowSorter = new TableRowSorter<DefaultTableModel>(tableModel) {
        {
            setRowFilter(null);
        }
    };
    private final JTable table = new JTable();
    private final JScrollPane scrollPane = new JScrollPane(table);
    private final JPanel filterPane = new JPanel();
    private final JPanel summaryPane = new JPanel();

    /**
     * Erzeugt eine neue {@link ExportFrame} Instanz.
     */
    public ExportFrame() {
        super(Messages.NEW_EXPORT.getMessage(), 640, 510, true);
        getContentPane().setBackground(Const.COLOR_BACKGROUND);
        createUI();
        loadProjectListData();
    }

    /**
     * {@inheritDoc}
     *
     * @see de.ronnyfriedland.time.ui.dialog.AbstractFrame#createUI()
     */
    @Override
    protected void createUI() {
        getContentPane().addKeyListener(new TimeTrackKeyAdapter());

        JMenuItem itemCopy = new JMenuItem(Messages.COPY.getMessage());
        itemCopy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                String selection = TableUtils.getSelectedValues(table);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                if (null != clipboard) {
                    clipboard.setContents(new StringSelection(selection), null);
                }
            }
        });
        popup.add(itemCopy);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, 2);

        ButtonGroup buttonGroup = new ButtonGroup();
        getContentPane().setLayout(new BorderLayout(0, 0));
        filterPane.setBackground(Const.COLOR_BACKGROUND);
        getContentPane().add(filterPane, BorderLayout.NORTH);
        filterPane.setBounds(0, 0, 600, 200);
        projects.setBackground(Const.COLOR_BACKGROUND);
        projects.setFont(new Font(projects.getFont().getName(), Font.PLAIN, projects.getFont().getSize()));
        days.setBackground(Const.COLOR_BACKGROUND);
        days.setValue(7);
        days.addKeyListener(new TimeTrackKeyAdapter());
        days.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                labelSelectedDays.setText(Messages.EXPORT_DURATION.getMessage(source.getValue(),
                        2 > source.getValue() ? "" : "e"));
            }
        });
        labelSelectedDays.setText(Messages.EXPORT_DURATION.getMessage(days.getValue(), 2 > days.getValue() ? "" : "e"));
        dateChooser.setBackground(Const.COLOR_BACKGROUND);
        dateChooser.setDate(cal.getTime());
        dateChooser.setChosenDateButtonColor(Const.COLOR_SELECTION);
        dateChooser.setYearSelectionRange(2);
        dateChooser.setBorder(BorderFactory.createEmptyBorder());
        dateChooser.addKeyListener(new TimeTrackKeyAdapter());

        formatOk(dateChooser, days);
        deleteYes.setBackground(Const.COLOR_BACKGROUND);
        deleteYes.addKeyListener(new TimeTrackKeyAdapter());
        buttonGroup.add(deleteYes);
        deleteNo.setBackground(Const.COLOR_BACKGROUND);
        deleteNo.setSelected(true);
        deleteNo.addKeyListener(new TimeTrackKeyAdapter());
        buttonGroup.add(deleteNo);
        preview.addKeyListener(new TimeTrackKeyAdapter());
        preview.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                tableModel.setDataVector(new Object[0][0], TABLE_HEADERS);

                Calendar from = getStartDate();
                Calendar to = getEndDate();
                String project = (String) projects.getSelectedItem();
                Collection<Entry> entries = getFilteredData(project, from, to);

                Map<String, Float> groupedSummary = new HashMap<String, Float>();
                List<Object[]> entryModelData = new ArrayList<Object[]>();
                float hours = 0;
                for (Entry entry : entries) {
                    Float duration = Float.valueOf(entry.getDuration());
                    hours += duration;
                    Float value = groupedSummary.get(entry.getProject().getName());
                    if (null == value) {
                        value = 0f;
                    }
                    groupedSummary.put(entry.getProject().getName(), duration + value);
                    entryModelData.add(new Object[] { entry.getUuid(), entry.getDateString(), entry.getDescription(),
                            entry.getProject().getName(), String.format("%1$.2f h", duration) });
                }
                for (Object[] entryData : entryModelData) {
                    String projectName = (String) entryData[3];
                    entryData[3] = String.format("%1$s (%3$s: %2$.2f h)", projectName, groupedSummary.get(projectName),
                            Messages.DURATION_PROJECT.getMessage());
                    tableModel.addRow(entryData);
                }
                summary.setText(Messages.EXPORT_SUMMARY.getMessage(hours, days.getValue()));
            }
        });
        export.addKeyListener(new TimeTrackKeyAdapter());
        export.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                try {
                    Calendar from = getStartDate();
                    Calendar to = getEndDate();
                    String project = (String) projects.getSelectedItem();
                    Collection<Entry> entries = getFilteredData(project, from, to);

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

                        ProtocolController.getInstance().writeProtocol(new Protocol(Protocol.ProtocolValue.EXPORT));

                        JOptionPane.showMessageDialog(
                                null,
                                Messages.EXPORT_SUCCESSFUL.getMessage(System.getProperty("user.dir")
                                        + System.getProperty("file.separator")
                                        + Configurator.CONFIG.getString(ConfiguratorKeys.PATH.getKey())));
                    }
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, "Error exporting data", ex);
                    formatError(export);
                }

            }
        });

        GroupLayout gl_filterPane = new GroupLayout(filterPane);
        gl_filterPane
        .setHorizontalGroup(gl_filterPane
                .createParallelGroup(Alignment.LEADING)
                .addGroup(
                        gl_filterPane
                        .createSequentialGroup()
                        .addGap(33)
                        .addGroup(
                                gl_filterPane
                                .createParallelGroup(Alignment.LEADING)
                                .addComponent(labelDate)
                                .addGroup(
                                        gl_filterPane
                                        .createParallelGroup(Alignment.LEADING, false)
                                        .addGroup(
                                                gl_filterPane
                                                .createSequentialGroup()
                                                .addComponent(
                                                        dateChooser,
                                                        GroupLayout.PREFERRED_SIZE,
                                                        185,
                                                        GroupLayout.PREFERRED_SIZE)
                                                        .addGap(32)
                                                        .addGroup(
                                                                gl_filterPane
                                                                .createParallelGroup(
                                                                        Alignment.LEADING)
                                                                        .addComponent(
                                                                                preview,
                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                Short.MAX_VALUE)
                                                                                .addComponent(
                                                                                        export,
                                                                                        Alignment.TRAILING,
                                                                                        GroupLayout.DEFAULT_SIZE,
                                                                                        331,
                                                                                        Short.MAX_VALUE)
                                                                                        .addGroup(
                                                                                                gl_filterPane
                                                                                                .createSequentialGroup()
                                                                                                .addGroup(
                                                                                                        gl_filterPane
                                                                                                        .createParallelGroup(
                                                                                                                Alignment.LEADING)
                                                                                                                .addComponent(
                                                                                                                        labelDays,
                                                                                                                        GroupLayout.PREFERRED_SIZE,
                                                                                                                        110,
                                                                                                                        GroupLayout.PREFERRED_SIZE)
                                                                                                                        .addComponent(
                                                                                                                                labelSelectedDays,
                                                                                                                                GroupLayout.PREFERRED_SIZE,
                                                                                                                                129,
                                                                                                                                GroupLayout.PREFERRED_SIZE)
                                                                                                                                .addComponent(
                                                                                                                                        days,
                                                                                                                                        GroupLayout.PREFERRED_SIZE,
                                                                                                                                        156,
                                                                                                                                        GroupLayout.PREFERRED_SIZE))
                                                                                                                                        .addGap(31)
                                                                                                                                        .addGroup(
                                                                                                                                                gl_filterPane
                                                                                                                                                .createParallelGroup(
                                                                                                                                                        Alignment.LEADING)
                                                                                                                                                        .addComponent(
                                                                                                                                                                labelDelete)
                                                                                                                                                                .addComponent(
                                                                                                                                                                        deleteNo)
                                                                                                                                                                        .addComponent(
                                                                                                                                                                                deleteYes,
                                                                                                                                                                                GroupLayout.PREFERRED_SIZE,
                                                                                                                                                                                129,
                                                                                                                                                                                GroupLayout.PREFERRED_SIZE))
                                                                                                                                                                                .addGap(9))))
                                                                                                                                                                                .addGroup(
                                                                                                                                                                                        gl_filterPane
                                                                                                                                                                                        .createSequentialGroup()
                                                                                                                                                                                        .addComponent(labelProjects)
                                                                                                                                                                                        .addGap(18)
                                                                                                                                                                                        .addComponent(
                                                                                                                                                                                                projects,
                                                                                                                                                                                                0,
                                                                                                                                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                                                                                                                                Short.MAX_VALUE))))
                                                                                                                                                                                                .addContainerGap(62, Short.MAX_VALUE)));
        gl_filterPane
        .setVerticalGroup(gl_filterPane
                .createParallelGroup(Alignment.LEADING)
                .addGroup(
                        gl_filterPane
                        .createSequentialGroup()
                        .addContainerGap(20, Short.MAX_VALUE)
                        .addGroup(
                                gl_filterPane
                                .createParallelGroup(Alignment.BASELINE)
                                .addComponent(labelProjects)
                                .addComponent(projects, GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addGap(18)
                                        .addGroup(
                                                gl_filterPane
                                                .createParallelGroup(Alignment.BASELINE)
                                                .addComponent(labelDate)
                                                .addComponent(labelDays, GroupLayout.PREFERRED_SIZE, 25,
                                                        GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(labelDelete, GroupLayout.PREFERRED_SIZE, 25,
                                                                GroupLayout.PREFERRED_SIZE))
                                                                .addGap(18)
                                                                .addGroup(
                                                                        gl_filterPane
                                                                        .createParallelGroup(Alignment.LEADING, false)
                                                                        .addGroup(
                                                                                gl_filterPane
                                                                                .createSequentialGroup()
                                                                                .addGroup(
                                                                                        gl_filterPane
                                                                                        .createParallelGroup(
                                                                                                Alignment.LEADING)
                                                                                                .addComponent(
                                                                                                        days,
                                                                                                        GroupLayout.PREFERRED_SIZE,
                                                                                                        GroupLayout.DEFAULT_SIZE,
                                                                                                        GroupLayout.PREFERRED_SIZE)
                                                                                                        .addComponent(deleteNo))
                                                                                                        .addGroup(
                                                                                                                gl_filterPane
                                                                                                                .createParallelGroup(
                                                                                                                        Alignment.LEADING)
                                                                                                                        .addGroup(
                                                                                                                                gl_filterPane
                                                                                                                                .createSequentialGroup()
                                                                                                                                .addGap(8)
                                                                                                                                .addComponent(
                                                                                                                                        labelSelectedDays,
                                                                                                                                        GroupLayout.PREFERRED_SIZE,
                                                                                                                                        25,
                                                                                                                                        GroupLayout.PREFERRED_SIZE))
                                                                                                                                        .addGroup(
                                                                                                                                                gl_filterPane
                                                                                                                                                .createSequentialGroup()
                                                                                                                                                .addPreferredGap(
                                                                                                                                                        ComponentPlacement.RELATED)
                                                                                                                                                        .addComponent(
                                                                                                                                                                deleteYes)))
                                                                                                                                                                .addPreferredGap(ComponentPlacement.RELATED,
                                                                                                                                                                        24, Short.MAX_VALUE)
                                                                                                                                                                        .addComponent(preview).addGap(18)
                                                                                                                                                                        .addComponent(export))
                                                                                                                                                                        .addComponent(dateChooser, GroupLayout.PREFERRED_SIZE, 162,
                                                                                                                                                                                GroupLayout.PREFERRED_SIZE)).addGap(25)));
        filterPane.setLayout(gl_filterPane);
        table.setModel(tableModel);
        table.setDefaultRenderer(Object.class, tableCellRenderer);
        table.setColumnModel(columnModel);
        table.getTableHeader().setReorderingAllowed(false);
        table.setCellSelectionEnabled(true);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                if (MouseEvent.BUTTON1 == e.getButton() && 2 == e.getClickCount()) {
                    int selectedRow = table.getSelectedRow();
                    String uuid = (String) table.getValueAt(selectedRow, 0);
                    Entry entry = EntityController.getInstance().findById(Entry.class, uuid);
                    new NewEntryFrame(entry).setVisible(true);
                } else if (MouseEvent.BUTTON3 == e.getButton()) {
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        table.setRowSorter(rowSorter);
        table.addKeyListener(new TimeTrackKeyAdapter());
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.setTransferHandler(null);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        scrollPane.addKeyListener(new TimeTrackKeyAdapter());

        getContentPane().add(summaryPane, BorderLayout.SOUTH);
        summaryPane.add(summary);
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
     * Lädt alle verfügbaren (aktiven) Projekte.
     */
    private void loadProjectListData() {
        Collection<Project> projectList = EntityController.getInstance().findAll(Project.class,
                new SortParam(Project.PARAM_NAME, SortOrder.ASC), false);
        projects.addItem(StringUtils.EMPTY);
        for (Project project : projectList) {
            projects.addItem(project.getName());
        }
    }

    /**
     * Liefert die gespeicherten Daten anhand des Filters
     *
     * @return Liste der Einträge
     */
    private Collection<Entry> getFilteredData(final String projectName, final Calendar from, final Calendar to) {
        Map<String, Object> params = new HashMap<String, Object>();

        params.put(Entry.PARAM_DATE_FROM, from.getTime());
        params.put(Entry.PARAM_DATE_TO, to.getTime());

        if (StringUtils.isEmpty(projectName)) {
            return getFilteredData(Entry.QUERY_FIND_FROM_TO, params);
        } else {
            Map<String, Object> p = new HashMap<String, Object>();
            p.put(Project.PARAM_NAME, projectName);
            Project project = EntityController.getInstance().findSingleResultByParameter(Project.class,
                    Project.QUERY_FINDBYNAME, p);

            params.put(Entry.PARAM_PROJECT, project);
            return getFilteredData(Entry.QUERY_FIND_FROM_TO_PROJECT, params);
        }
    }

    /**
     * Liefert die gespeicherten Daten anhand des Filters
     *
     * @return Liste der Einträge
     */
    private Collection<Entry> getFilteredData(final String query, final Map<String, Object> params) {
        return EntityController.getInstance().findResultlistByParameter(Entry.class, query, params);
    }
}
