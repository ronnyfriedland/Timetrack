package de.ronnyfriedland.time.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import de.ronnyfriedland.time.entity.validation.NotBlank;

/**
 * @author ronnyfriedland
 */
@Entity
@NamedQuery(name = Entry.QUERY_FIND_TODAY, query = "SELECT e FROM Entry e WHERE e.creationDate >= :date")
public class Entry extends AbstractEntity {

    public static final String QUERY_FIND_TODAY = "Entry.findToday";

    private static final long serialVersionUID = -6406081124935463200L;
    @NotBlank
    @Pattern(regexp = "(\\+?((([0-9]+(\\.)?)|([0-9]*\\.[0-9]+))([eE][+-]?[0-9]+)?))")
    @Column(name = "DURATION", nullable = false)
    private String duration;
    @NotBlank
    @Pattern(regexp = "[0-9]{2}\\.[0-9]{2}\\.[0-9]{4}")
    @Column(name = "DATE", nullable = false)
    private String date;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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
