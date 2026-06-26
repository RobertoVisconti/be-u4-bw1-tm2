package robertovisconti.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;

import java.util.List;

public class GenericDAO {
    private final EntityManager em;

    public GenericDAO(EntityManager em) {
        this.em = em;
    }

    public <T> boolean isTableEmpty(Class<T> entityClass) {
        List<T> result = em.createQuery(
                "SELECT e FROM " + entityClass.getSimpleName() + " e", entityClass).setMaxResults(1).getResultList();

        return result.isEmpty();
    }

//    public <T> void deleteEntity(<T> entity) {
//        EntityTransaction transaction = em.getTransaction();
//        Query query = em.createQuery("DELETE e FROM " + entity.class.getSimpleName() + " e WHERE ");
//    }

}
