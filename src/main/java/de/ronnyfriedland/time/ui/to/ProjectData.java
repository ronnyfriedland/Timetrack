package de.ronnyfriedland.time.ui.to;

import org.apache.commons.lang.StringUtils;

public class ProjectData {
    public final String projectName;
    public final String description;
    public final Integer entryCount;
    public final Boolean enabled;

    public ProjectData(final String projectName, final String description, final boolean enabled,
            final Integer entryCount) {
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