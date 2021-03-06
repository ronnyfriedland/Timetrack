package de.ronnyfriedland.time.logic;

import de.ronnyfriedland.time.entity.AbstractEntity;
import de.ronnyfriedland.time.sort.SortParam;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.CompositeResourceAccessor;
import liquibase.resource.FileSystemResourceAccessor;

import javax.persistence.*;
import java.sql.Connection;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Controller für den Zugriff auf die Persistenz-Schicht.
 *
 * @author Ronny Friedland
 */
public final class EntityController {

    private static EntityController instance;

    private final EntityManagerFactory emf;
    private final EntityManager em;

    /**
     * Liefert eine Instanz von {@link EntityController}.
     *
     * @return the {@link EntityController}
     */
    public static EntityController getInstance() {
        synchronized (EntityController.class) {
            if (null == instance) {
                instance = new EntityController();
            }
        }
        return instance;
    }

    /**
     * Erzeugt eine neue {@link EntityController} Instanz.
     */
    private EntityController() {
        emf = Persistence.createEntityManagerFactory("timetrack");
        em = emf.createEntityManager();
    }

    /**
     * Migriert die Datenbank mit dem aktuellen Stand.
     */
    public void migrateDb() {
        em.getTransaction().begin();
        Connection c = em.unwrap(Connection.class);

        try {
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(c));
            Liquibase lb = new Liquibase("db/liquibase/changesets.xml", new CompositeResourceAccessor(
                    new ClassLoaderResourceAccessor(), new FileSystemResourceAccessor()), database);
            lb.update("");
        } catch (LiquibaseException e) {
            throw new RuntimeException("Error during db migration", e);
        }

        em.getTransaction().commit();
    }

    /**
     * Ermittelt einen Datensatz anhand der übergebenen UUID.
     *
     * @param <T> type
     * @param clazz Typ
     * @param uuid eindeutige ID des Datensatz
     * @return Datensatz
     */
    public <T> T findById(final Class<T> clazz, final String uuid) {
        return em.find(clazz, uuid);
    }

    /**
     * Ermittelt alle Datensätze. Es kann entschieden werden, ob deaktivierte Datensätze ebenfalls berücksichtigt werden
     * sollen.
     *
     * @param <T> type
     * @param clazz Typ
     * @param includeDisabled Flag ob deaktiverte Datensätze berücksichtigt werden sollen.
     * @return Ergebnisliste
     */
    public <T> Collection<T> findAll(final Class<T> clazz, final boolean includeDisabled) {
        String queryString = "SELECT e FROM %1$s e ";
        if (!includeDisabled) {
            queryString += "WHERE e.enabled = 1 ";
        }
        TypedQuery<T> query = em.createQuery(String.format(queryString, clazz.getSimpleName()), clazz);
        return query.getResultList();
    }

    /**
     * Ermittelt alle Datensätze (sortiert). Die Anzahl der Ergebnisse kann eingeschränkt werden. Es kann entschieden
     * werden, ob deaktivierte Datensätze ebenfalls berücksichtigt werden sollen.
     *
     * @param <T> type
     * @param clazz Typ
     * @param sortParam Sortierung
     * @param max Anzahl maximaler Einträge
     * @param includeDisabled Flag ob deaktiverte Datensätze berücksichtigt werden sollen.
     * @return Ergebnisliste
     */
    public <T> Collection<T> findAll(final Class<T> clazz, final SortParam sortParam, final int max,
            final boolean includeDisabled) {
        String queryString = "SELECT e FROM %1$s e ";
        if (!includeDisabled) {
            queryString += "WHERE e.enabled = 1 ";
        }
        queryString += "ORDER BY e.%2$s %3$s";
        TypedQuery<T> query = em.createQuery(
                String.format(queryString, clazz.getSimpleName(), sortParam.getAttribute(), sortParam.getOrder()),
                clazz);
        query.setMaxResults(max);
        return query.getResultList();
    }

    /**
     * Ermittelt alle Datensätze (sortiert). Es kann entschieden werden, ob deaktivierte Datensätze ebenfalls
     * berücksichtigt werden sollen.
     *
     * @param <T> type
     * @param clazz Typ
     * @param sortParam Sortierung
     * @param includeDisabled Flag ob deaktiverte Datensätze berücksichtigt werden sollen.
     * @return Ergebnisliste
     */
    public <T> Collection<T> findAll(final Class<T> clazz, final SortParam sortParam, final boolean includeDisabled) {
        String queryString = "SELECT e FROM %1$s e ";
        if (!includeDisabled) {
            queryString += "WHERE e.enabled = 1 ";
        }
        queryString += "ORDER BY e.%2$s %3$s";
        TypedQuery<T> query = em.createQuery(
                String.format(queryString, clazz.getSimpleName(), sortParam.getAttribute(), sortParam.getOrder()),
                clazz);
        return query.getResultList();
    }

    /**
     * Liefert die Ergebnisse einer NamedQuery.
     *
     * @param <T> type
     * @param clazz Typ
     * @param namedQuery Name der Query
     * @param parameters Abfragekriterien / -parameter
     * @return Ergebnisliste
     */
    @SuppressWarnings("unchecked")
    public <T> Collection<T> findResultlistByParameter(final Class<T> clazz, final String namedQuery,
            final Map<String, Object> parameters) {
        Query query = em.createNamedQuery(namedQuery);
        if (null != parameters) {
            for (Entry<String, Object> parameter : parameters.entrySet()) {
                query.setParameter(parameter.getKey(), parameter.getValue());
            }
        }
        return query.getResultList();
    }

    /**
     * Liefert die Ergebnisse einer NamedQuery.
     *
     * @param <T> type
     * @param clazz Typ
     * @param namedQuery Name der Query
     * @param parameters Abfragekriterien / -parameter
     * @return Datensatz
     */
    @SuppressWarnings("unchecked")
    public <T> T findSingleResultByParameter(final Class<T> clazz, final String namedQuery,
            final Map<String, Object> parameters) {
        Query query = em.createNamedQuery(namedQuery);
        if (null != parameters) {
            for (Entry<String, Object> parameter : parameters.entrySet()) {
                query.setParameter(parameter.getKey(), parameter.getValue());
            }
        }
        return (T) query.getSingleResult();
    }

    /**
     * Löscht alle Datensätze eines Typs.
     *
     * @param <T> type
     * @param clazz Typ
     * @return Anzahl der gelöschten Datensätze
     */
    public <T> int deleteAll(final Class<T> clazz) {
        int updatedRows = 0;
        TypedQuery<T> query = em.createQuery("DELETE FROM " + clazz.getSimpleName() + " e", clazz);
        try {
            em.getTransaction().begin();
            query.executeUpdate();
            em.getTransaction().commit();
        } catch (RuntimeException rte) {
            if (em.getTransaction().isActive() || em.getTransaction().getRollbackOnly()) {
                em.getTransaction().rollback();
            }
            throw rte;
        }
        return updatedRows;
    }

    /**
     * Erstellt einen Datesatz.
     *
     * @param <T> type
     * @param entity {@link AbstractEntity}
     */
    public <T> void create(final T entity) {
        try {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
        } catch (RuntimeException rte) {
            if (em.getTransaction().isActive() || em.getTransaction().getRollbackOnly()) {
                em.getTransaction().rollback();
            }
            throw rte;
        }
    }

    /**
     * Erstellt eine Liste von Datesätzen.
     *
     * @param <T> type
     * @param entities list of {@link AbstractEntity}
     */
    public <T> void create(final Collection<T> entities) {
        try {
            em.getTransaction().begin();
            for (T entity : entities) {
                em.persist(entity);
            }
            em.getTransaction().commit();
        } catch (RuntimeException rte) {
            if (em.getTransaction().isActive() || em.getTransaction().getRollbackOnly()) {
                em.getTransaction().rollback();
            }
            throw rte;
        }
    }

    /**
     * Aktualisiert einen Datensatz.
     *
     * @param <T> type
     * @param entity {@link AbstractEntity}
     */
    public <T> void update(final T entity) {
        try {
            em.getTransaction().begin();
            em.merge(entity);
            em.getTransaction().commit();
        } catch (RuntimeException rte) {
            if (em.getTransaction().isActive() || em.getTransaction().getRollbackOnly()) {
                em.getTransaction().rollback();
            }
            throw rte;
        }
    }

    /**
     * Löscht einen Datensatz
     *
     * @param <T> type
     * @param entity {@link AbstractEntity}
     */
    public <T> void delete(final T entity) {
        try {
            em.getTransaction().begin();
            em.remove(entity);
            em.getTransaction().commit();
        } catch (RuntimeException rte) {
            if (em.getTransaction().isActive() || em.getTransaction().getRollbackOnly()) {
                em.getTransaction().rollback();
            }
            throw rte;
        }
    }

    /**
     * Löscht einen Datesatz.
     *
     * @param <T> type
     * @param entity {@link AbstractEntity}
     */
    public <T> void deleteDetached(final T entity) {
        delete(em.merge(entity));
    }
}
