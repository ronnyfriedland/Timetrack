package de.ronnyfriedland.time.ui.to;

import org.apache.commons.lang3.StringUtils;

/**
 * Transfer object for {@link de.ronnyfriedland.time.entity.Project}.
 * 
 * @author Ronny Friedland
 */
public class ProjectData {
    public final String projectUuid;
    public final String projectName;
    public final String description;
    public final Integer entryCount;
    public final Boolean enabled;

    /**
     * Creates a new ProjectData instance.
     * 
     * @param projectUuid the uuid of the {@link de.ronnyfriedland.time.entity.Project}
     * @param projectName the name of the {@link de.ronnyfriedland.time.entity.Project}
     * @param description the description of the {@link de.ronnyfriedland.time.entity.Project}
     * @param enabled the flag if enabled of the {@link de.ronnyfriedland.time.entity.Project}
     * @param entryCount the number of entries in the {@link de.ronnyfriedland.time.entity.Project}
     */
    public ProjectData(final String projectUuid, final String projectName, final String description,
            final boolean enabled, final Integer entryCount) {
        this.projectUuid = projectUuid;
        this.projectName = projectName;
        this.enabled = enabled;
        this.entryCount = entryCount;
        if (!StringUtils.isBlank(description)) {
            this.description = description;
        } else {
            this.description = StringUtils.EMPTY;
        }
    }
}