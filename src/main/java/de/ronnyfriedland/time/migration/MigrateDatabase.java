package de.ronnyfriedland.time.migration;

/**
 * @author Ronny Friedland
 */
public class MigrateDatabase {

	public enum Version {
		/** Version 1.0 -> current */
		VERSION10("1.0", new MigrateToVersion12()),
		/** Version 1.1 -> current */
		VERSION11("1.1", new MigrateToVersion12());

		private final String version;
		private final MigrateVersion[] migrations;

		private Version(final String aVersion, final MigrateVersion... aMigrations) {
			version = aVersion;
			migrations = aMigrations;
		}

		public String getVersion() {
			return version;
		}

		public void migrate() {
			for (MigrateVersion migration : migrations) {
				migration.migrate();
			}
		}

		public static Version valueOfVersion(final String versionString) {
			for (Version version : Version.values()) {
				if (version.getVersion().equals(versionString)) {
					return version;
				}
			}
			throw new IllegalArgumentException("No enum const " + Version.class);

		}
	}

	public static void main(String[] args) {
		if (1 != args.length) {
			StringBuilder sbuild = new StringBuilder(
			        "Die Ausgangsversion der Anwendung muss als Parameter angegeben werden: \n");
			for (Version version : Version.values()) {
				sbuild.append(version.getVersion()).append("\n");
			}
			throw new IllegalArgumentException(sbuild.toString());
		}
		Version targetVersion = Version.valueOfVersion(args[0]);
		targetVersion.migrate();
	}
}
