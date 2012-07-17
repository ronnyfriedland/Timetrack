package de.ronnyfriedland.time.entity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.PostUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import de.ronnyfriedland.time.entity.validation.IsFloat;
import de.ronnyfriedland.time.entity.validation.NotBlank;

/**
 * Entität zum Speichern der Einträge.
 * 
 * @author Ronny Friedland
 */
@Entity
@NamedQueries({ @NamedQuery(name = Entry.QUERY_FIND_FROM_TO, query = "SELECT e FROM Entry e WHERE e.date >= :from AND e.date < :to ORDER BY e.date"),
	@NamedQuery(name = Entry.QUERY_FIND_BY_STATE, query = "SELECT e FROM Entry e WHERE e.state = :state")})
public class Entry extends AbstractEntity {

	public static final String DATESTRINGFORMAT = "dd.MM.yyyy";

	public static final String QUERY_FIND_BY_STATE = "Entry.findByState";
	public static final String QUERY_FIND_FROM_TO = "Entry.findFromTo";

	public static final String PARAM_DATE_FROM = "from";
	public static final String PARAM_DATE_TO = "to";
	public static final String PARAM_STATE = "state";

	private static final long serialVersionUID = -6406081124935463200L;
	
	@NotBlank
	@IsFloat
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
	@Column(name = "ENABLED", nullable = false)
	private Boolean enabled = Boolean.TRUE;
	@NotNull
	@ManyToOne(cascade = CascadeType.PERSIST, optional = false, fetch=FetchType.LAZY)
	@JoinColumn(name = "PROJECT_UUID", nullable = false)
	private Project project;
	@NotNull
	@OneToOne(cascade = CascadeType.ALL, optional = false, fetch=FetchType.LAZY)
	@JoinColumn(name = "ENTRYSTATE_UUID", nullable = false)
	private EntryState state;

	public Entry() {
		super();
	}

	public Entry(final String uuid) {
		super(uuid);
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(final String duration) {
		this.duration = duration.replaceAll(",", ".").trim();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
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

	public void setDate(final Date date) {
		this.date = date;
		dateString = new SimpleDateFormat(DATESTRINGFORMAT).format(date);
	}

	public String getDateString() {
		return dateString;
	}

	public void setDateString(final String dateString) {
		this.dateString = dateString;
		try {
			date = new SimpleDateFormat(DATESTRINGFORMAT).parse(dateString);
		} catch (ParseException e) {
			date = new Date();
		}
	}

	public EntryState getState() {
		return state;
	}
	
	public void setState(EntryState state) {
		this.state = state;
	}
	
	@PostLoad
	@PostUpdate
	public void updateDateString() {
		setDate(getDate());
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
		sbuild.append(String.format("enabled: %s, ", getEnabled()));
		sbuild.append(String.format("state: %s, ", getState()));
		sbuild.append(String.format("project: %s]", getProject()));
		return sbuild.toString();
	}
}
