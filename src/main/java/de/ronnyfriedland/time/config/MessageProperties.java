package de.ronnyfriedland.time.config;

import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.logging.Logger;

/**
 * @author Ronny Friedland
 */
public final class MessageProperties {
    /** The logger for {@link MessageProperties} */
    private static final Logger LOG = Logger.getLogger(MessageProperties.class.getName());

    /**
     * Erzeugt eine Instanz mit den angegebenen Werten.
     */
    private MessageProperties() {
        // empty
    }

    /**
     * Liefert den Text zum angegebenen Message-Key für das angegebene Locale.
     * 
     * @param key
     *            der Message-Key
     * @param values
     *            die (optionalen) Parameter
     * @return der Text
     */
    public static String getString(final String key, final String... values) {
        String result = '!' + key + '!';
        try {
            result = PropertyResourceBundle.getBundle("messages").getString(key);
            if (values != null) {
                result = String.format(result, values);
            }
        } catch (MissingResourceException e) {
            LOG.info("Bundle für Textbausteine nicht gefunden! " + e);
        }
        return result;
    }
}
