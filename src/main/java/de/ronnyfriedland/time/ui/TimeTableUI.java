package de.ronnyfriedland.time.ui;

import java.awt.AWTException;
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
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import de.ronnyfriedland.time.config.Messages;
import de.ronnyfriedland.time.entity.Entry;
import de.ronnyfriedland.time.logic.EntityController;
import de.ronnyfriedland.time.ui.dialog.ExportFrame;
import de.ronnyfriedland.time.ui.dialog.NewEntryFrame;
import de.ronnyfriedland.time.ui.dialog.NewProjectFrame;

/**
 * @author ronnyfriedland
 */
public class TimeTableUI {

    private static final Logger LOG = Logger.getLogger(TimeTableUI.class.getName());

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

    private void createAndShowGUI() {
        // Check the SystemTray support
        if (!SystemTray.isSupported()) {
            LOG.severe("SystemTray not supported!");
        } else {

            // initialize EntityController ...
            EntityController.getInstance();

            try {
                LogManager.getLogManager().readConfiguration(
                        Thread.currentThread().getContextClassLoader().getResourceAsStream("logging.properties"));
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            final PopupMenu popup = new PopupMenu();
            final TrayIcon trayIcon = new TrayIcon((new ImageIcon(Thread.currentThread().getContextClassLoader()
                    .getResource("images/icon.gif"))).getImage());
            final SystemTray tray = SystemTray.getSystemTray();

            // Create a popup menu components
            final MenuItem newProject = new MenuItem(Messages.NEW_PROJECT.getText());
            final MenuItem newItem = new MenuItem(Messages.NEW_ENTRY.getText());
            final Menu todayItems = new Menu(Messages.TODAY_ENTRIES.getText());
            final MenuItem exportItem = new MenuItem(Messages.EXPORT_DATA.getText());
            final MenuItem exitItem = new MenuItem(Messages.EXIT.getText());

            // Add components to popup menu
            popup.add(newProject);
            popup.add(newItem);
            popup.addSeparator();
            popup.add(todayItems);
            popup.add(exportItem);
            popup.addSeparator();
            popup.add(exitItem);

            trayIcon.setPopupMenu(popup);

            trayIcon.addMouseListener(new MouseListener() {

                @Override
                public void mouseReleased(MouseEvent e) {
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    Map<String, Object> params = new HashMap<String, Object>();

                    Calendar from = Calendar.getInstance();
                    from.set(Calendar.HOUR_OF_DAY, 0);
                    from.set(Calendar.MINUTE, 0);
                    from.set(Calendar.SECOND, 0);
                    from.set(Calendar.MILLISECOND, 0);

                    params.put(Entry.PARAM_DATE_FROM, from.getTime());

                    Collection<Entry> todayEntries = EntityController.getInstance().findResultlistByParameter(
                            Entry.class, Entry.QUERY_FIND_BY_LASTMODIFIEDDATE, params);

                    todayItems.removeAll();

                    for (final Entry entry : todayEntries) {
                        MenuItem menuItem = new MenuItem(String.format("%1$s %2$sh (%3$s)", entry.getDescription(),
                                entry.getDuration(), entry.getProject().getName()));
                        menuItem.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                new NewEntryFrame(entry).setVisible(true);
                            }
                        });
                        todayItems.add(menuItem);
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                }
            });

            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                System.out.println("TrayIcon could not be added.");
                return;
            }

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

            exitItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    tray.remove(trayIcon);
                    System.exit(0);
                }
            });

        }
    }
}
