package de.ronnyfriedland.time.logic;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.ronnyfriedland.time.entity.Protocol;

/**
 * Controller für das Erzeugen von {@link Protocol} Einträgen.
 * 
 * @author Ronny Friedland
 */
public final class ProtocolController {

    private static final Logger LOG = Logger.getLogger(ProtocolController.class.getName());

    private static ProtocolController instance;

    private final transient Set<Protocol> protocolEntries = new CopyOnWriteArraySet<Protocol>();

    /**
     * Liefert eine Instanz von {@link EntityController}.
     * 
     * @return the {@link EntityController}
     */
    public static ProtocolController getInstance() {
        synchronized (ProtocolController.class) {
            if (null == instance) {
                instance = new ProtocolController();
            }
        }
        return instance;
    }

    private ProtocolController() {
        // empty
    }

    /**
     * Fügt ein neues {@link Protocol} hinzu.
     * 
     * @param protocol die {@link Protocol} Instanz
     */
    public void writeProtocol(final Protocol protocol) {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Add protocol: " + protocol);
        }
        this.protocolEntries.add(protocol);
    }

    /**
     * Entfernt die angegebenen {@link Protocol} Einträge.
     * 
     * @param protocol die zu entfernenden {@link Protocol} Einträge.
     */
    public void removeProtocol(final Protocol... protocol) {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Remove protocol(s): " + protocol);
        }
        if (null != protocol) {
            protocolEntries.removeAll(Arrays.asList(protocol));
        }
    }

    /**
     * Liefert alle {@link Protocol} Einträge zurück.
     * 
     * @return Liste der {@link Protocol} Einträge.
     */
    public Set<Protocol> getProtocols() {
        return Collections.unmodifiableSet(protocolEntries);
    }
}
