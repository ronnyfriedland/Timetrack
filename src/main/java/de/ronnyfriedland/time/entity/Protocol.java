package de.ronnyfriedland.time.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

import de.ronnyfriedland.time.entity.validation.NotBlank;

/**
 * Entität zum Speichern der Projekte.
 * 
 * @author Ronny Friedland
 */
@Entity
public class Protocol extends AbstractEntity {
    private static final long serialVersionUID = 7364095843976457542L;

    public enum ProtocolValue {
        APP_STARTED, APP_STOPPED, ENTRY_CREATED, ENTRY_UPDATED, ENTRY_DELETED, ENTRY_STARTED, ENTRY_STOPPED, PROJECT_CREATED, PROJECT_UPDATED, PROJECT_DELETED, EXPORT, IMPORT;
    }

    @NotBlank
    @Column(name = "DESCRIPTION", nullable = true)
    private String description;

    /**
     * Erzeugt eine neue {@link Protocol} Instanz.
     */
    public Protocol() {
        super();
    }

    /**
     * Erzeugt eine neue {@link Protocol} Instanz.
     * 
     * @param uuid die uuid
     */
    public Protocol(final String uuid) {
        super(uuid);
    }

    /**
     * Erzeugt eine neue {@link Protocol} Instanz.
     * 
     * @param description die description
     */
    public Protocol(final ProtocolValue description) {
        super();
        this.description = description.name();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sbuild = new StringBuilder(super.toString());
        sbuild.append(String.format("[description: %s]", getDescription()));
        return sbuild.toString();
    }
}
