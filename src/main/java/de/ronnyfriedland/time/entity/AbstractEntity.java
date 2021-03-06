package de.ronnyfriedland.time.entity;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.ronnyfriedland.time.entity.validation.NotBlank;

/**
 * Abstrakte Basisklasse für alle Entitäten.
 * 
 * @author Ronny Friedland
 */
@MappedSuperclass
public abstract class AbstractEntity implements Serializable {
    private static final long serialVersionUID = 3220515724286916541L;

    @NotBlank
    @Id
    @Column(name = "UUID", nullable = false)
    private final String uuid;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LASTMODIFIEDDATE", nullable = false)
    private final Date lastModifiedDate;

    /**
     * Erzeugt eine neue {@link AbstractEntity} Instanz.
     */
    public AbstractEntity() {
        this(UUID.randomUUID().toString());
    }

    /**
     * Erzeugt eine neue {@link AbstractEntity} Instanz.
     * 
     * @param uuid
     *            die uuid des Entity
     */
    public AbstractEntity(final String uuid) {
        this.uuid = uuid;
        lastModifiedDate = Calendar.getInstance().getTime();
    }

    /**
     * Liefert die {@link #uuid} des Entity.
     * 
     * @return die uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Liefert das {@link #lastModifiedDate} des Entity.
     * 
     * @return das lastModifiedDate
     */
    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof AbstractEntity) {
            return getUuid().equals(((AbstractEntity) obj).getUuid());
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sbuild = new StringBuilder(super.toString());
        sbuild.append(String.format("[uuid: %s, ", getUuid()));
        sbuild.append(String.format("lastModifiedDate: %s]", getLastModifiedDate()));
        return sbuild.toString();
    }
}
