package de.ronnyfriedland.time.migration;

import javax.persistence.EntityTransaction;
import javax.persistence.Query;

public class MigrateToVersion12 extends MigrateVersion {

	@Override
	public void migrate() {
		try {
			EntityTransaction entr = em.getTransaction();
			entr.begin();
			Query query = em.createNativeQuery("ALTER TABLE PROJECT ADD ENABLED INTEGER DEFAULT 1");
			query.executeUpdate();
			entr.commit();
			LOG.info("Migration auf Version 1.2 erfolgreich.");
		} finally {
			em.close();
		}
	}
}
