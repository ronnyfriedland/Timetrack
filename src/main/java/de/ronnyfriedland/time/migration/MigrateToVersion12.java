package de.ronnyfriedland.time.migration;

import javax.persistence.EntityTransaction;
import javax.persistence.Query;

public class MigrateToVersion12 extends MigrateVersion {

	String[] queries = new String[] { "ALTER TABLE PROJECT ADD ENABLED INTEGER DEFAULT 1",
	        "ALTER TABLE ENTRY ADD ENABLED INTEGER DEFAULT 1" };

	@Override
	public void migrate() {
		try {
			EntityTransaction entr = em.getTransaction();
			entr.begin();
			for (String queryString : queries) {
				Query query = em.createNativeQuery(queryString);
				query.executeUpdate();
			}
			entr.commit();
			LOG.info("Migration auf Version 1.2 erfolgreich.");
		} finally {
			em.close();
		}
	}
}
