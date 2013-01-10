package de.ronnyfriedland.time.logic;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import de.ronnyfriedland.time.entity.AbstractEntity;
import de.ronnyfriedland.time.sort.SortParam;

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
        if (null == instance) {
            instance = new EntityController();
        }
        return instance;
    }

    /**
     * Erzeugt eine neue {@link EntityController} Instanz.
     */
    private EntityController() {
        emf = Persistence.createEntityManagerFactory("timetable");
        em = emf.createEntityManager();
        em.setFlushMode(FlushModeType.COMMIT);
    }

    /**
     * Ermittelt einen Datensatz anhand der übergebenen UUID.
     * 
     * @param clazz
     *            Typ
     * @param uuid
     *            eindeutige ID des Datensatz
     * @return Datensatz
     */
    public <T> T findById(final Class<T> clazz, final String uuid) {
        return em.find(clazz, uuid);
    }

    /**
     * Ermittelt alle Datensätze. Es kann entschieden werden, ob deaktivierte
     * Datensätze ebenfalls berücksichtigt werden sollen.
     * 
     * @param clazz
     *            Typ
     * @param includeDisabled
     *            Flag ob deaktiverte Datensätze berücksichtigt werden sollen.
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
     * Ermittelt alle Datensätze (sortiert). Die Anzahl der Ergebnisse kann
     * eingeschränkt werden. Es kann entschieden werden, ob deaktivierte
     * Datensätze ebenfalls berücksichtigt werden sollen.
     * 
     * @param clazz
     *            Typ
     * @param sortParam
     *            Sortierung
     * @param max
     *            Anzahl maximaler Einträge
     * @param includeDisabled
     *            Flag ob deaktiverte Datensätze berücksichtigt werden sollen.
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
     * Ermittelt alle Datensätze (sortiert). Es kann entschieden werden, ob
     * deaktivierte Datensätze ebenfalls berücksichtigt werden sollen.
     * 
     * @param clazz
     *            Typ
     * @param sortParam
     *            Sortierung
     * @param includeDisabled
     *            Flag ob deaktiverte Datensätze berücksichtigt werden sollen.
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
     * @param clazz
     *            Typ
     * @param namedQuery
     *            Name der Query
     * @param parameters
     *            Abfragekriterien / -parameter
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
     * @param clazz
     *            Typ
     * @param namedQuery
     *            Name der Query
     * @param parameters
     *            Abfragekriterien / -parameter
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
     * @param clazz
     *            Typ
     * @return Anzahl der gelöschten Datensätze
     */
    public <T> int deleteAll(final Class<T> clazz) {
        int updatedRows = 0;
        TypedQuery<T> query = em.createQuery("DELETE FROM " + clazz.getSimpleName() + " e", clazz);
        try {
            em.getTransaction().begin();
            query.executeUpdate();
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive() && em.getTransaction().getRollbackOnly()) {
                em.getTransaction().rollback();
            }
        }
        return updatedRows;
    }

    /**
     * Erstellt einen Datesatz.
     * 
     * @param entity
     *            {@link AbstractEntity}
     */
    public <T> void create(final T entity) {
        try {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive() && em.getTransaction().getRollbackOnly()) {
                em.getTransaction().rollback();
            }
        }
    }

    /**
     * Aktualisiert einen Datensatz.
     * 
     * @param entity
     *            {@link AbstractEntity}
     */
    public <T> void update(final T entity) {
        try {
            em.getTransaction().begin();
            em.merge(entity);
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive() && em.getTransaction().getRollbackOnly()) {
                em.getTransaction().rollback();
            }
        }
    }

    /**
     * Löscht einen Datensatz
     * 
     * @param entity
     *            {@link AbstractEntity}
     */
    public <T> void delete(final T entity) {
        try {
            em.getTransaction().begin();
            em.remove(entity);
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive() && em.getTransaction().getRollbackOnly()) {
                em.getTransaction().rollback();
            }
        }

    }

    /**
     * Löscht einen Datesatz.
     * 
     * @param entity
     *            {@link AbstractEntity}
     */
    public <T> void deleteDetached(final T entity) {
        delete(em.merge(entity));
    }
}
