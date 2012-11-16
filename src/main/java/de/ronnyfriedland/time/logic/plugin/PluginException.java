package de.ronnyfriedland.time.logic.plugin;

/**
 * @author Ronny Friedland
 */
public class PluginException extends Exception {
    private static final long serialVersionUID = 394292116039818809L;

    /**
     * Erzeugt eine neue {@link PluginException}.
     * 
     * @param message
     *            the mssage
     * @param cause
     *            the cause
     */
    public PluginException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Erzeugt eine neue {@link PluginException}.
     * 
     * @param message
     *            the mssage
     */
    public PluginException(final String message) {
        super(message);
    }

    /**
     * Erzeugt eine neue {@link PluginException}.
     * 
     * @param cause
     *            the cause
     */
    public PluginException(final Throwable cause) {
        super(cause);
    }
}
