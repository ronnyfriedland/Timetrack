package de.ronnyfriedland.time.migration;

import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Abstrakte Basisklasse für eine Datenbankmigration.
 * 
 * @author Ronny Friedland
 */
public abstract class MigrateVersion {
	/** The logger */
	protected static final Logger LOG = Logger.getLogger(MigrateDatabase.class.getName());
	/** The {@link EntityManager} */
	protected final EntityManager em;

	/**
	 * Konstruktor mit der Initialisierung des {@link EntityManager}.
	 */
	public MigrateVersion() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("timetable");
		em = emf.createEntityManager();
	}

	/**
	 * Ausführungslogik für die Migration
	 */
	public abstract void migrate();
}
