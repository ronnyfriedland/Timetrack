package de.ronnyfriedland.time.entity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import de.ronnyfriedland.time.entity.validation.NotBlank;

/**
 * @author ronnyfriedland
 */
@Entity
@NamedQueries({
        @NamedQuery(name = Entry.QUERY_FIND_FROM_TO, query = "SELECT e FROM Entry e WHERE e.date >= :from AND e.date < :to ORDER BY e.date"),
        @NamedQuery(name = Entry.QUERY_FIND_TODAY_BY_CREATIONDATE, query = "SELECT e FROM Entry e WHERE e.creationDate >= :from") })
public class Entry extends AbstractEntity {

    public static final String QUERY_FIND_TODAY_BY_CREATIONDATE = "Entry.findTodayByCreationdate";
    public static final String QUERY_FIND_FROM_TO = "Entry.findFromTo";

    public static final String PARAM_DATE_FROM = "from";
    public static final String PARAM_DATE_TO = "to";

    private static final long serialVersionUID = -6406081124935463200L;
    @NotBlank
    @Pattern(regexp = "(\\+?((([0-9]+(\\.)?)|([0-9]*\\.[0-9]+))([eE][+-]?[0-9]+)?))")
    @Column(name = "DURATION", nullable = false)
    private String duration;
    @Column(name = "DATE", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date date;
    @NotBlank
    @Pattern(regexp = "[0-9]{2}\\.[0-9]{2}\\.[0-9]{4}")
    private transient String dateString;
    @NotBlank
    @Column(name = "DESCRIPTION", nullable = false)
    private String description;
    @NotNull
    @ManyToOne(cascade = CascadeType.PERSIST, optional = false)
    @JoinColumn(name = "PROJECT_UUID", nullable = false)
    private Project project;

    public String getDuration() {
        return duration;
    }

    public void setDuration(final String duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(final Project project) {
        this.project = project;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        dateString = sdf.format(date);
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        try {
            date = sdf.parse(dateString);
        } catch (ParseException e) {
            date = new Date();
        }
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sbuild = new StringBuilder(super.toString());
        sbuild.append(String.format("[duration: %s, ", getDuration()));
        sbuild.append(String.format("date: %s, ", getDate()));
        sbuild.append(String.format("description: %s, ", getDescription()));
        sbuild.append(String.format("project: %s]", getProject()));
        return sbuild.toString();
    }
}
