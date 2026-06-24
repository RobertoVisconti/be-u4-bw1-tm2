package robertovisconti.dao;

import jakarta.persistence.EntityManager;

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

}
