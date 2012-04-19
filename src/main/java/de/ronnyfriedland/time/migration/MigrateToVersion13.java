package de.ronnyfriedland.time.migration;

import javax.persistence.EntityTransaction;
import javax.persistence.Query;

public class MigrateToVersion13 extends MigrateVersion {

	String[] queries = new String[] { "ALTER TABLE ENTRY ALTER COLUMN DESCRIPTION SET DATA TYPE VARCHAR(2000)",
	        "ALTER TABLE PROJECT ALTER COLUMN DESCRIPTION SET DATA TYPE VARCHAR(2000)" };

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
			LOG.info("Migration auf Version 1.3 erfolgreich.");
		} finally {
			em.close();
		}
	}
}
