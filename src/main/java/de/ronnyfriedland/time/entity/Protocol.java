package de.ronnyfriedland.time.entity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PostLoad;
import javax.persistence.PostUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Pattern;

import de.ronnyfriedland.time.entity.validation.NotBlank;

/**
 * Entität zum Speichern der Projekte.
 * 
 * @author Ronny Friedland
 */
@Entity
public class Protocol extends AbstractEntity {
    private static final long serialVersionUID = 7364095843976457542L;

    public static final String DATETIMESTRINGFORMAT = "dd.MM.yyyy HH:mm:ss";

    public enum ProtocolValue {
        APP_STARTED, APP_STOPPED, ENTRY_CREATED, ENTRY_UPDATED, ENTRY_DELETED, ENTRY_STARTED, ENTRY_STOPPED, PROJECT_CREATED, PROJECT_UPDATED, PROJECT_DELETED, EXPORT, IMPORT;
    }

    @Column(name = "DATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    @NotBlank
    @Pattern(regexp = "[0-9]{2}\\.[0-9]{2}\\.[0-9]{4} [0-9]{2}:[0-9]{2}:[0-9]{2}")
    private transient String dateString;

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
        setDescription(description.name());
        setDate(new Date());
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(final Date date) {
        this.date = date;
        dateString = new SimpleDateFormat(DATETIMESTRINGFORMAT).format(date);
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(final String dateString) {
        this.dateString = dateString;
        try {
            date = new SimpleDateFormat(DATETIMESTRINGFORMAT).parse(dateString);
        } catch (ParseException e) {
            date = new Date();
        }
    }

    @PostLoad
    @PostUpdate
    public void updateDateString() {
        setDate(getDate());
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
