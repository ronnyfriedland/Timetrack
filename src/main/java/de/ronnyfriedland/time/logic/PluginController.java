package de.ronnyfriedland.time.logic;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import de.ronnyfriedland.time.logic.plugin.Plugin;

/**
 * Controller für die Verwaltung und das Initialisieren von Plugins.
 * 
 * @author Ronny Friedland
 */
public final class PluginController {

    private static final Logger LOG = Logger.getLogger(PluginController.class.getName());

    private static PluginController instance;

    /**
     * Liefert eine Instanz von {@link PluginController}.
     * 
     * @return the {@link PluginController}
     */
    public static PluginController getInstance() {
        synchronized (PluginController.class) {
            if (null == instance) {
                instance = new PluginController();
            }
        }
        return instance;
    }

    private PluginController() {
        // empty
    }

    public void executePlugins() {
        Reflections reflections = new Reflections(new ConfigurationBuilder().addUrls(
                ClasspathHelper.forClass(Plugin.class)).setScanners(new TypeAnnotationsScanner()));

        Set<Class<?>> plugins = reflections.getTypesAnnotatedWith(Plugin.class);
        for (final Class<?> plugin : plugins) {
            new Thread(new Runnable() {
                /**
                 * {@inheritDoc}
                 * 
                 * @see java.lang.Runnable#run()
                 */
                @Override
                public void run() {
                    try {
                        if (LOG.isLoggable(Level.FINE)) {
                            LOG.fine("Initialize plugin: " + plugin);
                        }
                        plugin.newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

}
