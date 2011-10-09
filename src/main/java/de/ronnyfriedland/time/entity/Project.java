package de.ronnyfriedland.time.entity;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

/**
 * @author ronnyfriedland
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "NAME" }))
public class Project extends AbstractEntity {
    private static final long serialVersionUID = -6146948125332405909L;

    @NotNull
    private String name;
    private String description;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "ENTRIES_UUID")
    private Collection<Entry> entries;

    public Project() {
        super();
        entries = new HashSet<Entry>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Collection<Entry> getEntries() {
        return entries;
    }

    public void setEntries(Collection<Entry> entries) {
        this.entries = entries;
    }

    public void addEntry(Entry entry) {
        this.entries.add(entry);
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sbuild = new StringBuilder(super.toString());
        sbuild.append(String.format("[name: %s, ", getName()));
        sbuild.append(String.format("description: %s]", getDescription()));
        return sbuild.toString();
    }
}
