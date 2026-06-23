package robertovisconti.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import robertovisconti.entities.TitoloViaggio;

public class TitoloViaggioDAO {
     public final EntityManager entityManager;

     public TitoloViaggioDAO(EntityManager em) {this.entityManager =em;}

    public void save (TitoloViaggio newTitoloViaggio) {
        EntityTransaction transaction = this.entityManager.getTransaction();

        transaction.begin();

        this.entityManager.persist(newTitoloViaggio);

        transaction.commit();

        System.out.println(newTitoloViaggio + "creato con successo!");
    }
}
