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
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.poi.ss.usermodel.Workbook;
import org.quartz.SchedulerException;

import de.ronnyfriedland.time.config.Configurator;
import de.ronnyfriedland.time.config.Configurator.ConfiguratorKeys;
import de.ronnyfriedland.time.config.Messages;
import de.ronnyfriedland.time.entity.Entry;
import de.ronnyfriedland.time.entity.Project;
import de.ronnyfriedland.time.logic.EntityController;
import de.ronnyfriedland.time.logic.ImportController;
import de.ronnyfriedland.time.logic.QuartzController;
import de.ronnyfriedland.time.logic.jobs.CheckEntryWorkflowStateJob;
import de.ronnyfriedland.time.logic.jobs.ShowMessagePopupJob;
import de.ronnyfriedland.time.sort.SortParam;
import de.ronnyfriedland.time.sort.SortParam.SortOrder;
import de.ronnyfriedland.time.ui.dialog.ExportFrame;
import de.ronnyfriedland.time.ui.dialog.NewEntryFrame;
import de.ronnyfriedland.time.ui.dialog.NewProjectFrame;

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
             * (non-Javadoc)
             * 
             * @see java.lang.Runnable#run()
             */
            @Override
            public void run() {
                new TimeTableUI().createAndShowGUI();
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
            final Menu todayItems = new Menu(Messages.LAST_ENTRIES.getMessage());
            final MenuItem exportItem = new MenuItem(Messages.EXPORT_DATA.getMessage());
            final MenuItem importItem = new MenuItem(Messages.IMPORT_DATA.getMessage());
            final MenuItem exitItem = new MenuItem(Messages.EXIT.getMessage());

            // Add components to popup menu
            popup.add(newProject);
            popup.add(newItem);
            popup.addSeparator();
            popup.add(todayItems);
            popup.addSeparator();
            popup.add(exportItem);
            popup.add(importItem);
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
                    if ((MouseEvent.BUTTON1 == e.getButton())) {
                        if (2 == e.getClickCount()) {
                            new NewEntryFrame().setVisible(true);
                        }
                    }
                    Collection<Entry> todayEntries = EntityController.getInstance().findAll(Entry.class,
                            new SortParam("date", SortOrder.DESC), 10, false);
                    todayItems.removeAll();

                    for (final Entry entry : todayEntries) {
                        MenuItem menuItem = new MenuItem(String.format("%1$s: %2$sh - %3$s (%4$s)", entry
                                .getDateString(), entry.getDuration(), entry.getDescription(), entry.getProject()
                                .getName()));
                        menuItem.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(final ActionEvent e) {
                                new NewEntryFrame(entry).setVisible(true);
                            }
                        });
                        todayItems.add(menuItem);
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

                importItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        try {
                            ImportController controller = new ImportController();
                            Workbook wb = controller.loadWorkbook(
                                    Configurator.CONFIG.getString(ConfiguratorKeys.PATH.getKey()),
                                    Configurator.CONFIG.getString(ConfiguratorKeys.EXPORT_FILE.getKey()));
                            Collection<Entry> entries = controller.loadSheet(wb);

                            EntityController.getInstance().deleteAll(Entry.class);
                            EntityController.getInstance().deleteAll(Project.class);

                            for (Entry entry : entries) {
                                Project project = entry.getProject();
                                Map<String, Object> parameters = new HashMap<String, Object>();
                                parameters.put(Project.PARAM_NAME, project.getName());
                                try {
                                    Project savedProject = EntityController.getInstance().findSingleResultByParameter(
                                            Project.class, Project.QUERY_FINDBYNAME, parameters);
                                    project = savedProject;
                                    project.addEntry(entry);
                                } catch (Exception ex) {
                                    EntityController.getInstance().create(project);
                                }
                                entry.setProject(project);
                                EntityController.getInstance().create(entry);
                            }
                        } catch (IOException ex) {
                            LOG.log(Level.SEVERE, "Error importing data.", ex);
                        }
                    }
                });

                exitItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        try {
                            QuartzController.getInstance().shutdownScheduler();
                        } catch (SchedulerException ex) {
                            LOG.log(Level.SEVERE, "Error shutting down scheduler.", ex);
                        }
                        tray.remove(trayIcon);
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
                // initialize Controller ...
                EntityController.getInstance();
                QuartzController.getInstance().initScheduler(ShowMessagePopupJob.class,
                        Configurator.CONFIG.getString(ConfiguratorKeys.CRON_EXPRESSION_POPUP.getKey()));
                QuartzController.getInstance().initScheduler(CheckEntryWorkflowStateJob.class,
                        Configurator.CONFIG.getString(ConfiguratorKeys.CRON_EXPRESSION_ENTRYWORKFLOW.getKey()));
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Unable to set look and feel or add tray icon", e);
            }
        }
    }
}
