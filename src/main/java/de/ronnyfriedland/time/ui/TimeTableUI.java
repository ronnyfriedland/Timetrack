package de.ronnyfriedland.time.ui;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

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
            final PopupMenu popup = new PopupMenu();
            final TrayIcon trayIcon = new TrayIcon(createImage("images/icon.gif", "Timetable"));
            final SystemTray tray = SystemTray.getSystemTray();

            // Create a popup menu components
            final MenuItem newProject = new MenuItem("Neues Projekt");
            final MenuItem newItem = new MenuItem("Neuer Eintrag");
            final Menu todayItems = new Menu("Heute erstellt");
            final MenuItem exportItem = new MenuItem("Daten exportieren");
            final MenuItem exitItem = new MenuItem("Beenden");

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
                            Entry.class, Entry.QUERY_FIND_TODAY_BY_CREATIONDATE, params);

                    todayItems.removeAll();

                    for (Entry entry : todayEntries) {
                        todayItems.add(new MenuItem(String.format("%1$s %2$sh (%3$s)", entry.getDescription(),
                                entry.getDuration(), entry.getProject().getName())));
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

    private static Image createImage(final String path, final String description) {
        Image result = null;
        URL imageURL = Thread.currentThread().getContextClassLoader().getResource(path);

        if (imageURL == null) {
            LOG.severe(String.format("Resource %1$s not found!", path));
        } else {
            result = (new ImageIcon(imageURL, description)).getImage();
        }
        return result;
    }
}
