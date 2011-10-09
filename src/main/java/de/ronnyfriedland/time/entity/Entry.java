package de.ronnyfriedland.time.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

/**
 * @author ronnyfriedland
 */
@Entity
public class Entry extends AbstractEntity {
    private static final long serialVersionUID = -6406081124935463200L;
    @NotNull
    private Float duration;
    @NotNull
    private String description;
    @ManyToOne(cascade = CascadeType.PERSIST)
    private Project project;

    public Float getDuration() {
        return duration;
    }

    public void setDuration(Float duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
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
        sbuild.append(String.format("description: %s, ", getDescription()));
        sbuild.append(String.format("project: %s]", getProject()));
        return sbuild.toString();
    }
}
