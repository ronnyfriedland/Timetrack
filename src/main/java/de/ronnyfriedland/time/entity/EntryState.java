package de.ronnyfriedland.time.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

/**
 * Entität zum Speichern für den Status der Einträge.
 * 
 * @author Ronny Friedland
 */
@Entity
@NamedQueries({ @NamedQuery(name = EntryState.QUERY_FIND_BY_STATE_AND_STARTDATE, query = "SELECT e FROM EntryState e WHERE e.state = :state AND e.start < :date") })
public class EntryState extends AbstractEntity {

    public static final String QUERY_FIND_BY_STATE_AND_STARTDATE = "EntryState.findByStateAndDate";

    public static final String PARAM_DATE = "date";
    public static final String PARAM_STATE = "state";

    public static String getDuration(final Date start, final Date end, final String duration) {
        return getDuration(start, end, Float.valueOf(duration));
    }

    public static String getDuration(final Date start, final Date end, final float duration) {
        // calculate duration
        return String.format("%.2f", (new Float(end.getTime() - start.getTime()) / 1000 / 60 / 60) + duration);
    }

    private static final long serialVersionUID = 4704359592289513635L;

    public enum State {
        /** Entry started - everything is ok */
        OK,
        /** Entry is running a period of time and reached warn time */
        WARN,
        /** Entry was stopped (manually or automatically) */
        STOPPED,
        /** Entry is fixed (no workflow) */
        FIXED;
    }

    @NotNull
    @Column(name = "STATE", nullable = false)
    @Enumerated(EnumType.STRING)
    private State state = State.OK;
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "STARTTIME", nullable = false)
    private Date start;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ENDTIME", nullable = true)
    private Date end;

    public EntryState() {
        super();
    }

    public EntryState(final Date start) {
        super();
        setStart(start);
    }

    public EntryState(final Date start, final State state) {
        this(start);
        setState(state);
    }

    public EntryState(final String uuid) {
        super(uuid);
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sbuild = new StringBuilder(super.toString());
        sbuild.append(String.format("[state: %s, ", getState()));
        sbuild.append(String.format("start: %s, ", getStart()));
        sbuild.append(String.format("end: %s]", getEnd()));
        return sbuild.toString();
    }
}
