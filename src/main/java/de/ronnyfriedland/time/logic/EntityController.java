package de.ronnyfriedland.time.logic;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

/**
 * @author ronnyfriedland
 */
public class EntityController {

    private static EntityController INSTANCE;

    private final EntityManagerFactory emf;
    private final EntityManager em;

    public static EntityController getInstance() {
        if (null == INSTANCE) {
            INSTANCE = new EntityController();
        }
        return INSTANCE;
    }

    public EntityController() {
        emf = Persistence.createEntityManagerFactory("timetable");
        em = emf.createEntityManager();
    }

    public <T> T findById(Class<T> clazz, String uuid) {
        return em.find(clazz, uuid);
    }

    public <T> Collection<T> findAll(Class<T> clazz) {
        TypedQuery<T> query = em.createQuery("SELECT e FROM " + clazz.getSimpleName() + " e", clazz);
        return query.getResultList();
    }

    public <T> void create(T entity) {
        em.getTransaction().begin();
        em.persist(entity);
        em.getTransaction().commit();
    }

    public <T> void update(T entity) {
        em.getTransaction().begin();
        em.merge(entity);
        em.getTransaction().commit();
    }

    public <T> void delete(T entity) {
        em.getTransaction().begin();
        em.remove(entity);
        em.getTransaction().commit();
    }
}
