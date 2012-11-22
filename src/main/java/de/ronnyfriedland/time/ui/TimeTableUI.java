package de.ronnyfriedland.time.ui;

import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.poi.ss.usermodel.Workbook;
import org.quartz.SchedulerException;

import de.ronnyfriedland.time.config.Configurator;
import de.ronnyfriedland.time.config.Configurator.ConfiguratorKeys;
import de.ronnyfriedland.time.config.Messages;
import de.ronnyfriedland.time.entity.Entry;
import de.ronnyfriedland.time.entity.EntryState;
import de.ronnyfriedland.time.entity.Project;
import de.ronnyfriedland.time.logic.EntityController;
import de.ronnyfriedland.time.logic.ImportController;
import de.ronnyfriedland.time.logic.PluginController;
import de.ronnyfriedland.time.logic.QuartzController;
import de.ronnyfriedland.time.logic.jobs.CheckEntryWorkflowStateJob;
import de.ronnyfriedland.time.logic.jobs.ShowReminderJob;
import de.ronnyfriedland.time.sort.SortParam;
import de.ronnyfriedland.time.sort.SortParam.SortOrder;
import de.ronnyfriedland.time.ui.dialog.ExportFrame;
import de.ronnyfriedland.time.ui.dialog.NewEntryFrame;
import de.ronnyfriedland.time.ui.dialog.NewProjectFrame;
import de.ronnyfriedland.time.ui.dialog.ShowHelpFrame;

/**
 * Die Hauptklasse (Grafisches Interface)
 * 
 * @author Ronny Friedland
 */
public final class TimeTableUI {

    private static final Logger LOG = Logger.getLogger(TimeTableUI.class.getName());

    /**
     * The main method.
     * 
     * @param args
     *            Argumente
     */
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            /**
             * {@inheritDoc}
             * 
             * @see java.lang.Runnable#run()
             */
            @Override
            public void run() {
                new TimeTableUI().createAndShowGUI();
            }
        });
        Runtime.getRuntime().addShutdownHook(new Thread() {
            /**
             * {@inheritDoc}
             * 
             * @see java.lang.Thread#run()
             */
            @Override
            public void run() {
                try {
                    QuartzController.getInstance().shutdownScheduler();
                } catch (SchedulerException ex) {
                    LOG.log(Level.SEVERE, "Error shutting down scheduler.", ex);
                }
            }
        });
    }

    private TimeTableUI() {
        // empty
    }

    private void createAndShowGUI() {
        // Check the SystemTray support
        if (!SystemTray.isSupported()) {
            LOG.severe("SystemTray not supported!");
        } else {
            final PopupMenu popup = new PopupMenu();
            final TrayIcon trayIcon = new TrayIcon((new ImageIcon(Thread.currentThread().getContextClassLoader()
                    .getResource("images/icon.gif"))).getImage());
            final SystemTray tray = SystemTray.getSystemTray();

            // Create a popup menu components
            final MenuItem newProject = new MenuItem(Messages.NEW_PROJECT.getMessage());
            final MenuItem newItem = new MenuItem(Messages.NEW_ENTRY.getMessage());
            final Menu lastItems = new Menu(Messages.LAST_ENTRIES.getMessage());
            final MenuItem exportItem = new MenuItem(Messages.EXPORT_DATA.getMessage());
            final MenuItem importItem = new MenuItem(Messages.IMPORT_DATA.getMessage());
            final MenuItem helpItem = new MenuItem(Messages.HELP.getMessage());
            final MenuItem exitItem = new MenuItem(Messages.EXIT.getMessage());

            // Add components to popup menu
            popup.add(newProject);
            popup.add(newItem);
            popup.addSeparator();
            popup.add(lastItems);
            popup.addSeparator();
            popup.add(exportItem);
            popup.add(importItem);
            popup.addSeparator();
            popup.add(helpItem);
            popup.addSeparator();
            popup.add(exitItem);

            trayIcon.setPopupMenu(popup);
            trayIcon.setToolTip(Messages.TITLE.getMessage());

            trayIcon.addMouseListener(new MouseListener() {

                @Override
                public void mouseReleased(final MouseEvent e) {
                }

                @Override
                public void mousePressed(final MouseEvent e) {
                    if ((MouseEvent.BUTTON1 == e.getButton()) && (2 == e.getClickCount())) {
                        new NewEntryFrame().setVisible(true);
                    }
                    Collection<Entry> lastEntries = EntityController.getInstance().findAll(Entry.class,
                            new SortParam("date", SortOrder.DESC), 10, false);
                    lastItems.removeAll();

                    for (final Entry entry : lastEntries) {
                        String duration = entry.getCalculatedDuration(new Date());
                        MenuItem menuItem = new MenuItem(String.format("%1$s: %2$sh - %3$s (%4$s)",
                                entry.getDateString(), duration, entry.getDescription(), entry.getProject().getName()));
                        menuItem.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(final ActionEvent e) {
                                new NewEntryFrame(entry).setVisible(true);
                            }
                        });
                        lastItems.add(menuItem);
                    }

                }

                @Override
                public void mouseExited(final MouseEvent e) {
                }

                @Override
                public void mouseEntered(final MouseEvent e) {
                }

                @Override
                public void mouseClicked(final MouseEvent e) {
                }
            });

            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

                tray.add(trayIcon);

                newProject.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        new NewProjectFrame().setVisible(true);
                    }
                });

                newItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        new NewEntryFrame().setVisible(true);
                    }
                });

                exportItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        new ExportFrame().setVisible(true);
                    }
                });

                importItem.setEnabled(new File(Configurator.CONFIG.getString(ConfiguratorKeys.PATH.getKey()),
                        Configurator.CONFIG.getString(ConfiguratorKeys.EXPORT_FILE.getKey())).exists());
                importItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        int option = JOptionPane.showConfirmDialog(null, Messages.IMPORT_CONFIRM.getMessage());
                        switch (option) {
                        case JOptionPane.OK_OPTION:
                            try {
                                ImportController controller = new ImportController();
                                Workbook wb = controller.loadWorkbook(
                                        Configurator.CONFIG.getString(ConfiguratorKeys.PATH.getKey()),
                                        Configurator.CONFIG.getString(ConfiguratorKeys.EXPORT_FILE.getKey()));
                                Collection<Entry> entries = controller.loadSheet(wb);

                                EntityController.getInstance().deleteAll(Entry.class);
                                EntityController.getInstance().deleteAll(EntryState.class);
                                EntityController.getInstance().deleteAll(Project.class);

                                for (Entry entry : entries) {
                                    Project project = entry.getProject();
                                    Map<String, Object> parameters = new HashMap<String, Object>();
                                    parameters.put(Project.PARAM_NAME, project.getName());
                                    try {
                                        Project savedProject = EntityController.getInstance()
                                                .findSingleResultByParameter(Project.class, Project.QUERY_FINDBYNAME,
                                                        parameters);
                                        project = savedProject;
                                        project.addEntry(entry);
                                    } catch (Exception ex) {
                                        EntityController.getInstance().create(project);
                                    }
                                    entry.setProject(project);
                                    EntityController.getInstance().create(entry);
                                }
                                JOptionPane.showMessageDialog(null,
                                        Messages.IMPORT_SUCCESSFUL.getMessage(String.valueOf(entries.size())));
                            } catch (IOException ex) {
                                LOG.log(Level.SEVERE, "Error importing data.", ex);
                            }
                            break;
                        default:
                            // nothing to do
                            break;
                        }
                    }
                });

                helpItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        new ShowHelpFrame().setVisible(true);
                    }
                });

                exitItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        System.exit(0);
                    }
                });

                // initialize Logging ...
                LogManager logManager = LogManager.getLogManager();
                try {
                    logManager.readConfiguration(Thread.currentThread().getContextClassLoader()
                            .getResourceAsStream("logging.properties"));
                } catch (IOException e) {
                    LOG.log(Level.SEVERE, "Error reading logging.properties to configure logger.", e);
                }
                // initialize entity controller ...
                EntityController.getInstance();
                // initialize quartz controller ...
                Map<String, Object> jobData = new HashMap<String, Object>();
                jobData.put("trayIcon", trayIcon);
                QuartzController.getInstance().initScheduler(ShowReminderJob.class,
                        Configurator.CONFIG.getString(ConfiguratorKeys.CRON_EXPRESSION_POPUP.getKey()), jobData);
                QuartzController.getInstance()
                        .initScheduler(CheckEntryWorkflowStateJob.class,
                                Configurator.CONFIG.getString(ConfiguratorKeys.CRON_EXPRESSION_ENTRYWORKFLOW.getKey()),
                                jobData);
                // initialize plugin controller ...
                PluginController.getInstance().executePlugins();
            } catch (Exception e) {
                throw new IllegalStateException("Unable to start timetable application", e);
            }
        }
    }
}
